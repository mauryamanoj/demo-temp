package com.saudi.tourism.core.models.components.anchors.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/** Links Model. */
@Model(
    adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class AnchorsModel {

  /**
   * Title of Links component.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * boolean to add a line or not under the title.
   */
  @ValueMapValue
  @Expose
  private boolean line;

  /**
   * the links to show in the component.
   */
  @ChildResource
  @Expose
  @Setter
  private List<LinkWithNumber> links;

  /**
   * boolean to show in responsive mode.
   */
  @ValueMapValue
  @Expose
  private boolean showInResponsive;

  /**
   * getJson method for Links component.
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
