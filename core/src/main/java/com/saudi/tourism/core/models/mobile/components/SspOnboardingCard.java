package com.saudi.tourism.core.models.mobile.components;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.io.Serializable;

import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/**
 * OnboardingCard model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SspOnboardingCard implements Serializable {

  /**
   * The resource resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * title.
   */
  @ValueMapValue
  private String title;
  /**
   * description.
   */
  @ValueMapValue
  private String description;
  /**
   * image.
   */
  @ValueMapValue
  private String imageUrl;

  @PostConstruct
  void init() {
    imageUrl =
      LinkUtils.getAuthorPublishAssetUrl(
        resourceResolver,
        imageUrl,
        settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}
