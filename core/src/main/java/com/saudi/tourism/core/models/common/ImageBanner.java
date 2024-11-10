package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageBanner {

  /** Type. */
  @Expose
  private String type;

  /** Type Label. */
  @Expose private String typeLabel;

  /** Location. */
  @Expose private String location;

  /** thumbnail. */
  @Expose private String thumbnail;

  /** Image Item. */
  @Expose
  private Image image;

  /** Video Item. */
  @Expose
  private Video video;
}
