package com.saudi.tourism.core.models.components.mapwidget.v1;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;

/**
 * MapWidgetModel.
 */
@Model(
    adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class MapWidgetModel {

  /**
   * Resource Resolver.
   */
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource currentResource;

  /**
   * Reference of Saudi Tourism Configuration.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Map API KEY.
   */
  @Expose
  private String mapApiKey = StringUtils.EMPTY;

  /**
   * title.
   */
  @Expose
  private String title;

  /**
   * locationLabel.
   */
  @Expose
  private String locationLabel;

  /**
   * locationValue.
   */
  @Expose
  private String locationValue;

  /**
   * Latitude.
   */
  @Expose
  private String latitude;

  /**
   * Longitude.
   */
  @Expose
  private String longitude;

  /**
   * CTA Label.
   */
  @Expose
  private String ctaLabel;

  @PostConstruct
  void init() {
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return;
    }

    final var cfPath = currentPage.getProperties().get("referencedFragmentPath", String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfPath);
    if (cfResource == null) {
      return;
    }

    final var cfModel = cfResource.adaptTo(MapWidgetCFModel.class);
    if (cfModel == null) {
      return;
    }

    mapApiKey = saudiTourismConfigs.getGoogleMapsKey();
    title = cfModel.getTitle();
    locationLabel = cfModel.getLocationLabel();
    locationValue = cfModel.getLocationValue();
    latitude = cfModel.getLatitude();
    longitude = cfModel.getLongitude();
    ctaLabel = cfModel.getCtaLabel();
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
