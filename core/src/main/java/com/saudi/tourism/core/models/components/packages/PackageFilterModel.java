package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * The Class FilterModel.
 */
@Data
public class PackageFilterModel implements Serializable {

  /**
   * The constant AREA.
   */
  public static final String PROP_AREA = "packageAreaTags";

  /**
   * the constant DURATION.
   */
  public static final String PROP_DURATION_AUTH = "durationAuth";

  /**
   * the constant TARGET.
   */
  public static final String PROP_TARGET_TAGS = "packageTargetTags";

  /**
   * the constant CATEGORY.
   */
  public static final String PROP_CATEGORY_TAGS = "packageCategoryTags";

  /**
   * The constant PRICE.
   */
  public static final String PROP_PRICE = "price";

  /**
   * The area.
   */
  @Expose
  private List<AppFilterItem> area;
  /**
   * The category.
   */
  @Expose
  private List<AppFilterItem> category;
  /**
   * The duration.
   */
  @Expose
  private List<AppFilterItem> duration;
  /**
   * The target.
   */
  @Expose
  private List<AppFilterItem> target;
  /**
   * The price.
   */
  @Expose
  private List<AppFilterItem> price;
  /**
   * The budget.
   */
  @Expose
  private List<AppFilterItem> budget;

  /**
   * Filter should be always sorted.
   * @param duration duration filter items
   */
  public void setDuration(List<AppFilterItem> duration) {
    this.duration = duration;
    this.duration.sort(AppFilterItem.DURATION_COMPARATOR);
  }

  /**
   * Generic setter method.
   * @param key property key
   * @param items items list
   */
  public void setItems(String key, List<AppFilterItem> items) {
    switch (key) {
      case PROP_AREA:
        this.setArea(items);
        break;
      case PROP_DURATION_AUTH:
        this.setDuration(items);
        break;
      case PROP_TARGET_TAGS:
        this.setTarget(items);
        break;
      case PROP_CATEGORY_TAGS:
        this.setCategory(items);
        break;
      case PROP_PRICE:
        this.setPrice(items);
        break;
      default:

        break;
    }
  }

  /**
   * Sort inner sortable lists.
   */
  public void sort() {
    if (CollectionUtils.isNotEmpty(getArea())) {
      getArea().sort(AppFilterItem.VALUE_COMPARATOR);
    }

    if (CollectionUtils.isNotEmpty(getCategory())) {
      getCategory().sort(AppFilterItem.VALUE_COMPARATOR);
    }

    if (CollectionUtils.isNotEmpty(getTarget())) {
      getTarget().sort(AppFilterItem.VALUE_COMPARATOR);
    }

  }
}
