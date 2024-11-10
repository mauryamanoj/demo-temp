package com.saudi.tourism.core.services.mobile.v1.calendar;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

import java.util.List;

/** Calendar. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CalendarModel {

  /** Public Holidays Calendar Item. */
  private List<CalendarItem> publicHoliday;

  /** Seasons Calendar Item. */
  private List<CalendarItem> season;

  /** Events Calendar Item. */
  private List<CalendarItem> event;
}
