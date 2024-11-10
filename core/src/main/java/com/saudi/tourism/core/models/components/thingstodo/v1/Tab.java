package com.saudi.tourism.core.models.components.thingstodo.v1;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
/**
 * Tab  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Tab {
  /**
   * title.
   */
  @Expose
  @ValueMapValue
  private String title;
  /**
   * icon.
   */
  @Expose
  @ValueMapValue
  private String icon;
  /**
   * category tabs: Tag based /content/cq:tags/sauditourism/categories.
   */
  @Expose
  @ValueMapValue
  private String category;
}
