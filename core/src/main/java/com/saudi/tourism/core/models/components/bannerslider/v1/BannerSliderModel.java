package com.saudi.tourism.core.models.components.bannerslider.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * BannerSlider Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class BannerSliderModel {

  /**
   * hide image brush.
   */
  @ValueMapValue
  @Expose
  private String hideImageBrush;

  /**
   * Cards.
   */
  @ChildResource
  @Setter
  @Expose
  private List<Card> cards;

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
