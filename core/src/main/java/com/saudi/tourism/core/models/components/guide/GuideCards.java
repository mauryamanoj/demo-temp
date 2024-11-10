package com.saudi.tourism.core.models.components.guide;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * GuideCards Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class GuideCards {
  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * items.
   */
  @ValueMapValue
  private boolean isItems;

  /**
   * type.
   */
  @Default(values = "GuideCards")
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
   * backgroundImage.
   */
  @ValueMapValue
  @Expose
  private String backgroundImage;

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
    if (isItems) {
      type = "GuideCardsItem";
    } else {
      type = "GuideCardsSlider";
    }
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
