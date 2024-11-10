package com.saudi.tourism.core.models;

import com.day.cq.wcm.api.components.Component;
import com.day.cq.wcm.api.components.ComponentManager;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is an exact copy of
 * {@link com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Editor}
 * class, except it contains list of {@link AnalyticsTabItem} that has an extra analyticsEventName
 * field. Storing a reference to Editor class and build own list of items based on it's
 * {@link com.adobe.cq.wcm.core.components.commons.editor.dialog.childreneditor.Editor#getItems()}
 * method is not possible, since we will have to identify resource to
 * inject it's analyticsEventName property.
 */
@Model(adaptables = {SlingHttpServletRequest.class})
public class AnalyticsTabDialog {

  /**
   * Request.
   */
  @Self
  private SlingHttpServletRequest request;

  /**
   * Tabs container.
   */
  @Getter
  private Resource container;

  /**
   * Tabs info.
   */
  private List<AnalyticsTabItem> items;

  /**
   * Method will be invoked after all fields are injected.
   */
  @PostConstruct
  private void initModel() {
    readChildren();
  }

  /**
   * Initialize class fields.
   */
  private void readChildren() {
    items = new ArrayList<>();
    String containerPath = request.getRequestPathInfo().getSuffix();
    if (StringUtils.isBlank(containerPath)) {
      return;
    }
    ResourceResolver resolver = request.getResourceResolver();
    container = resolver.getResource(containerPath);
    ComponentManager componentManager = request.getResourceResolver()
        .adaptTo(ComponentManager.class);
    if (container != null && componentManager != null) {
      for (Resource resource : container.getChildren()) {
        if (resource != null) {
          Component component = componentManager.getComponentOfResource(resource);
          if (component != null) {
            items.add(new AnalyticsTabItem(request, resource));
          }
        }
      }
    }
  }

  /**
   * Retrieves the child items associated with this children editor.
   *
   * @return a list of child items
   */
  public List<AnalyticsTabItem> getItems() {
    return Collections.unmodifiableList(items);
  }

}
