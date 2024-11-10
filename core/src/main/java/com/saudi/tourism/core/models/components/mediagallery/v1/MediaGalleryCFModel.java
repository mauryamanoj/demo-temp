package com.saudi.tourism.core.models.components.mediagallery.v1;

import java.util.List;

import com.saudi.tourism.core.models.common.ImageBanner;
import lombok.Builder;
import lombok.Getter;

/** Media Gallery CF Model. */
@Getter
@Builder
public class MediaGalleryCFModel {

  /** Assets. */
  private List<ImageBanner> images;
}
