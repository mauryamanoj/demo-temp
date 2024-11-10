package com.saudi.tourism.core.models.components.thingstodo.v1;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**  Cards Sort Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class ThingsToDoCardsSortModel {

  /**
   * Single Sort By : sort by property.
   */
  @ValueMapValue(name = "sortBy")
  private String singleSortBy;

  /**
   * Sort : sort by property as list.
   */
  @Expose
  private List<String> sortBy = new ArrayList<>();

  @PostConstruct
  protected void init() {
    if (StringUtils.isNotEmpty(singleSortBy)) {
      sortBy.add(singleSortBy);
    }
  }
}
