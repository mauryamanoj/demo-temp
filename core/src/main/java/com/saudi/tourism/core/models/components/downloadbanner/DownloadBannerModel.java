package com.saudi.tourism.core.models.components.downloadbanner;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Download Banner Application Model.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class DownloadBannerModel {

  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Subtitle.
   */
  @Expose
  @ValueMapValue
  private String subtitle;

  /**
   * Banner image.
   */
  @Expose
  @ChildResource
  private Image banner;

  /**
   * Title for Applications.
   */
  @Expose
  @ValueMapValue
  private String appstitle;

  /**
   * List of applications.
   */
  @Expose
  @ChildResource
  private List<Apps> applications;

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
