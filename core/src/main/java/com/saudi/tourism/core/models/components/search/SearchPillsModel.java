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
 * The Search pills model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class SearchPillsModel implements Serializable {

  /**
   * Variable to store title of pills block.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Variable to store list of pills.
   */
  @Expose
  @ChildResource
  private List<SearchPill> pills;
}
