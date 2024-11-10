package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Price Widget Model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PriceWidget {

  /**
   * type.
   */
  @ValueMapValue
  private String type;

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * description.
   */
  @ValueMapValue
  private String description;

  /**
   * minimumNumberOfPeople.
   */
  @ValueMapValue
  private Integer minimumNumberOfPeople;

  /**
   * Price.
   */
  @ChildResource(name = "prices")
  private Price price;

  /**
   * customAction.
   */
  @ChildResource
  private CustomAction customAction;

  /**
   * Inner class Price represents price item of price widget.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Price {
    /**
     * Price.
     */
    @ValueMapValue
    private String price;

    /**
     * Tier.
     */
    @ValueMapValue
    private String tier;

    /**
     * Currency.
     */
    @ValueMapValue
    private String currency;

  }

}
