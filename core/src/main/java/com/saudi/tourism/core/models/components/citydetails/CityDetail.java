package com.saudi.tourism.core.models.components.citydetails;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import javax.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * City Detail.
 */
@Model(adaptables = {
    Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class CityDetail {

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String readMoreText;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String readLessText;

  /**
   * Map Details.
   */
  @ChildResource
  @Setter
  @Expose
  private MapDetail mapDetails;

  /**
   * When to Visit.
   */
  @ChildResource
  @Setter
  @Expose
  private Details whenToVisit;

  /**
   * What to wear.
   */
  @ChildResource
  @Setter
  @Expose
  private Details whatToWear;

  /**
   * Transportation.
   */
  @ChildResource
  @Setter
  @Expose
  private Details transportation;

  /**
   * Travel Regulations.
   */
  @ChildResource
  @Setter
  @Expose
  private Details travelRegulations;

  /**
   * Component ID.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /**
   * Custom Branding.
   */
  @ValueMapValue
  @Expose
  private String enableCustomBranding;

  /**
   * getJson method for About city details. component.
   *
   * @return json representation.
   */
  @PostConstruct
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

}
