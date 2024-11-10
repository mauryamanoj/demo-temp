package com.saudi.tourism.core.models.components.tripplan;

import com.saudi.tourism.core.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.List;

/**
 * Class stores all parameters for searching / filtering trip plans.
 */
@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
@Slf4j
public class CreateTripPlanFilter implements PathLocaleFilter {

  /**
   * Flag that trip plan needs to be produced with activities / events.
   */
  private Boolean withData;

  /**
   * Title for the new trip plan.
   */
  private String title;

  /**
   * Requesting a Trip Plan by trip itinerary page path.
   */
  private String path;

  /**
   * Language parameter.
   */
  private String locale;
  /**
   * Cities list.
   */
  private List<String> city;

  /**
   * Trip Plan start date.
   */
  private Calendar startDate;

  /**
   * Trip Plan end date.
   */
  private Calendar endDate;

  @Override
  public String toString() {
    try {
      return "{" + "withData=" + withData + ", title='" + title + '\'' + ", path='" + path + '\''
          + ", locale='" + locale + '\'' + ", city=" + city + ", startDate=" + CommonUtils
          .dateToString(startDate) + ", endDate=" + CommonUtils.dateToString(endDate) + '}';
    } catch (Exception e) {
      LOGGER.error("Could not reformat date, {} or {}", startDate, endDate, e);
      return "{" + "withData=" + withData + ", title='" + title + '\'' + ", path='" + path + '\''
          + ", locale='" + locale + '\'' + ", city=" + city + ", startDate=" + startDate
          + ", endDate=" + endDate + '}';
    }
  }
}
