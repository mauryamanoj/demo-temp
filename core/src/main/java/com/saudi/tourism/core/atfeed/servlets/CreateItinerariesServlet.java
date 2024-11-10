package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Session;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.eclipse.jetty.util.StringUtil;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.search.QueryBuilder;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.utils.GeneralUtils;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;

/**
 * The type Get all attractions for a locale.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Create Itineraries Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/createitineraries",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class CreateItinerariesServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";

  /** Integer for Inventory. */
  public static final int ZERO = 0;

  /** THREE split from content path. */
  public static final int THREE = 3;

  /** SEVEN split from content path. */
  public static final int SEVEN = 7;
  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    try {
      String locale = Constants.DEFAULT_LOCALE;
      if (null != request.getRequestParameter(Constants.LOCALE)
           && StringUtils.isNotBlank(request.getRequestParameter(Constants.LOCALE).getString())) {
        locale = request.getRequestParameter(Constants.LOCALE).getString();
      }
      String sourcePath = request.getRequestParameter(Constants.PATH_PROPERTY).getString();
      Session session = request.getResourceResolver().adaptTo(Session.class);
      List<Entity> entities = new ArrayList<Entity>();
      if (session.nodeExists(sourcePath)) {
        Node parentNode = session.getNode(sourcePath);
        if (parentNode.hasNodes()) {
          NodeIterator items = parentNode.getNodes();
          while (items.hasNext()) {
            Node item =  items.nextNode();
            if (!StringUtils.equalsIgnoreCase(item.getName(), JcrConstants.JCR_CONTENT)) {
              NodeIterator cityItems = item.getNodes();
              while (cityItems.hasNext()) {
                Entity entity = new Entity();
                Node nextNode =  cityItems.nextNode();
                if (!StringUtils.equalsIgnoreCase(nextNode.getName(), JcrConstants.JCR_CONTENT)) {
                  Node city = nextNode.getNode(JcrConstants.JCR_CONTENT);
                  String path = city.getPath().replaceAll(AIRConstants.SLASH + JcrConstants.JCR_CONTENT,
                        StringUtils.EMPTY);
                  String cardId = city.getProperty(JcrConstants.JCR_TITLE).getString()
                        .replaceAll(Constants.SPACE, Constants.SPACE).strip();
                  String uniqueId = path.split(AIRConstants.SLASH)[SEVEN];
                  entity.setId(uniqueId);
                  entity.setName(cardId);
                  if (city.hasProperty(JcrConstants.JCR_DESCRIPTION)) {
                    String message = city.getProperty(JcrConstants.JCR_DESCRIPTION).getString()
                        .replaceAll(AIRConstants.NEW_LINE_UNIX, StringUtils.EMPTY);
                    if (StringUtil.isEmpty(message)) {
                      entity.setMessage(StringUtils.EMPTY);
                    } else {
                      entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
                    }
                  } else {
                    String message = city.getProperty(AIRConstants.DESCRIPTION).getString()
                        .replaceAll(AIRConstants.NEW_LINE_UNIX, StringUtils.EMPTY);
                    if (StringUtil.isEmpty(message)) {
                      entity.setMessage(StringUtils.EMPTY);
                    } else {
                      entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
                    }
                  }
                  if (city.hasProperty(AIRConstants.CITY)) {
                    entity.setCategoryId(city.getProperty(AIRConstants.CITY).getString());
                  }
                  if (city.hasNode(AIRConstants.IMAGE) && null != city.getNode(AIRConstants.IMAGE)) {
                    Node imageNode = city.getNode(AIRConstants.IMAGE);
                    if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                      entity.setThumbnailUrl(StringEscapeUtils.escapeCsv(setHost(imageNode
                          .getProperty(AIRConstants.FILE_REFERENCE).getString(), getHost(request))));
                      if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                        entity.setThumbnailS7Url(StringEscapeUtils.escapeCsv(setHost(imageNode
                             .getProperty(AIRConstants.S7FILE_REFERENCE).getString(), getHost(request))));
                      } else {
                        entity.setThumbnailS7Url(StringUtils.EMPTY);
                      }
                    } else {
                      entity.setThumbnailUrl(StringUtils.EMPTY);
                      entity.setThumbnailS7Url(StringUtils.EMPTY);
                    }
                  } else {
                    entity.setThumbnailUrl(StringUtils.EMPTY);
                    entity.setThumbnailS7Url(StringUtils.EMPTY);
                  }
                  entity.setPageUrl(AIRConstants.NULL);
                  entity.setInventory(ZERO);
                  entity.setMargin(AIRConstants.NULL);
                  entity.setInterests(AIRConstants.NULL);
                  entity.setTags(AIRConstants.NULL);
                  entity.setBrand(path);
                  if (city.hasProperty(AIRConstants.CITY)) {
                    entity.setCtaText("[" + AIRConstants.DOUBLE_QUOTES + city.getProperty(AIRConstants.CITY).getString()
                           + AIRConstants.DOUBLE_QUOTES + "]");
                  }
                  entity.setCustom1(ComponentUtils.generateId("pathId", path));
                  entity.setCustom2(path.split(AIRConstants.SLASH)[THREE]);
                  entities.add(entity);
                }
              }
            }
          }
        }
      }
      String csv = GeneralUtils.getCreateItinerariesFeeds(request, entities, locale);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "CreateItinerariesServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("Error in servlet :{}, {}", "CreateItinerariesServlet",
          h.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "CreateItinerariesServlet",
          e.getStackTrace());
    }
  }


}
