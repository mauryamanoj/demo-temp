package com.saudi.tourism.core.models.components.eventpagehero.v1;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.content.PathModel;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains the EvenPageHero details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
@ToString
public class EventPageHeroModel {

  /**
   * List of paths.
   */
  @ChildResource
  private List<PathModel> events;

  /**
   * List of slides.
   */
  private List<EventDetail> slides;

  /**
   * Resource resolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Init method of sling model.
   */
  @PostConstruct protected void init() {
    this.slides = new ArrayList<>();

    for (PathModel currentItem : events) {
      Resource currentResource = resourceResolver.getResource(
          currentItem.getPath() + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
      if (currentResource != null) {
        EventDetail eventDetail = currentResource.adaptTo(EventDetail.class);
        this.slides.add(eventDetail);
      }

    }
  }
}
