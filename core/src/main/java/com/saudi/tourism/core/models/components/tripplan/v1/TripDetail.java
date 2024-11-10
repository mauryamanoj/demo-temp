package com.saudi.tourism.core.models.components.tripplan.v1;

import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ResourceUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * This class represents one Trip Detail component.
 * It's cached in Sling Resource Resolver and also in our memory cache, so don't leave here the
 * request references after adapting.
 *
 * @see <a href="https://bit.ly/2LHyA01">A note about cache = true and using the self injector</a>
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_TRIP_DETAIL)
@NoArgsConstructor
@Slf4j
public class TripDetail implements Serializable {

  /**
   * Handles all days for this trip plan.
   */
  @Getter
  private List<CityItinerary> cities = new LinkedList<>();

  /**
   * Injection constructor, calls when adapting calling `adaptTo`. Iterates child components and
   * adds them as City Itineraries.
   *
   * @param currentResource injected current resource
   */
  @Inject
  public TripDetail(
      @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED) final Resource currentResource) {
    this.cities
        .addAll(ResourceUtil.getContainerChildComponents(currentResource, CityItinerary.class));
  }

  /**
   * This is a copy constructor to clone this instance.
   *
   * @param source the instance to be copied
   */
  public TripDetail(@NotNull final TripDetail source) {
    source.getCities().forEach(city -> this.cities.add(new CityItinerary(city)));
  }
}
