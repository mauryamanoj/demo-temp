package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.name;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Name POJO of Route Body of Days.
 */
public class Name {

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

  /**
   * locale type.
   */
  @Getter
  @SerializedName("ar")
  @Expose
  private String ar;

  /**
   * locale type.
   */
  @Getter
  @SerializedName("es")
  @Expose
  private String es;

  /**
   * locale type.
   */
  @Getter
  @SerializedName("de")
  @Expose
  private String de;

  /**
   * locale type.
   */
  @Getter
  @SerializedName("ja")
  @Expose
  private String ja;

  /**
   * locale type.
   */
  @Getter
  @SerializedName("ru")
  @Expose
  private String ru;
}
