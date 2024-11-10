package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

/**
 * Tabs model.
 */
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class HeaderResponseModel {



  /**
   * Titles Object.
   */
  @ChildResource
  private Titles titles;

  /**
   * Boolean flag indicating whether a map should be displayed on the page.
   */
  @ValueMapValue(name = "showMap")
  private boolean showMapButton;

  /**
   * Boolean flag indicating whether a search bar should be displayed on the page.
   */
  @ValueMapValue(name = "showSearch")
  private boolean showSearchBar;

  /**
   * Specifies the UI type of the header component style. It determines the visual styling of the page header.
   */
  @ChildResource
  private HeaderComponentStyle headerComponentStyle;

  /**
   * The steps.
   */
  @ChildResource
  private Steps steps;
  /**
   * sideActions.
   */
  @ChildResource
  private SideActions sideActions;

  /**
   * tabs.
   */
  @ChildResource
  private IconTab tabs;
  /**
   * Inner class representing each item in the 'icons' multifield of the dialog.
   */
  @Data
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class IconItem {

    /**
     * Unique identifier for the icon item.
     */
    @ValueMapValue
    private String id;

    /**
     * Title or label associated with the icon item.
     */
    @ValueMapValue
    private String title;

    /**
     * The icon symbol or image associated with the icon item.
     */
    @ValueMapValue
    private String icon;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Steps {
    /**
     * Boolean flag indicating whether a particular step in a sequence or process should be displayed.
     */
    @ValueMapValue
    private Boolean show;
    /**
     * The value of a specific step in a process or sequence, as defined in the dialog.
     */
    @ValueMapValue
    private String value;
  }

  @Data
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class IconTab {
    /**
     * Boolean flag indicating whether a tab should be displayed on the page.
     */
    @ValueMapValue
    private boolean show;
    /**
     * A list of icon items. Each icon item represents a configurable element in the multifield of the dialog.
     */
    @ChildResource
    private List<IconItem> items;

  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  @Builder
  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Titles {

    /**
     * Subtitle of the mobile page. This is typically used as a secondary heading.
     */
    @ValueMapValue
    private String subTitle;
    /**
     * Side title of the mobile page. This could be used for sidebar headings or additional context.
     */
    @ValueMapValue
    private String sideTitle;
    /**
     * Title.
     */
    @ValueMapValue
    private String title;
  }

  /**
   * Represents the custom action section of the dialog.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class CustomAction {

    /**
     * The resource resolver.
     */
    @JsonIgnore
    @Inject
    private transient ResourceResolver resourceResolver;

    /** Sling settings service. */
    @JsonIgnore
    @OSGiService
    private transient SlingSettingsService settingsService;

    /**
     * title.
     */
    @ValueMapValue
    private String title;
    /**
     * show.
     */
    @ValueMapValue
    private Boolean show;
    /**
     * enable.
     */
    @ValueMapValue
    private Boolean enable;

    /**
     * leftIcon.
     */
    @ValueMapValue
    private String leftIcon;
    /**
     * rightIcon.
     */
    @ValueMapValue
    private String rightIcon;
    /**
     * The buttonComponentStyle.
     */
    @ChildResource
    private ButtonComponentStyle buttonComponentStyle;
    /**
     * The CTA.
     */
    @ChildResource
    private CTA cta;
    /**
     * Represents the cta.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class ButtonComponentStyle {
      /**
       * componentUIType.
       */
      @ValueMapValue
      private String componentUIType;
    }

    /**
     * Represents the cta.
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
    public static class CTA {
      /**
       * Url value.
       */
      @ValueMapValue(name = "value")
      private String url;
      /**
       * type.
       */
      @ValueMapValue
      private String type;
    }

    @PostConstruct
    void init() {
      leftIcon =
        LinkUtils.getAuthorPublishAssetUrl(
          resourceResolver,
          leftIcon,
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
      rightIcon =
        LinkUtils.getAuthorPublishAssetUrl(
          resourceResolver,
          rightIcon,
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }


  }
  /**
   * Represents the side actions section of the dialog.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class SideActions {
    /**
     * The resource resolver.
     */
    @JsonIgnore
    @Inject
    private transient ResourceResolver resourceResolver;

    /** Sling settings service. */
    @JsonIgnore
    @OSGiService
    private transient SlingSettingsService settingsService;
    /**
     * showFilter.
     */
    @ValueMapValue
    private Boolean showFilter;
    /**
     * showShare.
     */
    @ValueMapValue
    private Boolean showShare;
    /**
     * Share URL.
     */
    @ValueMapValue
    private String shareUrl;
    /**
     * showMap.
     */
    @ValueMapValue
    private Boolean showMap;
    /**
     * showFavorite.
     */
    @ValueMapValue
    private Boolean showFavorite;
    /**
     *  The customAction.
     */
    @ChildResource
    private CustomAction customAction;

    @PostConstruct
    void init() {
      shareUrl =
        LinkUtils.getAuthorPublishUrl(
          resourceResolver,
          shareUrl,
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class HeaderComponentStyle {
    /**
     * componentUIType.
     */
    @ValueMapValue
    private String componentUIType;
  }
}
