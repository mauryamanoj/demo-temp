package com.sta.core.solr.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.sta.core.solr.services.SolrServerConfiguration;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.json.JSONArray;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

/**
 * The type Solr utils.
 */
public final class SolrUtils {

  /**
   * Private constructor to avoid unnecessary instantiation of the class.
   */
  private SolrUtils() {
  }


  /**
   * This method is used to extract the tags from the content page.
   *
   * @param tags tag list
   * @param tagManager TagManager
   * @param locale current locale
   * @return Array of tags which are attached to the page.
   */
  public static JSONArray getPageTags(String[] tags, TagManager tagManager, Locale locale) {
    JSONArray tagList = new JSONArray();
    if (tags != null) {
      for (int i = 0; i < tags.length; i++) {
        tagList.put(CommonUtils.getTagName(tags[i], tagManager, locale));
      }
    }
    return tagList;
  }


  /**
   * This method converts jcr formatted date to Solr specification format.
   *
   * @param cal the cal
   * @return Solr formatted date of type string
   */
  public static String solrDate(Calendar cal) {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss");
    return dateFormat.format(cal.getTime()) + "Z";
  }

  /**
   * This method returns "" if string is null.
   *
   * @param property the property
   * @return String value. if string value is "null" then ""
   */
  public static String checkNull(String property) {
    if (StringUtils.isEmpty(property)) {
      return "";
    }
    return property;

  }

  /**
   * This method returns solr search url.
   *
   * @param solrConfigurationService SolrServerConfiguration
   * @param lang language
   * @return Solr url
   */
  public static String buildSolrSearchUrl(SolrServerConfiguration solrConfigurationService,
                                          String lang) {
    return String.format("%s/%s/select?"
            + "fq=language:%s"
            //+ "&rows=10&start=0"
            + "&q={!dismax "
            + "qf=title^%s "
            + "qf=subtitle^%s "
            + "qf=text "
            + "pf=title^%s "
            + "pf=text^%s}",
        formatURL(solrConfigurationService.getSolrUrl()),
        solrConfigurationService.getSolrCore(),
        lang,
        solrConfigurationService.getTitleScore(),
        solrConfigurationService.getSubtitleScore(),
        solrConfigurationService.getExactMatchScore(),
        solrConfigurationService.getExactMatchScore());
  }

  /**
   * This method returns solr suggest url.
   *
   * @param solrConfigurationService SolrServerConfiguration
   * @param lang language
   * @return Solr url
   */
  public static String buildSolrSuggestUrl(SolrServerConfiguration solrConfigurationService,
      String lang) {
    return String.format("%s/%s/suggest?"
            + "fq=language:%s"
            //+ "&rows=10&start=0"
            + "&q={!dismax "
            + "qf=title^%s "
            + "qf=subtitle^%s "
            + "qf=text "
            + "pf=title^%s "
            + "pf=text^%s}",
        formatURL(solrConfigurationService.getSolrUrl()),
        solrConfigurationService.getSolrCore(),
        lang,
        solrConfigurationService.getTitleScore(),
        solrConfigurationService.getSubtitleScore(),
        solrConfigurationService.getExactMatchScore(),
        solrConfigurationService.getExactMatchScore());
  }

  /**
   * This method removes last slash if present.
   *
   * @param url url string to format
   * @return formatted url
   */
  private static String formatURL(String url) {
    if (url.endsWith("/")) {
      return url.substring(0, url.length() - 1);
    }
    return url;
  }

  /**
   * This method checks resource path before sending to solr.
   *
   * @param path resource path
   * @return boolean result
   */
  public static boolean allowedSolrPath(String path) {
    return path.startsWith("/content/")
        && (!path.contains("/Configs")
            || path.endsWith("/admin")
            || (path.contains("Configs/articles")))
        && !path.contains("archive")
        && !path.contains("/errors")
        && !path.contains(("/sections"))
        && !path.contains("tabs");
  }

  /**
   * This method checks resource should be recursevely index.
   *
   * @param path resource path
   * @return boolean result
   */
  public static boolean allowedSolrRecursiveIndex(String path) {
    return path.startsWith("/content/") && (path.endsWith("/Configs") || path.endsWith("/items"));
  }

  /**
   * This method checks resource before sending to solr.
   *
   * @param resource resource
   * @return boolean result
   */
  public static boolean allowedSolrResource(Resource resource) {
    return resource.getResourceType().equals(NT_PAGE) && isActiveEvent(resource)
      && !resource.getPath().endsWith("/items");
  }

  /**
   * Checks calendarEndDate property from event's jcr:content.
   *
   * @param resource current resource
   * @return boolean result
   */
  private static boolean isActiveEvent(Resource resource) {
    Resource jcrContent = resource.getChild(JcrConstants.JCR_CONTENT);
    if (jcrContent != null && Constants.EVENT_DETAIL_RES_TYPE
        .equals(jcrContent.getResourceType())) {
      ValueMap properties = jcrContent.getValueMap();
      Date eventsEndDate = properties.get(Constants.EVENT_END, Date.class);
      if (eventsEndDate != null && eventsEndDate.before(new Date())) {
        return false;
      }
    }
    return true;
  }
}
