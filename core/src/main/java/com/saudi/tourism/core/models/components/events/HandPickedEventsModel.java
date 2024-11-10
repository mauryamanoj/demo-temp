package com.saudi.tourism.core.models.components.events;


import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.servlets.EventsServlet;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Hand-picked Events Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HandPickedEventsModel {
  /**
   * Card Type 'calendar'.
   */
  public static final String CARD_TYPE_CALENDAR = "calendar";

  /**
   * Card Style 'hand-pick'.
   */
  public static final String CARD_STYLE_HAND_PICKED_EVENTS = "hand-picked-events";
  /**
   * The Sling settings service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

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
  private String cardStyle = CARD_STYLE_HAND_PICKED_EVENTS;

  /**
   * Events API URL.
   */
  @Expose
  private String apiUrl = EventsServlet.SERVLET_PATH;

  /**
   * View All Label.
   * Headline.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * View All Label.
   */
  @Getter
  @ValueMapValue
  private String viewAllLabel;

  /**
   * Analytics View All Label.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String analyticsViewAllLabel;

  /**
   * View All Url.
   */
  @Getter
  @ValueMapValue
  private String viewAllUrl;

  /**
   * View all link.
   */
  @Getter
  @Expose
  private Link link;

  /**
   * Hand-picked events paths.
   */
  @Getter
  @ChildResource(injectionStrategy = InjectionStrategy.OPTIONAL)
  private List<HandPickedEventModel> handPickedEvents;

  /**
   * Hand-picked events ids.
   */
  @Getter
  @Expose
  private List<String> eventsIds;

  /**
   * Resource resolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    viewAllUrl = LinkUtils
      .getAuthorPublishUrl(resolver, viewAllUrl, settingsService.getRunModes().contains(Externalizer.PUBLISH));
    eventsIds = Optional.ofNullable(handPickedEvents)
      .orElse(Collections.emptyList())
      .stream()
      .filter(e -> e != null)
      .filter(e -> e.getEventDetail() != null)
      .map(e -> e.getEventDetail().getId())
      .collect(Collectors.toList());

    link = new Link(viewAllUrl, viewAllLabel, false);
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
