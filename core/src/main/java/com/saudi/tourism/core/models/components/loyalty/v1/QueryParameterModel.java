package com.saudi.tourism.core.models.components.loyalty.v1;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * QueryParameter Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class QueryParameterModel {
  /**
   * Name.
   */
  @Expose
  @ValueMapValue
  private String name;

  /**
   * Value.
   */
  @Expose
  @ValueMapValue
  private String value;
}
