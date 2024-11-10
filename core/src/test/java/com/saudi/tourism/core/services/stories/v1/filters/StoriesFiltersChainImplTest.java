package com.saudi.tourism.core.services.stories.v1.filters;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;

import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.Story;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class StoriesFiltersChainImplTest {

  private StoriesFiltersChainImpl filtersChain = new StoriesFiltersChainImpl();

  @Mock
  private Resource locationResource;

  @Test
  public void doFilterShouldExcludeOnDestination() throws ParseException {
    // Arrange
    filtersChain.setFilters(
        Arrays.asList(
            new DestinationsFilter()));

    final var request = FetchStoriesRequest.builder()
      .locale("en")
      .destinations(Collections.singletonList("riyadh"))
      .build();

    final var story = Story.builder()
      .destinationPath("/content/dam/sauditourism/cf/en/destinations/alula")
      .build();

    //Act
    final var result = filtersChain.doFilter(request, story);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnDestination() throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new DestinationsFilter()));

    final var request = FetchStoriesRequest.builder()
      .locale("en")
      .destinations(Collections.singletonList("riyadh"))
      .build();

    final var story = Story.builder()
      .destinationPath("/content/dam/sauditourism/cf/en/destinations/riyadh")
      .build();

    //Act
    final var result = filtersChain.doFilter(request, story);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnCategories() throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new CategoriesFilter()));

    final var request = FetchStoriesRequest.builder()
      .locale("en")
      .categories(Arrays.asList("sauditourism:categories/cat1", "sauditourism:categories/cat2"))
      .build();

    final var story = Story.builder()
      .categoriesTags(Arrays.asList("sauditourism:categories/cat3", "sauditourism:categories/cat4"))
      .build();

    //Act
    final var result = filtersChain.doFilter(request, story);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnCategories() throws ParseException {
    // Arrange
    filtersChain.setFilters(
      Arrays.asList(
        new CategoriesFilter()));

    final var request = FetchStoriesRequest.builder()
      .locale("en")
      .categories(Arrays.asList("sauditourism:categories/cat1", "sauditourism:categories/cat2"))
      .build();

    final var story = Story.builder()
      .categoriesTags(Arrays.asList("sauditourism:categories/cat1", "sauditourism:categories/cat4"))
      .build();

    //Act
    final var result = filtersChain.doFilter(request, story);

    //Assert
    assertTrue(result);
  }
}