package com.saudi.tourism.core.models.components.brands;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Brand Partners coordinates.
 */

@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Coordinates implements Serializable {

  /**
   * The Latitude.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String latitude;

  /**
   * The longitude.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String longitude;

}
