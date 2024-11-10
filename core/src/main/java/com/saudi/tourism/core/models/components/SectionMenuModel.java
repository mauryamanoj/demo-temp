package com.saudi.tourism.core.models.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.app.page.CardMenu;
import javax.annotation.PostConstruct;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;
/**
 * Section Menu Model.
 */
@Model(adaptables = {Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SectionMenuModel {

  /**
   * SectionMenuList multi field items.
   */
  @ChildResource
  @Expose
  private List<CardMenu> sectionMenuList;

  /**
   * getJson method for About Section menu component.
   *
   * @return json representation.
   */
  @PostConstruct
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }
}
