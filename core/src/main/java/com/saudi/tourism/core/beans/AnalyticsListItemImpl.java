package com.saudi.tourism.core.beans;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * List item with analytics event name.
 * This class is exact copy of
 * {@link com.adobe.cq.wcm.core.components.internal.models.v1.PanelContainerItemImpl} with an extra
 * @see #eventName field.
 */
@Data
public class AnalyticsListItemImpl {

  /**
   * Item title property.
   */
  private static final String PN_PANEL_TITLE = "cq:panelTitle";

  /**
   * Item analytics event name property.
   */
  private static final String EVENT_NAME = "analyticsEventName";

  /**
   * Item analytics unique id property.
   */
  private static final String UNIQUE_ID = "uniqueId";

  /**
   * URL.
   */
  private String url;

  /**
   * Title.
   */
  private String title;

  /**
   * Description.
   */
  private String description;

  /**
   * Last modification date.
   */
  private Calendar lastModified;

  /**
   * Path.
   */
  private String path;

  /**
   * Name.
   */
  private String name;

  /**
   * Analytics event name.
   */
  private String eventName;

  /**
   *  unique id.
   */
  private String uniqueId;

  /**
   * Analytics event name.
   */
  private List<AnalyticsListItemCard> cards;

  /**
   * Constructor.
   * @param resource resource
   */
  public AnalyticsListItemImpl(@NotNull Resource resource) {
    ValueMap valueMap = resource.adaptTo(ValueMap.class);
    if (valueMap != null) {
      String jcrTitle = valueMap.get(JcrConstants.JCR_TITLE, String.class);
      title = valueMap.get(PN_PANEL_TITLE, jcrTitle);

      description = valueMap.get(JcrConstants.JCR_DESCRIPTION, String.class);
      lastModified = valueMap.get(JcrConstants.JCR_LASTMODIFIED, Calendar.class);
      eventName = valueMap.get(EVENT_NAME, String.class);
      uniqueId = valueMap.get(UNIQUE_ID, String.class);
    }
    path = resource.getPath();
    name = resource.getName();
    url = null;

    if (resource.isResourceType("wcm/foundation/components/responsivegrid")) {
      cards = CommonUtils.iteratorToStream(resource.listChildren())
        .map(r -> new AnalyticsListItemCard(r.getPath()))
        .collect(Collectors.toList());
    } else {
      cards = Collections.singletonList(new AnalyticsListItemCard(resource.getPath()));
    }
  }

  /**
   * AnalyticsListItemCard.
   */
  @Getter
  @AllArgsConstructor
  public static class AnalyticsListItemCard {
    /**
     * path.
     */
    private String path;
  }
}
