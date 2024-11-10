package com.saudi.tourism.core.services;

import com.saudi.tourism.core.models.components.EventsFiltersSettings;
import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventFilterModel;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.List;

/**
 * This defines all methods to execute business of Events.
 */
public interface EventService {

  /**
   * Get list of filtered events.
   *
   * @param queryParam the query parameter
   * @return a list of feature events
   * @throws RepositoryException the repository exception
   */
  EventListModel getFilteredEvents(EventsRequestParams queryParam) throws RepositoryException;

  /**
   * Get list of event filters.
   *
   * @param queryParam the query parameter
   * @return a list of event filters
   * @throws RepositoryException the repository exception
   */
  EventFilterModel getEventFilters(EventsRequestParams queryParam) throws RepositoryException;

  /**
   * Get related of events.
   *
   * @param language     the language
   * @param isAllExpired Boolean
   * @param eventDetail  the event detail
   * @return a list of event filters
   * @throws RepositoryException the repository exception
   */
  List<EventDetail> getRelatedEvents(String language, Boolean isAllExpired, EventDetail eventDetail)
      throws RepositoryException;

  /**
   * Get related of events paths. Based in getRelatedEvents but with a different return
   *
   * @param language    the language
   * @param isAllExpired Boolean
   * @param eventDetail the event detail
   * @return a list of event app filters items
   * @throws RepositoryException repository exception
   */
  List<String> getRelatedEventsPaths(String language, Boolean isAllExpired, EventDetail eventDetail)
      throws RepositoryException;

  /**
   * Gets event app filters.
   *
   * @param locale the locale
   * @return the event app filters
   */
  EventAppFilterModel getEventAppFilters(String locale);
  /**
   * Get all Dynamic Events Filters.
   * @param locale locale.
   * @return EventsFiltersSettings events filters data
   * @throws IOException IOException .
   */
  EventsFiltersSettings getDynamicEventsFilters(String locale) throws IOException;


  /**
   * Gets all events from "/content/sauditourism/{language}/events"
   * and lists them. Also it extracts filters and send to filter.
   *
   * @param language     the language
   * @param isAllExpired Boolean
   * @return the all states
   */
  EventListModel getAllEvents(String language, boolean isAllExpired);
}
