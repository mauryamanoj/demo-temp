package com.saudi.tourism.core.models.components.events;


import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * This Month Events.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ThisWeekMonthEventsModel {
 /**
   * The Sling settings service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * Card Type.
   */
  @Expose
  private String cardType = "calendar";

  /** Is this model called in week or month context. type should be 'this-week' or 'this-month' */
  @Inject
  @Expose
  private String cardStyle;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String filtersPagePath;

  /**
   * View All Label.
   */
  @ValueMapValue
  private String viewAllMonth;

  /**
   * View All Label.
   */
  @ValueMapValue
  private String viewAllWeek;

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;


  /**
   * Events api url.
   */
  @Expose
  private String apiUrl;

  /**
   * View all link.
   */
  @Expose
  private Link link;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();

    filtersPagePath = LinkUtils.getAuthorPublishUrl(
            resolver,
            filtersPagePath,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));

    if (StringUtils.equals(CardStyle.THIS_WEEK.type, cardStyle)) {
      link = new Link(filtersPagePath, viewAllWeek, false);
    }

    if (StringUtils.equals(CardStyle.THIS_MONTH.type, cardStyle)) {
      link = new Link(filtersPagePath, viewAllMonth, false);
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

  /**
   * Card Style enum.
   *
   * @return
   */
  @AllArgsConstructor
  private enum CardStyle {
    /**
     * This week card type.
     */
    THIS_WEEK("this-week"),

    /**
     * This month card type.
     */
    THIS_MONTH("this-month");

    /** Type. */
    private String type;
  }
}
