package com.saudi.tourism.core.models.components.events.eventscards.v1;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/** Events Cards Hand Pick Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class EventsCardsHandPickModel {

  /**
   * Events.
   */
  @ValueMapValue
  @Expose
  @SerializedName("cfPaths")
  private List<String> eventCFPaths;
}
