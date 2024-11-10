package com.saudi.tourism.core.models.common;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

/**
 * Analytics Link Model. can be used to set properties for analytics link model.
 */
@Data
@Model(adaptables = SlingHttpServletRequest.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageAnalyticsModel {

  /**
   * INT_FOUR.
   */
  public static final int INT_FOUR = 4;

  /**
   * INT_THREE.
   */
  public static final int INT_FIVE = 5;

  /**
   * INT_FOUR.
   */
  public static final int INT_SIX = 6;

  /**
   * INT_FIVE.
   */
  public static final int INT_SEVEN = 7;

  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * currentPage.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * analyticsPageName.
   */
  @ValueMapValue
  @Expose
  private String analyticsPageName;

  /**
   * canonicalUrl.
   */
  @ValueMapValue
  private String canonicalUrl;

  /**
   * City Name.
   */
  private String city = StringUtils.EMPTY;

  /**
   * Page Category.
   */
  private String pageCategory = StringUtils.EMPTY;

  /**
   * Page Sub Category.
   */
  private String pageSubCategory = StringUtils.EMPTY;

  /**
   * country.
   */
  private String country;

  /**
   * processedUrl.
   */
  private String processedUrl;

  /**
   * country and page name values for analytics.
   */
  @PostConstruct
  protected void init() {
    String[] pathArray = currentPage.getPath().split(Constants.FORWARD_SLASH_CHARACTER);

    if (pathArray.length > INT_FOUR && StringUtils.isNotBlank(pathArray[INT_FOUR])) {

      if ("destinations".equalsIgnoreCase(pathArray[INT_FOUR])) {
        // if (pathArray.length > INT_SEVEN && StringUtils.isNotBlank(pathArray[INT_SEVEN])) {
        //   pageSubCategory = pathArray[INT_SEVEN];
        // }
        if (pathArray.length > INT_SIX && StringUtils.isNotBlank(pathArray[INT_SIX])) {
          pageCategory = pathArray[INT_SIX];
        }
        if (pathArray.length > INT_FIVE && StringUtils.isNotBlank(pathArray[INT_FIVE])) {
          city = pathArray[INT_FIVE];
        }

      }
    }


    //String locale = pathArray[INT_THREE];
    String locale = currentPage.getLanguage().getLanguage();
    if (null != locale && locale.equals("zh")) {
      country = "cn";
    } else {
      country = locale;
    }

    if (StringUtils.isNotBlank(canonicalUrl)) {
      processedUrl = canonicalUrl;
    } else {
      processedUrl = currentPage.getPath();
    }
    processedUrl = LinkUtils.getAuthorPublishUrl(
        resourceResolver,
        processedUrl,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}
