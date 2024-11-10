package com.saudi.tourism.core.models.components.events;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * The Class EventAppFilterModel.
 */
@Setter
@Getter
public class EventAppFilterModel implements Serializable {

  /**
   * The cities.
   */
  private List<AppFilterItem> cities;

  /**
   * The categories.
   */
  private List<AppFilterItem> categories;

  /**
   * The targets.
   */
  private List<AppFilterItem> target;

  /**
   * The priorites items.
   */
  private List<AppFilterItem> priorities;

  /**
   * The freePaid items.
   */
  private List<AppFilterItem> freePaid;

  /**
   * The seasons.
   */
  private List<AppFilterItem> season;

  /**
   * Age ranges.
   */
  private List<AppFilterItem> ageRange;

  /**
   * Genders.
   */
  private List<AppFilterItem> gender;

  /**
   * Travel partners.
   */
  private List<AppFilterItem> travelPartner;

  /**
   * Interests.
   */
  private List<AppFilterItem> interests;
}
