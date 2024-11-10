package com.saudi.tourism.core.models.components.tripplan.v1;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants.SCHEDULE;

/**
 * This class represents one day for Trip Plans, contains a list of activities (visiting points)
 * for the particular day.
 * It's cached in Sling Resource Resolver and also in our memory cache, so don't leave here the
 * request reference after adapting.
 *
 * @see <a href="https://bit.ly/2LHyA01">A note about cache = true and using the self injector</a>
 */
@Model(adaptables = Resource.class,
       adapters = {TripDay.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_TRIP_DAY)
@NoArgsConstructor
@Slf4j
public class TripDay implements Serializable {

  /**
   * id of this day for web displaying.
   */
  @Getter
  @Setter
  private String id;

  /**
   * Date of this trip day.
   */
  @Getter
  @Setter
  private String date;

  /**
   * Display date for this trip day.
   */
  @Getter
  @Setter
  private String displayedDate;

  /**
   * Possible event categories for this day.
   */
  @Getter
  @Setter
  @ValueMapValue(name = Constants.PN_CATEGORY)
  private String[] eventCategories;

  /**
   * {@code true} if this day hits a holiday.
   */
  @Getter
  private boolean schedulePossiblyAffected = false;

  /**
   * Holiday name if this day hits a holiday.
   */
  @Getter
  private String affectedScheduleReason;

  /**
   * List of all visiting points (activities, events etc) for this day.
   */
  @Getter
  @Setter
  @SerializedName(SCHEDULE)
  @JsonProperty(SCHEDULE)
  private LinkedList<TripPoint> tripPoints = new LinkedList<>();

  /**
   * Injection constructor, calls when adapting calling `adaptTo` using resource.
   *
   * @param resource        current resource
   * @param activityService activities service to get one activity by path
   * @param settingsService slingSettings service to get runMode
   */
  @Inject
  public TripDay(@Self(injectionStrategy = InjectionStrategy.REQUIRED) final Resource resource,
      @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
      final ActivityService activityService,
      @OSGiService final SlingSettingsService settingsService) {
    final ValueMap properties = resource.getValueMap();

    final String resourcePath = resource.getPath();
    final String language = CommonUtils.getLanguageForPath(resourcePath);

    // Get Id from panel title
    final String jcrTitle = properties.get(JcrConstants.JCR_TITLE, StringUtils.EMPTY);
    final String panelTitle = properties.get(TripPlanConstants.PN_PANEL_TITLE, jcrTitle);
    setId(StringUtils.remove(panelTitle.toLowerCase(), StringUtils.SPACE));

    // List of all activities (location pages) for this day.
    final String[] pointPaths =
        properties.get(com.adobe.cq.wcm.core.components.models.List.PN_PAGES, String[].class);

    // Check if paths is null or empty
    if (ArrayUtils.isEmpty(pointPaths)) {
      LOGGER.error("Activity points don't exist for the trip day {}", resourcePath);
      return;
    }
    assert pointPaths != null;

    final ResourceResolver resolver = resource.getResourceResolver();

    // Iterate paths and add them as points into this day
    Stream.of(pointPaths).map(pointPath -> {
      try {
        return TripPoint
            .createForPath(pointPath, language, resolver, activityService, settingsService);
      } catch (Exception ignored) {
        LOGGER.error("Couldn't find trip point by path {}. Check if the page was removed or "
            + "unpublished.", pointPath);
        return null;
      }
    }).filter(Objects::nonNull).collect(Collectors.toCollection(() -> tripPoints));
  }

  /**
   * This is a copy constructor to clone this instance.
   *
   * @param source the TripDay instance to be copied
   */
  public TripDay(final TripDay source) {
    setId(source.getId());
    setDate(source.getDate());
    setDisplayedDate(source.getDisplayedDate());
    setEventCategories(source.getEventCategories());
    this.schedulePossiblyAffected = false;
    this.affectedScheduleReason = null;

    source.getTripPoints().forEach(point -> tripPoints.add(new TripPoint(point)));
  }

  /**
   * Setter for schedulePossiblyAffected / affectedScheduleReason.
   *
   * @param holidayName holiday name for this day
   */
  public void setHoliday(final String holidayName) {
    if (StringUtils.isBlank(holidayName)) {
      this.schedulePossiblyAffected = false;
      this.affectedScheduleReason = null;

    } else {
      this.schedulePossiblyAffected = true;
      this.affectedScheduleReason = holidayName;
    }
  }
}
