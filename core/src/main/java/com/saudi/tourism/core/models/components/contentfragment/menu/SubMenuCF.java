package com.saudi.tourism.core.models.components.contentfragment.menu;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SubMenuCF {

  /**
   * Image.
   */
  @Expose
  private Image image;

  /**
   * CTA Label. (Navigation Title or Title).
   */
  @Expose
  private String ctaLabel;

  /**
   * CTA Link.
   */
  @Expose
  private String ctaLink;

}
