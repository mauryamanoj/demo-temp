package com.saudi.tourism.core.models.components;

import com.adobe.cq.wcm.core.components.models.Tabs;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.beans.AnalyticsListItemImpl;
import com.saudi.tourism.core.models.common.ComponentHeading;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ResourceSuperType;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A c09-tabs model that returns list of {@link AnalyticsListItemImpl} instances and delegates other
 * methods to {@list com.adobe.cq.wcm.core.components.internal.models.v1.TabsImpl} class.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy =
    DefaultInjectionStrategy.OPTIONAL)
public class AnalyticsTabsImpl {
  /**
   * Variable to hold component title.
   */
  @Getter
  @ChildResource
  private ComponentHeading componentHeading;
  /**
   * HORIZONTAL.
   */
  public static final String HORIZONTAL = "horizontal";
  /**
   *ORNAMENT_BAR.
   */
  public static final String ORNAMENT_BAR = "ornament-bar";
  /**
   * value ten.
   */
  public static final String TEN = "10";
  /**
   * Resource resolver.
   */
  @SlingObject
  private ResourceResolver resourceResolver;

  /**
   * Model that is referenced to process most of the logic.
   */
  @Self
  @Via(type = ResourceSuperType.class)
  private Tabs tabs;

  /**
   * List of {@link AnalyticsListItemImpl} instances that should be used instead of @see
   * Tabs#getItems().
   */
  @Getter
  private List<AnalyticsListItemImpl> items;

  /**
   * Tab heading.
   */
  @Getter
  @ChildResource(name = "componentHeading")
  private ComponentHeading heading;

  /**
   * Ornament ID.
   */
  @Getter
  @ValueMapValue
  @Expose
  @Named("ornament")
  private String patternId;
  /**
   * orientation.
   */
  @Getter
  @Expose
  private String orientation;
  /**
   * className.
   */
  @Getter
  @Expose
  private String className;
  /**
   * title.
   */
  @Getter
  @Expose
  private String title;
  /**
   * Post construction.
   */
  @PostConstruct
  public void init() {
    orientation = HORIZONTAL;
    className = ORNAMENT_BAR;

    if (Objects.nonNull(componentHeading) && Objects.nonNull(componentHeading.getHeading())) {
      title = componentHeading.getHeading().getText();
    }

    if (StringUtils.isBlank(patternId)) {
      patternId = TEN;
    }
    if (Objects.nonNull(tabs) && !tabs.getItems().isEmpty()) {
      this.items = tabs.getItems().stream().map(item -> new AnalyticsListItemImpl(Objects
          .requireNonNull(resourceResolver.getResource(Objects.requireNonNull(item.getPath())))))
        .collect(Collectors.toList());
    }
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
  /**
   * Delegating to TabsImpl class.
   * @return accessibility layer
   */
  public String getAccessibilityLabel() {
    return tabs.getAccessibilityLabel();
  }

  /**
   * Delegating to TabsImpl class.
   * @return active item
   */
  public String getActiveItem() {
    return tabs.getActiveItem();
  }

}
