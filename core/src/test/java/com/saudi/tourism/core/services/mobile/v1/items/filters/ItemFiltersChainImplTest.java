package com.saudi.tourism.core.services.mobile.v1.items.filters;

import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.mobile.components.atoms.Date;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ItemFiltersChainImplTest {
  final SimpleDateFormat dateFormatter = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
  private ItemFiltersChainImpl filtersChain = new ItemFiltersChainImpl();

  @Test
  public void doFiltersShouldIncludeItem_OnSeasons_MatchItemSeason() {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new SeasonFilter()));

    final var filters = "filters=seasons:eq:winter,spring,summer,autumn";
    var request = MobileRequestParams.builder()
      .locale("ar")
      .filters(filters)
      .seasons(List.of(new String[]{"winter", "spring", "summer", "autumn"}))
      .build();

    final var season = ItemResponseModel.Season.builder()
      .id("summer")
      .title("Summer Season")
      .build();

    final var item = new ItemResponseModel();
    item.setSeason(season);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnDestinations_MatchItemDestination() {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new DestinationFilter()));

    final var filters = "filters=destinations:eq:riyadh,abha,tabouk,najrane";
    var request = MobileRequestParams.builder()
      .locale("ar")
      .filters(filters)
      .destinations(List.of(new String[]{"riyadh", "abha", "tabouk", "najrane"}))
      .build();

    final var destination = Destination.builder()
      .id("najrane")
      .title("Najrane City")
      .lat("30.000")
      .lng("30.000")
      .build();

    final var location = new Location();
    location.setDestination(destination);

    final var item = new ItemResponseModel();
    item.setLocation(location);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnType_MatchItemTypes() {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new TypeFilter()));

    final var filters = "filters=type:eq:attraction,event,activity";
    var request = MobileRequestParams.builder()
      .locale("ar")
      .filters(filters)
      .types(List.of(new String[]{"attraction", "event", "activity"}))
      .build();

    final var item = new ItemResponseModel();
    item.setType("event");

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnCategories_MatchItemCategories() {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new CategoryFilter()));
    final var filters = "filters=categories:eq:categories/nature,categories/music,categories/art";
    var request = MobileRequestParams.builder()
      .locale("ar")
      .filters(filters)
      .categories(List.of(new String[]{"categories/nature", "categories/music", "categories/art"}))
      .build();

    final var category = new Category();
    category.setId("categories/music/jazz");
    category.setTitle("Jazz music");
    category.setIcon("/content/dam/sauditourism/tag-icons/categories/music/jazz.svg");

    final List<Category> categories = new ArrayList<>();
    categories.add(category);

    final var item = new ItemResponseModel();
    item.setCategories(categories);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldExclude_OnStartDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("2024-01-01")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2023-12-27T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);
    date.setEndDate(itemEndDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldInclude_OnStartDate_GreaterThanRequestStartDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("2024-01-01")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2024-01-15T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldInclude_OnStartDate_WhenItemEndDateNonNullGreaterThanRequest() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("2024-01-01")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2024-01-02T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);
    date.setEndDate(itemEndDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldInclude_OnStartDate_WhenItemEndDateIsNull() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("2024-01-01")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldExcludeItem_OnStartDate_ForParsingException() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("some-text")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnStartDate_WhenRequestStartDateIsBlank() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq: ;";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("")
      .build();

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldExclude_OnStartDate_WhenItemStartDateIsNull() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new StartDateFilter()));

    final var filters = "filters=startDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .startDate("2024-01-01")
      .build();

    final var date = new Date();
    date.setStartDate(null);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldInclude_OnEndDate_BeforeRequestEndDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("2024-01-01")
      .startDate("2024-12-25")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2023-12-15T00:00:00.000+00:00"));

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-15T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldInclude_OnEndDate_AfterRequestEndDate_AndItemStartDateBeforeRequestEndDate() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("2024-01-01")
      .startDate("2024-12-25")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2024-01-15T00:00:00.000+00:00"));

    final var itemStartDate = Calendar.getInstance();
    itemStartDate.setTime(dateFormatter.parse("2023-12-15T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);
    date.setStartDate(itemStartDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldExclude_OnEndDate_AfterRequestEndDateAndItemStartIsNull() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("2024-01-01")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2024-01-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);
    date.setStartDate(null);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldInclude_OnEndDate_WhenItemStartDateIsNull() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("2024-01-01")
      .startDate("2023-12-24")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2024-01-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);
    date.setStartDate(null);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnEndDate_WhenItemEndDateIsNull() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("2024-01-01")
      .build();

    final var date = new Date();
    date.setEndDate(null);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldIncludeItem_OnEndDate_WhenRequestEndDateIsBlank() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2024-01-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

  @Test
  public void doFiltersShouldExcludeItem_OnEndDate_ForParsingException() throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new EndDateFilter()));

    final var filters = "filters=endDate:eq:2024-01-01";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .endDate("some-text")
      .build();

    final var itemEndDate = Calendar.getInstance();
    itemEndDate.setTime(dateFormatter.parse("2023-12-25T00:00:00.000+00:00"));

    final var date = new Date();
    date.setEndDate(itemEndDate);

    final var item = new ItemResponseModel();
    item.setDate(date);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertFalse(result);
  }

  @Test
  public void doFiltersShouldInclude_OnSearch_WhenItemTitleContainsSearchText() {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new SearchFilter()));

    final var filters = "filters=search:contains:ero";
    var request = MobileRequestParams.builder()
      .locale("en")
      .filters(filters)
      .search("ero")
      .build();

    final var titles = new Titles();
    titles.setTitle("I am a hero");

    final var item = new ItemResponseModel();
    item.setTitles(titles);

    // Act
    final var result = filtersChain.doFilter(request, item);

    // Assert
    Assertions.assertTrue(result);
  }

}
