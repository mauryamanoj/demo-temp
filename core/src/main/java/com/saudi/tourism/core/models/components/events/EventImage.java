package com.saudi.tourism.core.models.components.events;

import lombok.Data;

import java.io.Serializable;

/**
 * The type Event Image, added to match frontend api structure.
 */
@Data
public class EventImage implements Serializable {

  /**
   * The image source.
   */
  private String src;

  /**
   * The image alt.
   */
  private String alt;

}
