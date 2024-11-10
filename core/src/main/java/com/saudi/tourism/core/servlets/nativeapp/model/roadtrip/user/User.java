package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.firstname.Firstname;
import lombok.Getter;

/**
 * Users POJO .
 */
public class User {
  /**
   * firstname .
   */
  @SerializedName("firstname")
  @Expose
  @Getter
  private Firstname firstname;
  /**
   * id .
   */
  @SerializedName("id")
  @Expose
  private String id;
}
