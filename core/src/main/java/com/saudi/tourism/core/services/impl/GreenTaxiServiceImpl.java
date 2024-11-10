package com.saudi.tourism.core.services.impl;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.greenTaxi.GreenTaxiCard;
import com.saudi.tourism.core.models.components.greenTaxi.GreenTaxiModel;
import com.saudi.tourism.core.services.GreenTaxiService;
import com.saudi.tourism.core.utils.AdminUtil;
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
import javax.jcr.Session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.day.cq.commons.jcr.JcrConstants.JCR_TITLE;
import static com.saudi.tourism.core.utils.Constants.TYPE_PAGE_CONTENT;

/**
 * The type green taxi service implementation.
 */
@Component(service = GreenTaxiService.class,
    immediate = true)
@Slf4j
public class GreenTaxiServiceImpl implements GreenTaxiService {

  /**
   * The memCache.
   */
  @Reference
  private Cache memCache;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * get data for green taxis.
   *
   * @param request    a {@link SlingHttpServletRequest}
   * @param language locale.
   * @return green taxi model.
   */
  @Override
  public GreenTaxiModel getData(SlingHttpServletRequest request,
                                String language) {

    String cacheKey = Constants.GREEN_TAXI_CACHE_KEY + language;

    List<GreenTaxiCard> cachedGreenTaxiCards = (List<GreenTaxiCard>) memCache.get(cacheKey);
    GreenTaxiModel greenTaxiModel = new GreenTaxiModel();
    AdminPageOption adminOptions =
        AdminUtil.getAdminOptions(language, StringUtils.EMPTY);
    String greenTaxiPath = adminOptions.getGreenTaxiPath();
    if (null != AdminUtil.getAdminSettingsService()) {
      greenTaxiModel.setDownloadApp(AdminUtil.getAdminSettingsService()
          .getGreenTaxiDownloadAppSettings(language));
    }
    if (null != greenTaxiModel.getDownloadApp()) {
      String qrImage = greenTaxiModel.getDownloadApp().getQrcodeImage();
      if (StringUtils.isNotBlank(qrImage) && qrImage.startsWith("/content/dam/")) {
        greenTaxiModel.getDownloadApp().setQrcodeImage(
            request.getServerName() + qrImage);
      }
    }
    if (cachedGreenTaxiCards != null) {
      greenTaxiModel.setCards(cachedGreenTaxiCards);
      return greenTaxiModel;
    }
    if (StringUtils.isBlank(greenTaxiPath)) {
      greenTaxiPath = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
        + Constants.FORWARD_SLASH_CHARACTER + Constants.CONFIGS
        + Constants.FORWARD_SLASH_CHARACTER + "green-taxi";
    }
    Query query = queryBuilder
        .createQuery(PredicateGroup.create(getPredicateQueryMap(greenTaxiPath)),
        request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    Iterator<Node> nodeIterator = searchResult.getNodes();
    List<GreenTaxiCard> cardList = getAllGreenTaxiCardDetails(request.getResourceResolver(), nodeIterator,
        new ArrayList<>(), language);
    if (null != cardList && !cardList.isEmpty()) {
      String serverName = request.getServerName();
      cardList.forEach(card -> {
        String cardImage = card.getImage();
        if (StringUtils.isNotBlank(cardImage) && cardImage.startsWith("/content/dam/")) {
          card.setImage(serverName + cardImage);
        }
      });
      memCache.add(cacheKey, cardList);
    }
    greenTaxiModel.setCards(cardList);
    return greenTaxiModel;
  }

  /**
   * update all green taxi card details list from query builder iterator items.
   *
   * @param resolver     the resolver.
   * @param nodeIterator the node iterator.
   * @param cardsList the cards list.
   * @param language locale.
   * @return the all card details
   */
  private List<GreenTaxiCard> getAllGreenTaxiCardDetails(final ResourceResolver resolver,
                                   final Iterator<Node> nodeIterator, final List<GreenTaxiCard> cardsList,
                                   final String language) {
    while (nodeIterator.hasNext()) {
      try {
        Resource res = resolver.getResource(nodeIterator.next().getPath());
        if (res != null) {
          GreenTaxiCard taxiCard = res.adaptTo(GreenTaxiCard.class);
          if (taxiCard != null) {
            String cityTag = taxiCard.getCity();
            if (StringUtils.isNotBlank(cityTag) && cityTag.contains("/")) { // handle tag names
              TagManager tagManager = resolver.adaptTo(TagManager.class);
              if (null != tagManager) {
                Tag tag = tagManager.resolve(cityTag);
                if (Objects.nonNull(tag)) {
                  taxiCard.setCity(tag.getTitle(new Locale(language)));
                }
              }
            }
            cardsList.add(taxiCard);
          }
        }
      } catch (Exception e) {
        LOGGER.error("Error in forming green taxi cards list", e);
      }
    }
    return cardsList;
  }

  /**
   * Querybuilder map for getting all greenTaxi cards.
   *
   * @param path hotels path
   * @return map predicate query map
   */
  private Map<String, String> getPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("type", TYPE_PAGE_CONTENT);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", Constants.GREEN_TAXI_RES_TYPE);
    map.put("2_property", JCR_TITLE);
    map.put("2_property.operation", "exists");
    // Needed to avoid random order
    map.put("orderby", "@cq:lastModified");
    map.put("orderby.sort", "desc");
    map.put("p.limit", "-1");
    return map;
  }

}
