package com.saudi.tourism.core.services.impl;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.brands.BrandDetail;
import com.saudi.tourism.core.models.components.brands.BrandFilterModel;
import com.saudi.tourism.core.models.components.brands.BrandListModel;
import com.saudi.tourism.core.models.components.brands.Location;
import com.saudi.tourism.core.models.components.brands.BrandRequestParams;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.services.BrandPartnerService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.List;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.Comparator;

import static com.saudi.tourism.core.utils.Constants.BRANDS_CACHE_KEY;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;
import static org.apache.jackrabbit.vault.util.JcrConstants.JCR_TITLE;

/**
 * The type Brand service.
 */
@Component(service = BrandPartnerService.class,
    immediate = true)
@Slf4j
public class BrandPartnerServiceImpl implements BrandPartnerService {
  /**
   * The constant CATEGORY.
   */
  private static final String CATEGORY = "category";
  /**
   * The constant CITY.
   */
  private static final String CITY = "city";

  /**
   * used for extracting filters for brands from search result,
   * should match query builder property number.
   */
  private static final String[] FILTER_GRPS =
      {"category:3", "city:4"};

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

  /**
   * ResourceBundleProvider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The User service.
   */
  @Reference
  private UserService userService;

  /**
   * @param request    request
   * @param queryParam the query parameter
   * @return
   * @throws RepositoryException
   */
  @Override
  public BrandListModel getFilteredBrands(SlingHttpServletRequest request,
                                          BrandRequestParams queryParam) throws RepositoryException {
    String cacheKey = "";
    BrandListModel brandListModel = null;
    if (queryParam.isNew()) {
      cacheKey = BRANDS_CACHE_KEY + "new" + "-" + queryParam.getLocale();
      brandListModel = (BrandListModel) memCache.get(cacheKey);
      if (brandListModel == null) {
        brandListModel = getAllBrands(request, queryParam.getLocale(),
          false, true, false, false);
        if (null != brandListModel && null != brandListModel.getData()
            && !brandListModel.getData().isEmpty()) {
          memCache.add(cacheKey, brandListModel);
        }
      }
      sortLocation(queryParam, brandListModel);
    } else if (queryParam.isPopular()) {
      cacheKey = BRANDS_CACHE_KEY + "popular" + "-" + queryParam.getLocale();
      brandListModel = (BrandListModel) memCache.get(cacheKey);
      if (brandListModel == null) {
        brandListModel = getAllBrands(request, queryParam.getLocale(),
          false, false, true, false);
        if (null != brandListModel && null != brandListModel.getData()
            && !brandListModel.getData().isEmpty()) {
          memCache.add(cacheKey, brandListModel);
        }
      }
      sortLocation(queryParam, brandListModel);
    } else if (queryParam.isClose()) {
      cacheKey = BRANDS_CACHE_KEY + "close" + "-" + queryParam.getLocale();
      brandListModel = (BrandListModel) memCache.get(cacheKey);
      if (brandListModel == null) {
        brandListModel = getAllBrands(request, queryParam.getLocale(),
          false, false, false, true);
        if (null != brandListModel && null != brandListModel.getData()
            && !brandListModel.getData().isEmpty()) {
          memCache.add(cacheKey, brandListModel);
        }
      }
      sortLocation(queryParam, brandListModel);
      sortData(queryParam, brandListModel);
    }
    if (null != brandListModel) {
      return filterPackagesOnParams(brandListModel, queryParam);
    }
    return new BrandListModel();
  }

  /**
   * sort location list for each partner.
   *
   * @param queryParam queryparam.
   * @param brandListModel brand partner list.
   */
  private void sortLocation(BrandRequestParams queryParam, BrandListModel brandListModel) {
    if (StringUtils.isNotBlank(queryParam.getLongitude())
        && StringUtils.isNotBlank(queryParam.getLatitude())
        && null != brandListModel && null != brandListModel.getData()) {
      brandListModel.getData().forEach(data -> {
        if (null != data.getLocations()) {
          data.getLocations().forEach(loc -> {
            loc.setDistance(CommonUtils.getDistanceByCoordinates(queryParam.getLatitude(),
                loc.getCoordinates().getLatitude(), queryParam.getLongitude(),
                loc.getCoordinates().getLongitude()));
          });
          data.getLocations().sort(Comparator.comparing(Location::getDistance));
        }
      });
    }
  }

  /**
   * sort as per closest partner.
   *
   * @param queryParam queryparam.
   * @param brandListModel brand partner list.
   */
  private void sortData(BrandRequestParams queryParam, BrandListModel brandListModel) {
    if (StringUtils.isNotBlank(queryParam.getLongitude())
        && StringUtils.isNotBlank(queryParam.getLatitude())
        && null != brandListModel && null != brandListModel.getData()) {
      brandListModel.getData().forEach(data -> {
        final double[] closestDistance = {Double.MAX_VALUE};
        if (null != data.getLocations()) {
          data.getLocations().forEach(loc -> {
            if (closestDistance[0] > loc.getDistance()) {
              closestDistance[0] = loc.getDistance();
            }
          });
        }
        data.setMinDistance(closestDistance[0]);
      });
      brandListModel.getData().sort(Comparator.comparing(BrandDetail::getMinDistance));
    }
  }

  /**
   * Gets all brands from "/content/sauditourism/{language}/brand-detail-page"
   * and lists them. Also it extracts filters and send to filter.
   * @param request the request
   * @param locale the locale
   * @param isHomePage iS Home page.
   * @param isNew is New.
   * @param isPopular is popular.
   * @param isClose is close.
   * @return brandListModel
   */
  private BrandListModel getAllBrands(SlingHttpServletRequest request, String locale,
                                      boolean isHomePage, boolean isNew, boolean isPopular, boolean isClose) {
    BrandListModel brandListModel = new BrandListModel();
    AdminPageOption adminOptions =
        AdminUtil.getAdminOptions(locale, StringUtils.EMPTY);
    String brandsPath = adminOptions.getBrandsPath();

    if (brandsPath == null) {
      brandsPath = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale
          + Constants.FORWARD_SLASH_CHARACTER + Constants.BRAND_ROOT_PAGE;
    }
    ResourceResolver resourceResolver = request.getResourceResolver();
    Query query;
    if (isHomePage && !isClose) {
      String limit = "4";
      if (null != request.getRequestParameter("limit")) {
        limit = request.getRequestParameter("limit").getString();
      }
      query = queryBuilder.createQuery(PredicateGroup.create(getHomePagePredicateQueryMap(brandsPath,
          isNew, isPopular, isClose, limit)),
              resourceResolver.adaptTo(Session.class));
    } else {
      query = queryBuilder.createQuery(PredicateGroup.create(getPredicateQueryMap(brandsPath, isNew,
          isPopular, isClose)), resourceResolver.adaptTo(Session.class));
    }
    SearchResult searchResult = query.getResult();
    Iterator<Node> nodeIterator = searchResult.getNodes();
    List<BrandDetail> brandList = new ArrayList<>();
    int count = 0;
    count = getAllBrandsDetails(resourceResolver, nodeIterator, brandList, count);
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    brandListModel.setData(brandList);
    brandListModel.setFilters(getFiltersFromResult(locale, searchResult, tagManager));
    Pagination pagination = new Pagination();
    pagination.setTotal(count);
    brandListModel.setPagination(pagination);
    return brandListModel;
  }

  /**
   * Querybuilder map for getting all brands.
   *
   * @param path brands path
   * @param isNew is New.
   * @param isPopular is popular.
   * @param isClose is close.
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path, boolean isNew, boolean isPopular,
                                                   boolean isClose) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.BRANDS_RES_TYPE);
    map.put("2_property", JCR_TITLE);
    map.put("2_property.operation", "exists");
    map.put("3_property", CATEGORY);
    map.put("4_property", CITY);
    if (isNew) {
      map.put("5_property", "isNew");
      map.put("5_property.value", "true");
    }
    if (isPopular) {
      map.put("5_property", "isPopular");
      map.put("5_property.value", "true");
    }
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");
    map.put("p.limit", "-1");
    return map;
  }

  /**
   * Querybuilder map for getting all brands.
   *
   * @param path brands path
   * @param isNew is New.
   * @param isPopular is popular.
   * @param isClose is close.
   * @param limit limit.
   * @return map predicate query map
   */
  private Map<String, String> getHomePagePredicateQueryMap(String path, boolean isNew,
      boolean isPopular, boolean isClose, String limit) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("type", TYPE_PAGE_CONTENT);
    if (isNew) {
      map.put("property", "isNew");
      map.put("property.value", "true");
    }
    if (isPopular) {
      map.put("property", "isPopular");
      map.put("property.value", "true");
    }
    if (isClose) {
      map.put("property", "isCloseToYou");
      map.put("property.value", "true");
    }
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");
    map.put("p.limit", limit);
    return map;
  }


  /**
   * Get all the brands info.
   *
   * @param resourceResolver the resourceResolver
   * @param nodeIterator     the nodeIterator
   * @param brandList        the brandList
   * @param count            the count
   * @return the count
   */

  private int getAllBrandsDetails(ResourceResolver resourceResolver,
                                  final Iterator<Node> nodeIterator, List<BrandDetail> brandList, int count) {
    while (nodeIterator.hasNext()) {
      try {
        Resource res = resourceResolver.getResource(nodeIterator.next().getPath());
        if (res != null) {
          BrandDetail brandItem = res.adaptTo(BrandDetail.class);
          if (brandItem != null) {
            brandList.add(brandItem);
            count++;
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in forming states Brands list", e);
      }
    }
    return count;
  }

  /**
   * Get Filters From Result.
   *
   * @param language     the language
   * @param searchResult the searchResult
   * @param tagManager   the tagManager
   * @return brandFilterModel
   */
  private BrandFilterModel getFiltersFromResult(final String language,
                                                SearchResult searchResult, TagManager tagManager) {

    BrandFilterModel brandFilterModel = new BrandFilterModel();
    try {
      Map<String, Facet> facets = searchResult.getFacets();
      for (String grp : FILTER_GRPS) {
        // 0th element is filtername and 1st element is queryparam number
        String[] filterSplit = grp.split(Constants.COLON_SLASH_CHARACTER);
        String key = filterSplit[1] + "_property";
        if (CITY.equals(filterSplit[0])) {
          brandFilterModel.setBrandCity(getFilteritems(facets, key, tagManager, true, language));
        } else if (CATEGORY.equals(filterSplit[0])) {
          brandFilterModel.setBrandCategory(getFilteritems(facets, key, tagManager, true, language));
        }
      }
    } catch (RepositoryException e) {
      LOGGER.error("RepositoryException during getFiltersFromResult() ", e);
    }
    return brandFilterModel;
  }

  /**
   * Gets filteritems.
   *
   * @param facets     the facets
   * @param key        the key
   * @param tagManager the tagManager
   * @param localise   the localise
   * @param language   the locale to localise
   * @return the filteritems
   */
  private List<AppFilterItem> getFilteritems(Map<String, Facet> facets, String key, final TagManager tagManager,
                                             final boolean localise, final String language) {
    final Locale locale1 = new Locale(language);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale1);

    List<AppFilterItem> items = new ArrayList<>();
    if ((!facets.isEmpty()) && (facets.containsKey(key))) {
      Facet fc = facets.get(key);

      for (Bucket bucket : fc.getBuckets()) {
        String bc = bucket.getValue();
        AppFilterItem item = new AppFilterItem(bc, bc);
        if (item.getId().contains("/")) { // handle tag names
          Tag tag = tagManager.resolve(item.getId());

          if (Objects.nonNull(tag)) {
            item.setId(AppUtils.stringToID(tag.getTitle()));
            item.setValue(tag.getTitle(locale1));
          }
        }
        //in case if tag not found but the translation needed
        if (Objects.nonNull(i18nBundle) && localise) { // update city to i18n value
          item.setValue(i18nBundle.getString(item.getValue()));
        }
        items.add(item);
      }

    }
    return items;
  }

  @Override
  public List<String> getRelatedPackagesPaths(SlingHttpServletRequest request,
                                              BrandRequestParams requestParams,
                                              BrandDetail brandDetail) throws RepositoryException {

    List<String> ouputList = new ArrayList<>();

    BrandListModel brandListModel = getAllBrands(request, requestParams.getLocale(),
        requestParams.isHomePage(), requestParams.isNew(), requestParams.isPopular(), requestParams.isClose());
    BrandRequestParams brandRequestParams = new BrandRequestParams();

    // Filter based on current city or region

    brandRequestParams.setPath(brandDetail.getPath());

    List<BrandDetail> filteredModel =
        filterPackagesOnParams(brandListModel, brandRequestParams).getData();
    for (BrandDetail brandItem : filteredModel) {
      ouputList.add(brandItem.getPath());
    }
    return ouputList;
  }

  /**
   * Filter hotels on params.
   *
   * @param listModel          the brands list
   * @param brandRequestParams the brandRequestParams
   * @return filteredPackageList the brands selected filters
   */
  private BrandListModel filterPackagesOnParams(BrandListModel listModel,
                                                BrandRequestParams brandRequestParams) {
    BrandListModel filteredBrandList = new BrandListModel();
    int count = 0;
    int offset = 0;
    int total = 0;

    if (listModel.getData() == null) {
      return filteredBrandList;
    }

    List<BrandDetail> brands = listModel.getData();
    List<BrandDetail> filteredbrands = new ArrayList<>();
    for (BrandDetail brandItem : brands) {

      if (!brandItem.getPath().equals(brandRequestParams.getPath()) && matchFilters(
          brandItem, brandRequestParams)) {
        total++;
        if (offset++ >= brandRequestParams.getOffset()) {
          count++;
          if (count <= brandRequestParams.getLimit()) { // skip once limit is crossed
            filteredbrands.add(brandItem);
          }
        }
      }
    }
    Pagination pagination = listModel.getPagination();
    pagination.setLimit(brandRequestParams.getLimit());
    pagination.setTotal(total);

    pagination.setOffset(brandRequestParams.getOffset());
    filteredBrandList.setData(filteredbrands);
    filteredBrandList.setPagination(pagination);
    filteredBrandList.setFilters(getSortedFilterValues(listModel));

    return filteredBrandList;
  }

  /**
   * Match filters boolean.
   *
   * @param brandItem          the brands
   * @param brandRequestParams the brands selected filters
   * @return the boolean
   */
  private boolean matchFilters(BrandDetail brandItem,
                               BrandRequestParams brandRequestParams) {

    boolean matchCategory = false;
    boolean matchCity = false;
    boolean matchNew = (brandRequestParams.isNew() && brandItem.getIsNew())
        || !brandRequestParams.isNew();
    boolean matchPopular = (brandRequestParams.isPopular() && brandItem.getIsPopular())
        || !brandRequestParams.isPopular();
    // City or Region match single match
    if (((Objects.nonNull(brandRequestParams.getBrandCategory()) && Objects
        .nonNull(brandItem.getCategories()))) || ((Objects.nonNull(brandRequestParams.getBrandCity())) && Objects
        .nonNull(brandItem.getCities()))) {
      if (brandRequestParams.getBrandCategory() != null) {
        for (String category : brandItem.getCategories()) {
          if (brandRequestParams.getBrandCategory().contains(category)) {
            matchCategory = true;
            break;
          }
        }
      } else {
        matchCategory = true;
      }
      if (brandRequestParams.getBrandCity() != null) {
        for (String city : brandItem.getCities()) {
          if (brandRequestParams.getBrandCity().contains(city)) {
            matchCity = true;
            break;
          }
        }
      } else {
        matchCity = true;
      }
    } else {
      return true;
    }
    return matchCategory && matchCity && matchNew && matchPopular;
  }

  /**
   * Sort filter values.
   *
   * @param listModel the model
   * @return BrandFilterModel
   */
  private BrandFilterModel getSortedFilterValues(BrandListModel listModel) {
    BrandFilterModel filters = listModel.getFilters();
    Comparator<AppFilterItem> comparator = Comparator.comparing(AppFilterItem::getValue);
    filters.getBrandCategory().sort(comparator);
    return filters;
  }

  @Override
  public Map<String, BrandListModel> getHomePageItems(SlingHttpServletRequest request,
                                                      BrandRequestParams queryParam) {
    BrandListModel isNewBrandList = getAllBrands(request, queryParam.getLocale(),
        true, true, false, false);
    sortLocation(queryParam, isNewBrandList);
    BrandListModel isPopular = getAllBrands(request, queryParam.getLocale(), true,
        false, true, false);
    sortLocation(queryParam, isPopular);
    BrandListModel topClosePartners = new BrandListModel();
    if (StringUtils.isNotBlank(queryParam.getLatitude())
        && StringUtils.isNotBlank(queryParam.getLatitude())) {
      BrandListModel isClose = getAllBrands(request, queryParam.getLocale(),
          true, false, false, true);
      sortLocation(queryParam, isClose);
      sortData(queryParam, isClose);
      if (null != isClose && null != isClose.getData() && !isClose.getData().isEmpty()) {
        Integer limit = Integer.parseInt("4");
        if (null != queryParam.getLimit()) {
          limit = queryParam.getLimit();
        }
        int i = 0;
        topClosePartners.setData(new ArrayList<>());
        while (i < limit &&  ((i + 1) <= isClose.getData().size())) {
          topClosePartners.getData().add(isClose.getData().get(i));
          i++;
        }
      }
    }

    Map<String, BrandListModel> homePageListMap = new HashMap<>();


    homePageListMap.put("isNew", isNewBrandList);
    homePageListMap.put("isPopular", isPopular);
    homePageListMap.put("isClose", topClosePartners);
    return homePageListMap;
  }

}
