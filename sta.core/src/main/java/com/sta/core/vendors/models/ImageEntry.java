package com.sta.core.vendors.models;

import lombok.Data;

import java.io.Serializable;

/**
 * Event entry model.
 */
@Data
public class ImageEntry implements Serializable {

  /**
   * Event name in English.
   */
  private String label;

  /**
   * ID.
   */
  private String value;
}
