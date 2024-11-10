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
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "CarRental Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/carrentals",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class CarRentalServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String PAGE_CATEGORIES = "pageCategory";

  /** THREE split from content path. */
  public static final int THREE = 3;

  /** Category of car deals. */
  public static final String CAR_RENTALS_CATEGORY = "transportationDeals";
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
      if (sourcePath.contains(AIRConstants.DEFAULT_SEPARATOR)) {
        String[] languagePath = sourcePath.split(AIRConstants.DEFAULT_SEPARATOR);
        List<Entity> entities = new ArrayList<Entity>();
        for (String datalang : languagePath) {
          Session session = request.getResourceResolver().adaptTo(Session.class);
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
                    entity.setPageUrl(linkNode.getProperty(AIRConstants.URL).getString()
                          .replace(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY));
                  }
                  if (linkNode.hasProperty(AIRConstants.COPY)) {
                    entity.setCustom1(linkNode.getProperty(AIRConstants.COPY).getString());
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
                if (item.hasNode(AIRConstants.IMAGE)) {
                  Node imageNode = item.getNode(AIRConstants.IMAGE);
                  if (imageNode.hasProperty(AIRConstants.FILE_REFERENCE)) {
                    entity.setThumbnailUrl(setHost(imageNode.getProperty(AIRConstants.FILE_REFERENCE).getString(),
                            getHost(request)));
                  }
                  if (imageNode.hasProperty(AIRConstants.S7FILE_REFERENCE)) {
                    entity.setThumbnailS7Url(setHost(imageNode.getProperty(AIRConstants.S7FILE_REFERENCE)
                          .getString(), getHost(request)));
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
                entity.setCategoryId(CAR_RENTALS_CATEGORY);
                entity.setTags(StringUtils.EMPTY);
                String langCode = item.getPath().toString();
                entity.setCustom2(langCode.split(AIRConstants.SLASH)[THREE]);
                entities.add(entity);
              }
            }
          }
        }
        String csv = GeneralUtils.getCarFeedsCSV(request, entities, locale);
        getOutputStream(response, csv);
        LOGGER.debug("Success :{}, {}", "CarRentalServlet", response);
      }
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "CarRentalServlet",
          e.getLocalizedMessage());
    }
  }


}
