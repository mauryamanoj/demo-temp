package com.saudi.tourism.core.models.components.greenTaxi;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.List;

/**
 * Green Taxi Model.
 */
@Data
public class GreenTaxiModel {

  /**
   * download app model.
   */
  @SerializedName(value = "download_app")
  private DownloadAppModel downloadApp;

  /**
   * Green taxi cards.
   */
  private List<GreenTaxiCard> cards;
}
