package com.saudi.tourism.core.models.components.tripplan.v1;

import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ResourceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * An itinerary for one city belonging to a trip plan.
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_CITY_ITINERARY)
@NoArgsConstructor
@Slf4j
public class CityItinerary implements Serializable {

  /**
   * City id for this itinerary.
   */
  @Getter
  private String cityId;

  /**
   * number of days.
   */
  @Getter
  @Setter
  private int numberOfDays;

  /**
   * City for this itinerary.
   */
  @Getter
  private RegionCity city;

  /**
   * Handles all days for this trip plan.
   */
  @Getter
  private List<TripDay> days = new LinkedList<>();

  /**
   * Create itinerary for city id.
   *
   * @param cityId the id of this itinerary's city
   */
  public CityItinerary(final String cityId) {
    this.cityId = cityId;
  }

  /**
   * Create itinerary for city.
   *
   * @param forCity this itinerary's city
   */
  public CityItinerary(@NotNull final RegionCity forCity) {
    this(forCity.getId());
    this.city = forCity;
  }

  /**
   * Injection constructor, calls when adapting calling `adaptTo`. Iterates child components and
   * adds them as Trip Days.
   *
   * @param currentResource injected current resource
   * @param cities          service instance to get city by id
   */
  @Inject
  public CityItinerary(
      @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED) final Resource currentResource,
      @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED) final RegionCityService cities) {
    this(AppUtils.stringToID(currentResource.getValueMap().get(Constants.CITY, String.class)));

    this.days = ResourceUtil.getContainerChildComponents(currentResource, TripDay.class);

    // Initialize city object
    if (StringUtils.isBlank(cityId)) {
      LOGGER.error("City wasn't specified for itinerary {}", currentResource);
    } else {
      final String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
      final RegionCityExtended extendedCityInfo = cities.getRegionCityExtById(cityId, locale);

      if (extendedCityInfo == null) {
        // If city was removed / city id changed - the itinerary still should work (workaround)
        LOGGER.error("Couldn't find city for the itinerary. CityId: {}, city itinerary: {}", cityId,
            currentResource.getPath());
        this.city = new RegionCity(cityId, cityId);

      } else {
        this.city = extendedCityInfo;
      }
    }

    int dayNum = 0;
    for (TripDay tripDay : this.days) {
      // Update day id according to the index (for the React component).
      dayNum++;
      // Ids are "day1", "day2", "day3" etc.
      tripDay.setId(TripPlanConstants.PREFIX_DAY + dayNum);
    }
  }

  /**
   * This is a copy constructor to clone this instance.
   *
   * @param source the instance to be copied
   */
  public CityItinerary(@NotNull final CityItinerary source) {
    this(source.getCityId());
    this.city = source.getCity();
    this.numberOfDays = source.getNumberOfDays();
    source.getDays().forEach(day -> days.add(new TripDay(day)));
  }

  /**
   * Count of days in this itinerary.
   *
   * @return count of days
   */
  public int getDaysCount() {
    return getDays().size();
  }

  @Override
  public String toString() {
    String cityName = "unknown";
    if (city != null) {
      cityName = CommonUtils.firstNotBlank(city.getName(), city.getId(), cityName);
    }

    return "{" + "city: " + cityName + ", length: " + days.size() + "day(s)" + "}";
  }
}
