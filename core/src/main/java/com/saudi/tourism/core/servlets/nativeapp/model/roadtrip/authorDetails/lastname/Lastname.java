package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.Ru;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.lastname.locale.De;
import lombok.Getter;

/**
 * Lastname POJO.
 */
public class Lastname {
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
}