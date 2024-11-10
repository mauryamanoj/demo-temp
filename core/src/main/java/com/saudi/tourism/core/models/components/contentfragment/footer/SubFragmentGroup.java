package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SubFragmentGroup {
  /**
   * orientation of the fragment.
   */
  @ValueMapValue
  @Expose
  private String orientation;

  /**
   * patternType of the fragment.
   */
  @ValueMapValue
  @Expose
  private String patternType;

}
