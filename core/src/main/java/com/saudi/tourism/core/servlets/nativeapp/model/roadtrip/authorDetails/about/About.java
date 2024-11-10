package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Fr;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Ja;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.PtBr;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.De;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.It;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.about.locale.Ru;
import lombok.Getter;

/**
 * About.
 */
public class About {
  /**
   * locale type.
   */
  @SerializedName("pt-br")
  @Expose
  @Getter
  private PtBr ptBr;

  /**
   * locale type.
   */
  @SerializedName("fr")
  @Expose
  @Getter
  private Fr fr;

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
  @SerializedName("ar")
  @Expose
  @Getter
  private Ar ar;

  /**
   * locale type.
   */
  @SerializedName("ja")
  @Expose
  @Getter
  private Ja ja;

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
  @SerializedName("en")
  @Expose
  @Getter
  private En en;

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
  @SerializedName("it")
  @Expose
  @Getter
  private It it;

  /**
   * locale type.
   */
  @SerializedName("es")
  @Expose
  @Getter
  private Es es;
}
