package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Calendar;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Date {

  /**
   * start Date.
   */
  @ValueMapValue
  private Calendar startDate;

  /**
   * end Date.
   */
  @ValueMapValue
  private Calendar endDate;

  /**
   * show.
   */
  @ValueMapValue
  private Boolean show;

  /**
   * duration.
   */
  @ChildResource
  private Duration duration;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Duration {

    /**
     * title.
     */
    @ValueMapValue
    private String title;

    /**
     * value.
     */
    @ValueMapValue
    private String value;
  }
}
