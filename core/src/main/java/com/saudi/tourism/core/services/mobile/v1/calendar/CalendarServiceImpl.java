package com.saudi.tourism.core.services.mobile.v1.calendar;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.holidays.v1.HolidaysCFService;
import com.saudi.tourism.core.services.mobile.v1.eventitem.EventItemService;
import com.saudi.tourism.core.services.seasons.v1.SeasonsCFService;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/** Calendar Service. */
@Component(service = CalendarService.class, immediate = true)
public class CalendarServiceImpl implements CalendarService {

  /** Saudi Tourism Config. */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference private UserService userService;

  /** Seasons CF Service. */
  @Reference private SeasonsCFService seasonsCFService;

  /** Holidays CF Service. */
  @Reference private HolidaysCFService holidaysCFService;

  /** All Events Service. */
  @Reference private EventItemService eventItemService;

  @Override
  public CalendarModel getCalendar(@NonNull FetchCalendarRequest request) {

    final var allEvents = eventItemService.fetchAllEventItems(request.getLocale());
    var event = Collections.EMPTY_LIST;
    if (CollectionUtils.isNotEmpty(allEvents)) {
      event =
          allEvents.stream()
              .filter(e -> this.isBetweenStartEndDates(request, e.getStartDate(), e.getEndDate()))
              .collect(Collectors.toList());
    }

    final var allSeasons = seasonsCFService.fetchAllSeasons(request.getLocale());
    var seasons = Collections.EMPTY_LIST;
    if (CollectionUtils.isNotEmpty(allSeasons)) {
      seasons =
          allSeasons.stream()
              .filter(Objects::nonNull)
              .map(CalendarItem::fromSeasonCF)
              .filter(Objects::nonNull)
              .filter(s -> this.isBetweenStartEndDates(request, s.getStartDate(), s.getEndDate()))
              .collect(Collectors.toList());
    }

    final var allHolidays = holidaysCFService.fetchAllHolidays(request.getLocale());
    var publicHolidays = Collections.EMPTY_LIST;
    if (CollectionUtils.isNotEmpty(allHolidays)) {
      publicHolidays =
          allHolidays.stream()
              .filter(Objects::nonNull)
              .map(CalendarItem::fromHolidayCF)
              .filter(Objects::nonNull)
              .filter(h -> this.isBetweenStartEndDates(request, h.getStartDate(), h.getEndDate()))
              .collect(Collectors.toList());
    }

    return CalendarModel.builder()
        .season(seasons)
        .publicHoliday(publicHolidays)
        .event(event)
        .build();
  }

  @Override
  public List<CalendarBottomCards> getCalendarBottomCards(@NonNull FetchCalendarRequest request) {
    var filteredSeasons = Collections.EMPTY_LIST;
    var filteredPublicHolidays = Collections.EMPTY_LIST;
    long count = Constants.ZERO;

    final var allSeasons = seasonsCFService.fetchAllSeasons(request.getLocale());
    if (CollectionUtils.isNotEmpty(allSeasons)) {
      filteredSeasons =
          allSeasons.stream()
              .filter(Objects::nonNull)
              .map(CalendarBottomCards::fromSeasonCF)
              .filter(Objects::nonNull)
              .filter(s -> this.isBetweenStartEndDates(request, s.getStartDate(), s.getEndDate()))
              .collect(Collectors.toList());
    }

    final var allHolidays = holidaysCFService.fetchAllHolidays(request.getLocale());
    if (CollectionUtils.isNotEmpty(allHolidays)) {
      filteredPublicHolidays =
          allHolidays.stream()
              .filter(Objects::nonNull)
              .map(CalendarBottomCards::fromHolidayCF)
              .filter(Objects::nonNull)
              .filter(h -> this.isBetweenStartEndDates(request, h.getStartDate(), h.getEndDate()))
              .collect(Collectors.toList());
    }

    final var allEvents = eventItemService.fetchAllEventItems(request.getLocale());
    if (CollectionUtils.isNotEmpty(allEvents)) {
      count = allEvents.stream()
                .filter(Objects::nonNull)
                .filter(e -> this.isBetweenStartEndDates(request, e.getStartDate(), e.getEndDate()))
                .count();
    }

    var eventCard = eventItemService.getEventCard(request);

    List<CalendarBottomCards> cards = new ArrayList<>();
    cards.addAll(filteredSeasons);
    cards.addAll(filteredPublicHolidays);

    if (Objects.nonNull(eventCard)) {
      eventCard.setEventsCount(count);
      cards.add(eventCard);
    }


    return cards;
  }

  public Boolean isBetweenStartEndDates(
      final @NonNull FetchCalendarRequest request,
      final Calendar startDate,
      final Calendar endDate) {

    if (StringUtils.isBlank(request.getStartDate()) && StringUtils.isBlank(request.getEndDate())) {
      return Boolean.TRUE;
    }

    try {
      final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      Date requestStartDate = null;
      Date requestEndDate = null;
      if (StringUtils.isNotBlank(request.getStartDate())) {
        requestStartDate = dateFormatter.parse(request.getStartDate());
      }

      if (StringUtils.isNotBlank(request.getEndDate())) {
        requestEndDate = dateFormatter.parse(request.getEndDate());
      }

      if (Objects.isNull(startDate) && Objects.isNull(endDate)) {
        return Boolean.FALSE;
      }

      if (Objects.isNull(requestEndDate) && Objects.nonNull(endDate)) {
        return requestStartDate.compareTo(endDate.getTime()) <= 0;
      }

      if (Objects.isNull(requestStartDate)
          && Objects.nonNull(requestEndDate)
          && Objects.nonNull(startDate)) {
        return requestEndDate.compareTo(startDate.getTime()) >= 0;
      }

      return endDate.getTime().compareTo(requestStartDate) >= 0
          && startDate.getTime().compareTo(requestEndDate) <= 0;
    } catch (ParseException e) {
      LOGGER.error("Error parsing start date in request", e);
      return Boolean.FALSE;
    }
  }
}
