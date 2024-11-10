package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.category.name;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Name POJO of Categories of Days .
 */
public class Name {

  /**
   * locale type.
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private String ru;

  /**
   * locale type.
   */
  @SerializedName("de")
  @Expose
  @Getter
  private String de;

  /**
   * locale type.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private String es;

  /**
   * locale type.
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private String ar;

  /**
   * locale type.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private String en;

  /**
   * locale type.
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private String pt;
}
