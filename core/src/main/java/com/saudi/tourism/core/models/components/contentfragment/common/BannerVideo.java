package com.saudi.tourism.core.models.components.contentfragment.common;

import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Video;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Banner Video.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerVideo {
  /**
   * Image.
   */
  private Video video;

  /**
   * Video thumbnail.
   */
  private Image thumbnail;

  /**
   * Image location.
   */
  private String location;
}
