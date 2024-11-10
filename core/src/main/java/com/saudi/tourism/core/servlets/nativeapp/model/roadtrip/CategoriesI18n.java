package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

/**
 * Categories Class .
 */
public class CategoriesI18n {
  /**
   * de .
   */
  @SerializedName("de")
  @Expose
  @Getter
  private String de;
  /**
   * ja.
   */
  @SerializedName("ja")
  @Expose
  @Getter
  private String ja;
  /**
   * ru.
   */
  @Getter
  @SerializedName("ru")
  @Expose
  private String ru;

  /**
   * pt.
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private String pt;

  /**
   * en.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private String en;
  /**
   * ar.
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private String ar;
  /**
   * es.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private String es;
}
