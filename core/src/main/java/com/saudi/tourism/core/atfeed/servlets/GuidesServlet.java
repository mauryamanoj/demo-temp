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
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Guides Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/guides",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + AIRConstants.CSV})
@Slf4j
public class GuidesServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";

  /** Static category of transportmode feed. */
  public static final String GUIDES = "Guides";

  /** SIMPLE_TAG_LINES to check. */
  public static final String SIMPLE_TAG_LINES = "simpleTagLines";
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
              if (StringUtil.isEmpty(message)) {
                entity.setMessage(StringUtils.EMPTY);
              } else {
                entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
              }
            }
            if (item.hasNode(AIRConstants.IMAGE)) {
              Node imageNode = item.getNode(AIRConstants.IMAGE);
              if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                entity.setThumbnailUrl(setHost(imageNode.getProperty(AIRConstants.FILE_REFERENCE).getString(),
                     getHost(request)));
              }
              if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                entity.setThumbnailS7Url(setHost(imageNode.getProperty(AIRConstants.S7FILE_REFERENCE).getString(),
                     getHost(request)));
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
            entity.setCategoryId(GUIDES);
            if (item.hasProperty(AIRConstants.FEATURED_IMAGE)) {
              entity.setTags(item.getProperty(AIRConstants.FEATURED_IMAGE).getString());
            }
            if (item.hasProperty(SIMPLE_TAG_LINES)) {
              entity.setRegion(item.getProperty(SIMPLE_TAG_LINES).getString());
            }
            entities.add(entity);
          }
        }
      }
      String csv = GeneralUtils.getGuidesFeeds(request, entities, locale);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "GuidesModeFeedServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "GuidesModeFeedServlet",
          e.getLocalizedMessage());
    }
  }


}
