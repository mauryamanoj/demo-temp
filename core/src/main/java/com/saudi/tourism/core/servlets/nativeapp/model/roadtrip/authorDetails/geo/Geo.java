package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.image.Image;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.geo.localnames.LocalNames;
import lombok.Getter;

import java.util.List;

/**
 * Geo POJO.
 */
public class Geo {
  /**
   * airports.
   */
  @SerializedName("airports")
  @Expose
  @Getter
  private List<Integer> airports = null;

  /**
   * countryCode.
   */
  @SerializedName("countryCode")
  @Expose
  @Getter
  private String countryCode;

  /**
   * id.
   */
  @SerializedName("_id")
  @Expose
  @Getter
  private Integer id;

  /**
   * featureCode.
   */
  @SerializedName("featureCode")
  @Expose
  @Getter
  private String featureCode;

  /**
   * localnames.
   */
  @SerializedName("localnames")
  @Expose
  @Getter
  private LocalNames localnames;

  /**
   * admin1Code.
   */
  @SerializedName("admin1Code")
  @Expose
  @Getter
  private String admin1Code;

  /**
   * loc.
   */
  @SerializedName("loc")
  @Expose
  @Getter
  private List<Float> loc = null;

  /**
   * featureClass.
   */
  @SerializedName("featureClass")
  @Expose
  @Getter
  private String featureClass;

  /**
   * timezone.
   */
  @SerializedName("timezone")
  @Expose
  @Getter
  private String timezone;

  /**
   * images.
   */
  @SerializedName("images")
  @Expose
  @Getter
  private List<Image> images = null;

  /**
   * iata.
   */
  @SerializedName("iata")
  @Expose
  @Getter
  private String iata;

}
