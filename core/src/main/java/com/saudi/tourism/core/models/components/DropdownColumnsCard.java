package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.nav.v4.NavCard;
import com.saudi.tourism.core.models.components.nav.v4.NavMap;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Object to store properties for Article navigation type.
 */
@Data
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DropdownColumnsCard {

  /**
   * Card Wrapper.
   */
  @ChildResource
  @Expose
  private CardWrapper cardWrapper;

  /**
   * Variation.
   */
  @ValueMapValue
  @Expose
  private String variation;

  /**
   * The Graphical mode.
   */
  @ChildResource
  @Expose
  private List<NavCard> cards;

  /**
   * The Graphical mode.
   */
  @ChildResource
  @Expose
  private NavMap map;
}
