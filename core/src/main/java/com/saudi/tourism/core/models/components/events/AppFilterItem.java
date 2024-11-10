package com.saudi.tourism.core.models.components.events;

import com.day.cq.tagging.Tag;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.AppUtils;
import lombok.Data;
import org.apache.sling.api.resource.ValueMap;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

/**
 * The Class AppFilterItem.
 */
@Data
public class AppFilterItem implements Serializable {

  /**
   * Value Comparator.
   */
  public static final Comparator<AppFilterItem> VALUE_COMPARATOR =
      Comparator.comparing(AppFilterItem::getValue);

  /**
   * Duration comparator.
   */
  public static final Comparator<AppFilterItem> DURATION_COMPARATOR =
      Comparator.comparingInt((AppFilterItem i) -> Integer.parseInt(i.getId().split("-")[0]));


  /**
   * Instantiates a new App filter item.
   *
   * @param valueMap the value map
   */
  public AppFilterItem(ValueMap valueMap) {

    this.id = AppUtils.stringToID(valueMap.get("value").toString());
    this.value = valueMap.get("text").toString();
  }

  /**
   * Instantiates a new App filter item.
   *
   * @param id    the id
   * @param value the value
   */
  public AppFilterItem(String id, String value) {

    this.id = AppUtils.stringToID(id);
    this.value = value;
  }

  /**
   * Instantiates a new App filter item with camel Case Id.
   *
   * @param id        the id
   * @param value     the value
   * @param camelCase the camel case
   */
  public AppFilterItem(String id, String value, boolean camelCase) {

    this.id = AppUtils.stringToID(id, camelCase);
    this.value = value;
  }

  /**
   * The id.
   */
  @Expose
  private String id;

  /**
   * The value.
   */
  @Expose
  private String value;

  /**
   * Static constructor.
   * @param tag tag
   * @param locale locale
   * @return new AppFilterItem instance
   */
  public static AppFilterItem of(Tag tag, Locale locale) {
    String id = AppUtils.stringToID(tag.getTitle());
    String value = tag.getTitle(locale);
    return new AppFilterItem(id, value);
  }

  /**
   * hashCode - comparison only by id.
   *
   * @return hash
   */
  @Override
  public int hashCode() {
    return Optional.ofNullable(getId()).map(String::hashCode).orElseGet(super::hashCode);
  }

  /**
   * equals - comparison only by id.
   *
   * @param o another object
   * @return true of the current object equals specified
   */
  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    return Objects.equals(getId(), ((AppFilterItem) o).getId());
  }
}
