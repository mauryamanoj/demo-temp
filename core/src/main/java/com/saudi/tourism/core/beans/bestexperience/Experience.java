package com.saudi.tourism.core.beans.bestexperience;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Experience.
 */
@Getter
@Setter
public class Experience {

  /**
   * Status.
   */
  private String status;

  /**
   * List of ExperienceData.
   */
  private List<ExperienceData> data;

}
