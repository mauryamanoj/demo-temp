package com.saudi.tourism.core.models.common;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Model class of link related data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Link implements Serializable {

  /**
   * Variable hideMap.
   */
  @ValueMapValue
  @Expose
  private boolean hideMap;

  /**
   * Variable of link url.
   */
  @ValueMapValue
  @Expose
  private String url;

  /**
   * Variable of regionId.
   */
  @ValueMapValue
  @Expose
  private String regionId;

  /**
   * Variable of cityId.
   */
  @ValueMapValue
  @Expose
  private String cityId;

  /**
   * Variable of locationType.
   */
  @ValueMapValue
  @Expose
  private String locationType;

  /**
   * Variable of mapLink.
   */
  @ValueMapValue
  @Expose
  private String mapLink;

  /**
   * Variable of name.
   */
  @ValueMapValue
  @Expose
  private String name;

  /**
   * Variable of code.
   */
  @ValueMapValue
  @Expose
  private String code;

  /**
   * Variable of link title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Variable of link image path.
   */
  @ValueMapValue
  @Expose
  private String image;

  /**
   * Variable of link image path.
   */
  @ValueMapValue
  @Expose
  private String icon;

  /**
   * Image caption.
   */
  @ChildResource
  @Expose
  private ImageCaption caption;

  /**
   * Variable of image scene7 path.
   */
  @ValueMapValue
  @Expose
  private String s7image;

  /**
   * Variable of link type i.e. internal, external, contact, download.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String type;

  /**
   * Variable contains the url with extension.
   */
  @Expose
  private String urlWithExtension;

  /**
   * Variable contains the urlSlingExporter.
   */
  @Expose
  private String urlSlingExporter;

  /**
   * Variable of link copy.
   */
  @Expose
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private String copy;

  /**
   * Variable of link copy.
   */
  @Expose
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private String analyticsDataCopy;
  /**
   * Open link in a new window.
   */
  @Expose
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private boolean targetInNewWindow;

  /**
   * Variable for storing link text.
   */
  @Expose
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private String text;

  /**
   * App Event Data.
   */
  @Expose
  private String appEventData;

  /**
   * App type.
   */
  @Expose
  private String appType;

  /**
   * site url.
   */
  private String internalUrl;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * External mode.
   */
  @ValueMapValue
  private String externalMode;


  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    this.urlWithExtension = LinkUtils.getUrlWithExtension(url);
    this.urlSlingExporter = LinkUtils.getFavoritePath(url);
    final PageManager pageManager;
    this.internalUrl = this.url;
    if (resolver != null) {
      pageManager = resolver.adaptTo(PageManager.class);
      this.appType = AppUtils.getAppTypeFromLink(url, pageManager);
      if (settingsService.getRunModes().contains(Externalizer.PUBLISH)
          && LinkUtils.isInternalLink(this.url)) {
        this.url = resolver.map(this.url);
      }
    }
  }

  /**
   * Constructor.
   * @param url url
   * @param copy copy
   * @param targetInNewWindow targetInNewWindow
   */
  public Link(final String url, final String copy, final boolean targetInNewWindow) {
    this.url = url;
    this.copy = copy;
    this.targetInNewWindow = targetInNewWindow;
    init();
  }
}
