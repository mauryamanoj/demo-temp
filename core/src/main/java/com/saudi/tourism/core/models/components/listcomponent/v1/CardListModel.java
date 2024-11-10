package com.saudi.tourism.core.models.components.listcomponent.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.LinkedList;
import java.util.List;

/**
 * Class to handle list of cards, to be used in List and Gallery components.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class CardListModel {

  /**
   * Variable to hold component title.
   */
  @ChildResource
  @Expose
  private Heading componentTitle;

  /**
   * The full header to replace {@link #componentTitle} of class {@link Heading}.
   * Also, this is a workaround property to have proper header here for component-title template
   * for resources that don't have child like this.
   */
  @ChildResource
  @Expose
  private ComponentHeading header;

  /**
   * Variable to hold list of cards.
   */
  @ChildResource
  @Expose
  private List<CardModel> cards = new LinkedList<>();

  /**
   * Returns component's header object.
   *
   * @return ComponentHeading object
   */
  public ComponentHeading getHeader() {
    if (null == header && null != this.componentTitle) {
      header = new ComponentHeading();
      header.setHeading(this.getComponentTitle());
    }

    return header;
  }

  /**
   * getJson method for list component.
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
