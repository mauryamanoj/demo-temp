package com.saudi.tourism.core.models.mobile.components.autosections.utils;

import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.OpeningHoursValue;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SectionsMapperTest {

  @Test
  void showOpeningHoursStatusShouldReturnFalseWhenTimingNotAvailable() {
    InformationListWidgetCFModel model = InformationListWidgetCFModel.builder()
      .startDate("2024-02-03")
      .endDate("2024-03-16")
      .openingHoursValue(Collections.emptyList())
      .build();

    assertFalse(SectionsMapper.showOpeningHoursStatus(model));
  }

  @Test
  void showOpeningHoursStatusShouldReturnFalseWhenTheEventIsExpired() {
    List<OpeningHoursValue> timings = new ArrayList<>();
    timings.add(new OpeningHoursValue("MON","", ""));

    InformationListWidgetCFModel model = InformationListWidgetCFModel.builder()
      .startDate("2024-02-03")
      .endDate("2024-03-16")
      .openingHoursValue(timings)
      .build();

    assertFalse(SectionsMapper.showOpeningHoursStatus(model));
  }

  @Test
  void showOpeningHoursStatusShouldReturnFalseWhenTheEventIsUpcoming() {
    List<OpeningHoursValue> timings = new ArrayList<>();
    timings.add(new OpeningHoursValue("MON","", ""));

    InformationListWidgetCFModel model = InformationListWidgetCFModel.builder()
        .startDate("2024-02-03")
        .endDate("2024-03-16")
        .openingHoursValue(timings)
        .build();

    LocalDate dateBeforeEvent = LocalDate.of(2024,1,1);


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(dateBeforeEvent);


      assertFalse(SectionsMapper.showOpeningHoursStatus(model));

    }
  }

  @Test
  void showOpeningHoursStatusShouldReturnTrueInAllOtherCases() {
    List<OpeningHoursValue> timings = new ArrayList<>();
    timings.add(new OpeningHoursValue("MON","", ""));

    InformationListWidgetCFModel model = InformationListWidgetCFModel.builder()
      .startDate("2024-02-03")
      .endDate("2024-03-29")
      .openingHoursValue(timings)
      .build();

    LocalDate dateBeforeEvent = LocalDate.of(2024,2,4);


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(dateBeforeEvent);


      assertTrue(SectionsMapper.showOpeningHoursStatus(model));

    }




  }

  @Test
  void isOpenNowShouldReturnTrueWhenSameTimeAccrossWeekAndCurrentTimeBetween() {



    LocalDate today = LocalDate.now();
    LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    LocalDate testDate = nextMonday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(10, 0);

    ZonedDateTime testDateTime = nextMonday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "09:00:00", "23:00:00"));
      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(true)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }



  }

  @Test
  void isOpenNowShouldReturnFalseWhenSameTimeAccrossWeekAndTimingNull() {

    LocalDate today = LocalDate.now();
    LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    LocalDate testDate = nextMonday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(10, 0);

    ZonedDateTime testDateTime = nextMonday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", null, null));
      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(true)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertFalse(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }

  }

  @Test
  void isOpenNowShouldReturnTrueWhenEndTimeIsExactlyMidnight() {




    LocalDate today = LocalDate.now();
    LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    LocalDate testDate = nextMonday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(17, 0);

    ZonedDateTime testDateTime = nextMonday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("FRI", "09:00:00", "18:00:00"));
      timings.add(new OpeningHoursValue("SUN", "08:00:00", "12:00:00"));
      timings.add(new OpeningHoursValue("MON", "16:00:00", "00:00:00"));
      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(false)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }
  }

  @Test
  void isOpenNowShouldReturnFalseWhenCurrentTimeIsBeforeStartTime() {
    List<OpeningHoursValue> timings = new ArrayList<>();
    timings.add(new OpeningHoursValue("FRI", "09:00:00", "18:00:00"));
    timings.add(new OpeningHoursValue("SUN", "08:00:00", "12:00:00"));
    timings.add(new OpeningHoursValue("MON", "23:00:00", "23:59:00"));
    InformationListWidgetCFModel model =
      InformationListWidgetCFModel.builder()
        .sameTimeAcrossWeek(false)
        .openingHoursValue(timings)
        .build();


    assertFalse(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));
  }


  @Test
  void isOpenNowShouldReturnTrueWhenEndTimeIsPostMidnight() {




    LocalDate today = LocalDate.now();
    LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
    LocalDate testDate = nextMonday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(0, 30);

    ZonedDateTime testDateTime = nextMonday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "08:00:00", "03:00:00"));
      timings.add(new OpeningHoursValue("TUE", "08:00:00", "21:00:00"));

      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(false)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }
  }

  @Test
  void isOpenNowShouldReturnTrueWhenEndTimeBeforeStartTime() {




    LocalDate today = LocalDate.now();
    LocalDate nextMonday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY));
    LocalDate testDate = nextMonday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(14, 30);

    ZonedDateTime testDateTime = nextMonday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "08:00:00", "03:00:00"));
      timings.add(new OpeningHoursValue("TUE", "08:00:00", "21:00:00"));

      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(false)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }
  }

  @Test
  void isOpenNowShouldReturnFalseWhenEndTimeBeforeStartTimeOfTodayNotYesterday() {




    LocalDate today = LocalDate.now();
    LocalDate nextTuesday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
    LocalDate testDate = nextTuesday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(02, 00);

    ZonedDateTime testDateTime = nextTuesday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "08:00:00", "21:00:00"));
      timings.add(new OpeningHoursValue("TUE", "08:00:00", "03:00:00"));

      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(false)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertFalse(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }
  }


  @Test
  void isOpenNowShouldReturnTrueWhenEndTimeEqualsStartTime() {




    LocalDate today = LocalDate.now();
    LocalDate nextTuesday = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.TUESDAY));
    LocalDate testDate = nextTuesday;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(02, 00);

    ZonedDateTime testDateTime = nextTuesday.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(testDate);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("TUE", "00:00:00", "00:00:00"));

      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(false)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",null,null));

    }
  }

  @Test
  void isOpenNowShouldReturnTrueWhenSameTimeAccrossWeekAndCurrentDateBetween() {



    LocalDate today = LocalDate.now();
    LocalDate dateBetweenEvent = LocalDate.of(2024,1,5);
    String startDateEvent = "2024-01-02";
    String endDateEvent = "2024-01-30";



    LocalDate testDate = dateBetweenEvent;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(10, 0);

    ZonedDateTime testDateTime = testDate.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(dateBetweenEvent);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "09:00:00", "23:00:00"));
      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(true)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertTrue(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",startDateEvent,endDateEvent));

    }



  }

  @Test
  void isOpenNowShouldReturnFalseWhenSameTimeAccrossWeekAndCurrentDateNotBetween() {



    LocalDate today = LocalDate.now();
    LocalDate dateBetweenEvent = LocalDate.of(2024,1,1);
    String startDateEvent = "2024-01-02";
    String endDateEvent = "2024-01-30";



    LocalDate testDate = dateBetweenEvent;
    // Define a time of day (10:00 AM)
    LocalTime timeOfDay = LocalTime.of(10, 0);

    ZonedDateTime testDateTime = testDate.atTime(timeOfDay).atZone(ZoneId.of("Asia/Riyadh"));


    // Use try-with-resources to ensure the static mock is closed
    try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class);
         MockedStatic<ZonedDateTime> mockedZonedDateTime = mockStatic(ZonedDateTime.class)) {

      // Setup the behavior for LocalDate.now()
      mockedLocalDate.when(() -> LocalDate.now()).thenReturn(dateBetweenEvent);

      // Setup the behavior for ZonedDateTime.now()
      mockedZonedDateTime.when(() -> ZonedDateTime.now(ZoneId.of("Asia/Riyadh"))).thenReturn(testDateTime);

      // Assuming you have a function to create your opening hours values

      List<OpeningHoursValue> timings = new ArrayList<>();
      timings.add(new OpeningHoursValue("MON", "09:00:00", "23:00:00"));
      InformationListWidgetCFModel model =
          InformationListWidgetCFModel.builder()
              .sameTimeAcrossWeek(true)
              .openingHoursValue(timings)
              .build();

      // Test the function
      assertFalse(SectionsMapper.isOpenNow(model.isSameTimeAcrossWeek(), model.getOpeningHoursValue(), "en",startDateEvent,endDateEvent));

    }



  }

}