package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackageFilterModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.jackrabbit.oak.commons.PathUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * The type Package service implementation.
 */
@Component(service = PackageService.class,
           immediate = true)
@Slf4j
public class PackageServiceImpl implements PackageService {

  /**
   * The constant MIN_PRICE.
   */
  private static final String MIN_PRICE = "minPrice";

  /**
   * The constant MAX_PRICE.
   */
  private static final String MAX_PRICE = "maxPrice";

  //TO CONFIRM IDs
  /**
   * used for extracting filters for packages from search result,
   * should match query builder property number.
   */
  private static final List<Pair<String, String>> FILTER_VALUES =
      Arrays.asList(new ImmutablePair<>("packageAreaTags", "3_property"),
          new ImmutablePair<>("durationAuth", "4_property"),
          new ImmutablePair<>("packageTargetTags", "5_property"),
          new ImmutablePair<>("packageCategoryTags", "6_property"),
          new ImmutablePair<>("price", "7_property"));

  /**
   * The User service.
   */
  @JsonIgnore
  @Reference
  private transient UserService userService;

  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;


  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;
  /**
   * The memCache.
   */
  @Reference
  private Cache memCache;

  @Override
  public PackagesListModel getFilteredPackages(PackagesRequestParams packagesRequestParams) {

    PackagesListModel packagesListModel =
        getAllPackages(packagesRequestParams.getLocale());
    // city filter to Filter on both City and Region
    if (Objects.nonNull(packagesRequestParams.getCity())) {
      packagesRequestParams.setRegion(packagesRequestParams.getCity());
    }

    //if the header includes key=all, value=true, retrieve all the packages for the locale
    if (packagesRequestParams.isAll()) {
      //get the list of related paths for each package
      for (PackageDetail packageItem : packagesListModel.getData()) {
        packageItem.setRelatedPackagesPaths(
            getRelatedPackagesPaths(packagesRequestParams.getLocale(),
                packagesRequestParams.isAllExpired(), packageItem));
      }
      return packagesListModel;
    } else {
      return filterPackagesOnParams(packagesListModel, packagesRequestParams);
    }

  }

  @Override
  public List<String> getRelatedPackagesPaths(String language, Boolean isAllExpired,
      PackageDetail packageDetail) {

    PackagesListModel packagesListModel = getAllPackages(language);
    PackagesRequestParams packagesRequestParams = new PackagesRequestParams();

    // Filter based on current city or region
    packagesRequestParams.setPath(packageDetail.getPath());

    return
        filterPackagesOnParams(packagesListModel, packagesRequestParams).getData()
        .stream().map(PackageDetail::getPath).collect(Collectors.toList());
  }

  @Override
  public List<PackageDetail> getRelatedPackages(SlingHttpServletRequest request, String language,
      Boolean isAllExpired, PackageDetail packageDetail) {

    PackagesListModel packagesListModel = getAllPackages(language);
    PackagesRequestParams packagesRequestParams = new PackagesRequestParams();

    // Filter based on current area
    packagesRequestParams.setPath(packageDetail.getPath());
    packagesRequestParams.setLimit(Constants.EVENTS_SLIDER_COUNT);
    packagesRequestParams.setArea(packageDetail.getArea().stream().
        map(AppFilterItem::getId).collect(Collectors.toList()));

    return filterPackagesOnParams(packagesListModel, packagesRequestParams).getData();

  }

  /**
   * Gets all packages from "/content/sauditourism/{language}/packages"
   * and lists them. Also it extracts filters and send to filter.
   *
   * @param language the language
   * @return the all states
   */
  private PackagesListModel getAllPackages(String language) {
    String cacheKey = Constants.PACKAGES_CACHE_KEY + language;

    PackagesListModel packageListModel = (PackagesListModel) memCache.get(cacheKey);
    if (packageListModel != null) {
      return packageListModel;
    }
    packageListModel = new PackagesListModel();

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {

      AdminPageOption adminOptions = AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
      String packagesPath = Optional.ofNullable(adminOptions.getPackagesPath()).orElseGet(
          () -> PathUtils.concat(Constants.ROOT_CONTENT_PATH, language, Constants.PACKAGES));
      Query query = queryBuilder.createQuery(PredicateGroup
              .create(getPredicateQueryMap(packagesPath)), resourceResolver.adaptTo(Session.class));
      SearchResult searchResult = query.getResult();
      List<Node> nodes = IteratorUtils.toList(searchResult.getNodes());
      List<PackageDetail> packagesList = getAllPackageDetails(resourceResolver, nodes);
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(language);

      packageListModel.setData(packagesList);
      packageListModel.setFilters(getFiltersFromResult(searchResult, tagManager, locale,
          i18nProvider.getResourceBundle(locale), adminOptions));

      Pagination pagination = new Pagination();
      pagination.setTotal(packagesList.size());
      packageListModel.setPagination(pagination);
      if (!packagesList.isEmpty()) {
        memCache.add(cacheKey, packageListModel);
      }

    } catch (Exception e) {
      LOGGER.error("Error getting ResourceResolver", e);
    }

    return packageListModel;
  }

  /**
   * update all packages details list from query builder iterator items.
   *
   * @param resolver     the resolver
   * @param nodes        the nodes list
   * @return package details list
   */
  private List<PackageDetail> getAllPackageDetails(final ResourceResolver resolver,
      final List<Node> nodes) {
    return nodes.stream()
            .map(node -> CommonUtils.resolveResource(resolver, node))
            .filter(Objects::nonNull)
            .map(resource -> resource.adaptTo(PackageDetail.class))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
  }

  /**
   * Filter packages on params.
   *
   * @param listModel             the packages list
   * @param packagesRequestParams the packagesRequestParams
   * @return filteredPackageList the package selected filters
   */
  private PackagesListModel filterPackagesOnParams(PackagesListModel listModel,
      PackagesRequestParams packagesRequestParams) {

    if (listModel.getData() == null) {
      return new PackagesListModel();
    }

    List<PackageDetail> filteredPackages = listModel.getData().stream()
        .filter(packageDetail -> !packageDetail.getPath().equals(packagesRequestParams.getPath())
            && matchFilters(packageDetail, packagesRequestParams))
        .collect(Collectors.toList());

    List<PackageDetail> filteredPackagesPage = filteredPackages.stream()
        .skip(packagesRequestParams.getOffset())
        .limit(packagesRequestParams.getLimit())
        .collect(Collectors.toList());

    Pagination pagination = listModel.getPagination();
    pagination.setLimit(packagesRequestParams.getLimit());
    pagination.setTotal(filteredPackages.size());
    pagination.setOffset(packagesRequestParams.getOffset());

    if (packagesRequestParams.getTarget() == null && packagesRequestParams.getArea() == null
          && packagesRequestParams.getCategory() == null
            && packagesRequestParams.getDuration() == null
            && packagesRequestParams.getMinPrice() == null
            && packagesRequestParams.getMaxPrice() == null) {

      pagination.setTotal(listModel.getData().size());
    }

    listModel.getFilters().sort();
    return new PackagesListModel(filteredPackagesPage, listModel.getFilters(),
        pagination);
  }

  /**
   * Match filters boolean.
   *
   * @param packageItem           the package
   * @param packagesRequestParams the package selected filters
   * @return the boolean
   */
  private boolean matchFilters(PackageDetail packageItem,
      PackagesRequestParams packagesRequestParams) {

    // City match filter
    if (Objects.nonNull(packagesRequestParams.getCity()) && Objects.nonNull(packageItem.getCities())
        && !packageItem.getCities().contains(packagesRequestParams.getCity())) {
      return false;
    }
    // Area match filter
    if (Objects.nonNull(packagesRequestParams.getArea())
        && !isMatchFilter(packageItem.getAreas(), packagesRequestParams.getArea())) {
      return false;
    }

    // Category match filter
    if (Objects.nonNull(packagesRequestParams.getCategory())
        && !isMatchFilter(packageItem.getCategories(), packagesRequestParams.getCategory())) {
      return false;
    }

    // Duration match filter
    if (Objects.nonNull(packagesRequestParams.getDuration()) && !packagesRequestParams
        .getDuration().contains(packageItem.getDurationAuth())) {
      return false;
    }

    // Min and max price match filter
    if (Objects.nonNull(packagesRequestParams.getMinPrice()) && Objects
        .nonNull(packagesRequestParams.getMaxPrice()) && !checkPrice(packageItem,
        packagesRequestParams)) {
      return false;
    }

    // Target match filter
    return !(Objects.nonNull(packagesRequestParams.getTarget())
            && !isMatchFilter(packageItem.getTargets(), packagesRequestParams.getTarget()));

  }

  /**
   * Check if price is suitable for filtering.
   *
   * @param packageItem           packageItem.
   * @param packagesRequestParams packagesRequestParams.
   * @return boolean.
   */
  private boolean checkPrice(PackageDetail packageItem,
      PackagesRequestParams packagesRequestParams) {
    return StringUtils.isNotEmpty(packageItem.getPrice())
        && Integer.parseInt((packageItem.getPrice().replace(" SAR", "")))
            >= Integer.parseInt(packagesRequestParams.getMinPrice())
        && Integer.parseInt((packageItem.getPrice().replace(" SAR", "")))
            <= Integer.parseInt(packagesRequestParams.getMaxPrice());
  }

  /**
   * Is match filter boolean.
   *
   * @param packageItemFilters           the package item
   * @param packagesRequestParamsFilters the packages request params
   * @return the boolean
   */
  private boolean isMatchFilter(final List<String> packageItemFilters,
      final List<String> packagesRequestParamsFilters) {
    return (Objects.nonNull(packageItemFilters) && packageItemFilters.stream()
        .anyMatch(packagesRequestParamsFilters::contains));
  }

  //TO FIX KEY USAGE FOR FILTERS TO CORRESPOND WITH RESULTS AND FILTER_GRPS
  /**
   * Gets filters from search result.
   *
   * @param result     the result
   * @param tagManager the tagManager
   * @param locale     locale
   * @param i18nBundle internationalization bundle
   * @param adminPageOption admin page option
   * @return the string
   */
  private PackageFilterModel getFiltersFromResult(SearchResult result,
      final TagManager tagManager, Locale locale, ResourceBundle i18nBundle,
      final AdminPageOption adminPageOption) {
    final PackageFilterModel filters = new PackageFilterModel();
    final Map<String, Facet> facets;
    try {
      facets = result.getFacets();
    } catch (RepositoryException e) {
      LOGGER.error("RepositoryException during getFiltersFromResult() ", e);
      return filters;
    }

    FILTER_VALUES.forEach(grp -> {
      if (PackageFilterModel.PROP_PRICE.equals(grp.getKey())) {
        if (!adminPageOption.isHidePackagesPrice()) {
          filters.setItems(PackageFilterModel.PROP_PRICE,
              getMinMaxPrice(facets.get(grp.getValue())));
        }
      } else {
        filters.setItems(grp.getKey(),
            getFilterItems(facets.get(grp.getValue()), tagManager, locale, i18nBundle));
      }
    });

    return filters;
  }

  /**
   * Gets filter items.
   * @param facet      facet
   * @param tagManager the tagManager
   * @param locale     locale
   * @param i18nBundle internationalization bundle
   * @return the filter items
   */
  private List<AppFilterItem> getFilterItems(Facet facet, final TagManager tagManager,
      Locale locale, ResourceBundle i18nBundle) {
    return Optional.ofNullable(facet).map(Facet::getBuckets).orElse(Collections.emptyList())
        .stream()
        .map(Bucket::getValue)
        .map(value ->
          Optional.ofNullable(tagManager.resolve(value))
              .map(tag -> AppFilterItem.of(tag, locale))
              .orElseGet(() -> new AppFilterItem(value, i18nBundle.getString(value)))
        ).collect(Collectors.toList());
  }

  /**
   * Get minimum and maximum prices for filters.
   * @param facet price facet
   * @return list with two items.
   */
  private List<AppFilterItem> getMinMaxPrice(Facet facet) {
    List<AppFilterItem> items = new ArrayList<>();
    List<Integer> prices =
        Optional.ofNullable(facet).map(Facet::getBuckets).orElse(Collections.emptyList())
        .stream().map(Bucket::getValue)
            .map(val -> val.replaceAll("\\D+", ""))
            .filter(StringUtils::isNotEmpty)
            .map(Integer::parseInt)
        .collect(Collectors.toList());

    if (prices.isEmpty()) {
      return items;
    }

    int minPrice = Collections.min(prices);
    int maxPrice = Collections.max(prices);
    items.add(new AppFilterItem(MIN_PRICE, String.valueOf(minPrice), true));
    items.add(new AppFilterItem(MAX_PRICE, String.valueOf(maxPrice), true));

    return items;
  }

  /**
   * Querybuilder map for getting all packages.
   *
   * @param path packages path
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);

    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.PACKAGE_DETAIL_RES_TYPE);
    map.put("2_property", PN_TITLE);
    map.put("2_property.operation", "exists");
    map.put("3_property", PackageFilterModel.PROP_AREA);
    map.put("4_property", PackageFilterModel.PROP_DURATION_AUTH);
    map.put("5_property", PackageFilterModel.PROP_TARGET_TAGS);
    map.put("6_property", PackageFilterModel.PROP_CATEGORY_TAGS);
    map.put("7_property", PackageFilterModel.PROP_PRICE);
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");

    map.put("p.limit", "-1");
    return map;
  }
}
