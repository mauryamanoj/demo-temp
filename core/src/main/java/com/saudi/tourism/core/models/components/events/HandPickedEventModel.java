package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.services.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;

/**
 * Hand-picked Event Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class HandPickedEventModel {
  /**
   *
   */
  @OSGiService
  private UserService userService;

  /**
   * Event Path.
   */
  @Getter
  @ValueMapValue
  private String eventPath;

  /**
   * Event Detail.
   */
  @Getter
  private EventDetail eventDetail;

  /** init method of sling model. */
  @PostConstruct
  protected void init() {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      if (StringUtils.isNotEmpty(eventPath)) {
        final Resource eventPage = resourceResolver.getResource(eventPath);
        if (eventPage != null && eventPage.getChild(JcrConstants.JCR_CONTENT) != null) {
          final Resource eventContent = eventPage.getChild(JcrConstants.JCR_CONTENT);

          if (eventContent != null) {
            eventDetail = eventContent.adaptTo(EventDetail.class);
          }
        }
      }
    }
  }
}
