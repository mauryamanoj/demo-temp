package com.saudi.tourism.core.services.mobile.v1.calendar;

import com.saudi.tourism.core.models.components.contentfragment.holiday.HolidayCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import lombok.Builder;
import lombok.Data;

import java.util.Calendar;
import java.util.Objects;

/** Calendar Item Model. */
@Data
@Builder
public class CalendarItem {
  /** Public Holiday Start Date. */
  private Calendar startDate;

  /** Public Holiday End Date. */
  private Calendar endDate;

  /**
   * method that build season calendar item from CF.
   *
   * @param model
   * @return season calendar item.
   */
  public static CalendarItem fromSeasonCF(final SeasonCFModel model) {
    if (Objects.isNull(model.getStartDate()) && Objects.isNull(model.getEndDate())) {
      return null;
    }

    return CalendarItem.builder()
        .startDate(model.getStartDate())
        .endDate(model.getEndDate())
        .build();
  }

  /**
   * method that build holiday calendar item from CF.
   *
   * @param model
   * @return holiday calendar item.
   */
  public static CalendarItem fromHolidayCF(final HolidayCFModel model) {
    if (Objects.isNull(model.getStartDate()) && Objects.isNull(model.getEndDate())) {
      return null;
    }

    return CalendarItem.builder()
        .startDate(model.getStartDate())
        .endDate(model.getEndDate())
        .build();
  }
}
