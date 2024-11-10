package com.saudi.tourism.core.services.mobile.v1.calendar;

import com.saudi.tourism.core.models.components.contentfragment.holiday.HolidayCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.services.holidays.v1.HolidaysCFService;
import com.saudi.tourism.core.services.mobile.v1.eventitem.EventItemService;
import com.saudi.tourism.core.services.seasons.v1.SeasonsCFService;
import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalendarServiceImplTest {
  @InjectMocks
  private CalendarServiceImpl calendarService;
  @Mock
  private EventItemService allEventsService;
  @Mock
  private SeasonsCFService seasonsService;
  @Mock
  private HolidaysCFService holidaysService;

  @Test
  void testGetCalendar() throws ParseException {

    // Arrange
    FetchCalendarRequest request = FetchCalendarRequest.builder()
      .locale("en")
      .build();

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var s1 = Calendar.getInstance();
    s1.setTime(dateFormatter.parse("2024-02-12T00:00:00.000Z"));
    final var s2 = Calendar.getInstance();
    s2.setTime(dateFormatter.parse("2024-02-25T00:00:00.000Z"));
    final var e1 = Calendar.getInstance();
    e1.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));
    final var e2 = Calendar.getInstance();
    e2.setTime(dateFormatter.parse("2024-02-27T00:00:00.000Z"));

    when(seasonsService.fetchAllSeasons(request.getLocale()))
        .thenReturn(List.of(new SeasonCFModel(s1, e1), new SeasonCFModel(s2, e2)));
    when(holidaysService.fetchAllHolidays(request.getLocale()))
        .thenReturn(List.of(new HolidayCFModel(s1, e1), new HolidayCFModel(s2, e2)));

    // Act
    CalendarModel result = calendarService.getCalendar(request);

    // Assert
    assertNotNull(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnTrueForBothRequestDateNull() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate(null)
      .endDate(null)
      .build();
    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-01T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));

    // Act
    boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertTrue(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnFalseWhenCatchingException() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate("some text")
      .endDate("some text")
      .build();
    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-01T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));

    // Act
    boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertFalse(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnFalseWhenCalendarDatesAreNull() {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate("2024-01-01")
      .endDate("2024-01-31")
      .build();

    // Act
    Boolean result = calendarService.isBetweenStartEndDates(request, null, null);

    // Assert
    assertFalse(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnTrueWhenCalendarDatesAreBetweenStartEnd() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate("2024-01-01")
      .endDate("2024-01-31")
      .build();

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-15T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-20T00:00:00.000Z"));

    // Act
    Boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertTrue(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnTrueWhenCalendarDatesContainsStartEnd() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate("2024-01-15")
      .endDate("2024-01-20")
      .build();

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-01T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));

    // Act
    Boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertTrue(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnTrueWhenRequestStartDateIsNull() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate(null)
      .endDate("2024-01-20")
      .build();

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-01T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));

    // Act
    Boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertTrue(result);
  }

  @Test
  void isBetweenStartEndDatesShouldReturnTrueWhenRequestEndDateIsNull() throws ParseException {
    // Arrange
    final var request = FetchCalendarRequest.builder()
      .startDate("2023-11-23")
      .endDate(null)
      .build();

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
    final var startDate = Calendar.getInstance();
    startDate.setTime(dateFormatter.parse("2024-01-01T00:00:00.000Z"));
    final var endDate = Calendar.getInstance();
    endDate.setTime(dateFormatter.parse("2024-01-31T00:00:00.000Z"));

    // Act
    Boolean result = calendarService.isBetweenStartEndDates(request, startDate, endDate);

    // Assert
    assertTrue(result);
  }
}
