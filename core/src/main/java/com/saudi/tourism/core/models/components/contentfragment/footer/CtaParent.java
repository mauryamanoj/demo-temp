package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CtaParent {

  /**
   * ctaLink of the fragment.
   */
  @Expose
  @Setter
  private String ctaLink;

  /**
   * Indicates whether the link should open in a new tab.
   */
  @Expose
  @Setter
  private boolean isOpenInNewTab;

  /**
   * Custom setter for isOpenInNewTab that accepts a String.
   * It checks if the provided string is "true", "yes", or other variations and sets the boolean field accordingly.
   * @param openInNewTabValue
   */
  @JsonSetter("openInNewTab")
  public void setOpenInNewTab(String openInNewTabValue) {
    this.isOpenInNewTab = "yes".equalsIgnoreCase(openInNewTabValue)
      || "true".equalsIgnoreCase(openInNewTabValue);
  }
}
