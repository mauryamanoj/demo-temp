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
import com.saudi.tourism.core.models.components.cards.CardDetail;
import com.saudi.tourism.core.models.components.cards.CardsFilterModel;
import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.services.CardsService;
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
import java.util.Arrays;
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
@Component(service = CardsService.class,
    immediate = true)
@Slf4j
public class CardsServiceImpl implements CardsService {

  /**
   * The constant Type.
   */
  private static final String CARD_CATEGORY = "cardCategory";

  /**
   * The constant City.
   */
  private static final String CARD_CITY = "cardCity";

  /**
   * The constant price range.
   */
  private static final String CARD_PRICE = "priceRange";

  /**
   * used for extracting filters for cards from search result,
   * should match query builder property number.
   */
  private static final String[] FILTER_GRPS =
      {"cardCategory:3", "cardCity:4", "priceRange:5"};

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

  @Override
  public CardsListModel getFilteredCards(SlingHttpServletRequest request,
                                         CardsRequestParams requestParams,
                                         String version) {

    CardsListModel cardsListModel =
        getAllPackages(request, requestParams.getLocale(), requestParams.getType(),
          version);

    return filterPackagesOnParams(cardsListModel, requestParams);
  }

  /**
   * Gets all cards from "/content/sauditourism/{language}/Configs/{category}"
   * and lists them. Also it extracts filters and send to filter.
   *
   * @param request  the request
   * @param language the language
   * @param type the type
   * @param version version
   * @return the all states
   */
  private CardsListModel getAllPackages(SlingHttpServletRequest request, String language,
                                        String type, String version) {
    String cacheKey = type + "-" + version + "-" + language;

    CardsListModel cardsListModel = (CardsListModel) memCache.get(cacheKey);
    if (cardsListModel != null) {
      return cardsListModel;
    }
    cardsListModel = new CardsListModel();
    String categoryPath = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
        + Constants.FORWARD_SLASH_CHARACTER + Constants.CONFIGS
        + Constants.FORWARD_SLASH_CHARACTER + type;
    Query query = queryBuilder
        .createQuery(PredicateGroup.create(getPredicateQueryMap(categoryPath)),
        request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    Iterator<Node> nodeIterator = searchResult.getNodes();
    List<CardDetail> cardsList = new ArrayList<>();
    int count = 0;
    count = getAllCardsDetails(request.getResourceResolver(), nodeIterator, cardsList, count);
    TagManager tagManager = request.getResourceResolver().adaptTo(TagManager.class);
    this.language = language;
    i18nBundle = request.getResourceBundle(new Locale(language));

    cardsListModel.setData(cardsList);
    cardsListModel.setFilters(getFiltersFromResult(searchResult, tagManager));

    Pagination pagination = new Pagination();
    pagination.setTotal(count);
    cardsListModel.setPagination(pagination);
    if (!cardsList.isEmpty()) {
      memCache.add(cacheKey, cardsListModel);
    }
    return cardsListModel;
  }

  /**
   * update all hotels details list from query builder iterator items.
   *
   * @param resolver     the resolver
   * @param nodeIterator the node iterator
   * @param cardsList the cards list
   * @param count        the count
   * @return the all hotel details
   */
  private int getAllCardsDetails(final ResourceResolver resolver,
                                   final Iterator<Node> nodeIterator, final List<CardDetail> cardsList, int count) {
    while (nodeIterator.hasNext()) {
      try {
        Resource res = resolver.getResource(nodeIterator.next().getPath());
        if (res != null) {
          CardDetail cardItem = res.adaptTo(CardDetail.class);
          if (cardItem != null) {
            cardsList.add(cardItem);
            count++;
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in forming states cards list", e);
      }
    }
    return count;
  }

  /**
   * Filter hotels on params.
   *
   * @param listModel             the cards list
   * @param requestParams the requestParams
   * @return filteredPackageList the cards selected filters
   */
  private CardsListModel filterPackagesOnParams(final CardsListModel listModel,
                                                 final CardsRequestParams requestParams) {
    CardsListModel filteredCardsList = new CardsListModel();
    int count = 0;
    int offset = 0;
    int total = 0;

    if (listModel.getData() == null) {
      return filteredCardsList;
    }

    List<CardDetail> cards = listModel.getData();
    List<CardDetail> filteredCards = new ArrayList<>();
    for (CardDetail cardItem : cards) {

      if (matchFilters(cardItem, requestParams)) {
        total++;
        if (offset++ >= requestParams.getOffset()) {
          count++;
          if (requestParams.isAll() || count <= requestParams.getLimit()) { // skip once limit is crossed
            filteredCards.add(cardItem);
          }
        }

      }
    }
    Pagination pagination = new Pagination();
    pagination.setLimit(requestParams.getLimit());
    pagination.setTotal(total);

    pagination.setOffset(requestParams.getOffset());
    filteredCardsList.setData(filteredCards);
    filteredCardsList.setPagination(pagination);
    filteredCardsList.setFilters(getSortedFilterValues(listModel));
    AdminPageOption adminOptions =
        AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
    boolean hideFilters = ((StringUtils.isNotBlank(requestParams.getArticleId())
        && requestParams.getType().equals("whereToEat"))
        && (adminOptions.isDisableArticleRestaurantsFilter()));
    filteredCardsList.setHideFilters(hideFilters);
    return filteredCardsList;
  }

  /**
   * Sort filter values.
   *
   * @param listModel the model
   * @return CardsFilterModel
   */
  private CardsFilterModel getSortedFilterValues(CardsListModel listModel) {
    CardsFilterModel filters = listModel.getFilters();
    Comparator<AppFilterItem> comparator = Comparator.comparing(AppFilterItem::getValue);
    filters.getCategory().sort(comparator);
    return filters;
  }

  /**
   * Match filters boolean.
   *
   * @param item           the card item
   * @param requestParams the card selected filters
   * @return the boolean
   */
  private boolean matchFilters(CardDetail item,
                               CardsRequestParams requestParams) {
    boolean matchType = isMatchItem(requestParams.getCategory(), item.getCardCategories());
    boolean matchCity = isMatchItem(requestParams.getCity(), item.getCardCities());
    boolean matchPriceRange = isMatchSingleItem(requestParams.getPriceRange(), item.getPriceRange());

    List<String> listRequestParamsId = null;
    if (StringUtils.isNotBlank(requestParams.getId())) {
      listRequestParamsId = Arrays.asList(requestParams.getId().split(Constants.COMMA));
    }

    boolean matchId = isMatchSingleItem(listRequestParamsId, item.getId());

    List<String> articleList = new ArrayList<>();
    if (null != requestParams.getArticleId()) {
      articleList.add(requestParams.getArticleId());
    }
    boolean matchArticle = isMatchItem(articleList, item.getArticleIds());
    return matchType && matchCity && matchPriceRange && matchArticle && matchId;
  }

  /**
   *
   * @param reqParams request params.
   * @param itemParams item params.
   * @return is match.
   */
  private boolean isMatchItem(List<String> reqParams, List<String> itemParams) {
    boolean isMatch = false;
    if ((Objects.nonNull(reqParams)) && (reqParams.size() > 0)) {
      if ((Objects
          .nonNull(itemParams)) && (itemParams.size() > 0)) {
        for (String param : itemParams) {
          if (reqParams.contains(param)) {
            isMatch = true;
            break;
          }
        }
      }
    } else {
      isMatch = true;
    }
    return isMatch;
  }

  /**
   *
   * @param reqParams request params.
   * @param itemParam item param.
   * @return is match.
   */
  private boolean isMatchSingleItem(List<String> reqParams, String itemParam) {
    boolean isMatch = false;
    if (Objects.nonNull(reqParams) && Objects
        .nonNull(itemParam)) {
      if (reqParams.contains(itemParam)) {
        isMatch = true;
      }
    } else {
      isMatch = true;
    }
    return isMatch;
  }
  /**
   * Gets filters from search result.
   *
   * @param result     the result
   * @param tagManager the tagManager
   * @return the string
   */
  private CardsFilterModel getFiltersFromResult(SearchResult result,
                                                 final TagManager tagManager) {
    CardsFilterModel filters = new CardsFilterModel();
    try {
      Map<String, Facet> facets = result.getFacets();
      for (String grp : FILTER_GRPS) {
        // 0th element is filtername and 1st element is queryparam number
        String[] filterSplit = grp.split(Constants.COLON_SLASH_CHARACTER);
        String key = filterSplit[1] + "_property";
        if (CARD_CATEGORY.equals(filterSplit[0])) {
          filters.setCategory(getFilteritems(facets, key, tagManager, true, true));
        }
        if (CARD_CITY.equals(filterSplit[0])) {
          filters.setCity(getFilteritems(facets, key, tagManager, true, true));
        }
        if (CARD_PRICE.equals(filterSplit[0])) {
          filters.setPriceRange(getFilteritems(facets, key, tagManager, false, false));
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
   * @param isTranslateFilter the isTranslateFilter
   * @return the filteritems
   */
  private List<AppFilterItem> getFilteritems(Map<String, Facet> facets, String key,
                                             final TagManager tagManager, final boolean localise,
                                             final boolean isTranslateFilter) {
    List<AppFilterItem> items = new ArrayList<>();
    if (facets != null && facets.containsKey(key)) {
      Facet fc = facets.get(key);

      for (Bucket bucket : fc.getBuckets()) {
        String bc = bucket.getValue();
        AppFilterItem item = new AppFilterItem(bc, bc);
        if (isTranslateFilter) {
          translateAppFilterItem(item, tagManager, localise);
        }
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
    map.put("property.value", Constants.CARDS_RES_TYPE);
    map.put("2_property", JCR_TITLE);
    map.put("2_property.operation", "exists");
    map.put("3_property", CARD_CATEGORY);
    map.put("4_property", CARD_CITY);
    map.put("5_property", CARD_PRICE);
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");

    map.put("p.limit", "-1");
    return map;
  }
}
