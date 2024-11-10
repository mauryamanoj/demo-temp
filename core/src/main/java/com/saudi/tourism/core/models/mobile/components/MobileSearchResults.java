package com.saudi.tourism.core.models.mobile.components;

import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;

@Builder
@Data
@Model(
    adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class MobileSearchResults {

  /** id. */
  private String id;

  /** mediaGallery. */
  private MediaGallery mediaGallery;

  /** title. */
  private String title;

  /** contentTypeTitle. */
  private String contentTypeTitle;

  /** type. */
  private String type;

  @Builder
  @Data
  public static class MediaGallery {
    /** type. */
    private String type;

    /** url. */
    private String url;
  }
}
