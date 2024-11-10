package com.saudi.tourism.core.models.components.guide;

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
 * Card  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Card {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Card Title.
   */
  @ValueMapValue
  @Expose
  private String cardTitle;

  /**
   * Image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image;


  /**
   * Card Cta Label.
   */
  @ValueMapValue
  @Expose
  private String cardCtaLabel;

  /**
   *  Card Ctal Link.
   */
  @ValueMapValue
  @Expose
  private String cardCtaLink;

  /**
   *  Card cta Eevent.
   */
  @ValueMapValue
  @Expose
  private String ctaEevent;

  /**
   * Cards.
   */
  @ChildResource
  @Setter
  @Expose
  private CtaData ctaData;
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
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-660x337", "crop-375x280",
        "1280", "420", currentResource.getResourceResolver(), currentResource);
    cardCtaLink = LinkUtils.getAuthorPublishUrl(resourceResolver, cardCtaLink,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
