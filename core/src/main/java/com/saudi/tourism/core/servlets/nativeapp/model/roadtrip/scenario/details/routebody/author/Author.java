package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.author;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Author Pojo class.
 */
public class Author {
  /**
   * business.
   */
  @SerializedName("business")
  @Expose
  @Getter
  private String business;

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Getter
  private Name name;

  /**
   * image .
   */
  @SerializedName("image")
  @Expose
  @Getter
  private String image;
}
