package com.saudi.tourism.core.models.components.contentfragment.category;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/** Model for Category content fragment. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class CategoryCFModel {
  /** Current resource. */
  @JsonIgnore
  @Self
  private Resource resource;

  /** Resource Resolver. */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /** Destination title. */
  @Expose
  private String title;

  /** Image Path. */
  @JsonIgnore
  private String imagePath;

  /** S7 Image Path. */
  @JsonIgnore
  private String s7ImagePath;

  /** Image. */
  @Expose
  private Image image;

  /** Icon. */
  @Expose
  private String icon;

  @PostConstruct
  void init() {
    if (resource != null) {
      var masterNode =
          resourceResolver.getResource(resource.getPath() + Constants.JCR_CONTENT_DATA_MASTER);

      if (masterNode != null) {
        title = masterNode.getValueMap().get("title", String.class);
        imagePath = masterNode.getValueMap().get("image", String.class);
        s7ImagePath = masterNode.getValueMap().get("s7image", String.class);
        icon = masterNode.getValueMap().get("icon", String.class);

        if (StringUtils.isNotEmpty(imagePath)) {
          image = new Image();
          image.setFileReference(imagePath);
          image.setS7fileReference(s7ImagePath);
        }
      }
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
