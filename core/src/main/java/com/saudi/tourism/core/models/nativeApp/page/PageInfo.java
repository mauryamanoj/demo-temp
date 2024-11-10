package com.saudi.tourism.core.models.nativeApp.page;

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

  /**
   * Title .
   */
  @Getter
  @Setter
  private String title;

  /**
   * Description .
   */
  @Getter
  @Setter
  private String description;


  /**
   * previewImage .
   */
  @Getter
  @Setter
  private String previewImage;

  /**
   * featuredImage .
   */
  @Getter
  @Setter
  private String featuredImage;

  /**
   * featured .
   */
  @Getter
  @Setter
  private Boolean featured;

  /**
   * type .
   */
  @Getter
  @Setter
  private String type;
}
