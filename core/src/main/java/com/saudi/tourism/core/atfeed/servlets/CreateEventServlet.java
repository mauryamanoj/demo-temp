package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

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
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Create Event Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/createevent",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class CreateEventServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";
  /** Property name of tags. */
  public static final String EVENT = "event";

  /** Property name of image of destination. */
  public static final String FEATURE_IMAGE = "featureImage";

  /** Integer for Inventory. */
  public static final int ZERO = 0;

  /** S7_FEATURE_EVENT_IMAGE to check. */
  public static final String S7_FEATURE_EVENT_IMAGE = "s7featureEventImage";
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
      String eventQuery = "SELECT * FROM [cq:PageContent] AS node WHERE ISDESCENDANTNODE(node,"
             + AIRConstants.DOUBLE_QUOTES + sourcePath + "\") and  node.[copy] IS NOT NULL AND node.[copy] <> \"\"";
//      Session session = request.getResourceResolver().adaptTo(Session.class);
      List<Entity> entities = new ArrayList<Entity>();
      @NotNull Iterator<Resource> resources = request.getResourceResolver().findResources(eventQuery,
         AIRConstants.JCR_SQL);
      while (resources.hasNext()) {
        Entity entity = new Entity();
        Node nextNode =  resources.next().adaptTo(Node.class);
        if (nextNode.hasProperty(AIRConstants.TITLE) && !StringUtils.contains(nextNode
             .getProperty(AIRConstants.TITLE).getString(), "Test")) {
          String uniqueId = StringEscapeUtils.escapeCsv(nextNode.getProperty(AIRConstants.TITLE)
                .getString().stripLeading());
          entity.setId(uniqueId.toLowerCase().replaceAll(Constants.SPACE, AIRConstants.DASH));
          entity.setName(nextNode.getProperty(AIRConstants.TITLE).getString().stripLeading());
          if (nextNode.hasProperty(AIRConstants.COPY)) {
            String message = nextNode.getProperty(AIRConstants.COPY).getString()
                .replaceAll(AIRConstants.NEW_LINE_WINDOWS, StringUtils.EMPTY);
            if (StringUtil.isEmpty(message)) {
              entity.setMessage(StringUtils.EMPTY);
            } else {
              entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
            }
          }
          entity.setCategoryId(EVENT);
          if (nextNode.hasProperty(Constants.PN_FEATURE_EVENT_IMAGE)) {
            entity.setThumbnailUrl(StringEscapeUtils.escapeCsv(setHost(nextNode
                .getProperty(Constants.PN_FEATURE_EVENT_IMAGE).getString(), getHost(request))));
            entity.setThumbnailS7Url(StringEscapeUtils.escapeCsv(setHost(nextNode
                .getProperty(S7_FEATURE_EVENT_IMAGE).getString(), getHost(request))));
          } else {
            entity.setThumbnailUrl(StringUtils.EMPTY);
            entity.setThumbnailS7Url(StringUtils.EMPTY);
          }
          entity.setPageUrl(AIRConstants.NULL);
          entity.setInventory(ZERO);
          entity.setMargin(AIRConstants.NULL);
          entity.setInterests(AIRConstants.NULL);
          entity.setTags(AIRConstants.NULL);
          entity.setBrand(nextNode.getPath().replaceAll(AIRConstants.SLASH + JcrConstants.JCR_CONTENT,
              StringUtils.EMPTY));
          if (nextNode.hasProperty(AIRConstants.CITY)) {
            entity.setCtaText("[" + AIRConstants.DOUBLE_QUOTES + nextNode.getProperty(AIRConstants.CITY).getString()
                + AIRConstants.DOUBLE_QUOTES + "]");
          }
          entities.add(entity);
        }
      }
      String csv = GeneralUtils.getCreateEventFeeds(request, entities, locale);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "CreateEventServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "CreateEventServlet",
          e.getStackTrace());
    }
  }


}
