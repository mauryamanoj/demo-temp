package com.saudi.tourism.core.models.app.page;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import java.io.Serializable;

import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Card Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class CardMenu implements Serializable {

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * URL.
   */
  @Expose
  @ValueMapValue
  private String url;

  /**
   * Label.
   */
  @ChildResource
  @Expose
  private Label label;

  /**
   * Icon path.
   */
  @Expose
  @ValueMapValue
  private String iconPath;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  @JsonIgnore
  private transient ResourceResolver resourceResolver;

  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    url = LinkUtils.getAuthorPublishUrl(resourceResolver, url,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}
