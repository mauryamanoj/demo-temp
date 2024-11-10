package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip;

import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.IdNameModel;
import lombok.Getter;

import java.util.List;

/**
 * Road trip filter data type.
 */
public class RoadTripFilterResponseModel {

  /**
   * duration.
   */
  @Getter
  private List<String> duration;

  /**
   * start city.
   */
  @Getter
  @SerializedName("start_city")
  private List<IdNameModel> startCity;

  /**
   * categories.
   */
  @Getter
  private List<RoadTripCategoryFilter> categories;

  /**
   * transport.
   */
  @Getter
  private List<String> transport;



}
