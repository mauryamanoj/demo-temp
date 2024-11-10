package com.saudi.tourism.core.models.components.hotels;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * The Class FilterModel.
 */
@Data
public class HotelsFilterModel implements Serializable {

  /**
   * The hotel city.
   */
  @Expose
  private List<AppFilterItem> area;

  /**
   * The hotel chain.
   */
  @Expose
  private List<AppFilterItem> hotelChain;

}
