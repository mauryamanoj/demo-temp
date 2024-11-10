package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;

/**
 * StartCityName .
 */
public class StartCityName {
  /**
   * de.
   */
  @Getter
  @SerializedName("de")
  @Expose
  private De de;
  /**
   * pt .
   */
  @Getter
  @SerializedName("pt")
  @Expose
  private Pt pt;
  /**
   * en.
   */
  @Getter
  @SerializedName("en")
  @Expose
  private En en;
  /**
   * es.
   */
  @Getter
  @SerializedName("es")
  @Expose
  private Es es;
  /**
   * ar.
   */
  @Getter
  @SerializedName("ar")
  @Expose
  private Ar ar;
  /**
   * ru.
   */
  @Getter
  @SerializedName("ru")
  @Expose
  private Ru ru;

}
