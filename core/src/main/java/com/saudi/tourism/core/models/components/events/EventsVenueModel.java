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
public class EventsVenueModel {

  /**
   * Card Type 'calendar'.
   */
  private static final String CARD_TYPE_CALENDAR = "calendar";

  /**
   * Card Style 'hand-pick'.
   */
  private static final String CARD_STYLE_GROUPED = "grouped";

  /**
   * The Sling settings service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * SaudiTourismConfigs.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * venue tags.
   */
  @Getter
  @ValueMapValue
  private String[] venueTag;

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
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * list of venue tag names.
   */
  @Getter
  @Setter
  @Expose
  private List<String> venues;

  /**
   * View All Label.
   */
  @ValueMapValue
  private String viewAllVenue;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String filtersPagePath;

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
   * post construct.
   */
  @PostConstruct
  protected void init() {
    apiUrl = saudiTourismConfigs.getEventsApiEndpoint();

    if (null != venueTag) {
      venues = CommonUtils.getCategoryFromTagName(venueTag, resourceResolver, Constants.DEFAULT_LOCALE);
    }

    filtersPagePath = LinkUtils
      .getAuthorPublishUrl(resolver, filtersPagePath, settingsService.getRunModes().contains(Externalizer.PUBLISH));

    link = new Link(filtersPagePath, viewAllVenue, false);
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
