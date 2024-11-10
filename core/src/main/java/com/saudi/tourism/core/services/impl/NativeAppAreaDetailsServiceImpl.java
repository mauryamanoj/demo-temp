package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.models.components.AreaDetailsModel;
import com.saudi.tourism.core.models.components.TabDetailsModel;
import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import com.saudi.tourism.core.models.components.hotels.HotelsRequestParams;
import com.saudi.tourism.core.services.CardsService;
import com.saudi.tourism.core.services.HotelService;
import com.saudi.tourism.core.services.NativeAppAreaDetailsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.saudi.tourism.core.utils.Constants.CITIES_PATH_FORMAT;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * Area Details implementation.
 */
@Component(service = NativeAppAreaDetailsService.class,
    immediate = true)
@Slf4j
public class NativeAppAreaDetailsServiceImpl implements NativeAppAreaDetailsService {


  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * The cards service.
   */
  @Reference
  private transient CardsService cardsService;

  /**
   * The hotels Service.
   */
  @Reference
  private transient HotelService hotelsService;

  @Override
  public AreaDetailsModel getAreaDetails(SlingHttpServletRequest request) throws RepositoryException {
    String area = request.getRequestParameter("area").toString();
    String locale = CommonUtils.getLocale(request);
    String path = String.format(CITIES_PATH_FORMAT, locale);
    Query query = queryBuilder
        .createQuery(PredicateGroup.create(getPredicateQueryMap(path, area)),
        request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    Iterator<Node> nodeIterator = searchResult.getNodes();
    if (nodeIterator.hasNext()) {
      Resource areaResource = request.getResourceResolver().getResource(nodeIterator.next().getPath());
      AreaDetailsModel areaDetailList = null;
      if (null != areaResource) {
        areaDetailList = areaResource.adaptTo(AreaDetailsModel.class);
      } else {
        return null;
      }
      if (null != areaDetailList && null != areaDetailList.getAreaDetails()) {
        if (!areaDetailList.getAreaDetails().isEmpty()) {
          areaDetailList.getAreaDetails().forEach(detail -> {
            if (null != detail.getType()) {
              final String type = detail.getType();
              if (type.equals("accommodations")) {
                CardsRequestParams articleQueryParams = getArticleCardsRequestParams(area, locale, type);
                setHotelsData(request, detail, articleQueryParams, area, locale);
              } else if ((type.equals("restaurants")) || type.equals("top-attractions") || type.equals("shopping")) {
                CardsRequestParams articleQueryParams = getArticleCardsRequestParams(area, locale, type);
                setData(request, detail, articleQueryParams, area, locale, type);
              } else if (type.equals("articles")) {
                if (StringUtils.isNotBlank(detail.getArticlePath())) {
                  // We will try to guess the article type from string after the last '/'
                  // If the article path is '/content/sauditourism/en/Configs/articles/jeddah/shopping'
                  // In this case the article type would be 'shopping'
                  final String articleType = StringUtils.substringAfterLast(detail.getArticlePath(), "/");
                  CardsRequestParams queryParam = getArticleCardsRequestParams(area, locale, articleType);
                  setData(request, detail, queryParam, null, null, null);
                }
              }
            }
          });
        }
      }
      return areaDetailList;
    }
    return null;
  }

  /**
   * Method sets the data for the article details.
   *
   * @param request request.
   * @param detail detail.
   * @param articleQueryParams queryparams for article details.
   * @param area area.
   * @param locale locale.
   * @param type type.
   */
  private void setData(SlingHttpServletRequest request, TabDetailsModel detail, CardsRequestParams articleQueryParams,
                       String area, String locale, String type) {
    try {
      if (null != articleQueryParams) {
        CardsListModel details = cardsService.getFilteredCards(request, articleQueryParams, Constants.VERSION_V2);
        detail.setData(details.getData());
      }
    } catch (RepositoryException e) {
      LOGGER.error("Error in forming states cards list", e);
    }
  }
  /**
   * Method sets the data for the article details.
   *
   * @param request request.
   * @param detail detail.
   * @param articleQueryParams queryparams for article details.
   * @param area area.
   * @param locale locale.
   */
  private void setHotelsData(SlingHttpServletRequest request, TabDetailsModel detail,
                             CardsRequestParams articleQueryParams,
                             String area, String locale) {
    try {
      if (null != articleQueryParams) {
        CardsListModel details = cardsService.getFilteredCards(request, articleQueryParams, Constants.VERSION_V2);
        detail.setData(details.getData());
      }
    } catch (RepositoryException e) {
      LOGGER.error("Error in forming states cards list", e);
    }
  }


  /**
   * Returns the cards query param obj.
   *
   * @param area area.
   * @param locale locale.
   * @param category category.
   * @return cards request params.
   */
  @NotNull
  private CardsRequestParams getArticleCardsRequestParams(String area, String locale,
                                                          String category) {
    CardsRequestParams queryParam = new CardsRequestParams();
    queryParam.setType("articles");
    List<String> categoryList = new ArrayList<>();
    categoryList.add(category);
    queryParam.setCategory(categoryList);
    List<String> city = new ArrayList<>();
    city.add(area);
    queryParam.setCity(city);
    queryParam.setLocale(locale);
    queryParam.setLimit(Integer.MAX_VALUE);
    return queryParam;
  }

  /**
   * Returns the cards query param obj.
   *
   * @param area area.
   * @param locale locale.
   * @param type type.
   * @param articleId articleId.
   * @return cards request params.
   */
  @NotNull
  private CardsRequestParams getCardsRequestParams(String area, String locale,
                                                   String type,
                                                   String articleId) {
    CardsRequestParams queryParam = new CardsRequestParams();
    if (StringUtils.isNotBlank(type) && type.equals("restaurants")) {
      queryParam.setType("whereToEat");
    } else {
      queryParam.setType(type);
    }
    List<String> city = new ArrayList<>();
    city.add(area);
    queryParam.setCity(city);
    queryParam.setLocale(locale);
    queryParam.setArticleId(articleId);
    queryParam.setLimit(Integer.MAX_VALUE);
    return queryParam;
  }

  /**
   * Returns the hotels query param obj.
   *
   * @param area area.
   * @param locale locale.
   * @param articleId articleId.
   * @return hotels request params.
   */
  @NotNull
  private HotelsRequestParams getHotelsRequestParams(String area, String locale,
                                                     String articleId) {
    HotelsRequestParams queryParam = new HotelsRequestParams();
    List<String> city = new ArrayList<>();
    city.add(area);
    queryParam.setArea(city);
    queryParam.setLocale(locale);
    queryParam.setArticleId(articleId);
    queryParam.setLimit(Integer.MAX_VALUE);
    return queryParam;
  }


  /**
   * Querybuilder map for getting all hotels.
   *
   * @param path area path
   * @param area area value
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path, String area) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);

    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.RT_APP_CITY_PAGE);
    map.put("2_property", "city");
    map.put("2_property.value", area);
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");
    map.put("p.limit", "1");
    return map;
  }
}
