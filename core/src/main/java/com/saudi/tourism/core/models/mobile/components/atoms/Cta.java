package com.saudi.tourism.core.models.mobile.components.atoms;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Cta {

  /**
   * type.
   */
  @ValueMapValue
  private String type;

  /**
   * url.
   */
  @ValueMapValue
  private String url;

  /**
   * filter.
   */
  @ChildResource
  private Filter filter;

}
