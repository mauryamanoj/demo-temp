package com.saudi.tourism.core.models.app.entertainer;

import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * EntertainerCitiesPage Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = {"sauditourism/components/structure/entertainer-cities-page"})
@Slf4j
public class EntertainerCitiesPageModel {

  /**
   * Reference to RegionCityService.
   */
  @OSGiService
  private transient RegionCityService regionCityService;

  /**
   * List of cities.
   */
  @Getter
  private List<RegionCity> cityList;


  /**
   * List of contact.
   */
  @Getter
  @Setter
  @ChildResource
  @Named("citiesList")
  private List<EntertainerCityModel> entertainerCityModelList;

  /**
   * Post construction.
   */
  @PostConstruct
  public void init() {

    this.cityList =
        Optional.ofNullable(regionCityService.getCities(Constants.DEFAULT_LOCALE)).orElse(
                Collections.emptyList()).stream().sorted(Comparator.comparing(RegionCity::getName))
            .collect(Collectors.toList());

  }
}
