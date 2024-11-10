package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Events Venue Model.
 */
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class EventsCategoryModel {
  /**
   * Card Type 'calendar'.
   */
  private static final String CARD_TYPE_CALENDAR = "calendar";

  /**
   * Card Style 'hand-pick'.
   */
  private static final String CARD_STYLE_GROUPED = "grouped";

  /**
   * Card type.
   */
  @Getter
  @Expose
  private String cardType = CARD_TYPE_CALENDAR;

  /**
   * Card Style.
   */
  @Getter
  @Expose
  private String cardStyle = CARD_STYLE_GROUPED;

  /**
   * Events API URL.
   */
  @Expose
  private String apiUrl;

  /**
   * category tags.
   */
  @Getter
  @ValueMapValue
  private String[] categoryTag;

  /**
   * ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * list of category tag names.
   */
  @Getter
  @Setter
  @Expose
  private List<String> categories;

  /**
   * View All Label.
   */
  @ValueMapValue
  private String viewAllCategory;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String filtersPagePath;

  /**
   * View all link.
   */
  @Expose
  private Link link;

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

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
   * post construct.
   */
  @PostConstruct
  protected void init() {
    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();

    if (null != categoryTag) {
      categories = CommonUtils.getCategoryFromTagName(
              categoryTag, resourceResolver, Constants.DEFAULT_LOCALE);
    }

    filtersPagePath = LinkUtils.getAuthorPublishUrl(
            resourceResolver,
            filtersPagePath,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));

    link = new Link(filtersPagePath, viewAllCategory, false);
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
