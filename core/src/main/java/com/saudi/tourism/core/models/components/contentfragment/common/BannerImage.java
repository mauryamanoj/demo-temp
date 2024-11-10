package com.saudi.tourism.core.models.components.contentfragment.common;

import com.saudi.tourism.core.models.common.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Banner Image.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerImage {
  /**
   * Image.
   */
  private Image image;

  /**
   * Image location.
   */
  private String location;
}
