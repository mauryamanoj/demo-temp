package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * NAme POJO under the Route Body.
 */
public class Name {

  /**
   * locale type .
   */
  @SerializedName("es")
  @Expose
  @Getter
  private String es;

  /**
   * locale type .
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private String ar;

  /**
   * locale type .
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private String pt;

  /**
   * locale type .
   */
  @SerializedName("en")
  @Expose
  @Getter
  private String en;

  /**
   * locale type .
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private String ru;

  /**
   * locale type .
   */
  @SerializedName("de")
  @Expose
  @Getter
  private String de;

  /**
   * locale type .
   */
  @SerializedName("ja")
  @Expose
  @Getter
  private String ja;
}


