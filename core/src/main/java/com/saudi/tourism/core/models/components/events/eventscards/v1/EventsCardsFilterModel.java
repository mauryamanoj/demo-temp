package com.saudi.tourism.core.models.components.events.eventscards.v1;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.Calendar;

/** Events Cards Filter Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class EventsCardsFilterModel {
  /**
   * Period Filter: this-month.
   */
  @Expose
  @ValueMapValue
  private String period;

  /**
   * Destination Filter: Path of a destination CF.
   */
  @Expose
  @ValueMapValue
  private String destination;

  /**
   * Season Filter: Path of a season CF.
   */
  @Expose
  @ValueMapValue
  private String season;

  /**
   * Start Date Filter: Start date of event filter.
   */
  @Expose
  @ValueMapValue
  private Calendar startDate;

  /**
   * End Date Filter: End date of event filter.
   */
  @Expose
  @ValueMapValue
  private Calendar endDate;



}
