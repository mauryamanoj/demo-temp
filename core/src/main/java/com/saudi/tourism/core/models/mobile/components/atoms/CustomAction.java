package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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

/**
 * Represents the custom action section of the dialog.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CustomAction {

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
  private Cta cta;

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
