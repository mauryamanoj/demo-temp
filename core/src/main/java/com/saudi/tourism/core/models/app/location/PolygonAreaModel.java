package com.saudi.tourism.core.models.app.location;


import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Named;
import java.util.List;

/**
 * Polygon Area.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class PolygonAreaModel {
  /**
   * Area Name.
   */
  @ValueMapValue
  @Named("areaName")
  private String area;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * coordinates.
   */
  @ChildResource
  private List<PolygonCoordinatesModel> coordinates;

}
