package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Data;

/**
 * Breakpoint for the image.
 */
@Data
public class Breakpoint {
  /**
   * desktop src set for breakpoint.
   */
  @Expose
  private String srcset;

  /**
   * media attribute.
   */
  @Expose
  private String media;


  /**
   * Aggregate sizes for the breakpoint.
   */
  @Expose
  private String sizes;
}
