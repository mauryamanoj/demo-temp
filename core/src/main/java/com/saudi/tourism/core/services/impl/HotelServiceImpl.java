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
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.hotels.HotelDetail;
import com.saudi.tourism.core.models.components.hotels.HotelsFilterModel;
import com.saudi.tourism.core.models.components.hotels.HotelsListModel;
import com.saudi.tourism.core.models.components.hotels.HotelsRequestParams;
import com.saudi.tourism.core.services.HotelService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * The type Package service implementation.
 */
@Component(service = HotelService.class,
           immediate = true)
@Slf4j
public class HotelServiceImpl implements HotelService {

  /**
   * The constant AREA.
   */
  private static final String HOTEL_CITY = "hotelCity";


  /**
   * The constant Chain.
   */
  private static final String HOTEL_CHAIN = "chain";

  /**
   * used for extracting filters for hotels from search result,
   * should match query builder property number.
   */
  private static final String[] FILTER_GRPS =
      {"hotelCity:3", "chain:4"};

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
   * The tagManager.
   */
  private ResourceBundle i18nBundle;
  /**
   * The language.
   */
  private String language;

  @Override public HotelsListModel getFilteredPackages(SlingHttpServletRequest request,
                                                       HotelsRequestParams hotelsRequestParams) {

    HotelsListModel hotelsListModel =
        getAllPackages(request, hotelsRequestParams.getLocale());

    //if the header includes key=all, value=true, retrieve all the hotels for the locale
    if (hotelsRequestParams.isAll()) {
      //get the list of related paths for each hotel
      for (HotelDetail hotelItem : hotelsListModel.getData()) {
        hotelItem.setRelatedPackagesPaths(
            getRelatedPackagesPaths(request, hotelsRequestParams.getLocale(),
                    hotelsRequestParams.isAllExpired(), hotelItem));
      }
      return hotelsListModel;
    } else {
      return filterPackagesOnParams(hotelsListModel, hotelsRequestParams);
    }

  }

  @Override
  public List<String> getRelatedPackagesPaths(SlingHttpServletRequest request, String language,
                                              Boolean isAllExpired, HotelDetail hotelDetail) {
    List<String> ouputList = new ArrayList<>();

    HotelsListModel hotelsListModel = getAllPackages(request, language);
    HotelsRequestParams hotelsRequestParams = new HotelsRequestParams();

    // Filter based on current city or region
    hotelsRequestParams.setPath(hotelDetail.getPath());

    List<HotelDetail> filteredModel =
        filterPackagesOnParams(hotelsListModel, hotelsRequestParams).getData();
    for (HotelDetail hotelItem : filteredModel) {
      ouputList.add(hotelItem.getPath());
    }
    return ouputList;

  }

  /**
   * Gets all hotels from "/content/sauditourism/{language}/Configs/hotels"
   * and lists them. Also it extracts filters and send to filter.
   *
   * @param request  the request
   * @param language the language
   * @return the all states
   */
  private HotelsListModel getAllPackages(SlingHttpServletRequest request, String language) {
    String cacheKey = Constants.HOTELS_CACHE_KEY + language;

    HotelsListModel hotelsListModel = (HotelsListModel) memCache.get(cacheKey);
    if (hotelsListModel != null) {
      return hotelsListModel;
    }
    hotelsListModel = new HotelsListModel();
    AdminPageOption adminOptions =
        AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
    String hotelsPath = adminOptions.getHotelsPath();
    if (hotelsPath == null) {
      hotelsPath = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
          + Constants.FORWARD_SLASH_CHARACTER + Constants.CONFIGS
          + Constants.FORWARD_SLASH_CHARACTER + "hotels";
    }
    Query query = queryBuilder
        .createQuery(PredicateGroup.create(getPredicateQueryMap(hotelsPath)),
            request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    Iterator<Node> nodeIterator = searchResult.getNodes();
    List<HotelDetail> hotelsList = new ArrayList<>();
    int count = 0;
    count = getAllPackageDetails(request.getResourceResolver(), nodeIterator, hotelsList, count);
    TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
    this.language = language;
    i18nBundle = request.getResourceBundle(new Locale(language));

    hotelsListModel.setData(hotelsList);
    hotelsListModel.setFilters(getFiltersFromResult(searchResult, tagManager));

    Pagination pagination = new Pagination();
    pagination.setTotal(count);
    hotelsListModel.setPagination(pagination);
    if (!hotelsList.isEmpty()) {
      memCache.add(cacheKey, hotelsListModel);
    }
    return hotelsListModel;

  }

  /**
   * update all hotels details list from query builder iterator items.
   *
   * @param resolver     the resolver
   * @param nodeIterator the node iterator
   * @param hotelsList the hotels list
   * @param count        the count
   * @return the all hotel details
   */
  private int getAllPackageDetails(final ResourceResolver resolver,
      final Iterator<Node> nodeIterator, final List<HotelDetail> hotelsList, int count) {
    while (nodeIterator.hasNext()) {
      try {
        Resource res = resolver.getResource(nodeIterator.next().getPath());
        if (res != null) {
          HotelDetail hotelItem = res.adaptTo(HotelDetail.class);
          if (hotelItem != null) {
            hotelsList.add(hotelItem);
            count++;
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in forming states hotels list", e);
      }
    }
    return count;
  }

  /**
   * Filter hotels on params.
   *
   * @param listModel             the hotels list
   * @param hotelsRequestParams the hotelsRequestParams
   * @return filteredPackageList the hotels selected filters
   */
  private HotelsListModel filterPackagesOnParams(HotelsListModel listModel,
      HotelsRequestParams hotelsRequestParams) {
    HotelsListModel filteredHotelsList = new HotelsListModel();
    int count = 0;
    int offset = 0;
    int total = 0;

    if (listModel.getData() == null) {
      return filteredHotelsList;
    }

    List<HotelDetail> hotels = listModel.getData();
    List<HotelDetail> filteredHotels = new ArrayList<>();
    for (HotelDetail hotelItem : hotels) {

      if (!hotelItem.getPath().equals(hotelsRequestParams.getPath()) && matchFilters(
              hotelItem, hotelsRequestParams)) {
        total++;
        if (offset++ >= hotelsRequestParams.getOffset()) {
          count++;
          if (count <= hotelsRequestParams.getLimit()) { // skip once limit is crossed
            filteredHotels.add(hotelItem);
          }
        }

      }
    }
    Pagination pagination = new Pagination();
    pagination.setLimit(hotelsRequestParams.getLimit());
    pagination.setTotal(total);

    pagination.setOffset(hotelsRequestParams.getOffset());
    filteredHotelsList.setData(filteredHotels);
    filteredHotelsList.setPagination(pagination);
    filteredHotelsList.setFilters(getSortedFilterValues(listModel));
    AdminPageOption adminOptions =
        AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
    boolean hideFilters = (StringUtils.isNotBlank(hotelsRequestParams.getArticleId())
        && adminOptions.isDisableArticleHotelsFilter());
    filteredHotelsList.setHideFilters(hideFilters);
    return filteredHotelsList;
  }

  /**
   * Sort filter values.
   *
   * @param listModel the model
   * @return HotelsFilterModel
   */
  private HotelsFilterModel getSortedFilterValues(HotelsListModel listModel) {
    HotelsFilterModel filters = listModel.getFilters();
    Comparator<AppFilterItem> comparator = Comparator.comparing(AppFilterItem::getValue);
    filters.getArea().sort(comparator);
    return filters;
  }

  /**
   * Match filters boolean.
   *
   * @param hotelItem           the hotel
   * @param hotelsRequestParams the hotel selected filters
   * @return the boolean
   */
  private boolean matchFilters(HotelDetail hotelItem,
      HotelsRequestParams hotelsRequestParams) {

    boolean matchArea  = false;
    boolean matchHotelChain = false;
    boolean matchArticle = false;
    // City or Region match single match
    if (Objects.nonNull(hotelsRequestParams.getArea()) && Objects
        .nonNull(hotelItem.getHotelCities())) {
      for (String area : hotelItem.getHotelCities()) {
        if (hotelsRequestParams.getArea().contains(area)) {
          matchArea = true;
          break;
        }
      }
    } else {
      matchArea = true;
    }
    if (Objects.nonNull(hotelsRequestParams.getHotelChain()) && Objects
        .nonNull(hotelItem.getHotelChains())) {
      for (String chain : hotelItem.getHotelChains()) {
        if (hotelsRequestParams.getHotelChain().contains(chain)) {
          matchHotelChain = true;
          break;
        }
      }
    } else {
      matchHotelChain = true;
    }
    if (Objects.nonNull(hotelsRequestParams.getArticleId())) {
      if (Objects
          .nonNull(hotelItem.getArticleIds())) {
        final String requestArticleId = hotelsRequestParams.getArticleId();
        for (String id : hotelItem.getArticleIds()) {
          if (requestArticleId.equals(id)) {
            matchArticle = true;
            break;
          }
        }
      }
    } else {
      matchArticle = true;
    }
    return matchArea && matchHotelChain && matchArticle;
  }

  /**
   * Gets filters from search result.
   *
   * @param result     the result
   * @param tagManager the tagManager
   * @return the string
   */
  private HotelsFilterModel getFiltersFromResult(SearchResult result,
      final TagManager tagManager) {
    HotelsFilterModel filters = new HotelsFilterModel();
    try {
      Map<String, Facet> facets = result.getFacets();
      for (String grp : FILTER_GRPS) {
        // 0th element is filtername and 1st element is queryparam number
        String[] filterSplit = grp.split(Constants.COLON_SLASH_CHARACTER);
        String key = filterSplit[1] + "_property";
        if (HOTEL_CITY.equals(filterSplit[0])) {
          filters.setArea(getFilteritems(facets, key, tagManager, true));
        }
        if (HOTEL_CHAIN.equals(filterSplit[0])) {
          filters.setHotelChain(getFilteritems(facets, key, tagManager, true));
        }
      }

    } catch (RepositoryException e) {
      LOGGER.error("RepositoryException during getFiltersFromResult() ", e);
    }
    return filters;
  }

  /**
   * Gets filteritems.
   *
   * @param facets     the facets
   * @param key        the key
   * @param tagManager the tagManager
   * @param localise   the localise
   * @return the filteritems
   */
  private List<AppFilterItem> getFilteritems(Map<String, Facet> facets, String key,
      final TagManager tagManager, final boolean localise) {
    List<AppFilterItem> items = new ArrayList<>();
    if (facets != null && facets.containsKey(key)) {
      Facet fc = facets.get(key);

      for (Bucket bucket : fc.getBuckets()) {
        String bc = bucket.getValue();
        AppFilterItem item = new AppFilterItem(bc, bc);
        translateAppFilterItem(item, tagManager, localise);
        items.add(item);
      }

    }
    return items;
  }

  /**
   * Adds tag names and translations.
   *
   * @param item       AppFilterItem object
   * @param tagManager the tagManager
   * @param localise   the localise
   */
  private void translateAppFilterItem(AppFilterItem item, TagManager tagManager, boolean localise) {
    if (item.getId().contains("/")) { // handle tag names
      Tag tag = tagManager.resolve(item.getId());

      if (Objects.nonNull(tag)) {
        String value = tag.getTitle(new Locale(language));
        item.setValue(value);
        item.setId(AppUtils.stringToID(tag.getTitle(new Locale(Constants.DEFAULT_LOCALE))));
      }
    }
    if (Objects.nonNull(i18nBundle) && localise) { // update city to i18n value
      item.setValue(i18nBundle.getString(item.getValue()));
    }
  }

  /**
   * Querybuilder map for getting all hotels.
   *
   * @param path hotels path
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);

    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.HOTELS_RES_TYPE);
    map.put("2_property", JCR_TITLE);
    map.put("2_property.operation", "exists");
    map.put("3_property", HOTEL_CITY);
    map.put("4_property", HOTEL_CHAIN);
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");

    map.put("p.limit", "-1");
    return map;
  }
}
