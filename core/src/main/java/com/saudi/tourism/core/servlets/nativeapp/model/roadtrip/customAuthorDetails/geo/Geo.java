package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.geo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.geo.image.Image;
import lombok.Getter;
import lombok.Setter;

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
  @Setter
  private List<Integer> airports = null;

  /**
   * countrycode.
   */
  @SerializedName("countryCode")
  @Expose
  @Getter
  @Setter
  private String countryCode;

  /**
   * id.
   */
  @SerializedName("_id")
  @Expose
  @Getter
  @Setter
  private Integer id;

  /**
   * featurecode.
   */
  @SerializedName("featureCode")
  @Expose
  @Getter
  @Setter
  private String featureCode;
  /**
   * localname.
   */
  @SerializedName("localnames")
  @Expose
  @Getter
  @Setter
  private String localnames;

  /**
   * admincode.
   */
  @SerializedName("admin1Code")
  @Expose
  @Getter
  @Setter
  private String admin1Code;

  /**
   * loc.
   */
  @SerializedName("loc")
  @Expose
  @Getter
  @Setter
  private List<Float> loc = null;

  /**
   * featureClass.
   */
  @SerializedName("featureClass")
  @Expose
  @Setter
  @Getter
  private String featureClass;
  /**
   * timezone.
   */
  @SerializedName("timezone")
  @Expose
  @Getter
  @Setter
  private String timezone;

  /**
   * images.
   */
  @SerializedName("images")
  @Expose
  @Getter
  @Setter
  private List<Image> images = null;

  /**
   * iata.
   */
  @SerializedName("iata")
  @Expose
  @Getter
  @Setter
  private String iata;
}
