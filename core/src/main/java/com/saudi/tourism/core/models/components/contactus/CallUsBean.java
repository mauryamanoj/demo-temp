package com.saudi.tourism.core.models.components.contactus;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * Call Us Bean.
 */
@Getter
@Setter
public class CallUsBean {
  /**
   * name.
   */
  @Expose
  private String name;
  /**
   * Phone.
   */
  @Expose
  private String phone;
  /**
   * Flag.
   */
  @Expose
  private String flag;
  /**
   * Status.
   */
  @Expose
  private String status;
  /**
   * Region.
   */
  @Expose
  private String region;
  /**
   * get country name.
   */
}
