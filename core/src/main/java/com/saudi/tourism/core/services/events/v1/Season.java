package com.saudi.tourism.core.services.events.v1;

import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import lombok.Builder;
import lombok.Data;

/**
 * Season.
 */
@Builder
@Data
public class Season {
  /**
   * Season Title.
   */
  private String title;

  public static final Season fromCFModel(final SeasonCFModel model) {
    if (model == null) {
      return null;
    }

    return Season.builder()
      .title(model.getTitle())
      .build();

  }
}
