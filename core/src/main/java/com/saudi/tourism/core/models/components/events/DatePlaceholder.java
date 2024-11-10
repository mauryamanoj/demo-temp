package com.saudi.tourism.core.models.components.events;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;

/**
 * The Class DatePlaceholder.
 */
@Data
public class DatePlaceholder implements Serializable {
  /**
   * The startDate.
   */
  @Expose
  private String startDate;

  /**
   * The endDate.
   */
  @Expose
  private String endDate;

  /**
   * The Select Date.
   */
  private String selectDate;

  /**
   * Custom.
   */
  @Expose
  private String custom;
}
