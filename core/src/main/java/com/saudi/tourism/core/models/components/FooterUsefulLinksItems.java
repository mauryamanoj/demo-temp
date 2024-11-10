package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import javax.inject.Inject;

/**
 * FooterItems to get the child items .
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
public class FooterUsefulLinksItems implements Serializable {
  /**
   * Variable of usefulLinkLabel.
   */
  @ValueMapValue
  @Expose
  private String usefulLinkLabel;

  /**
   * usefulLinkUrl .
   */
  @ValueMapValue
  @Expose
  private String usefulLinkUrl;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  private transient SaudiModeConfig saudiModeConfig;

  /**
   * setting service.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;
  /**
   * init method.
   */
  @PostConstruct
  public void init() {
    usefulLinkUrl = LinkUtils.getAuthorPublishUrl(resourceResolver, usefulLinkUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
