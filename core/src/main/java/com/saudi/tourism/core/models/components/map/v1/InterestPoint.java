package com.saudi.tourism.core.models.components.map.v1;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Model class of link InterestPoint.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class InterestPoint {

  /**
   * Variable of link url.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Variable of link image path.
   */
  @ValueMapValue
  private String cardImage;

  /**
   * Variable of link s7 image path.
   */
  @ValueMapValue
  private String s7cardImage;

  /**
   * Variable of image alt.
   */
  @ValueMapValue
  private String alt;
  /**
   * Variable of address.
   */
  @ValueMapValue
  @Expose
  private String address;
  /**
   * Variable of phone.
   */
  @ValueMapValue
  @Expose
  private String phone;
  /**
   * Variable of hour.
   */
  @ValueMapValue
  @Expose
  private String hour;
  /**
   * Variable of description.
   */
  @ValueMapValue
  @Expose
  private String description;
  /**
   * Variable of latitude.
   */
  @ValueMapValue
  @Expose
  private String latitude;
  /**
   * Variable of longitude.
   */
  @ValueMapValue
  @Expose
  private String longitude;
  /**
   * Variable of linkText.
   */
  @ValueMapValue
  @Expose
  private String linkText;
  /**
   * Variable of linkUrl.
   */
  @ValueMapValue
  @Expose
  private String linkUrl;
  /**
   * Variable of openNewTab.
   */
  @ValueMapValue
  @Expose
  private boolean openNewTab;

  /**
   * Variable of image.
   */
  @Expose
  private Image image = new Image();

  /**
   * Resolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * Resource.
   */
  @Self
  @JsonIgnore
  private transient Resource resource;

  /**
   * Setting service.
   */
  @OSGiService
  @JsonIgnore
  private SlingSettingsService settingsService;

  /**
   * Retrieve key from config.
   */
  @PostConstruct
  protected void init() {
    image.setFileReference(cardImage);
    image.setS7fileReference(s7cardImage);
    image.setAlt(alt);
    DynamicMediaUtils.setAllImgBreakPointsInfo(
        image,
        CropEnum.CROP_375x210.getValue(),
        CropEnum.CROP_375x210.getValue(),
        BreakPointEnum.DESKTOP.getValue(),
        BreakPointEnum.MOBILE.getValue(),
        resolver,
        resource);
    linkUrl = LinkUtils.getAuthorPublishUrl(resolver, linkUrl,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }



}
