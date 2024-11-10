package com.saudi.tourism.core.models.components.tripplan.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.saudi.tourism.core.login.servlets.SSIDDeleteFavTripServlet;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Objects;

import static com.saudi.tourism.core.models.components.tripplan.TripPlannerUtils.getTripPlannerDetailPageUrl;
import static com.saudi.tourism.core.utils.Constants.TRIPS_EXTENSION;

/**
 * The Class TripsOverviewSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = "sauditourism/components/content/trip-planner/v1/trips-overview")
@Data
public class TripsOverviewSlingModel implements Serializable {
  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;
  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;
  /**
   * TripPlans endpoint.
   */
  @ValueMapValue
  private String getTripPlansEndpoint = SSIDGetUserDetailsServlet.SERVLET_PATH + TRIPS_EXTENSION;
  /**
   * delete TripPlans endpoint.
   */
  @ValueMapValue
  private String deleteTripPlanEndpoint = SSIDDeleteFavTripServlet.SERVLET_PATH + TRIPS_EXTENSION;
  /**
   * delete TripPlans endpoint.
   */
  @ValueMapValue
  private String tripDetailsUrl;
  /**
   * titles.
   */
  @ChildResource
  private Titles titles;
  /**
   * exploreButton.
   */
  @ChildResource
  private ExploreButton exploreButton;
  /**
   * cardCopies.
   */
  @ChildResource
  private CardCopies cardCopies;
  /**
   * deleteModal.
   */
  @ChildResource
  private DeleteModal deleteModal;


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
   * init method.
   */
  @PostConstruct
  public void init() {
    boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    this.tripDetailsUrl = getTripPlannerDetailPageUrl(currentResource.getResourceResolver(),
        this.tripDetailsUrl, isPublish, CommonUtils.getLanguageForPath(currentResource.getPath()));
    if (Objects.nonNull(exploreButton) && Objects.nonNull(exploreButton.href)) {
      exploreButton.href = LinkUtils.getAuthorPublishUrl(
        currentResource.getResourceResolver(), exploreButton.href, Boolean.toString(isPublish));
    }
  }

  /**
   * Titles class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class Titles implements Serializable {
    /**
     * upcomingTrips.
     */
    @ValueMapValue
    private String upcomingTrips;
    /**
     * upcomingTrips.
     */
    @ValueMapValue
    private String pastTrips;
  }

  /**
   * ExploreButton class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class ExploreButton implements Serializable {
    /**
     * label.
     */
    @ValueMapValue
    private String label;
    /**
     * href.
     */
    @ValueMapValue
    private String href;
  }

  /**
   * CardCopies class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class CardCopies implements Serializable {
    /**
     * deleteButtonLabel.
     */
    @ValueMapValue
    private String deleteButtonLabel;
    /**
     * featuredLabel.
     */
    @ValueMapValue
    private String featuredLabel;
    /**
     * multipleSelectionLabel.
     */
    @ValueMapValue
    private String multipleSelectionLabel;
  }

  /**
   * LabeledClass class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class LabeledClass implements Serializable {
    /**
     * upcomingTrips.
     */
    @ValueMapValue
    private String label;
  }

  /**
   * DeleteModal class.
   */
  @Model(adaptables = {Resource.class},
         defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class DeleteModal implements Serializable {
    /**
     * firstLine.
     */
    @ValueMapValue
    private String firstLine;
    /**
     * secondLine.
     */
    @ValueMapValue
    private String secondLine;
    /**
     * smallMessage.
     */
    @ValueMapValue
    private String smallMessage;
    /**
     * primaryButton.
     */
    @ChildResource
    private LabeledClass primaryButton;
    /**
     * secondaryButton.
     */
    @ChildResource
    private LabeledClass secondaryButton;
  }
}
