package com.saudi.tourism.core.models.components.header;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Author  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Author {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * authorText.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String authorText;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;


  /**
   *  Author Cta Link.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String authorCtaLink;


  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-375x280", "crop-375x280",
        "420", "420", currentResource.getResourceResolver(), currentResource);
    authorCtaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, authorCtaLink,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
