package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * The Event static sling model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class ItineraryStaticModel {

  /**
   * The events.
   */
  @ChildResource
  @Getter
  private List<EventPathModel> events;

  /**
   * The ctaText.
   */
  @ValueMapValue
  @Getter
  private String ctaText;

  /**
   * The EventDetails.
   */
  @Getter
  private List<EventDetail> eventDetails;

  /**
   * The Resource resolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Initialize the properties.
   */
  @PostConstruct private void init() {

    try {

      eventDetails = new ArrayList<>();
      for (EventPathModel eventPathModel : events) {
        Resource resource = resourceResolver.getResource(
            eventPathModel.getEventPath() + Constants.FORWARD_SLASH_CHARACTER
                + JcrConstants.JCR_CONTENT);
        EventDetail eventDetail;
        if (Objects.nonNull(resource)) {
          eventDetail =
              Optional.ofNullable(resource.adaptTo(EventDetail.class)).orElse(new EventDetail());
          eventDetail.setCalendarStartDate(
              CommonUtils.convertDateToSTring(eventDetail.getCalendarStartDate(), "dd/MM"));
          eventDetail.setCalendarEndDate(
              CommonUtils.convertDateToSTring(eventDetail.getCalendarEndDate(), "dd/MM"));
          eventDetails.add(eventDetail);
        }

      }

    } catch (Exception e) {
      LOGGER.error(" Error in event static component");
    }

  }

}
