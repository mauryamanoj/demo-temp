package com.saudi.tourism.core.models.mobile.components.atoms;

import com.saudi.tourism.core.models.common.Category;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Builder
public class CategoryFilterItem {

  /**
   * id.
   */
  private String id;

  /**
   * items.
   */
  @ValueMapValue
  private List<Category> items;

}
