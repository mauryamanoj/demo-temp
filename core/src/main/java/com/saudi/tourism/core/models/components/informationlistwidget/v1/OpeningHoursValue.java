package com.saudi.tourism.core.models.components.informationlistwidget.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import lombok.Builder;
import lombok.Data;

/**
 * OpeningHoursValue.
 */
@Data
@Builder
public class OpeningHoursValue {

  @JsonCreator
  public OpeningHoursValue(@JsonProperty("dayLabel") String dayLabel,
                           @JsonProperty("startTimeLabel") String startTimeLabel,
                           @JsonProperty("endTimeLabel") String endTimeLabel) {
    this.dayLabel = dayLabel;
    this.startTimeLabel = startTimeLabel;
    this.endTimeLabel = endTimeLabel;
  }

  /**
   * dayLabel.
   */
  @Expose
  private String dayLabel;

  /**
   * startTimeLabel.
   */
  @Expose
  private String startTimeLabel;

  /**
   * endTimeLabel.
   */
  @Expose
  private String endTimeLabel;
}
