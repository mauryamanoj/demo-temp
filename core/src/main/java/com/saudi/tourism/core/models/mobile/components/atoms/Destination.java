package com.saudi.tourism.core.models.mobile.components.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

/**
 * Location destination model.
 */
@Builder
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Destination {

  /**
   * id.
   */
  private String id;

  /**
   * weather City Id.
   */
  @JsonIgnore
  private String weatherCityId;

  /**
   * destination Title.
   */
  private String title;

  /**
   * destination Lat.
   */
  private String lat;

  /**
   * destination Lng.
   */
  private String lng;
}
