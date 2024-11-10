package com.saudi.tourism.core.models.components.map.v1;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


/**
 * This Class contains SimpleSlider details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class MapModel {

  /**
   * Saudi Tourism Configurations.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * Component title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Title Weight.
   */
  @ValueMapValue
  @Expose
  private String titleWeight;

  /**
   * Component latitude.
   */
  @ValueMapValue
  @Expose
  private Double latitude;

  /**
   * Component longitude.
   */
  @ValueMapValue
  @Expose
  private Double longitude;

  /**
   * Map Type.
   */
  @ValueMapValue
  @Expose
  private String mapType;

  /**
   * Property zoom.
   */
  @ValueMapValue
  @Expose
  private String zoom;

  /**
   * Map API key.
   */
  @Expose
  private String mapKey;
  /**
   * Google Map API key.
   */
  @Expose
  private String googleMapsKey;
  /**
   * Ornament ID.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String ornamentId;

  /**
   * Variant.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String variant;

  /**
   * List of links.
   */
  @ChildResource()
  @Expose
  private List<Link> links;

  /**
   * Resource Resolver object.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Resource Object.
   */
  @SlingObject
  private Resource resource;

  /**
   * Retrieve key from config.
   */
  @PostConstruct protected void setKey() {
    try {
      setVariant(Optional.ofNullable(variant).orElse(Constants.MAP_COMPONENT_DEFAULT_VARIANT));
      setOrnamentId(Optional.ofNullable(ornamentId).
          orElse(Constants.MAP_COMPONENT_DEFAULT_ORNAMENT));

      mapKey = saudiTourismConfig.getMapKey();
      googleMapsKey = saudiTourismConfig.getGoogleMapsKey();
      PageManager pageManager = resourceResolver.adaptTo(PageManager.class);

      if (zoom == null) {
        zoom = Constants.DEFAULT_ZOOM;
      }
      if (!links.isEmpty() && Objects.nonNull(pageManager)) {
        Page page = pageManager.getContainingPage(resource.getPath());

        String vendorName = CommonUtils.getVendorName(page);
        String packageName = CommonUtils.getPackageName(page);

        for (Link link : links) {
          link.setAppEventData(CommonUtils
              .getAnalyticsEventData(Constants.DEFAULT_TOUR_PACKAGE_HERO_EVENT,
                  LinkUtils.getAppFormatUrl(link.getUrl()),
                  StringUtils.defaultIfBlank(link.getTitle(), link.getCopy()), vendorName,
                  packageName));
        }
      }
    } catch (Exception e) {
      LOGGER.debug("Could not get OSGI object.");
    }
  }
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
