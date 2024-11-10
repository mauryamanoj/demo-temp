package com.saudi.tourism.core.models.components;

import com.saudi.tourism.core.models.components.card.v1.CardModel;

import lombok.Data;
import lombok.Getter;

/**
 * Object to store properties for Article navigation type.
 */
@Data
public class ArticleItem extends CardModel {

  /**
   * The city.
   */
  @Getter
  private String city;

  /**
   * The Start date.
   */
  @Getter
  private String startDate;

  /**
   * The End date.
   */
  @Getter
  private String endDate;
}
