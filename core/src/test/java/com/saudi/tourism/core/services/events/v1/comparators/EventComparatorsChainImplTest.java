package com.saudi.tourism.core.services.events.v1.comparators;


import com.saudi.tourism.core.services.events.v1.Event;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EventComparatorsChainImplTest {

  final SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);

  private EventComparatorsChainImpl eventComparatorsChain = new EventComparatorsChainImpl();

  @Test
  public void buildComparatorShouldHandleUnkownSortKey() throws ParseException {
    // Arrange
    eventComparatorsChain.setComparators(Arrays.asList(new StartDateEventComparator(), new EndDateEventComparator(), new PeriodEventComparator()));

    final var eventStartDate1 = Calendar.getInstance();
    eventStartDate1.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var event1 = Event.builder()
      .title("Event 1")
      .startDate(eventStartDate1)
      .build();

    final var eventStartDate2 = Calendar.getInstance();
    eventStartDate2.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var event2 = Event.builder()
      .title("Event 2")
      .startDate(eventStartDate2)
      .build();

    final var eventStartDate3 = Calendar.getInstance();
    eventStartDate3.setTime(aemDateFormat.parse("2023-11-06T00:00:00.000+00:00"));
    final var event3 = Event.builder()
      .title("Event 3")
      .startDate(eventStartDate3)
      .build();

    final var request = FetchEventsRequest.builder()
      .sortBy(Arrays.asList("unkown"))
      .build();

    //Act
    final var ordredEvents= Arrays.asList(event1, event2, event3).stream()
      .sorted(eventComparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Event 3", ordredEvents.get(0).getTitle());
    assertEquals("Event 2", ordredEvents.get(1).getTitle());
    assertEquals("Event 1", ordredEvents.get(2).getTitle());
  }

  @Test
  public void buildComparatorShouldHandleStartDateSort() throws ParseException {
    // Arrange
    eventComparatorsChain.setComparators(Arrays.asList(new StartDateEventComparator(), new EndDateEventComparator(), new PeriodEventComparator()));

    final var eventStartDate1 = Calendar.getInstance();
    eventStartDate1.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var event1 = Event.builder()
      .title("Event 1")
      .startDate(eventStartDate1)
      .build();

    final var eventStartDate2 = Calendar.getInstance();
    eventStartDate2.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var event2 = Event.builder()
      .title("Event 2")
      .startDate(eventStartDate2)
      .build();

    final var eventStartDate3 = Calendar.getInstance();
    eventStartDate3.setTime(aemDateFormat.parse("2023-11-06T00:00:00.000+00:00"));
    final var event3 = Event.builder()
      .title("Event 3")
      .startDate(eventStartDate3)
      .build();

    final var request = FetchEventsRequest.builder()
      .sortBy(Arrays.asList("startDate"))
      .build();

    //Act
    final var ordredEvents= Arrays.asList(event1, event2, event3).stream()
      .sorted(eventComparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Event 3", ordredEvents.get(0).getTitle());
    assertEquals("Event 2", ordredEvents.get(1).getTitle());
    assertEquals("Event 1", ordredEvents.get(2).getTitle());
  }

  @Test
  public void buildComparatorShouldHandleEndDateSort() throws ParseException {
    // Arrange
    eventComparatorsChain.setComparators(Arrays.asList(new StartDateEventComparator(), new EndDateEventComparator(), new PeriodEventComparator()));

    final var eventStartDate1 = Calendar.getInstance();
    eventStartDate1.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var event1 = Event.builder()
      .title("Event 1")
      .startDate(eventStartDate1)
      .build();

    final var eventStartDate2 = Calendar.getInstance();
    eventStartDate2.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var eventEndDate2 = Calendar.getInstance();
    eventEndDate2.setTime(aemDateFormat.parse("2023-11-11T00:00:00.000+00:00"));
    final var event2 = Event.builder()
      .title("Event 2")
      .startDate(eventStartDate2)
      .endDate(eventEndDate2)
      .build();

    final var eventStartDate3 = Calendar.getInstance();
    eventStartDate3.setTime(aemDateFormat.parse("2023-11-06T00:00:00.000+00:00"));
    final var eventEndDate3 = Calendar.getInstance();
    eventEndDate3.setTime(aemDateFormat.parse("2023-11-12T00:00:00.000+00:00"));
    final var event3 = Event.builder()
      .title("Event 3")
      .startDate(eventStartDate3)
      .endDate(eventEndDate3)
      .build();

    final var request = FetchEventsRequest.builder()
      .sortBy(Arrays.asList("endDate", "startDate"))
      .build();

    //Act
    final var ordredEvents= Arrays.asList(event1, event2, event3).stream()
      .sorted(eventComparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Event 1", ordredEvents.get(0).getTitle());
    assertEquals("Event 2", ordredEvents.get(1).getTitle());
    assertEquals("Event 3", ordredEvents.get(2).getTitle());
  }

  @Test
  public void buildComparatorShouldHandlePeriodSort() throws ParseException {
    // Arrange
    eventComparatorsChain.setComparators(Arrays.asList(new StartDateEventComparator(), new EndDateEventComparator(), new PeriodEventComparator()));

    final var eventStartDate1 = Calendar.getInstance();
    eventStartDate1.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var eventEndDate1 = Calendar.getInstance();
    eventEndDate1.setTime(aemDateFormat.parse("2023-11-10T00:00:00.000+00:00"));
    final var event1 = Event.builder()
      .title("Event 1")
      .startDate(eventStartDate1)
      .endDate(eventEndDate1)
      .build();

    final var eventStartDate2 = Calendar.getInstance();
    eventStartDate2.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var eventEndDate2 = Calendar.getInstance();
    eventEndDate2.setTime(aemDateFormat.parse("2023-11-11T00:00:00.000+00:00"));
    final var event2 = Event.builder()
      .title("Event 2")
      .startDate(eventStartDate2)
      .endDate(eventEndDate2)
      .build();

    final var eventStartDate3 = Calendar.getInstance();
    eventStartDate3.setTime(aemDateFormat.parse("2023-11-06T00:00:00.000+00:00"));
    final var event3 = Event.builder()
      .title("Event 3")
      .startDate(eventStartDate3)
      .build();

    final var request =
        FetchEventsRequest.builder()
            .sortBy(Arrays.asList("period", "endDate", "startDate"))
            .build();

    //Act
    final var ordredEvents= Arrays.asList(event1, event2, event3).stream()
      .sorted(eventComparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Event 3", ordredEvents.get(0).getTitle());
    assertEquals("Event 1", ordredEvents.get(1).getTitle());
    assertEquals("Event 2", ordredEvents.get(2).getTitle());
  }

  @Test
  public void buildComparatorShouldHandlePublishDateSort() throws ParseException {
    // Arrange
    eventComparatorsChain.setComparators(Arrays.asList(new StartDateEventComparator(), new EndDateEventComparator(), new PeriodEventComparator()));

    final var eventStartDate1 = Calendar.getInstance();
    eventStartDate1.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var eventEndDate1 = Calendar.getInstance();
    eventEndDate1.setTime(aemDateFormat.parse("2023-11-10T00:00:00.000+00:00"));
    final var eventPublishDate1 = Calendar.getInstance();
    eventEndDate1.setTime(aemDateFormat.parse("2023-12-15T00:00:00.000+00:00"));

    final var event1 = Event.builder()
      .title("Event 1")
      .startDate(eventStartDate1)
      .endDate(eventEndDate1)
      .publishedDate(eventPublishDate1)
      .build();

    final var eventStartDate2 = Calendar.getInstance();
    eventStartDate2.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var eventEndDate2 = Calendar.getInstance();
    eventEndDate2.setTime(aemDateFormat.parse("2023-11-11T00:00:00.000+00:00"));
    final var eventPublishDate2 = Calendar.getInstance();
    eventEndDate1.setTime(aemDateFormat.parse("2023-12-14T00:00:00.000+00:00"));
    final var event2 = Event.builder()
      .title("Event 2")
      .startDate(eventStartDate2)
      .endDate(eventEndDate2)
      .publishedDate(eventPublishDate2)
      .build();

    final var eventStartDate3 = Calendar.getInstance();
    eventStartDate3.setTime(aemDateFormat.parse("2023-11-06T00:00:00.000+00:00"));
    final var eventEndDate3 = Calendar.getInstance();
    eventEndDate3.setTime(aemDateFormat.parse("2023-11-12T00:00:00.000+00:00"));
    final var eventPublishDate3 = Calendar.getInstance();
    eventEndDate1.setTime(aemDateFormat.parse("2023-12-10T00:00:00.000+00:00"));
    final var event3 = Event.builder()
      .title("Event 3")
      .startDate(eventStartDate3)
      .endDate(eventEndDate3)
      .publishedDate(eventPublishDate3)
      .build();

    final var request =
      FetchEventsRequest.builder()
        .sortBy(Arrays.asList("period", "endDate", "startDate"))
        .build();

    //Act
    final var ordredEvents= Arrays.asList(event1, event2, event3).stream()
      .sorted(eventComparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Event 2", ordredEvents.get(0).getTitle());
    assertEquals("Event 3", ordredEvents.get(1).getTitle());
    assertEquals("Event 1", ordredEvents.get(2).getTitle());
  }
}