package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
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
 * Best Experiences.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class BestExperiences {

  /**
   * Sling settings service to get sling run modes.
   */
  @OSGiService
  private transient SlingSettingsService saudiModeConfig;

  /**
   * Component ID.
   */
  @ValueMapValue
  private String headline;

  /**
   * Component ID.
   */
  @ValueMapValue
  private String city;

  /**
   * Component ID.
   */
  @ValueMapValue
  private String packageDetailPagePath;

  /**
   * Component ID.
   */
  @ValueMapValue
  private String componentId;


  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  private void init() {
    // Add .html or remove /content/sauditourism according to sling run mode for internal link
    if (StringUtils.isNotBlank(this.packageDetailPagePath)) {
      this.packageDetailPagePath = LinkUtils.getAuthorPublishUrl(resolver, this.packageDetailPagePath,
              saudiModeConfig.getRunModes().contains(Externalizer.PUBLISH));
    }
  }
}
