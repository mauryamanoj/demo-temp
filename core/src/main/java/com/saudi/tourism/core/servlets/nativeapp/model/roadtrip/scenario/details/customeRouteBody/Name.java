package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Setter;

/**
 * NAme POJO under the Route Body.
 */
public class Name {

  /**
   * locale type .
   */
  @SerializedName("es")
  @Expose
  @Setter
  private String es;

  /**
   * locale type .
   */
  @SerializedName("ar")
  @Expose
  @Setter
  private String ar;

  /**
   * locale type .
   */
  @SerializedName("pt")
  @Expose
  @Setter
  private String pt;

  /**
   * locale type .
   */
  @SerializedName("en")
  @Expose
  @Setter
  private String en;

  /**
   * locale type .
   */
  @SerializedName("ru")
  @Expose
  @Setter
  private String ru;

  /**
   * locale type .
   */
  @SerializedName("de")
  @Expose
  @Setter
  private String de;

  /**
   * locale type .
   */
  @SerializedName("ja")
  @Expose
  @Setter
  private String ja;
}


