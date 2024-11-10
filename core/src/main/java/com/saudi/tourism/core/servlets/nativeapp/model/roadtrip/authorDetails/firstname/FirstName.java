package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.De;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.firstname.locale.Ru;
import lombok.Getter;


/**
 * Firstname POJO.
 */
public class FirstName {
  /**
   * locale type.
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private Ar ar;

  /**
   * locale type.
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private Pt pt;

  /**
   * locale type.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private Es es;

  /**
   * locale type.
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private Ru ru;

  /**
   * locale type.
   */
  @SerializedName("de")
  @Expose
  @Getter
  private De de;

  /**
   * locale type.
   */
  @SerializedName("en")
  @Expose
  @Getter
  private En en;
}
