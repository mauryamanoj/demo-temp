package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.description;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Description POJO Under Days of  RouteBody .
 */
public class Description {
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
