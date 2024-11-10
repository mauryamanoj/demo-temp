package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.Ja;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.De;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.visitesPlaces.locale.Ru;
import lombok.Getter;
import lombok.Setter;

/**
 * Visited Places POJO.
 */
public class VisitedPlaces {

  /**
   * locale.
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private Ar ar;

  /**
   * locale.
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private Pt pt;

  /**
   * locale.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private Es es;

  /**
   * locale.
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private Ru ru;

  /**
   * locale.
   */
  @SerializedName("de")
  @Expose
  @Getter
  private De de;

  /**
   * locale.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private En en;

  /**
   * locale.
   */
  @SerializedName("ja")
  @Getter
  @Setter
  private Ja ja;
}
