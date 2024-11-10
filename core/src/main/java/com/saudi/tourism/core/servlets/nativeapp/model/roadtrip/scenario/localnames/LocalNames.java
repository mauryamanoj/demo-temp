package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.localnames;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.Ar;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.De;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.En;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.Es;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.Pt;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.startcityname.Ru;
import lombok.Getter;
/**
 * Json Strcuture LocalNames.
 */
public class LocalNames {

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
