package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.jcr.Node;
import javax.jcr.Value;
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
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "General Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/air/v1/general",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv"})
@Slf4j
public class GeneralServlet extends AbstractATFeedServlet {

  /** Property name of tags. */
  public static final String GENERAL = "general";
  /** Integer for Inventory. */
  public static final int ZERO = 0;

  /** Integer for Inventory. */
  public static final int FIVE = 5;
  /** S7FEATURE_IMAGE to check. */
  public static final String S7FEATURE_IMAGE = "s7featureImage";
  /** PAGE_CATEGORY to check. */
  public static final String PAGE_CATEGORY = "pageCategory";
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
      String sourcePath = request.getRequestParameter("path").getString();
      String eventQuery = "SELECT * FROM [cq:PageContent] AS node WHERE ISDESCENDANTNODE(node,"
             + " \"" + sourcePath + "\") and  node.[jcr:description] IS NOT NULL AND node.[jcr:description] <> \"\"";
      List<Entity> entities = new ArrayList<Entity>();
      @NotNull Iterator<Resource> resources = request.getResourceResolver().findResources(eventQuery,
         AIRConstants.JCR_SQL);
      while (resources.hasNext()) {
        Entity entity = new Entity();
        Node nextNode =  resources.next().adaptTo(Node.class);
        if (!StringUtils.equalsIgnoreCase(nextNode.getPath().toString(), JcrConstants.JCR_CONTENT)) {
          String uniqueId = StringEscapeUtils.escapeCsv(nextNode.getPath()
              .toString().split(AIRConstants.DESTINATIONS_TAG)[1].stripLeading().replaceAll(AIRConstants.SLASH
                + JcrConstants.JCR_CONTENT, StringUtils.EMPTY));
          if (!uniqueId.contains(JcrConstants.JCR_CONTENT)) {
            entity.setId(uniqueId.replaceAll(AIRConstants.SLASH, AIRConstants.DASH));
            entity.setName(nextNode.getProperty(JcrConstants.JCR_TITLE).getString().stripLeading());
            String message = nextNode.getProperty(JcrConstants.JCR_DESCRIPTION).getString()
                  .replaceAll(AIRConstants.NEW_LINE_WINDOWS, StringUtils.EMPTY);
            entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
            entity.setCategoryId(GENERAL);
            if (nextNode.hasProperty(Constants.FEATURE_IMAGE)) {
              entity.setThumbnailUrl(StringEscapeUtils.escapeCsv(setHost(nextNode
                  .getProperty(Constants.FEATURE_IMAGE).getString(), getHost(request))));
            } else {
              entity.setThumbnailUrl(StringUtils.EMPTY);
            }
            if (nextNode.hasProperty(S7FEATURE_IMAGE)) {
              entity.setThumbnailS7Url(StringEscapeUtils.escapeCsv(setHost(nextNode
                  .getProperty(S7FEATURE_IMAGE).getString(), getHost(request))));
            } else {
              entity.setThumbnailS7Url(StringUtils.EMPTY);
            }
            entity.setPageUrl(nextNode.getPath().split(AIRConstants.SAUDITOURISM)[1].replaceAll(AIRConstants.SLASH
                + JcrConstants.JCR_CONTENT, StringUtils.EMPTY));
            entity.setInventory(ZERO);
            entity.setMargin(AIRConstants.NULL);
            if (nextNode.hasProperty(PAGE_CATEGORY)) {
              Value[] tagsArray = nextNode.getProperty(PAGE_CATEGORY).getValues();
              String tagsList = StringUtils.EMPTY;
              for (int k = 0; k < tagsArray.length; k++) {
                String destinationTags = tagsArray[k].toString();
                String finalResult = destinationTags.split(AIRConstants.SLASH)[1];
                tagsList = tagsList + finalResult + AIRConstants.DEFAULT_SEPARATOR + Constants.SPACE;
              }
              entity.setTags(StringEscapeUtils.escapeCsv(tagsList.substring(0, tagsList.length() - 2))); //entity.tags
              Map<String, List<String>> data = getTagsInterestMapping();
              Set<String> interests = new HashSet<String>();
              for (Entry<String, List<String>> entry : data.entrySet()) {
                String[] tagsValue = tagsList.split(AIRConstants.DEFAULT_SEPARATOR + Constants.SPACE);
                for (String tag: tagsValue) {
                  if (entry.getValue().contains(tag)) {
                    interests.add(entry.getKey());
                  }
                }
              }
              List<String> interestTags = new ArrayList<String>();
              if (interests.size() > 0) {
                interestTags.addAll(interests);
              }
              entity.setInterests(Entity.interestToString(interestTags)); //entity.interests
            } else {
              entity.setTags(StringUtils.EMPTY);
              entity.setInterests(StringUtils.EMPTY);
            }
            if (nextNode.hasProperty(AIRConstants.REGION)) {
              entity.setBrand(nextNode.getProperty(AIRConstants.REGION).getString());
            } else {
              entity.setBrand(StringUtils.EMPTY);
            }
            if (nextNode.hasProperty(AIRConstants.CITY)) {
              entity.setCtaText(AIRConstants.LEFT_SQUARE_BRACKET + AIRConstants.DOUBLE_QUOTES
                  + nextNode.getProperty(AIRConstants.CITY).getString() + AIRConstants.DOUBLE_QUOTES
                  + AIRConstants.RIGHT_SQUARE_BRACKET);
            } else {
              entity.setCtaText(StringUtils.EMPTY);
            }
            entities.add(entity);
          }
        }
      }
      String csv = GeneralUtils.getGeneralFeeds(request, entities, locale);
      getOutputStream(response, csv);
      LOGGER.debug("Success :{}, {}", "GeneralServlet", response);
    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error",
          h.getMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "GeneralServlet",
          e.getStackTrace());
    }
  }


}
