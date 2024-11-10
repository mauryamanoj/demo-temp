package com.saudi.tourism.core.models.components;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.filters.v1.FiltersItemFilterModel;
import com.saudi.tourism.core.services.mobile.v1.filters.FilterCategories;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Things to do filters Admin page.
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class FiltersSettings {

  /**
   * The type.
   */
  @ValueMapValue
  private String type;

  /**
   * filters.
   */
  @ChildResource
  private List<FiltersItemFilterModel> filters;

  /**
   * The mobile filters.
   */
  @ChildResource(name = "filters")
  @JsonIgnore
  private List<FilterCategories> mobileFilters;

}

