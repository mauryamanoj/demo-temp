package com.saudi.tourism.core.models.components.offersanddeals.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Offers And Deals Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class OffersAndDealsModel {
  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;
  /**
   * Current resource.
   */
  @Self
  private Resource resource;
  /**
   * title offer and deal.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Type offer or deal.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * Background Transparent.
   * @ValueMapValue
   *   @Expose
   *   private Boolean bgTransparent;
   */

  /**
   * Add Ornament.
   */
  @ValueMapValue
  @Expose
  private Boolean isWithOrnament;

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;
  /**
   * Cards.
   */
  @ChildResource
  @Expose
  private List<Card> cards;

  /**
   * CTA Analytics Data.
   */
  @ChildResource
  @Expose
  private CtaData ctaData;

  @PostConstruct
  void init() {
    if (cards != null) {
      for (Card card : cards) {
        Image image = card.getImage();
        if (image != null) {
          DynamicMediaUtils.setAllImgBreakPointsInfo(
              image,
              "crop-600x600",
              "crop-600x600",
              "1280",
              "420",
              resource.getResourceResolver(),
              resource);
          card.setImage(image);
        }
      }
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
