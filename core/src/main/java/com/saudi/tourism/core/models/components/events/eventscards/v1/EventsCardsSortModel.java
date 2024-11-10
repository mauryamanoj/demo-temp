package com.saudi.tourism.core.models.components.events.eventscards.v1;

import java.util.List;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Events Cards Sort Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EventsCardsSortModel {
  /**
   * Sort : sort by property.
   */
  @Expose
  @ValueMapValue
  private List<String> sortBy;
}
