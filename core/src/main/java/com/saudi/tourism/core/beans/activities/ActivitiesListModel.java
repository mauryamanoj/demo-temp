package com.saudi.tourism.core.beans.activities;

import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Activities List Model.
 */
@Getter
@Setter
public class ActivitiesListModel {

  /**
   * Activities Data.
   */
  private List<Activity> data;

  /**
   * Activities Filter Modelx.
   */
  private ActivitiesFilterModel filters;

  /**
   * The pagination.
   */
  private Pagination pagination;
}
