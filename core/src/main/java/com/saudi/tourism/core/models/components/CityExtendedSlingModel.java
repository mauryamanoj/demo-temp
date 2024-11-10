package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * CityExtendedSlingModel.
 */
@Generated
@Model(adaptables = SlingHttpServletRequest.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class CityExtendedSlingModel {

  /**
   * Reference to RegionCityService.
   */
  @OSGiService
  private RegionCityService regionCityService;

  /**
   * List of cities.
   */
  @Getter
  private List<RegionCityExtended> cityList;

  /**
   * Post construction.
   */
  @PostConstruct
  public void init() {
    this.cityList = regionCityService.getCitiesExt(Constants.DEFAULT_LOCALE);
  }
}
