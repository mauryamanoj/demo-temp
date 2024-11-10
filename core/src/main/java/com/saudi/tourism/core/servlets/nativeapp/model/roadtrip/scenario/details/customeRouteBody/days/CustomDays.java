package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.routes.Routes;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata.Metadata;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
/**
 * Day POJO.
 */
public class CustomDays {

  /**
   * Photos.
   */
  @SerializedName("photos")
  @Expose
  @Getter
  @Setter
  private List<String> photos = null;

  /**
   * Title .
   */
  @SerializedName("title")
  @Expose
  @Getter
  @Setter
  private String title;

  /**
   * Routes .
   */
  @SerializedName("routes")
  @Expose
  @Getter
  @Setter
  private Routes routes;

  /**
   * description.
   */
  @SerializedName("description")
  @Getter
  @Expose
  @Setter
  private String description;

  /**
   * MEtadata.
   */
  @SerializedName("metadata")
  @Expose
  @Getter
  @Setter
  private Metadata metadata;

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Getter
  @Setter
  private String name;
  /**
   * point .
   */
  @SerializedName("point")
  @Expose
  @Getter
  @Setter
  private List<Float> point = null;

  /**
   * category.
   */

  @SerializedName("category")
  @Expose
  @Getter
  @Setter
  private String category;

  /**
   * id .
   */
  @SerializedName("id")
  @Expose
  @Getter
  @Setter
  private String id;

  /**
   * categories.
   */
  @SerializedName("categories")
  @Expose
  @Getter
  @Setter
  private List<String> categories = null;

}
