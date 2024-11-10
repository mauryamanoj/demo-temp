package com.saudi.tourism.core.models.components.reachus.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * Call Us Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class ReachUsModel {
  /**
   * Resource.
   */
  @SlingObject
  private Resource resource;
  /**
   * Card Type.
   */
  @ValueMapValue(name = "cardType")
  @Expose
  private String type;

  /**
   * Card Size.
   */
  @ValueMapValue(name = "displayForm")
  @Expose
  private String variation;

  /**
   * Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Phone Card.
   */
  @ChildResource
  @Expose
  private PhoneCardModel phoneCard;

  /**
   * Quick Link Card.
   */
  @ChildResource
  @Expose
  private QuickLinkCardModel quickLinkCard;

  /**
   * Social Media Cards.
   */
  @ChildResource
  @Expose
  private List<SocialMediaCardModel> socialMediaCards;


  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    if ("disabled".equals(type)) {
      // return empty JSON
      return gson.toJson(new JsonObject());
    }
    return gson.toJson(this);
  }
}
