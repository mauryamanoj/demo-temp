package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Weather {

  /**
   * The resource resolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * degrees.
   */
  @ValueMapValue
  private Integer degrees;

  /**
   * max Degrees.
   */
  @ValueMapValue
  private Integer maxDegrees;

  /**
   * Min Degrees.
   */
  @ValueMapValue
  private Integer minDegrees;

  /**
   * weather Icon URL.
   */
  @ValueMapValue
  private String weatherIconURL;

  /**
   * show.
   */
  @ValueMapValue
  private Boolean show;

  @PostConstruct
  void init() {
    weatherIconURL =
        LinkUtils.getAuthorPublishAssetUrl(
            resourceResolver,
            weatherIconURL,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
