package com.saudi.tourism.core.models.components.map.v1;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;


/**
 * This Class contains InterestPointsModel details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class InterestPointsModel {

  /**
   * Saudi Tourism Configurations.
   */
  @Inject
  @JsonIgnore
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * google API key.
   */
  private String googleMapsKey;

  /**
   * List of links.
   */
  @ChildResource
  @Expose
  private List<InterestPoint> interestPoints;
  /**
   * Map Type.
   */
  @ValueMapValue
  @Expose
  private String mapType;
  /**
   * The data.
   */
  @Setter
  @JsonIgnore
  private String data;

  /**
   * Retrieve key from config.
   */
  @PostConstruct protected void setKey() {
    try {
      googleMapsKey = saudiTourismConfig.getGoogleMapsKey();
      GsonBuilder gsonBuilder = new GsonBuilder();
      gsonBuilder.excludeFieldsWithoutExposeAnnotation();
      Gson gson = gsonBuilder.create();
      setData(gson.toJson(this));
    } catch (Exception e) {
      LOGGER.debug("Could not get OSGI object.");
    }
  }
}
