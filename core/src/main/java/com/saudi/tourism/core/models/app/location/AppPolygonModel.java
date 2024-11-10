package com.saudi.tourism.core.models.app.location;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;
/**
 * App Polygon.
 */
@Model(adaptables = Resource.class,
    resourceType = AppPolygonModel.VAR_PATH,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
/**
 * Instantiates a new app polygon.
 */
@Data
@Slf4j
public class AppPolygonModel {

  /**
   * VAR_PATH.
   */
  public static final String VAR_PATH = "sauditourism/components/structure/app-polygon-page";
  /**
   * polygon.
   */
  @ChildResource
  private List<PolygonAreaModel> polygon;

}
