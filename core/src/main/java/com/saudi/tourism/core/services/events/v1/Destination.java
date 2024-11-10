package com.saudi.tourism.core.services.events.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import lombok.Builder;
import lombok.Data;

/**
 * Season.
 */
@Builder
@Data
public class Destination {
  /**
   * Destination id.
   */
  @JsonIgnore
  private String id;
  /**
   * Detination Title.
   */
  private String title;

  public static final Destination fromCFModel(final DestinationCFModel model) {
    if (model == null) {
      return null;
    }

    return Destination.builder()
      .id(model.getDestinationId())
      .title(model.getTitle())
      .build();

  }
}
