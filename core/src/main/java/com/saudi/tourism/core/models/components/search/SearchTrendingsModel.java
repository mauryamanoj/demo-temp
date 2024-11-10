package com.saudi.tourism.core.models.components.search;

import com.google.gson.annotations.Expose;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Search trending model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SearchTrendingsModel implements Serializable {
  /**
   * Variable to store title of block.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Variable to store list of trending pages.
   */
  @ChildResource
  @Expose
  private List<TrendingCard> cards;
}
