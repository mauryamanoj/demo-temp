package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

@Getter
public class CtaIcon {
  /**
   * ctaImage of the fragment.
   */
  @Expose
  private String iconLabel;

  /**
   * iconLink of the fragment.
   */
  @Expose
  private String iconLink;

  /**
   * openInNewTab of the fragment.
   */
  private String openInNewTab;

  /**
   * isOpenInNewTab of the fragment.
   */
  @Expose
  @Setter
  private boolean isOpenInNewTab;

  @JsonSetter("openInNewTab")
  public void setOpenInNewTab(String openInNewTabValue) {
    this.isOpenInNewTab = "yes".equalsIgnoreCase(openInNewTabValue)
      || "true".equalsIgnoreCase(openInNewTabValue);
  }

}
