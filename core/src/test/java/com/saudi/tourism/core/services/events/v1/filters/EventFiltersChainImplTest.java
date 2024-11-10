package com.saudi.tourism.core.services.events.v1.filters;

import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.utils.Constants;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventFiltersChainImplTest {
  final SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);

  private EventFiltersChainImpl filtersChain = new EventFiltersChainImpl();

  @Mock
  private Resource seasonResource;

  @Mock
  private Resource locationResource;

  @Test
  public void doFilterShouldExcludeOnStartDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-10T00:00:00.000+00:00"));
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .build();

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnStartDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .build();

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnEndDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter(), new EndDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-15";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-17T00:00:00.000+00:00"));
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .build();

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnEndDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter(), new EndDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .build();

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnSeason() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter(), new EndDateFilter(), new SeasonFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .season("/content/dam/sauditourism/cf/en/seasons/riyadh-season")
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var season = SeasonCFModel.builder()
      .resource(seasonResource)
      .build();
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .season(season)
      .build();

    when(seasonResource.getPath())
        .thenReturn("/content/dam/sauditourism/cf/en/seasons/alulah-season");

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnSeason() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter(), new EndDateFilter(), new SeasonFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .season("/content/dam/sauditourism/cf/en/seasons/riyadh-season")
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var season = SeasonCFModel.builder()
      .resource(seasonResource)
      .build();
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .season(season)
      .build();

    when(seasonResource.getPath())
      .thenReturn("/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnLocation() throws ParseException {
    // Arrange
    filtersChain.setFilters(
        Arrays.asList(
            new StartDateFilter(), new EndDateFilter(), new SeasonFilter(), new DestinationFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .season("/content/dam/sauditourism/cf/en/seasons/riyadh-season")
      .destination("/content/dam/sauditourism/cf/en/destinations/riyadh")
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var season = SeasonCFModel.builder()
      .resource(seasonResource)
      .build();
    final var destination = DestinationCFModel.builder()
      .resource(locationResource)
      .build();
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .season(season)
      .destination(destination)
      .build();

    when(seasonResource.getPath())
      .thenReturn("/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    when(locationResource.getPath())
      .thenReturn("/content/dam/sauditourism/cf/en/destinations/alula");

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnLocation() throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new StartDateFilter(), new EndDateFilter(), new SeasonFilter(), new DestinationFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchEventsRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .season("/content/dam/sauditourism/cf/en/seasons/riyadh-season")
      .destination("/content/dam/sauditourism/cf/en/destinations/riyadh")
      .build();

    final var eventStartDate = Calendar.getInstance();
    eventStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var eventEndDate = Calendar.getInstance();
    eventEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var season = SeasonCFModel.builder()
      .resource(seasonResource)
      .build();
    final var destination = DestinationCFModel.builder()
      .resource(locationResource)
      .build();
    final var event = EventCFModel.builder()
      .startDate(eventStartDate)
      .endDate(eventEndDate)
      .season(season)
      .destination(destination)
      .build();

    when(seasonResource.getPath())
      .thenReturn("/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    when(locationResource.getPath())
      .thenReturn("/content/dam/sauditourism/cf/en/destinations/riyadh");

    //Act
    final var result = filtersChain.doFilter(request, event);

    //Assert
    assertTrue(result);
  }
}