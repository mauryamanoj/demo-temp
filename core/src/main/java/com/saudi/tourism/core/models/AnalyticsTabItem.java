package com.saudi.tourism.core.models;

import com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Item;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;

/**
 * Extends {@code com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Item} class
 * with analytics event name attribute. All pre-existing fields requests are delegated to Item
 * class.
 */
public class AnalyticsTabItem extends Item {


  /**
   * Analytics event name.
   */
  @Getter
  private String analyticsEventName;

  /**
   * Unique id.
   */
  @Getter
  private String uniqueId;

  /**
   * Constructor.
   * @param request request
   * @param resource resource
   */
  public AnalyticsTabItem(SlingHttpServletRequest request, Resource resource) {
    super(request, resource);
    this.analyticsEventName = resource.getValueMap().get("analyticsEventName", String.class);
    this.uniqueId = resource.getValueMap().get("uniqueId", String.class);
  }

}
