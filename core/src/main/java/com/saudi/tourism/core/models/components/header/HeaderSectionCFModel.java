package com.saudi.tourism.core.models.components.header;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class HeaderSectionCFModel {

  /**
   * Title.
   */

  private String title;


  /**
   * Image.
   */
  private String image;

  /**
   *  subtitle.
   */
  private String subtitle;

  /**
   *  alt.
   */
  private String alt;

  /**
   * s7image.
   */
  private String s7image;

  /**
   *  authorCtaLink.
   */
  private String authorCtaLink;

  /**
   *  authorText.
   */
  private String authorText;

  /**
   * TextImage.
   */
  private String titleImage;

  /**
   * altTitleImage.
   */
  private String altTitleImage;

  /**
   * s7Textimage.
   */
  private String s7titleImage;

}
