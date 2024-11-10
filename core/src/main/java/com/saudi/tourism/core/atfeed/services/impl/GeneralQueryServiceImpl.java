package com.saudi.tourism.core.atfeed.services.impl;

import static com.saudi.tourism.core.utils.PrimConstants.RT_APP_CITY_PAGE;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.jcr.resource.api.JcrResourceConstants;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.atfeed.servlets.AbstractATFeedServlet;
import com.saudi.tourism.core.atfeed.utils.GeneralUtils;
import com.saudi.tourism.core.models.app.location.AppCity;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for requesting activities.
 */
@Component(service = GeneralQueryService.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL
                        + GeneralQueryServiceImpl.GENERAL_QUERY_SERVICE})
@Slf4j
public class GeneralQueryServiceImpl implements GeneralQueryService {
  /**
   * This GENERAL_QUERY_SERVICE description for OSGi.
   */
  static final String GENERAL_QUERY_SERVICE = "General Query Service";

  /**Discover regions category. */
  private static final String ATTRACTIONS = "attractions";
  /** EN_SEE_DO AS CONSTANT. */
  public static final String EN_SEE_DO = "/en/see-do/";
  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";
  /** Static category of destination feed. */
  public static final String ITINERARIES = "itineraries";
  /** SIMPLE_TAG_LINES to check. */
  public static final String SIMPLE_TAG_LINES = "simpleTagLines";

  /** Static category of PACKAGES feed. */
  public static final String PACKAGES = "packages";

  /** THREE split from content path. */
  public static final int THREE = 3;
  /** Category of partners promotions deals. */
  public static final String PARTNER_PROMOTIONS_CATEGORY  = "partner_2";

  /** Static category of TRANSPORT_MODE feed. */
  public static final String TRANSPORT_MODE = "transportmode";

  /** CARDS to check. */
  public static final String CARDS = "cards";
  /** SUFFIXTITLE to add to the id. */
  public static final String SUFFIXTITLE = "_tabcategory";

  /** ITEM0 to check. */
  public static final String ITEM0 = "item0";
  /** PANEL_TITLE to check. */
  public static final String PANEL_TITLE = "cq:panelTitle";
  /**
   * The service for obtaining resource resolver.
   */
  @Reference
  private UserService resolverService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
  *
  * Search  regions in content folders.
  *
  * @param request             SlingHttpRequest
  * @param trendingPath Path of destinations page
  * @return Result
  */
  @Override
 public SearchResult findRegionModel(SlingHttpServletRequest request, String trendingPath) {
    Query query = queryBuilder.createQuery(PredicateGroup.create(getPredicateQueryMap(trendingPath)),
        request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    return searchResult;
  }

 /**
  * Querybuilder map for getting all regions.
  *
  * @param path trending
  * @return map predicate query map
  */
  public Map getPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("property", "jcr:primaryType");
    map.put("property.value", "cq:Page");
    map.put("p.limit", "-1");
    return map;
  }


  /**
   * Returns the cities information.
   * @param resolver the resource resolver.
   * @param path path of the city.
   * @return tag returns cities.
   */
  @Override
   public Map<String, AppCity> getCities(ResourceResolver resolver, String path) {

    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page root = pageManager.getPage(path);
    if (Objects.isNull(root)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    Map<String, AppCity> cities = new HashMap<>();
    Set<String> idSet = new HashSet<>();
    for (@NotNull Iterator<Page> it = root.listChildren(); it.hasNext();) {
      Page page = it.next();
      if (page.getContentResource().isResourceType(RT_APP_CITY_PAGE)) {
        AppCity appCity = page.getContentResource().adaptTo(AppCity.class);
        if (page.getContentResource().getValueMap().containsKey(Constants.PN_ID)) {
          appCity.setId(page.getContentResource().getValueMap().get(Constants.PN_ID, String.class));
        }
        if (Objects.nonNull(appCity) && !idSet.contains(appCity.getId())) {
          cities.put(appCity.getId(), appCity);
          idSet.add(appCity.getId());
        }
      }
    }
    return cities;
  }

/**
 *  Locale from Request.
 * @param request SlingHttpServletRequest
 * @return Return locale
 */
  @Override
  public String getLocale(SlingHttpServletRequest request) {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)) {
      locale = request.getRequestParameter(Constants.LOCALE).toString();
    }
    return locale;
  }
 /**
  * Attraction Feed.
  * @param request SlingHttpServletRequest
  * @param response SlingHttpServletResponse
  * @return Return csv
  */
  public String getAttractionFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException  {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)
        && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
      locale = request.getRequestParameter(Constants.LOCALE).getString();
    }
    String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
    Session session = request.getResourceResolver().adaptTo(Session.class);
    List<Entity> entities = new ArrayList<Entity>();
    try {
      if (session.nodeExists(sourcePath)) {
        Node parentNode = session.getNode(sourcePath);
        if (parentNode.hasNodes()) {
          NodeIterator items = parentNode.getNodes();
          while (items.hasNext()) {
            Entity entity = new Entity();
            Node item = items.nextNode();
            if (item.hasNode(AIRConstants.LINK)) {
              Node linkNode = item.getNode(AIRConstants.LINK);
              if (linkNode.hasProperty(AIRConstants.URL)) {
                String[] authoredUrl = linkNode.getProperty(AIRConstants.URL).getString().split(AIRConstants.SLASH);
                entity.setId(authoredUrl[authoredUrl.length - 2] + AIRConstants.DASH
                    + authoredUrl[authoredUrl.length - 1]);
                entity.setPageUrl(linkNode.getProperty(AIRConstants.URL).getString()
                      .replace(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
              }
            }
            if (item.hasProperty(AIRConstants.TITLE)) {
              entity.setName(item.getProperty(AIRConstants.TITLE).getString());
            }
            if (item.hasProperty(AIRConstants.DESCRIPTION)) {
              String message = item.getProperty(AIRConstants.DESCRIPTION).getString();
              entity.setMessage(message.strip());
            }
            if (item.hasNode(AIRConstants.IMAGE)) {
              Node imageNode = item.getNode(AIRConstants.IMAGE);
              entity.setThumbnailUrl(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants.FILE_REFERENCE)
                  .getString(), AbstractATFeedServlet.getHost(request)));
              if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode
                    .getProperty(AIRConstants.S7FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
              } else {
                entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode
                    .getProperty(AIRConstants.FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
              }
            }
            if (item.hasProperty(AIRConstants.CTA_TYPE)) {
              entity.setValue(item.getProperty(AIRConstants.CTA_TYPE).getLength());
            }
            if (item.hasProperty(AIRConstants.ICON)) {
              entity.setInventory(item.getProperty(AIRConstants.ICON).getType());
            }
            if (item.hasProperty(AIRConstants.CITY_TYPE)) {
              entity.setMargin(item.getProperty(AIRConstants.CITY_TYPE).getString());
            }
            entity.setCategoryId(ATTRACTIONS);
            if (item.hasProperty(AIRConstants.FEATURED_IMAGE)) {
              entity.setTags(item.getProperty(AIRConstants.FEATURED_IMAGE).getString());
            }
            if (item.hasProperty(AIRConstants.LEBELS)) {
              entity.setRegion(item.getProperty(AIRConstants.LEBELS).getString());
            }
            entities.add(entity);
          }
        }
      }
    } catch (PathNotFoundException e) {
      e.printStackTrace();
    } catch (ValueFormatException e) {
      e.printStackTrace();
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    String csv = GeneralUtils.getAttractionFeedsCSV(request, entities, locale);
    return csv;
  }
 /**
  * Itineraries feed.
  * @param request SlingHttpServletRequest
  * @param response SlingHttpServletResponse
  * @return Return csv
  */
  public String getItinerariesFeed(SlingHttpServletRequest request, SlingHttpServletResponse response) {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)
        && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
      locale = request.getRequestParameter(Constants.LOCALE).getString();
    }
    String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
    Session session = request.getResourceResolver().adaptTo(Session.class);
    List<Entity> entities = new ArrayList<Entity>();
    try {
      if (session.nodeExists(sourcePath)) {
        Node parentNode = session.getNode(sourcePath);
        if (parentNode.hasNodes()) {
          NodeIterator items = parentNode.getNodes();
          while (items.hasNext()) {
            Entity entity = new Entity();
            Node item = items.nextNode();
            if (item.hasNode(AIRConstants.LINK)) {
              Node linkNode = item.getNode(AIRConstants.LINK);
              if (linkNode.hasProperty(AIRConstants.URL)) {
                String authoredUrl = linkNode.getProperty(AIRConstants.URL).getString();
                authoredUrl = authoredUrl.replace(AIRConstants.CONTENT_SAUDITOURISM + EN_SEE_DO, StringUtils.EMPTY);
                authoredUrl = authoredUrl.replace(AIRConstants.SLASH, AIRConstants.DASH);
                if (authoredUrl.contains(AIRConstants.DESTINATIONS)) {
                  entity.setId(authoredUrl.replaceAll(AIRConstants.DESTINATIONS_DASH, StringUtils.EMPTY));
                } else {
                  entity.setId(authoredUrl);
                }
                entity.setPageUrl(linkNode.getProperty(AIRConstants.URL).getString()
                     .replace(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
              }
            }
            if (item.hasProperty(AIRConstants.TITLE)) {
              entity.setName(item.getProperty(AIRConstants.TITLE).getString());
            }
            if (item.hasProperty(AIRConstants.DESCRIPTION)) {
              String summary = item.getProperty(AIRConstants.DESCRIPTION).getString();
              if (StringUtil.isEmpty(summary)) {
                entity.setMessage(StringUtils.EMPTY);
              } else {
                entity.setMessage(StringEscapeUtils.escapeCsv(summary.strip()));
              }
            }
            if (item.hasNode(AIRConstants.IMAGE)) {
              Node imageNode = item.getNode(AIRConstants.IMAGE);
              if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                entity.setThumbnailUrl(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants.FILE_REFERENCE)
                    .getString(), AbstractATFeedServlet.getHost(request)));
              }
              if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                    .S7FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
              }
            }
            if (item.hasProperty(AIRConstants.CTA_TYPE)) {
              entity.setValue(item.getProperty(AIRConstants.CTA_TYPE).getLength());
            }
            if (item.hasProperty(AIRConstants.ICON)) {
              entity.setInventory(item.getProperty(AIRConstants.ICON).getType());
            }
            if (item.hasProperty(AIRConstants.CITY_TYPE)) {
              entity.setMargin(item.getProperty(AIRConstants.CITY_TYPE).getString());
            }
            entity.setCategoryId(ITINERARIES);
            if (item.hasProperty(AIRConstants.LEBELS)) {
              entity.setRegion(AIRConstants.LEFT_SQUARE_BRACKET + AIRConstants.DOUBLE_QUOTES
                  + item.getProperty(AIRConstants.LEBELS).getString() + AIRConstants.DOUBLE_QUOTES
                   + AIRConstants.RIGHT_SQUARE_BRACKET);
            }
            if (item.hasProperty(SIMPLE_TAG_LINES)) {
              entity.setTags(item.getProperty(SIMPLE_TAG_LINES).getString());
            }
            entities.add(entity);
          }
        }
      }
    } catch (PathNotFoundException e) {
      e.printStackTrace();
    } catch (ValueFormatException e) {
      e.printStackTrace();
    } catch (RepositoryException e) {
      e.printStackTrace();
    }
    String csv = GeneralUtils.getItinerariesFeeds(request, entities, locale);
    return csv;
  }
  /**
   * Package feed.
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse
   * @return Return csv
   */
  public String getPackageCSVFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)
        && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
      locale = request.getRequestParameter(Constants.LOCALE).getString();
    }
    String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
    String csv = StringUtils.EMPTY;
    if (sourcePath.contains(AIRConstants.DEFAULT_SEPARATOR)) {
      String[] languagePath = sourcePath.split(AIRConstants.DEFAULT_SEPARATOR);
      List<Entity> entities = new ArrayList<Entity>();
      for (String datalang : languagePath) {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        try {
          if (session.nodeExists(datalang)) {
            Node parentNode = session.getNode(datalang);
            if (parentNode.hasNodes()) {
              NodeIterator items = parentNode.getNodes();
              while (items.hasNext()) {
                Entity entity = new Entity();
                Node item = items.nextNode();
                if (item.hasNode(AIRConstants.LINK)) {
                  Node linkNode = item.getNode(AIRConstants.LINK);
                  if (linkNode.hasProperty(AIRConstants.URL)) {
                    entity.setPageUrl(linkNode.getProperty(AIRConstants.URL).getString());
                  }
                  if (linkNode.hasProperty(AIRConstants.COPY)) {
                    entity.setInventory(linkNode.getProperty(AIRConstants.COPY).getType());
                  }
                }
                if (item.hasProperty(AIRConstants.TITLE)) {
                  entity.setId(ComponentUtils.generateId("card-items", item.getProperty(AIRConstants.TITLE)
                      .getString()));
                  entity.setName(item.getProperty(AIRConstants.TITLE).getString());
                }
                if (item.hasProperty(AIRConstants.DESCRIPTION)) {
                  String message = item.getProperty(AIRConstants.DESCRIPTION).getString()
                      .replaceAll(AIRConstants.WHITE_SPACE, StringUtils.EMPTY);
                  if (StringUtil.isEmpty(message)) {
                    entity.setMessage(StringUtils.EMPTY);
                  } else {
                    entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
                  }
                }
                if (item.hasNode(AIRConstants.IMAGE)) {
                  Node imageNode = item.getNode(AIRConstants.IMAGE);
                  if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                    entity.setThumbnailUrl(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                        .FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
                    entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                        .FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
                  }
                }
                if (item.hasProperty(AIRConstants.CTA_TYPE)) {
                  entity.setValue(item.getProperty(AIRConstants.CTA_TYPE).getLength());
                }
                if (item.hasProperty(AIRConstants.CITY_TYPE)) {
                  entity.setMargin(item.getProperty(AIRConstants.CITY_TYPE).getString());
                }
                entity.setCategoryId(PACKAGES);
                if (item.hasProperty(AIRConstants.FEATURED_IMAGE)) {
                  entity.setRegion(item.getProperty(AIRConstants.FEATURED_IMAGE).getString());
                }
                String path = item.getPath().replaceAll(AIRConstants.SLASH + JcrConstants.JCR_CONTENT,
                      StringUtils.EMPTY);
                entity.setCustom1(path.split(AIRConstants.SLASH)[THREE]);
                entities.add(entity);
              }
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      csv = GeneralUtils.getPackageFeeds(request, entities, locale);
    }
    return csv;
  }

  /**
   * Package feed.
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse
   * @return Return csv
   */
  public String getPartnerPromotionFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)
         && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
      locale = request.getRequestParameter(Constants.LOCALE).getString();
    }
    String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
    String csv = StringUtils.EMPTY;
    if (sourcePath.contains(AIRConstants.DEFAULT_SEPARATOR)) {
      String[] languagePath = sourcePath.split(AIRConstants.DEFAULT_SEPARATOR);
      List<Entity> entities = new ArrayList<Entity>();
      for (String datalang : languagePath) {
        Session session = request.getResourceResolver().adaptTo(Session.class);
        try {
          if (session.nodeExists(datalang)) {
            Node parentNode = session.getNode(datalang);
            NodeIterator items = parentNode.getNodes();
            while (items.hasNext()) {
              Entity entity = new Entity();
              Node item = items.nextNode();
              if (item.hasNode(AIRConstants.LINK)) {
                Node linkNode = item.getNode(AIRConstants.LINK);
                if (linkNode.hasProperty(AIRConstants.URL)) {
                  entity.setPageUrl(linkNode.getProperty(AIRConstants.URL).getString()
                       .replace(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
                }
                if (linkNode.hasProperty(AIRConstants.COPY)) {
                  entity.setCustom1(linkNode.getProperty(AIRConstants.COPY).getString());
                }
              }
              if (item.hasNode(AIRConstants.IMAGE)) {
                Node imageNode = item.getNode(AIRConstants.IMAGE);
                if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                  entity.setThumbnailUrl(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                      .FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
                }
                if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                  entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                      .S7FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
                }
              }
              if (item.hasProperty(AIRConstants.TITLE)) {
                entity.setId(ComponentUtils.generateId("card-items", item.getProperty(AIRConstants.TITLE)
                     .getString()));
                entity.setName(item.getProperty(AIRConstants.TITLE).getString());
              }
              if (item.hasProperty(AIRConstants.DESCRIPTION)) {
                String message = item.getProperty(AIRConstants.DESCRIPTION).getString();
                if (StringUtil.isEmpty(message)) {
                  entity.setMessage(StringUtils.EMPTY);
                } else {
                  entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
                }
              }
              if (item.hasProperty(AIRConstants.CTA_TYPE)) {
                entity.setValue(item.getProperty(AIRConstants.CTA_TYPE).getLength());
              }
              if (item.hasProperty(AIRConstants.ICON)) {
                entity.setInventory(item.getProperty(AIRConstants.ICON).getType());
              }
              if (item.hasProperty(AIRConstants.CITY_TYPE)) {
                entity.setMargin(item.getProperty(AIRConstants.CITY_TYPE).getString());
              }
              entity.setCategoryId(PARTNER_PROMOTIONS_CATEGORY);
              entity.setTags(StringUtils.EMPTY);
              String langCode = item.getPath().toString();
              entity.setCustom2(langCode.split(AIRConstants.SLASH)[THREE]);
              entities.add(entity);
            }
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      csv = GeneralUtils.getPartnerPromotionFeeds(request, entities, locale);
    }
    return csv;
  }

  /**
   * Transport feed.
   * @param request SlingHttpServletRequest
   * @param response SlingHttpServletResponse
   * @return Return csv
   */
  public String getTransportModeFeed(SlingHttpServletRequest request, SlingHttpServletResponse response)
       throws ServletException, IOException {
    String locale = Constants.DEFAULT_LOCALE;
    if (null != request.getRequestParameter(Constants.LOCALE)
         && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
      locale = request.getRequestParameter(Constants.LOCALE).getString();
    }
    String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
    Session session = request.getResourceResolver().adaptTo(Session.class);
    List<Entity> entities = new ArrayList<Entity>();
    try {
      if (session.nodeExists(sourcePath)) {
        Node parentNode = session.getNode(sourcePath);
        if (parentNode.hasNodes()) {
          NodeIterator items = parentNode.getNodes();
          while (items.hasNext()) {
            Entity entity = new Entity();
            Node item = items.nextNode();
            if (item.hasProperty(PANEL_TITLE)) {
              Node cardItemNode = item.getNode(CARDS).getNode(ITEM0);
              if (cardItemNode.hasProperty(AIRConstants.TITLE)) {
                String cardName = cardItemNode.getProperty(AIRConstants.TITLE).getString()
                        .replace(Constants.SPACE, AIRConstants.UNDERSCORE);
                entity.setId(cardName.toLowerCase().replace(AIRConstants.DASH, AIRConstants.UNDERSCORE));
                entity.setName(cardItemNode.getProperty(AIRConstants.TITLE).getString());
              }
              if (cardItemNode.hasProperty(AIRConstants.DESCRIPTION)) {
                String message = cardItemNode.getProperty(AIRConstants.DESCRIPTION).getString()
                    .replaceAll(AIRConstants.NEW_LINE_UNIX, StringUtils.EMPTY);
                entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
              }
              if (cardItemNode.hasProperty(AIRConstants.CTA_TYPE)) {
                entity.setValue(cardItemNode.getProperty(AIRConstants.CTA_TYPE).getLength());
              }
              entity.setPageUrl(AIRConstants.NULL);
              if (cardItemNode.hasProperty(AIRConstants.ICON)) {
                entity.setInventory(cardItemNode.getProperty(AIRConstants.ICON).getType());
              }
              if (cardItemNode.hasProperty(AIRConstants.CITY_TYPE)) {
                entity.setMargin(cardItemNode.getProperty(AIRConstants.CITY_TYPE).getString());
              }
              if (cardItemNode.hasNode(AIRConstants.IMAGE)) {
                Node imageNode = cardItemNode.getNode(AIRConstants.IMAGE);
                if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                  entity.setThumbnailS7Url(AbstractATFeedServlet.setHost(imageNode.getProperty(AIRConstants
                      .S7FILE_REFERENCE).getString(), AbstractATFeedServlet.getHost(request)));
                }
              }
              entity.setCategoryId(TRANSPORT_MODE);
              entity.setInterests(AIRConstants.NULL);
              entity.setTags(AIRConstants.NULL);
              entities.add(entity);
            } else {
              continue;
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    String csv = GeneralUtils.getTransportFeeds(request, entities, locale);
    return csv;
  }

  /**
  *
  * Search  regions in content folders.
  *
  * @param request             SlingHttpRequest
  * @param discoverRegionsPath Path of destinations page
  * @return Result
  */
  public SearchResult findDiscover(@NotNull SlingHttpServletRequest request, String discoverRegionsPath) {
    Query query = queryBuilder.createQuery(PredicateGroup.create(getDiscoverPredicateQueryMap(discoverRegionsPath)),
        request.getResourceResolver().adaptTo(Session.class));
    SearchResult searchResult = query.getResult();
    return searchResult;
  }

  /**
   * Querybuilder map for getting all regions.
   *
   * @param path discover regions
   * @return map predicate query map
   */
  public Map getDiscoverPredicateQueryMap(String path) {
    Map<String, String> map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, path);
    map.put("property", JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY);
    map.put("property.value", AIRConstants.BIG_SLIDER);
    map.put("p.limit", "-1");
    return map;
  }
}
