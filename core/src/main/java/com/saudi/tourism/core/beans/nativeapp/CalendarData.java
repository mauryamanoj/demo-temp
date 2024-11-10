package com.saudi.tourism.core.beans.nativeapp;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Bean for calendar api objects.
 */
@Data
@Builder
public class CalendarData {
  /**
   * name of the section.
   */
  private String name;
  /**
   * Data related to specific section.
   */
  private List<EventsDataObj> data;
  /**
   * Subtitle of the section.
   */
  private String subtitle;
  /**
   * filter path of the section for filter's page.
   */
  private String filterPath;
  /**
   * filter path of the section for filter's page.
   */
  private List<String> filter;

}
