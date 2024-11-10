package com.saudi.tourism.core.models.components.stories.v1;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Latest Story Sort Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class LatestStoriesSortModel {
  /**
   * Sort : sort by property.
   */
  @Expose
  @ValueMapValue
  private String sortBy;
}
