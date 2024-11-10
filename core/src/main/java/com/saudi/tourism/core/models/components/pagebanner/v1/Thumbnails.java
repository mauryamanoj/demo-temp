package com.saudi.tourism.core.models.components.pagebanner.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ImageBanner;
import lombok.Data;

import java.util.List;

/**
 * Thumbnails dto.
 */
@Data
public class Thumbnails {


  /**
   * Destination moreLabel.
   */
  @Expose
  private String moreLabel;

  /**
   * Gallery.
   */
  @Expose
  private List<ImageBanner> gallery;


}
