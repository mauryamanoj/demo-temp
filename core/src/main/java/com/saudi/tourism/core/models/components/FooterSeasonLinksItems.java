package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.api.resource.ResourceResolver;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * FooterItems to get the child items .
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
public class FooterSeasonLinksItems implements Serializable {
  /**
   * Variable of seasonLinkLabel.
   */
  @ValueMapValue
  @Expose
  private String seasonLinkLabel;

  /**
   * seasonLinkUrl .
   */
  @ValueMapValue
  @Expose
  private String seasonLinkUrl;

  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  private transient SaudiModeConfig saudiModeConfig;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

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
    seasonLinkUrl = LinkUtils.getAuthorPublishUrl(resourceResolver, seasonLinkUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
