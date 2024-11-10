package com.saudi.tourism.core.models.components.events;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Events Cards Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class EventsCardsModel {

  /**
   * The Sling settings service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * Component Type.
   */
  @ValueMapValue
  private String componentType;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String headline;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String filtersPagePath;

  /**
   * The min number of events to show the '+sign' for 'upcoming-months'.
   * By default it will be 99.
   */
  @ValueMapValue
  private Integer minNumberEventShowPlusSign;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    filtersPagePath = LinkUtils
            .getAuthorPublishUrl(resolver, filtersPagePath,
              settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}


