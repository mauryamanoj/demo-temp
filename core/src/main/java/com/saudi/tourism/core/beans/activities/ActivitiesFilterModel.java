package com.saudi.tourism.core.beans.activities;

import com.saudi.tourism.core.models.components.events.AppFilterItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Activities Filter Model.
 */
@Getter
@Setter
public class ActivitiesFilterModel {

  /**
   * The cities.
   */
  private List<AppFilterItem> city;


}
