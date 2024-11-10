package com.saudi.tourism.core.models.components.destinationguide;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * DestinationGuideCards Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class DestinationGuideCards {
  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;
  /**
   * type.
   */
  @Default(values = "DestinationGuideCards")
  @ValueMapValue
  @Expose
  private String type;

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
   * background.
   */
  @ChildResource
  @Setter
  @Expose
  private Image background;

  /**
   * haveBackgroundImage.
   */
  @Expose
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  private boolean haveBackgroundImage;

  /**
   * Link.
   */
  @ChildResource
  @Getter
  @Expose
  private Link link;

  /**
   * Cards.
   */
  @ChildResource
  @Setter
  @Expose
  private List<Card> cards;

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
    if (link != null && StringUtils.isNotBlank(link.getUrl())) {
      link.setUrl(LinkUtils.getAuthorPublishUrl(resourceResolver, link.getUrl(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    }
    DynamicMediaUtils.setAllImgBreakPointsInfo(background, "crop-1920x768", "crop-375x480",
        "1280", "420", currentResource.getResourceResolver(), currentResource);
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
