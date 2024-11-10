package com.saudi.tourism.core.models.components.highlights;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class Button {

  /**
   * The Sling settings service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Variable of link url.
   */
  @ValueMapValue
  @Expose
  private String url;

  /**
   * Variable of link image path.
   */
  @ValueMapValue
  @Expose
  private String icon;

  /**
   * Variable for storing link text.
   */
  @Expose
  @ValueMapValue
  private String text;

  /**
   * Variable for boolean is External or not.
   */
  @Expose
  @ValueMapValue
  private Boolean external;


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
    url = LinkUtils
      .getAuthorPublishUrl(resolver, url, settingsService.getRunModes().contains(
        Externalizer.PUBLISH));
  }
}
