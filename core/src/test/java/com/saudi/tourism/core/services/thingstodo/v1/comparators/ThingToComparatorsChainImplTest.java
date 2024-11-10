package com.saudi.tourism.core.services.thingstodo.v1.comparators;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import com.saudi.tourism.core.utils.Constants;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThingToComparatorsChainImplTest {
  final SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);

  private ThingToComparatorsChainImpl comparatorsChain = new ThingToComparatorsChainImpl();

  @Test
  public void buildComparatorShouldHandleUnkownSortKey() throws ParseException {
    //Arrange
    comparatorsChain.setComparators(Arrays.asList(new RecentlyAddedThingToDoComparator()));

    final var publishDate_1 = Calendar.getInstance();
    publishDate_1.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var thingToDo_1 = ThingToDoModel.builder()
      .title("Thing ToDo 1")
      .publishedDate(publishDate_1)
      .build();

    final var publishDate_2 = Calendar.getInstance();
    publishDate_2.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var thingToDo_2 = ThingToDoModel.builder()
      .title("Thing ToDo 2")
      .publishedDate(publishDate_2)
      .build();


    final var request = FetchThingsToDoRequest.builder()
      .sortBy(Arrays.asList("unkown"))
      .build();

    //Act
    final var ordredThingsToDo= Arrays.asList(thingToDo_1, thingToDo_2).stream()
      .sorted(comparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Thing ToDo 2", ordredThingsToDo.get(0).getTitle());
    assertEquals("Thing ToDo 1", ordredThingsToDo.get(1).getTitle());
  }

  @Test
  public void buildComparatorShouldHandleRecentlyAddedSortKey() throws ParseException {
    //Arrange
    comparatorsChain.setComparators(Arrays.asList(new RecentlyAddedThingToDoComparator()));

    final var publishDate_1 = Calendar.getInstance();
    publishDate_1.setTime(aemDateFormat.parse("2023-11-07T00:00:00.000+00:00"));
    final var thingToDo_1 = ThingToDoModel.builder()
      .title("Thing ToDo 1")
      .publishedDate(publishDate_1)
      .build();

    final var publishDate_2 = Calendar.getInstance();
    publishDate_2.setTime(aemDateFormat.parse("2023-11-08T00:00:00.000+00:00"));
    final var thingToDo_2 = ThingToDoModel.builder()
      .title("Thing ToDo 2")
      .publishedDate(publishDate_2)
      .build();


    final var request = FetchThingsToDoRequest.builder()
      .sortBy(Arrays.asList("recentlyAdded"))
      .build();

    //Act
    final var ordredThingsToDo= Arrays.asList(thingToDo_1, thingToDo_2).stream()
      .sorted(comparatorsChain.buildComparator(request))
      .collect(Collectors.toList());

    //Assert
    assertEquals("Thing ToDo 2", ordredThingsToDo.get(0).getTitle());
    assertEquals("Thing ToDo 1", ordredThingsToDo.get(1).getTitle());
  }
}