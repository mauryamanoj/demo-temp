package com.saudi.tourism.core.models.components.greenTaxi;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Green Taxi Card Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class GreenTaxiCard {

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * city.
   */
  @ValueMapValue
  private String city;

  /**
   * date.
   */
  @ValueMapValue
  private String date;

  /**
   * image.
   */
  @ValueMapValue
  private String image;

}

