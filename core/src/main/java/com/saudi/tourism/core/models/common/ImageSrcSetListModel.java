package com.saudi.tourism.core.models.common;

import lombok.Data;

import java.util.List;

/**
 * Model for Image Src Set.
 */
@Data
public class ImageSrcSetListModel {

  /**
   * list of breakpoints.
   */
  private List<Breakpoint> breakpoints;

  /**
   * default Image.
   */
  private String defaultImage;
}
