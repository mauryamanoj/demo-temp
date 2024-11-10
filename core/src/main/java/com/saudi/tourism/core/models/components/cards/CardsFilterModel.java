package com.saudi.tourism.core.models.components.cards;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The Class FilterModel.
 */
@Data
public class CardsFilterModel implements Serializable {

  /**
   * The card type.
   */
  @Expose
  private List<AppFilterItem> category;

  /**
   * The card type.
   */
  @Expose
  private List<AppFilterItem> city;

  /**
   * The price range.
   */
  @Expose
  private List<AppFilterItem> priceRange;

}
