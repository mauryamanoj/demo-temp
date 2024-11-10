package com.saudi.tourism.core.servlets.nativeapp.model.roadtrip;

import com.google.gson.annotations.Expose;
import lombok.Getter;

import java.io.Serializable;

/**
 * Road trip category Filter.
 */
public class RoadTripCategoryFilter implements Serializable {

  /**
   * alias.
   */
  @Getter
  @Expose
  private String alias;

  /**
   * name.
   */
  @Getter
  @Expose
  private String name;

}
