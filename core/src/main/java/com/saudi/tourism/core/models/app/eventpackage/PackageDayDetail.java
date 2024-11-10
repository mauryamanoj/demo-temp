package com.saudi.tourism.core.models.app.eventpackage;

import com.saudi.tourism.core.utils.Constants;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lists of package day events.
 */
@Data
public class PackageDayDetail implements Serializable {

  /**
   * List of events in the morning.
   */
  private List<PackageItemModel> morning = new ArrayList<>();

  /**
   * List of events in the noon.
   */
  private List<PackageItemModel> noon = new ArrayList<>();

  /**
   * List of events in the afternoon.
   */
  private List<PackageItemModel> afternoon = new ArrayList<>();

  /**
   * List of events in the evening.
   */
  private List<PackageItemModel> evening = new ArrayList<>();

  /**
   * Adds {@link PackageItemModel} instance to
   * an appropriate inner list field.
   * @param item PackageItemModel instance
   * @return addition result
   */
  public boolean addItem(PackageItemModel item) {
    switch (item.getDayPart()) {
      case Constants.CONST_MORNING:
        return this.getMorning().add(item);
      case Constants.CONST_NOON:
        return this.getNoon().add(item);
      case Constants.CONST_AFTERNOON:
        return this.getAfternoon().add(item);
      case Constants.CONST_EVENING:
        return this.getEvening().add(item);
      default:
        return false;
    }
  }
}
