package com.saudi.tourism.core.models.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Calendar App Model.
 */
@Data
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CalendarAppModel {
  /**
   * weekHeading.
   */
  @ValueMapValue
  private String weekHeading;
  /**
   * monthHeading.
   */
  @ValueMapValue
  private String monthHeading;
  /**
   * upComingMonthsHeading.
   */
  @ValueMapValue
  private String upComingMonthsHeading;

}
