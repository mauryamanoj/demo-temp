package com.saudi.tourism.core.models.components.events;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Class contains details of Seasons and Festivals on Events Card Component.
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Seasons {

  /**
   * Type of season.
   */
  @Getter
  @ValueMapValue
  private String seasonType;

  /**
   * Title on the card.
   */
  @Getter
  @ValueMapValue
  private String titleSeason;


  /**
   * Description on hte card.
   */
  @Getter
  @ValueMapValue
  private String seasonDescription;


  /**
   * Card Image.
   */
  @Getter
  @ValueMapValue
  private String fileReference;

  /**
   * Card Scene7 image.
   */
  @Getter
  @ValueMapValue
  private String s7fileReference;

  /**
   * Image Alt text.
   */
  @Getter
  @ValueMapValue
  private String altText;


  /**
   * Filters path.
   */
  @Getter
  @ValueMapValue
  private String filtersPath;

}
