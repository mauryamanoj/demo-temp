package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * Category POJO under RouteBodyCustom.
 */
public class Category {
  /**
   * locale type .
   */
  @SerializedName("de")
  @Expose
  @Setter
  @Getter
  private String de;

  /**
   * locale type .
   */
  @SerializedName("ja")
  @Expose
  @Setter
  @Getter
  private String ja;

  /**
   * locale type .
   */
  @SerializedName("ru")
  @Expose
  @Setter
  @Getter
  private String ru;

  /**
   * locale type .
   */
  @SerializedName("en")
  @Expose
  @Setter
  @Getter
  private String en;

  /**
   * locale type .
   */
  @SerializedName("pt")
  @Expose
  @Setter
  @Getter
  private String pt;

  /**
   * locale type .
   */
  @SerializedName("es")
  @Expose
  @Setter
  @Getter
  private String es;

  /**
   * locale type .
   */
  @SerializedName("ar")
  @Expose
  @Setter
  @Getter
  private String ar;
}
