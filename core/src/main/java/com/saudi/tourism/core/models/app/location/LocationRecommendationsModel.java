package com.saudi.tourism.core.models.app.location;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class LocationRecommendationsModel implements Serializable {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * All locations for this page.
   */
  @ChildResource
  private List<LocationRecommendationModel> items;

}
