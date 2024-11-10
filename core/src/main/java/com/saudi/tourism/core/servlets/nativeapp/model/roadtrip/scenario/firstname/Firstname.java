package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.firstname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Firstname JSON Structure .
 */
public class Firstname {
  /**
   * ru.
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private Ru ru;
  /**
   * ja.
   */
  @SerializedName("ja")
  @Expose
  @Getter
  private Ja ja;
  /**
   * de.
   */
  @SerializedName("de")
  @Expose
  @Getter
  private De de;

  /**
   * es.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private Es es;
  /**
   * ar.
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private Ar ar;
  /**
   * pt.
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private Pt pt;
  /**
   * en.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private En en;
}
