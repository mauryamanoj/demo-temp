package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.Fr;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.De;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.PtBr;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.Ru;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.locales.It;
import lombok.Getter;

/**
 * Title PoJo Of Days under RouteBody.
 */
public class Title {

  /**
   * locale type .
   */
  @Getter
  @SerializedName("de")
  @Expose
  private De de;

  /**
   * locale type .
   */
  @SerializedName("fr")
  @Expose
  @Getter
  private Fr fr;

  /**
   * locale type .
   */
  @SerializedName("ru")
  @Expose
  @Getter
  private Ru ru;

  /**
   * locale type .
   */
  @SerializedName("en")
  @Expose
  @Getter
  private En en;

  /**
   * locale type .
   */
  @SerializedName("pt")
  @Expose
  @Getter
  private Pt pt;

  /**
   * locale type .
   */
  @SerializedName("it")
  @Expose
  @Getter
  private It it;

  /**
   * locale type .
   */
  @SerializedName("pt-br")
  @Getter
  @Expose
  private PtBr ptBr;

  /**
   * locale type .
   */
  @SerializedName("es")
  @Expose
  @Getter
  private Es es;

  /**
   * locale type .
   */
  @SerializedName("ar")
  @Expose
  @Getter
  private Ar ar;
}
