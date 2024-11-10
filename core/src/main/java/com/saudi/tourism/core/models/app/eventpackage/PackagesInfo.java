package com.saudi.tourism.core.models.app.eventpackage;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * App packages info.
 */
@Data
public class PackagesInfo implements Serializable {
  /**
   * List of the cities in packages.
   */
  private List<String> availableCities;

  /**
   * List of the event packages.
   */
  private List<EventPackageModel> packages;
}
