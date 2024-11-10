package com.saudi.tourism.core.models.components.specialshowwidget.v1;

import com.google.gson.annotations.Expose;
import lombok.Getter;

@Getter
public class SpecialShowItem  {
  /**
   * key of special show.
   */
  @Expose
  private String key;

  /**
   * value of special show.
   */
  @Expose
  private String value;
}
