package com.saudi.tourism.core.services.mobile.v1.youmayalsolike;

import com.saudi.tourism.core.models.components.events.eventscards.v1.EventsCardsModel;
import com.saudi.tourism.core.models.components.thingstodo.v1.ThingsToDoCardsModel;
import com.saudi.tourism.core.services.events.v1.EventsCFService;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Component(service = YouMayAlsoLikeService.class, immediate = true)
public class YouMayAlsoLikeServiceImpl implements YouMayAlsoLikeService {
  /**
   * static offset.
   */
  private static final Integer OFFSET = 0;
  /**
   * static limit.
   */
  private static final Integer LIMIT = -1;
  /**
   * The Event service.
   */
  @Reference
  private transient EventsCFService eventsService;

  /**
   * ThingsToDo CF Service.
   */
  @Reference
  private transient ThingsToDoCFService thingsToDoCFService;

  /**
   * This method fill the events request objects from events cards model.
   * @param eventsCards eventsCards
   * @return fetchEventRequest
   */
  @Override
  public FetchEventsRequest fillEventsRequestFromEventsCards(EventsCardsModel eventsCards) {
    String startDate = null;
    String endDate = null;
    String destination = null;
    String season = null;
    List<String> paths = null;

    if (eventsCards.getFilter() != null) {
      LocalDate localStartDate = CommonUtils.calendarToLocalDate(eventsCards.getFilter().getStartDate());
      LocalDate localEndDate = CommonUtils.calendarToLocalDate(eventsCards.getFilter().getEndDate());

      if (Objects.nonNull(localStartDate)) {
        startDate = CommonUtils.dateToString(localStartDate, Constants.FORMAT_DATE);
      }

      if (Objects.nonNull(localEndDate)) {
        endDate = CommonUtils.dateToString(localEndDate, Constants.FORMAT_DATE);
      }

      destination = eventsCards.getFilter().getDestination();
      season = eventsCards.getFilter().getSeason();
    }

    if (eventsCards.getHandpick() != null) {
      paths = eventsCards.getHandpick().getEventCFPaths();
    }

    return FetchEventsRequest.builder()
      .locale(eventsCards.getLang())
      .offset(OFFSET)
      .limit(LIMIT)
      .startDate(startDate)
      .endDate(endDate)
      .season(season)
      .destination(destination)
      .paths(paths)
      .build();
  }

  @Override
  public FetchThingsToDoRequest fillThingsToDoRequestFromEventCards(
      ThingsToDoCardsModel thingsToDoCards) {

    List<String> destinations = null;
    List<String> paths = null;
    List<String> types = null;
    List<String> categories = null;

    if (thingsToDoCards.getFilter() != null) {
      destinations = thingsToDoCards.getFilter().getDestinations();
      types = thingsToDoCards.getFilter().getType();
      categories = thingsToDoCards.getFilter().getCategories();
    }

    if (thingsToDoCards.getHandpick() != null) {
      paths = thingsToDoCards.getHandpick().getThingsToDoCFPaths();
    }

    return FetchThingsToDoRequest.builder()
        .locale(thingsToDoCards.getLang())
        .offset(OFFSET)
        .limit(LIMIT)
        .destinations(destinations)
        .type(types)
        .categories(categories)
        .paths(paths)
        .build();
  }

  /**
   * This method will get all filtered events.
   * @param request request
   * @return filtered events
   */
  @Override
  public FetchEventsResponse getFilteredEvents(FetchEventsRequest request) {
    return eventsService.getFilteredEvents(request);
  }

  /**
   * This method will get all filtered things to do.
   * @param request request
   * @return filtered things to do
   */
  @Override
  public FetchThingsToDoResponse getFilteredThingsToDo(FetchThingsToDoRequest request) {
    return thingsToDoCFService.getFilteredThingsToDo(request);
  }
}
