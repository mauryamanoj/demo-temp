package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.category.Category;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.description.Description;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.metadata.Metadata;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.name.Name;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.routes.Routes;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.title.Title;
import lombok.Getter;
import java.util.List;
/**
 * Day POJO of route body.
 */
public class Day {

  /**
   * Photos.
   */
  @SerializedName("photos")
  @Expose
  @Getter
  private List<String> photos = null;

  /**
   * Title .
   */
  @SerializedName("title")
  @Expose
  @Getter
  private Title title;

  /**
   * Routes .
   */
  @SerializedName("routes")
  @Expose
  @Getter
  private Routes routes;
  /**
   * description.
   */
  @SerializedName("description")
  @Getter
  @Expose
  private Description description;

  /**
   * MEtadata.
   */
  @SerializedName("metadata")
  @Expose
  @Getter
  private Metadata metadata;

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Getter
  private Name name;
  /**
   * point .
   */
  @SerializedName("point")
  @Expose
  @Getter
  private List<Float> point = null;

  /**
   * category.
   */

  @SerializedName("category")
  @Expose
  @Getter
  private String category;

  /**
   * id .
   */
  @SerializedName("id")
  @Expose
  @Getter
  private String id;

  /**
   * categories.
   */
  @SerializedName("categories")
  @Expose
  @Getter
  private List<Category> categories = null;

}
