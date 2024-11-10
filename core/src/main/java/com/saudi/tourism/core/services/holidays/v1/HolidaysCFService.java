package com.saudi.tourism.core.services.holidays.v1;

import com.saudi.tourism.core.models.components.contentfragment.holiday.HolidayCFModel;

import java.util.List;

/** Holidays CF service to fetch all holidays cfs. */
public interface HolidaysCFService {
  /**
   * Fetch all Holiday CFs authored for a locale.
   *
   * @param locale Current locale
   * @return list of Holiday CF
   */
  List<HolidayCFModel> fetchAllHolidays(String locale);
}
