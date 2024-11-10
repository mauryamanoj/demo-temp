package com.saudi.tourism.core.models.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;

/**
 * This class contains the Event and Simple Sliders details.
 */
@Data
public class SliderDataLayer implements Serializable {

  /**
   * Variable to store image carouselName.
   */
  @Expose
  private String carouselName;

  /**
   * Variable to store image carouselTitle.
   */
  @Expose
  private String carouselTitle;

  /**
   * Slide name.
   */
  private String slideName;

  /**
   * Slide title.
   */
  private String slideTitle;

  /**
   * Variable to store image slideAsset.
   */
  private String slideAsset;

  /**
   * Variable to store image itemNumber.
   */
  @Expose
  private int itemNumber;

  /**
   * Variable to store image totalItems.
   */
  @Expose
  private int totalItems;

  /**
   * Getter for DataLayer Json.
   * @return Json String.
   */
  public String getDataLayerJson() {
    return new Gson().toJson(this);
  }
}
