package com.saudi.tourism.core.models.components.promotional;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
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
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * PromotionalSection Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class PromotionalSectionModel {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;
  /**
   *
   * Resource resolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  @JsonIgnore
  private transient ResourceResolver resolver;
  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * subTitle.
   */
  @ValueMapValue
  @Expose
  private String subTitle;

  /**
   * ctaLabel.
   */
  @ValueMapValue
  @Expose
  private String ctaLabel;

  /**
   * ctaLink.
   */
  @ValueMapValue
  @Expose
  private String ctaLink;

  /**
   * CTA Analytics Data.
   */
  @ChildResource
  @Expose
  private CtaData ctaData;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;

  /**
   * init method.
   */
  @PostConstruct
  protected void init() {
    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    // Get the best image to be displayed (s7 image or dam image if s7 is empty)
    if (image != null) {
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-275x275", "crop-275x275",
          "1280", "420", resourceResolver, currentResource);
    }
    ctaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, ctaLink,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
