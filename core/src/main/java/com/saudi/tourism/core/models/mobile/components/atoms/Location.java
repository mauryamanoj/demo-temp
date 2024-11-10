package com.saudi.tourism.core.models.mobile.components.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Location {

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * lat.
   */
  @ValueMapValue
  private String lat;

  /**
   * lng.
   */
  @ValueMapValue
  private String lng;

  /**
   * destination.
   */
  @ChildResource
  private Destination destination;

  /**
   * destination.
   */
  @ValueMapValue
  @JsonIgnore
  private String destinationCFPath;

  /**
   * allDestinations.
   */
  @ValueMapValue
  private Boolean allDestinations;

}
