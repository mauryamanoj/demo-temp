package com.saudi.tourism.core.models.components.citydetails;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Map Detail.
 */
@Model(adaptables = {
    Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class MapDetail {

  /**
   * Map link text.
   */
  @ValueMapValue
  @Expose
  private String mapLinkText;

  /**
   * mapLinkAnalyticsText.
   */
  @ValueMapValue
  @Expose
  private String mapLinkAnalyticsText;
  /**
   * Map link.
   */
  @ValueMapValue
  @Expose
  private String mapLink;


}
