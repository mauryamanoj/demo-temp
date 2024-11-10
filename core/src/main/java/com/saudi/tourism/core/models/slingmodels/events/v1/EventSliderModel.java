package com.saudi.tourism.core.models.slingmodels.events.v1;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.common.SliderDataLayer;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

/**
 * This Class contains EventSliderModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class EventSliderModel {

  /**
   * Component title.
   */
  @ValueMapValue
  private String title;

  /**
   * Analytics title.
   */
  @ValueMapValue
  private String analyticsTitle;

  /**
   * Component Hide Title.
   */
  @ValueMapValue
  private boolean hideTitle;

  /**
   * Component title.
   */
  @Getter
  private List<EventDetail> events;

  /**
   * The Current page.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * The Event service.
   */
  @Inject
  private EventService eventService;

  /**
   * The constant EVENTS_SLIDER_COUNT.
   */
  private static final int EVENTS_SLIDER_COUNT = NumberConstants.THREE;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {

    try {
      Resource currentResource = currentPage.getContentResource();
      if (currentResource != null) {
        String language = CommonUtils
            .getPageNameByIndex(currentPage.getPath(), Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
        EventDetail eventDetail = currentResource.adaptTo(EventDetail.class);
        events = eventService.getRelatedEvents(language, Boolean.FALSE, eventDetail);
        if (events.isEmpty()) {
          EventsRequestParams eventsRequestParams = new EventsRequestParams();
          eventsRequestParams.setLocale(language);
          eventsRequestParams.setPath(eventDetail.getPath());
          eventsRequestParams.setLimit(EVENTS_SLIDER_COUNT);
          events = eventService.getFilteredEvents(eventsRequestParams).getData();
        }
      }

      int eventNumber = 0;
      int totalEvents = events.size();

      for (EventDetail event : events) {
        eventNumber++;
        SliderDataLayer dataTracker = event.getDataTracker();
        dataTracker.setCarouselTitle(title);
        dataTracker.setCarouselName(analyticsTitle);
        dataTracker.setItemNumber(eventNumber);
        dataTracker.setTotalItems(totalEvents);
      }
    } catch (Exception ex) {
      LOGGER.error(" Error in EventSliderModel");
    }
  }
}
