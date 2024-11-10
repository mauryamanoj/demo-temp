package com.saudi.tourism.core.models.components.events.eventdatewidget.v1;

import lombok.Builder;
import lombok.Getter;

import java.util.Calendar;

/**
 * Event Date Widget Model.
 */
@Builder
@Getter
public class EventDateWidgetCFModel {

  /**
   * Title.
   */
  @Getter
  private String title;

  /**
   * start date.
   */
  @Getter
  private Calendar startDate;

  /**
   * end date.
   */
  @Getter
  private Calendar endDate;

  /**
   * Coming soon Label.
   */
  @Getter
  private String comingSoonLabel;


  /**
   * Expired Label.
   */
  @Getter
  private String expiredLabel;
}
