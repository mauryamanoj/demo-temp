package com.saudi.tourism.core.models.components.title.v2;

import com.adobe.cq.wcm.style.ComponentStyleInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.common.Link;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

/** This class contains the C02-Title component details. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TitleModel {
  /**
   * Component Style.
   */
  @Self
  private ComponentStyleInfo componentStyleInfo;

  /**
   * Component Heading.
   */
  @Self
  private ComponentHeading componentHeading;

  /**
   * Heading.
   */
  @Expose
  private Heading heading;

  /**
   * Title.
   */
  @Expose
  private Link link;

  /**
   * Variable indicate heading showUnderline.
   */
  @Expose
  private boolean showUnderline = true;

  /**
   * Background.
   */
  @Expose
  private String background = null;

  /**
   * Init.
   */
  @PostConstruct
  void init() {
    if (componentHeading != null) {
      heading = componentHeading.getHeading();
      link = componentHeading.getLink();
    }
    if (componentStyleInfo != null) {
      background = componentStyleInfo.getAppliedCssClasses();
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
