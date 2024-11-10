package com.saudi.tourism.core.models.components.tripplan.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.saudi.tourism.core.login.servlets.SSIDDeleteFavTripServlet;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import com.saudi.tourism.core.login.servlets.SSIDUpdateUserDetailsServlet;
import com.saudi.tourism.core.servlets.ActivityServlet;
import com.saudi.tourism.core.servlets.EventsServlet;
import com.saudi.tourism.core.servlets.HolidaysServlet;
import com.saudi.tourism.core.servlets.SSIDTripPlanFilterServlet;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
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
import java.io.Serializable;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.JSON_EXTENSION;
import static com.saudi.tourism.core.utils.Constants.TRIPS_EXTENSION;

/**
 * The Class TripDetailSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = {"sauditourism/components/content/trip-planner/v1/trip-detail",
           "sauditourism/components/content/trip-planner/v1/trip-editor"})
@Data
public class TripDetailSlingModel implements Serializable {
  /**
   * Activities endpoint.
   */
  private String attractionsEndpoint = ActivityServlet.SERVLET_PATH + JSON_EXTENSION;
  /**
   * Trip plan endpoint.
   */
  private String tripPlanEndpoint = SSIDGetUserDetailsServlet.SERVLET_PATH + TRIPS_EXTENSION;
  /**
   * Update trip plan endpoint.
   */
  private String updateTripPlanEndpoint = SSIDUpdateUserDetailsServlet.SERVLET_PATH + TRIPS_EXTENSION;
  /**
   * Holidays endpoint.
   */
  private String holidaysEndpoint = HolidaysServlet.SERVLET_PATH + JSON_EXTENSION;
  /**
   * Delete trip plan endpoint.
   */
  private String deleteTripPlanEndpoint = SSIDDeleteFavTripServlet.SERVLET_PATH + TRIPS_EXTENSION;
  /**
   * Events endpoint.
   */
  private String eventsEndpoint = EventsServlet.SERVLET_PATH;

  /**
   * Trip plan filter (available cities) endpoint.
   */
  private String citiesEndpoint = SSIDTripPlanFilterServlet.SERVLET_PATH;

  /**
   * Favorites path.
   */
  @ValueMapValue
  private String favoritesPath;
  /**
   * Redirection url.
   */
  @ValueMapValue
  private String redirectionUrl;
  /**
   * 'Not Found' url.
   */
  @ValueMapValue
  private String notFoundUrl;

  /**
   * Trip detail copies.
   */
  @ChildResource
  private TripDetailCopies copies;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }

  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;
  /**
   * init method.
   */
  @PostConstruct
  public void init() {
    boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    if (Objects.nonNull(redirectionUrl)) {
      redirectionUrl = LinkUtils.getAuthorPublishUrl(resolver, redirectionUrl, isPublish);
    }
    if (Objects.nonNull(notFoundUrl)) {
      notFoundUrl = LinkUtils.getAuthorPublishUrl(resolver, notFoundUrl, isPublish);
    }
  }
}
