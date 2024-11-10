package com.sta.core.vendors.models;

import lombok.Getter;
import lombok.Setter;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Package entry itinerary details model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class PackageEntryItinerary {

  /**
   * Title.
   */
  @ValueMapValue
  private String id;
  /**
   * Title.
   */
  @ValueMapValue
  private String description_ar;

  /**
   * Image.
   */
  @ValueMapValue
  private ImageEntry image;

  /**
   * Description.
   */
  @ValueMapValue
  private String description;

}
