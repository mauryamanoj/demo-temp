package com.saudi.tourism.core.beans;

import lombok.Data;

import java.io.Serializable;
import java.sql.Date;

/**
 * The is used as a bean class to capture like related client data.
 */
@Data
public class ClientData implements Serializable {

  /**
   * startDate.
   */
  private Date startDate;

  /**
   * latitude.
   */
  private Double latitude;

  /**
   * longitude.
   */
  private Double longitude;

  /**
   * ip.
   */
  private String ip;

  /**
   * likesCount.
   */
  private Integer likesCount;
}
