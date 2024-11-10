package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The type Vendor link.
 */
@Getter
@Setter
public class VendorLink implements Serializable {
  /**
   * The Title.
   */
  @Expose
  private String title;
  /**
   * The Url.
   */
  @Expose
  private String url;
}
