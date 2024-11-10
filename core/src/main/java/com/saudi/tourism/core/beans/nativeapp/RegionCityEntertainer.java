package com.saudi.tourism.core.beans.nativeapp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.location.PolygonCoordinatesModel;
import com.saudi.tourism.core.models.common.RegionCity;
import lombok.Value;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * RegionCityEntertainer object.
 */
@Value
@SuperBuilder
public class RegionCityEntertainer extends RegionCity {

  /**
   * Images for this city.
   */
  private String image;

  /**
   * List of destination tags wrapped into the CategoryTag object.
   */
  private List<String> destinationFeatureTags;


  /**
   * coordinates.
   */
  private List<PolygonCoordinatesModel> coordinates;
  /**
   * Entertainer location id.
   */
  private String locationId;

  /**
   * Entertainer city latitude.
   */
  private String latitude;


  /**
   * Entertainer city longitude.
   */
  private String longitude;

  /**
   * Entertainer city radius.
   */
  private String radius;

  /**
   * disableEntertainer.
   */
  @JsonIgnore
  private transient boolean disableEntertainer;
}
