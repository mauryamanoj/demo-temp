package com.saudi.tourism.core.services.mobile.v1.calendar;

import com.saudi.tourism.core.models.components.contentfragment.holiday.HolidayCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Objects;

/**
 * Calendar Bottom Cards Model.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CalendarBottomCards {
  /**
   * Type.
   */
  private String type;
  /**
   * Title.
   */
  private String title;
  /**
   * Start Date.
   */
  private Calendar startDate;
  /**
   * End Date.
   */
  private Calendar endDate;
  /**
   * Redirection ID.
   */
  private String redirectionId;
  /**
   * Events Count.
   */
  private Long eventsCount;

  /**
   * method that build season card from CF.
   *
   * @param model
   * @return season card.
   */
  public static CalendarBottomCards fromSeasonCF(final SeasonCFModel model) {
    if (Objects.isNull(model.getStartDate()) && Objects.isNull(model.getEndDate())) {
      return null;
    }

    return CalendarBottomCards.builder()
        .type(model.TYPE)
        .title(model.getTitle())
        .startDate(model.getStartDate())
        .endDate(model.getEndDate())
        .redirectionId(MobileUtils.extractItemId(model.getItemId()))
        .build();
  }

  /**
   * method that build holiday card from CF.
   *
   * @param model
   * @return holiday card.
   */
  public static CalendarBottomCards fromHolidayCF(final HolidayCFModel model) {
    if (Objects.isNull(model.getStartDate()) && Objects.isNull(model.getEndDate())) {
      return null;
    }

    return CalendarBottomCards.builder()
        .type(model.TYPE)
        .title(model.getTitle())
        .startDate(model.getStartDate())
        .endDate(model.getEndDate())
        .redirectionId(MobileUtils.extractItemId(model.getItemId()))
        .build();
  }
}
