package com.saudi.tourism.core.models.components.events;


import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Upcoming Events.
 */
@Getter
@Setter
public class UpcomingEvents {

  /**
   * Background Image.
   */
  @Expose
  private Image backGroundImage;

  /**
   * Month.
   */
  @Expose
  private String month;

  /**
   * Count.
   */
  @Expose
  private int count;

  /**
   * Link.
   */
  @Expose
  private String link;

  /**
   * Event Images.
   */
  @Expose
  private List<String> eventImages;

  /**
   * Events Text.
   */
  @Expose
  private String eventsText;

  /**
   * The min number of events to show the '+sign' for 'upcoming-months'.
   * By default it will be 99.
   */
  @Expose
  private Integer minNumberEventShowPlusSign;

  /**
   * Constructor.
   *
   * @param month Month.
   * @param count Count.
   * @param eventsText eventsText.
   * @param minNumberEventShowPlusSign minNumberEventShowPlusSign.
   * @param link Link.
   */
  public UpcomingEvents(
      final String month,
      final int count,
      final String eventsText,
      final Integer minNumberEventShowPlusSign,
      final String link) {
    this.month = month;
    this.count = count;
    this.eventsText = eventsText;
    this.link = link;
    this.minNumberEventShowPlusSign = minNumberEventShowPlusSign;
  }
}
