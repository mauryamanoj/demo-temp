package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class CtaLabel extends CtaParent {
  /**
   * ctaImage of the fragment.
   */
  @Expose
  private String ctaLabel;

}
