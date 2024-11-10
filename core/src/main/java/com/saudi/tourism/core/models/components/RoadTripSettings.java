package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

/**
 * The type Admin page road Trip settings.
 */
@Model(adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class RoadTripSettings {
  /**
   * The filters.
   */
  @ChildResource
  @Expose
  private List<RoadTripFilterModel> filters;

}
