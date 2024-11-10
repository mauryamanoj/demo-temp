package com.saudi.tourism.core.services.thingstodo.v1.filters;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ThingsToDoFiltersChainImplTest {
  final SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);

  private ThingsToDoFiltersChainImpl filtersChain = new ThingsToDoFiltersChainImpl();

  @Test
  public void doFilterShouldExcludeOnStartDate(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-10T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnStartDate(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnEndDate(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-15";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-17T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnEndDate(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);

    Resource mockResource = aemContext.create().resource("/content/test", props);;

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnSeason(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter(), new SeasonsFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .seasons(Arrays.asList("riyadh-season"))
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);
    props.put("season", "/content/dam/sauditourism/cf/en/seasons/alulah-season");

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnSeason(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter(), new SeasonsFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .seasons(Arrays.asList("riyadh-season"))
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);
    props.put("season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnLocation(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(
        Arrays.asList(
            new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter(), new SeasonsFilter(), new DestinationsFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .seasons(Arrays.asList("riyadh-season"))
      .destinations(Arrays.asList("riyadh"))
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);
    props.put("locationValue", "/content/dam/sauditourism/cf/en/destinations/alula");
    props.put("season", "/content/dam/sauditourism/cf/en/seasons/alulah-season");

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnLocation(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new com.saudi.tourism.core.services.thingstodo.v1.filters.StartDateFilter(), new com.saudi.tourism.core.services.thingstodo.v1.filters.EndDateFilter(), new SeasonsFilter(), new DestinationsFilter()));

    final var requestStartDate = "2023-11-14";
    final var requestEndDate = "2023-11-17";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .startDate(requestStartDate)
      .endDate(requestEndDate)
      .seasons(Arrays.asList("riyadh-season"))
      .destinations(Arrays.asList("riyadh"))
      .build();

    final var thingsToDoStartDate = Calendar.getInstance();
    thingsToDoStartDate.setTime(aemDateFormat.parse("2023-11-15T00:00:00.000+00:00"));
    final var thingsToDoEndDate = Calendar.getInstance();
    thingsToDoEndDate.setTime(aemDateFormat.parse("2023-11-16T00:00:00.000+00:00"));
    Map<String, Object> props = new HashMap<>();
    props.put("startDate", thingsToDoStartDate);
    props.put("endDate", thingsToDoEndDate);
    props.put("locationValue", "/content/dam/sauditourism/cf/en/destinations/riyadh");
    props.put("season", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldIncludeOnKeyword(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new com.saudi.tourism.core.services.thingstodo.v1.filters.KeywordFilter()));

    final var requestKeyword = "FinD mE";
    final var request = FetchThingsToDoRequest.builder()
      .locale("en")
      .keyword(requestKeyword)
      .build();

    Map<String, Object> props = new HashMap<>();
    props.put("title", "this is the title with this keyword : find me");
    props.put("aboutDescription", "this is the description");

    Resource mockResource = aemContext.create().resource("/content/test", props);

    //Act
    final var result = filtersChain.doFilter(request, mockResource);

    //Assert
    assertTrue(result);

    Map<String, Object> props_case2 = new HashMap<>();
    props_case2.put("title", "this is the title");
    props_case2.put("aboutDescription", "this is the description with this keyword : find me");

    Resource mockResource_case2 = aemContext.create().resource("/content/test2", props_case2);

    //Act
    final var result_case2 = filtersChain.doFilter(request, mockResource_case2);

    //Assert
    assertTrue(result_case2);


    Map<String, Object> props_case3 = new HashMap<>();
    props_case3.put("title", "this is the title");
    props_case3.put("aboutDescription", "this is the description");

    Resource mockResource_case3 = aemContext.create().resource("/content/test3", props_case3);

    //Act
    final var result_case3 = filtersChain.doFilter(request, mockResource_case3);

    //Assert
    assertFalse(result_case3);
  }
}