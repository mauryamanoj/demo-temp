package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Titles {

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
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Url for title icon.
   */
  @ValueMapValue
  private String titleIconUrl;

  /**
   * sub Title.
   */
  @ValueMapValue
  private String subTitle;

  /**
   * URL for the subtitle icon.
   */
  @ValueMapValue
  private String subTitleIconUrl;

  @PostConstruct
  void init() {
    titleIconUrl =
        LinkUtils.getAuthorPublishAssetUrl(
            resourceResolver,
            titleIconUrl,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
    subTitleIconUrl =
        LinkUtils.getAuthorPublishAssetUrl(
            resourceResolver,
            subTitleIconUrl,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
