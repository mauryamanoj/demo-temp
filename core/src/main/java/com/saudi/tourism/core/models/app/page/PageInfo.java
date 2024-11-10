package com.saudi.tourism.core.models.app.page;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Page information.
 */
public class PageInfo implements Serializable {


  /**
   * Page path.
   */
  @Getter
  @Setter
  private String page;

  /**
   * Publish date.
   */
  @Getter
  @Setter
  private String publishDate = "";

  /**
   * Loyalty .
   */
  @Getter
  @Setter
  private Boolean loyalty;
}
