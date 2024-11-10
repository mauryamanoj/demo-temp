package com.saudi.tourism.core.models.components.events;

import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Card grid model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class CardGridModel {

  /**
   * The events.
   */
  @ChildResource
  @Getter
  private List<EventDetail> events;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {

    try {
      events = Optional.ofNullable(events).orElse(new ArrayList<>());
      for (EventDetail eventDetail : events) {
        eventDetail.setCalendarStartDate(
            CommonUtils.convertDateToSTring(eventDetail.getCalendarStartDate(), "dd/MM"));
        eventDetail.setCalendarEndDate(
            CommonUtils.convertDateToSTring(eventDetail.getCalendarEndDate(), "dd/MM"));
      }

    } catch (Exception e) {
      LOGGER.error(" Error in card grid component");
    }

  }

}
