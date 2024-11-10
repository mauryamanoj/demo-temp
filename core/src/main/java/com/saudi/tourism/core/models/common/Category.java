package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Class represents info for one tag.
 */
@Data
@NoArgsConstructor
@Builder
public class Category implements Serializable {

  /**
   * Tag Id.
   */
  @Expose
  private String id;

  /**
   * Label for the tag, localized.
   */
  @Expose
  private String title;

  /**
   * Icon for the tag.
   */
  @Expose
  private String icon;

  /**
   * Constructor with label.
   *
   * @param title label for the tag, localized
   */
  public Category(final String title) {
    setTitle(title);
  }

  /**
   * Constructor with label and icon.
   *
   * @param title label for the tag, localized
   * @param icon icon name
   */
  public Category(final String title, final String icon) {
    setTitle(title);
    setIcon(icon);
  }

  /**
   * Constructor with id,title and icon.
   *
   * @param id identifier for the tag
   * @param title title for the tag, localized
   * @param icon icon svg
   */
  public Category(final String id, final String title, final String icon) {
    setId(id);
    setTitle(title);
    setIcon(icon);
  }
}
