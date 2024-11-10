package com.saudi.tourism.core.models.components.singleapppromotionalbanner.v1;

import java.util.List;
import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class SingleAppPromotionalBannerModel {

  /**
   * currentResource.
   */
  @Self
  private Resource currentResource;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;

  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * CTA Analytics Data.
   */
  @ChildResource
  @Expose
  private CtaData ctaData;

  /**
   *
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Stores.
   */
  @ChildResource
  @Expose
  private List<StoreModel> stores;

  @PostConstruct
  protected void init() {
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-600x600",
        "crop-600x600", "1280", "420",
        resourceResolver, currentResource);
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
