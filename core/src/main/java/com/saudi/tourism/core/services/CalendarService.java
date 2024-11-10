package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.common.Holidays;
import com.saudi.tourism.core.models.components.tripplan.StartEndDatesFilter;

import java.time.LocalDate;

/**
 * Service to work with important dates (holidays etc.).
 */
public interface CalendarService {

  /**
   * Returns list of configured holidays for the specified locale.
   *
   * @param language current locale
   * @return holidays map instance
   */
  Holidays getConfiguredHolidaysMap(String language);

  /**
   * Search configured holiday for the specified LocalDate date and locale.
   *
   * @param date     date to search a holiday
   * @param language the current locale
   * @return name of holiday
   */
  String getHolidayNameByDate(LocalDate date, String language);

  /**
   * Search configured holiday for the specified date (String) date and locale.
   *
   * @param date     date to search a holiday
   * @param language the current locale
   * @return name of holiday
   */
  String getHolidayNameByDate(String date, String language);

  /**
   * Get list of all holidays and filter it according to the provided obiect.
   *
   * @param filter start / end dates etc. filter
   * @return filtered holidays map
   */
  Holidays getConfiguredHolidaysMap(StartEndDatesFilter filter);
}
