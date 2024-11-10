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

/**
 * Mobile Media Gallery Model
 * representing the media gallery configuration.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MediaGallery {

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
   * URL of the media item.
   */
  @ValueMapValue
  private String url;

  /**
   * Type of the media.
   */
  @ValueMapValue
  private String type;

  /**
   * URL for the thumbnail of the media.
   */
  @ValueMapValue
  private String thumbnailUrl;

  /**
   * URL for the small image representation of the media.
   */
  @ValueMapValue
  private String smallImageUrl;

  /**
   * Height of the media item.
   */
  @ValueMapValue
  private Double height;

  /**
   * Width of the media item.
   */
  @ValueMapValue
  private Double width;

  /**
   * URL for the centered logo in the media gallery.
   */
  @ValueMapValue
  private String centeredLogoUrl;

  /**
   * Init method for processing images url.
   */
  @PostConstruct
  protected void init() {
    url = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, url,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));

    thumbnailUrl = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, thumbnailUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));

    smallImageUrl = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, smallImageUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));

    centeredLogoUrl = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, centeredLogoUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
