package com.saudi.tourism.core.beans.nativeapp;

import lombok.Data;

import java.util.List;

/**
 * Section Bean for calendar app api.
 */
@Data
public class CalendarResponse {
  /**
   * List of all Sections.
   */
  private List<CalendarData> section;

}
