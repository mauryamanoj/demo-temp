package com.saudi.tourism.core.models.app.content;

import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * Related model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class RelatedModel implements Serializable {

  /**
   * List of pages.
   */
  @Getter
  @Setter
  @ChildResource
  private List<PathModel> pages;

  /**
   * Tags.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String[] tags;

}
