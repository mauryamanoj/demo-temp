package com.saudi.tourism.core.models.app.location;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Polygon Coordinates.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PolygonCoordinatesModel {
  /**
   * latitude.
   */
  @ValueMapValue
  private String latitude;
  /**
   * longitude.
   */
  @ValueMapValue
  private String longitude;
  /**
   * lat.
   */
  @Getter
  @Setter
  private double lat;
  /**
   * lng.
   */
  @Getter
  @Setter
  private double lng;

  /**
   * post construct method.
   */
  @PostConstruct
  protected void init() {
    if (null != latitude) {
      lat = Double.parseDouble(latitude);
    }
    if (null != longitude) {
      lng = Double.parseDouble(longitude);
    }
  }

}
