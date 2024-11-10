package com.saudi.tourism.core.models.components.highlights;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import javax.annotation.PostConstruct;
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
 * Highlights Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class HighlightsModel {

  /**
   * Headline.
   */
  @ValueMapValue
  private String headline;

  /**
   * header.
   */
  @ChildResource
  @Setter
  @Expose
  private Header header;

  /**
   * Show More Text.
   */
  @ValueMapValue
  @Expose
  private String showMoreText;

  /**
   * Show Less Text.
   */
  @ValueMapValue
  @Expose
  private String showLessText;

  /**
   * Highlight Items.
   */
  @ChildResource
  @Setter
  private List<HighLightItem> items;

  /**
   * Highlight Items.
   */
  @Setter
  @Expose
  private List<HighLightItem> cards;

  /**
   * Component ID.
   */
  @ValueMapValue
  private String componentId;

  /**
   * Component ID.
   */
  @Expose
  @Setter
  private String scrollId;


  @PostConstruct
  public void init() {
    if (null == header) {
      header = new Header();
    }
    header.setTitle(headline);
    setScrollId(componentId);
    setCards(items);
  }

  /**
   * getJson method for Highlights component.
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
