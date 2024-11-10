package com.saudi.tourism.core.models.components.filterExplorer.v1;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Filter Explorer Type Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class FilterExplorerTypeModel {

  /**
   * The id.
   */
  @ValueMapValue
  @Expose
  private String id;

  /**
   * The label.
   */
  @ValueMapValue
  @Expose
  private String label;

}
