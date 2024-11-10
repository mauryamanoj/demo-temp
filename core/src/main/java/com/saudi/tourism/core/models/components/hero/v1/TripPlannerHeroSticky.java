package com.saudi.tourism.core.models.components.hero.v1;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
class TripPlannerHeroSticky {
  /** Title. */
  @Expose
  private String title;

  /** Subtitle. */
  @Expose
  private String subtitle;

  /** Link Ref. */
  @Expose
  private String linkRef;

  /** Link Copy. */
  @Expose
  private String linkCopy;

  /** Cta Type. */
  @Expose
  private String ctaType;
}
