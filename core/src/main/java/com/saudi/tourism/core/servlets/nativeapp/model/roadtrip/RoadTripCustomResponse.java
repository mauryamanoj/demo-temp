package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip;

import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response.NativeAppCustomCardResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Bean for response of roadrtip service.
 */
@Data
@Builder
public class RoadTripCustomResponse {

  /**
   *  list of trips.
   */
  private List<NativeAppCustomCardResponse> trips;
   /**
   * The pagination.
   */
  private Pagination pagination;

}
