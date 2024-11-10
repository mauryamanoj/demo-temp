package com.saudi.tourism.core.models.components.cards;

import com.saudi.tourism.core.models.components.events.BaseRequestParams;
import lombok.Data;

import java.util.List;

/**
 * The Class DealsRequestParams.
 */
@Data
public class CardsRequestParams extends BaseRequestParams {

  /**
   * Name.
   */
  private String name;

  /**
   * The category.
   */
  private List<String> category;


  /**
   * The city.
   */
  private List<String> city;

  /**
   * The type.
   */
  private String type;

  /**
   * The priceRange.
   */
  private List<String> priceRange;


  /**
   * The Article.
   */
  private String articleId;

  /**
   * id.
   */
  private String id;

}
