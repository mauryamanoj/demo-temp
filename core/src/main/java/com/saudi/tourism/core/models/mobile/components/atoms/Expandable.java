package com.saudi.tourism.core.models.mobile.components.atoms;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Expandable Model.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Expandable {

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * text.
   */
  @ValueMapValue
  private String text;

  /**
   * sideTitle.
   */
  @ChildResource
  private SideTitle sideTitle;

  /**
   * rows.
   */
  @ChildResource
  private List<Rows> rows;
}
