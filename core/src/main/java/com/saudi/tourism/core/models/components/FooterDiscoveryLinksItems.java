package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
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
import java.io.Serializable;

/**
 * FooterDiscoveryLinksItems to get the child items .
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@NoArgsConstructor
public class FooterDiscoveryLinksItems implements Serializable {
  /**
   * Variable of discoveryLinkLabel.
   */
  @ValueMapValue
  @Expose
  private String discoveryLinkLabel;

  /**
   * discoveryLinkUrl .
   */
  @ValueMapValue
  @Expose
  private String discoveryLinkUrl;

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
    discoveryLinkUrl = LinkUtils.getAuthorPublishUrl(resourceResolver, discoveryLinkUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}


