package com.saudi.tourism.core.models.components.events;

import com.google.gson.annotations.Expose;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * The type Pagination.
 */
@Data
@NoArgsConstructor
public class Pagination implements Serializable {
  /**
   * The Total.
   */
  @Expose
  private int total;
  /**
   * The offset.
   */
  @Expose
  private int offset;
  /**
   * The limit.
   */
  @Expose
  private int limit;

  /**
   * Constructor.
   *
   * @param total total
   */
  public Pagination(final int total) {
    this.total = total;
  }
}
