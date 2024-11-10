package com.saudi.tourism.core.models.components.brands;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The OtherLocation Brands.
*/
@Model(adaptables = Resource.class,
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Location implements Serializable {

  /**
   * The LocationName.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String locationName;


  /**
   * The Coordinate.
   */
  @Getter
  @ChildResource
  @Expose
  private Coordinates coordinates;

  /**
   * Distance from user location.
   */
  private double distance;

}
