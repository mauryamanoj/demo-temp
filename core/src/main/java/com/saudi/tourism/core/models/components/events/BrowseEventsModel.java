package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * Browse Events Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class BrowseEventsModel {

  /**
   * Days left in Week.
   */
  private static final int DAYS_LEFT_IN_WEEK = 6;

  /**
   * The Sling settings service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private transient Page currentPage;

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * Today Events Text.
   */
  @ValueMapValue
  @Expose
  private String todayEventsText;

  /**
   * This Week Events Text.
   */
  @ValueMapValue
  @Expose
  private String thisWeekEventsText;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String eventsFilterPagePath;

  /**
   * This Month Events Text.
   */
  @ValueMapValue
  @Expose
  private String thisMonthEventsText;

  /**
   * Today Events Link.
   */
  @Expose
  private String todayEventsLink;

  /**
   * This Week Events Link.
   */
  @Expose
  private String thisWeekEventsLink;

  /**
   * This Month Events Link.
   */
  @Expose
  private String thisMonthEventsLink;

  /**
   * Events api url.
   */
  @Expose
  private String apiUrl;

  /**
   * Empty Calendar Title.
   */
  @ValueMapValue
  @Expose
  private String emptyCalendarTitle;

  /**
   * Empty Calendar Subtitle.
   */
  @ValueMapValue
  @Expose
  private String emptyCalendarSubtitle;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    if (null != currentPage.getContentResource()) {
      eventsFilterPagePath = LinkUtils
        .getAuthorPublishUrl(currentPage.getContentResource().getResourceResolver(),
          eventsFilterPagePath, settingsService.getRunModes().contains(Externalizer.PUBLISH));

    }
    InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentPage.getContentResource());
    String locale = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);

    String todayDate = LocalDate.now().toString();
    if (StringUtils.isNotEmpty(eventsFilterPagePath) && StringUtils.isNotBlank(todayEventsText)) {
      todayEventsLink = eventsFilterPagePath + "?" + Constants.LOCALE + "=" + locale + "&"
              + Constants.START_DATE + "=" + todayDate + "&" + Constants.END_DATE + "="
              + todayDate + "&type=today";
    }

    if (StringUtils.isNotEmpty(eventsFilterPagePath) && StringUtils.isNotBlank(thisWeekEventsText)) {
      String thisWeeksEndDate = LocalDate.now().plusDays(DAYS_LEFT_IN_WEEK).toString();
      thisWeekEventsLink = eventsFilterPagePath + "?" + Constants.LOCALE + "=" + locale + "&"
              + Constants.START_DATE + "=" + todayDate + "&" + Constants.END_DATE + "="
              + thisWeeksEndDate + "&type=this-week";
    }

    if (StringUtils.isNotEmpty(eventsFilterPagePath) && StringUtils.isNotBlank(thisMonthEventsText)) {
      String thisMonthsEndDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();
      thisMonthEventsLink = eventsFilterPagePath + "?" + Constants.LOCALE + "=" + locale + "&"
              + Constants.START_DATE + "=" + todayDate + "&" + Constants.END_DATE + "="
              + thisMonthsEndDate + "&type=this-month";
    }

    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();
  }

  /**
   * Generate json serialization of the model.
   *
   * @return json serialization.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
