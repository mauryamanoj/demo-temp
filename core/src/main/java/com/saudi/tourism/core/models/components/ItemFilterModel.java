package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * The item filter options.
 */
@Model(adaptables = Resource.class,
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class ItemFilterModel implements Serializable {

  /**
   * Filter name.
   */
  @ValueMapValue
  @Expose
  private String name;

  /**
   * Filter key.
   */
  @ValueMapValue
  @Expose
  private String key;
  /**
   * Filter type.
   */
  @ValueMapValue
  @Expose
  private String type;
  /**
   * filter data.
   */
  @Expose
  private List<IdValueModel> data;
}
