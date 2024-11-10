package com.saudi.tourism.core.models.components.categories;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * GuideCards Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class CategoriesCards {

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * type.
   */
  @Default(values = "CategoryCards")
  @ValueMapValue
  @Expose
  private String type;

  /**
   * view.
   */
  @ValueMapValue
  @Expose
  private String view;

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * backgroundImage.
   */
  @ChildResource
  @ValueMapValue
  @Expose
  private Image backgroundImage;



  /**
   * Cards.
   */
  @ChildResource
  @Setter
  @Expose
  private List<Card> cards;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    DynamicMediaUtils.setAllImgBreakPointsInfo(backgroundImage, "crop-660x337", "crop-375x280",
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
