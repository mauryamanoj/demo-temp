package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.highlights;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Highlights POJO Under RouteBodyCustom .
 */
public class Highlights {
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
  @SerializedName("en")
  @Expose
  @Getter
  private String en;

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
}
