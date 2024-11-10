package com.saudi.tourism.core.services.mobile.v1.filters;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TypeFilterModel {

  /**
   * Items.
   */
  @ChildResource(name = "types")
  private List<Type> items;

  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class Type {
    /** id. */
    @ValueMapValue private String id;

    /** title. */
    @ValueMapValue private String title;

    /** Icon url. */
    private String iconUrl = "";
  }
}
