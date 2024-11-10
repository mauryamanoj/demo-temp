package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
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
 * Represents the TextTemplate .
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TextTemplate {

  /**
   * showPattern.
   */
  @ValueMapValue
  private Boolean showPattern;
  /**
   * largeTitle.
   */
  @ValueMapValue
  private String largeTitle;
  /**
   * title.
   */
  @ValueMapValue
  private String title;
  /**
   * html.
   */
  @ValueMapValue
  private String html;
  /**
   * image.
   */
  @ValueMapValue
  private String image;

  /**
   * customAction.
   */
  @ChildResource
  private CustomAction customAction;

  /**
   * cta.
   */
  @ValueMapValue
  private Cta cta;
  /**
   * resourceResolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  @PostConstruct
  void init() {
    if (StringUtils.isNotBlank(image)) {
      image = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, image,
          settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }
  }

}
