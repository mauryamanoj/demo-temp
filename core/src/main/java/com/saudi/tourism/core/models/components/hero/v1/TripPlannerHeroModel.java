package com.saudi.tourism.core.models.components.hero.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.tripplan.CreateItineraryConfig;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.annotation.PostConstruct;

/**
 * Model used in Trip Planner Hero component.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class TripPlannerHeroModel {

  /**Secondary Hero Data.*/
  @Self
  @Expose
  private CommonHeroModel secondaryHeroData;

  /**
   * Trip Planner Modal Data.
   */
  @Self
  @Expose
  private CreateItineraryConfig tripPlannerModalData;

  /**
   * Trip Planner Sticky Data.
   */
  @Self
  @Expose
  private TripPlannerHeroSticky tripPlannerStickyData;

  @PostConstruct
  protected void initTripPlannerHeroModel() {
    tripPlannerStickyData = TripPlannerHeroSticky.builder()
      .title(secondaryHeroData.getTitle())
      .subtitle(secondaryHeroData.getSubtitle())
      .linkRef(secondaryHeroData.getItineraryPath())
      .linkCopy(secondaryHeroData.getCreateItineraryLabel())
      .ctaType(secondaryHeroData.getItineraryType())
      .build();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
