package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * The type Vendor list.
 */
@Getter
@Setter
public class VendorList implements Serializable {

  /**
   * The Vendor title.
   */
  @Expose
  private String vendorTitle;

  /**
   * The Items.
   */
  @Expose
  private VendorLink items;
}
