package com.saudi.tourism.core.models.mobile.components.atoms;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AutoItemFilter {
  /**
   * Sort by.
   */
  @ChildResource
  @JsonIgnore
  private SortBy sortBy;
  /**
   * Period.
   */
  @ChildResource
  @JsonIgnore
  private Period period;
  /**
   * Filter.
   */
  @ChildResource
  @JsonIgnore
  private Filter filter;

  /**
   * Experience.
   */
  @ChildResource
  @JsonIgnore
  private Experience experience;
}
