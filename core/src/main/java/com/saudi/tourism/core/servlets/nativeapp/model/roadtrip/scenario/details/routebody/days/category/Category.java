package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.category;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.category.name.Name;
import lombok.Getter;

/**
 * Category POJO under Routebody of days .
 */
public class Category {

  /**
   * name.
   */
  @SerializedName("name")
  @Expose
  @Getter
  private Name name;

}
