package com.saudi.tourism.core.models.components.neighborhoodssection.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Builder
public class NeighborhoodsSectionCard {

  /**
   * Title.
   */
  @Expose
  private String title;

  /**
   * Image.
   */
  @Expose
  private Image image;


  /**
   * Cta Link.
   */
  @Expose
  @Setter
  private String ctaLink;

}
