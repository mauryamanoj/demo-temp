package com.saudi.tourism.core.models.components.tripplan.v1;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

/**
 * The model represents one Trip Plan for search and json output.
 * In comparison with TripPlanComponent this one is used for Trip Plan page, not for the
 * component and also is adapted from resource, not from the request.
 * It's cached in Sling Resource Resolver and also in our memory cache, so don't leave here the
 * request reference after adapting.
 * Also, this model produces json output using Sling Model Exporter feature, the json is used in
 * the corresponding react component (trip-detail).
 *
 * @see <a href="https://bit.ly/2LHyA01">A note about cache = true and using the self injector</a>
 */
@Model(adaptables = Resource.class,
       adapters = {TripPlan.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_TRIP_PLAN_PAGE)
@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
          name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION,
          options = {@ExporterOption(name = "SerializationFeature.WRAP_ROOT_VALUE",
                                     value = Constants.STR_TRUE),
              @ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                              value = Constants.STR_TRUE)})
@JsonRootName("data")
@Slf4j
public class TripPlan implements Serializable {

  /**
   * Trip plan id = {@link TripPlanConstants#NEW_TRIP_PLAN_ID} for a new trip, UUID after the
   * trip plan was saved into user's login (Auth0) storage.
   */
  @Default(values = TripPlanConstants.NEW_TRIP_PLAN_ID)
  @Getter
  @Setter
  private String id = TripPlanConstants.NEW_TRIP_PLAN_ID;

  /**
   * Trip Plan title.
   */
  @Getter
  @Setter
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  private String title;

  /**
   * Trip Plan description.
   */
  @Getter
  @Setter
  @ValueMapValue(name = JcrConstants.JCR_DESCRIPTION)
  private String description;

  /**
   * Path to this trip itinerary page.
   */
  @Getter
  @Setter
  private String path;

  /**
   * Image for this Trip Plan.
   */
  @Getter
  @ChildResource
  @JsonIgnore
  private transient Image image;

  /**
   * Desktop image asset reference (for json export).
   */
  @Getter
  @Setter
  private String desktopImage;

  /**
   * Mobile image asset reference (for json export).
   */
  @Getter
  @Setter
  private String mobileImage;

  /**
   * Start date.
   */
  @Getter
  @Setter
  private String startDate;

  /**
   * Displayed start date.
   */
  @Getter
  @Setter
  private String displayedStartDate;

  /**
   * End date.
   */
  @Getter
  @Setter
  private String endDate;

  /**
   * Displayed end date.
   */
  @Getter
  @Setter
  private String displayedEndDate;

  /**
   * Trip Days Counter.
   */
  @Getter
  @Setter
  private Integer daysCount;
  /**
   * numOfDays.
   */
  @Getter
  @Setter
  private Integer numberOfDays;
  /**
   * Stores the current Trip Plan data (city itineraries). Is adapted from Trip Detail component.
   */
  @Getter
  @JsonUnwrapped
  @ChildResource(name = TripPlanConstants.TRIP_DETAIL_PATH_ON_PAGE)
  private TripDetail data;

  /**
   * The page content resource from the adaptable (clear after PostConstruct!).
   */
  @JsonIgnore
  @Self(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient Resource resource;

  /**
   * The Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * No args constructor.
   */
  public TripPlan() {
    this.data = new TripDetail();
    this.daysCount = calculateTripDays();
  }

  /**
   * This is a copy constructor to clone this instance.
   *
   * @param source the TripPlan instance to be copied
   */
  public TripPlan(@NotNull final TripPlan source) {
    this.id = source.getId();
    setTitle(source.getTitle());
    setDescription(source.getDescription());
    this.path = source.getPath();
    this.image = source.getImage();
    setDesktopImage(source.getDesktopImage());
    setMobileImage(source.getMobileImage());
    setStartDate(source.getStartDate());
    setDisplayedStartDate(source.getDisplayedStartDate());
    setEndDate(source.getEndDate());
    setDisplayedEndDate(source.getDisplayedEndDate());

    final TripDetail tripDetail = source.getData();
    if (tripDetail != null) {
      this.data = new TripDetail(tripDetail);
      this.daysCount = calculateTripDays();
    }
  }

  /**
   * Calculate trip days.
   *
   * @return daysCount
   */
  private Integer calculateTripDays() {
    final List<CityItinerary> cities = getData().getCities();
    if (CollectionUtils.isEmpty(cities)) {
      return 0;
    } else if (StringUtils.isBlank(startDate)) {
      // in case of empty trip planner, daysCount == number of cities in trip list
      return cities.size();
    }
    return cities.stream().map(CityItinerary::getDaysCount).reduce(0, Integer::sum);
  }

  /**
   * This model after construct initialization.
   */
  @PostConstruct
  private void init() {
    try {
      if (image != null) {
        // Prepare image for using dynamic media
        DynamicMediaUtils.prepareDMImages(image, TripPlanConstants.CROP_HERO_DSK,
            TripPlanConstants.CROP_HERO_MOB, DynamicMediaUtils.isCnServer(settingsService));
      }
      // Update properties that are related to current page path
      updatePathRelatedProperties();

    } finally {
      // Clear services and request-related data for sling model caching
      this.resource = null;
    }
  }

  /**
   * Updates id, path fields according to the current page content
   * resource path.
   */
  private void updatePathRelatedProperties() {
    if (resource == null) {
      final String errorMessage = "Can't inject the current resource, check if the model was "
          + "adapted from request instead of resource.";
      final IllegalStateException exception = new IllegalStateException(errorMessage);
      LOGGER.error(errorMessage, exception);
      throw exception;
    }

    final ResourceResolver resourceResolver = resource.getResourceResolver();
    final PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    assert pageManager != null;

    final Page currentPage = pageManager.getContainingPage(resource);
    assert currentPage != null;

    this.path = currentPage.getPath();
  }
}
