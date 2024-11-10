package com.saudi.tourism.core.models.components.simpleTitleIntro;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * I04 Simple Title Intro Model.
 *
 * @author balkenov
 */

@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class SimpleTitleIntroModel {

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
   * Large Text size for copy.
   */
  @ValueMapValue
  @Expose
  private boolean largeTextSize;

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
