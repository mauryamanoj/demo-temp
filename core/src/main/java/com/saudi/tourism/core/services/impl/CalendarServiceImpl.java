package com.saudi.tourism.core.services.impl;


import lombok.Generated;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Set;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.Holidays;
import com.saudi.tourism.core.models.components.tripplan.StartEndDatesFilter;
import com.saudi.tourism.core.services.CalendarService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.services.impl.CalendarServiceImpl.CALENDAR_SERVICE_DESCRIPTION;

/**
 * Implementation of the CalendarService, is used to work with important dates, e.g. holidays.
 */
@Component(service = CalendarService.class,
           immediate = true,
           property = {
               Constants.SERVICE_DESCRIPTION + Constants.EQUAL + CALENDAR_SERVICE_DESCRIPTION})
@Slf4j
public class CalendarServiceImpl implements CalendarService {

  /**
   * This Service description for OSGi.
   */
  static final String CALENDAR_SERVICE_DESCRIPTION = "Calendar / Holidays service";

  /**
   * Global in-memory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * The service to provide read-only resource resolver.
   */
  @Reference
  private UserService resolverProvider;

  @Override
  public Holidays getConfiguredHolidaysMap(final String language) {
    final String memCacheKey = Constants.KEY_PREFIX_ADMIN_HOLIDAYS + language;

    Holidays holidaysMap = (Holidays) memCache.get(memCacheKey);
    if (holidaysMap != null) {
      return holidaysMap;
    }

    // Get trip plans list using query if couldn't find it in the cache
    holidaysMap = queryHolidaysMap(language);

    if (!holidaysMap.isEmpty()) {
      memCache.add(memCacheKey, holidaysMap);
    }

    return holidaysMap;
  }

  /**
   * Get holidays from crx by static path related to the locale specified.
   *
   * @param language current locale
   * @return holidays map instance, empty if any errors.
   */
  Holidays queryHolidaysMap(final String language) {
    String path = StringUtils
        .replaceEach(Constants.ADMIN_HOLIDAYS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
            new String[] {language});

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      Resource resource = resolver.getResource(path);
      if (resource == null) {
        LOGGER.warn("Can't find admin holidays resource by path {}. Check that holidays are "
            + "configured correctly.", path);
        // Return empty list
        return new Holidays();
      }

      final Holidays holidays = resource.adaptTo(Holidays.class);
      if (holidays == null) {
        LOGGER.error("Can't adapt admin holidays resource {}", path);
        // Return empty list
        return new Holidays();
      }

      return holidays;
    }
  }

  @Generated
  @Override
  public String getHolidayNameByDate(final LocalDate date, final String language) {
    return getHolidayNameByDate(String.valueOf(date), language);
  }

  @Override
  public String getHolidayNameByDate(final String date, final String language) {
    final Holidays holidays = getConfiguredHolidaysMap(language);
    if (holidays.isEmpty()) {
      return null;
    }

    return holidays.get(date);
  }

  @Override
  public Holidays getConfiguredHolidaysMap(final StartEndDatesFilter filter) {
    final Holidays allHolidays = getConfiguredHolidaysMap(filter.getLocale());
    if (allHolidays.isEmpty()) {
      return allHolidays;
    }

    final Calendar startDateCal = filter.getStartDate();
    final String startDate = CommonUtils.dateToString(startDateCal, Constants.FORMAT_DATE);
    final Calendar endDateCal = filter.getEndDate();
    final String endDate = CommonUtils.dateToString(endDateCal, Constants.FORMAT_DATE);

    final Holidays filteredList = new Holidays();

    final Set<String> dates = allHolidays.keySet();
    for (String date : dates) {
      // Note: 0 eq, < 0 first is less, > 0 first is greater
      final int compareStart = StringUtils.compare(startDate, date, true);
      final int compareEnd = StringUtils.compare(endDate, date, true);
      if ((compareStart == 0 || compareStart < 0) && (compareEnd == 0 || compareEnd > 0)) {
        filteredList.put(date, allHolidays.get(date));
      }
    }

    return filteredList;
  }
}
