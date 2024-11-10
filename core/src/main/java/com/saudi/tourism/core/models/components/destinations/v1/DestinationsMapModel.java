package com.saudi.tourism.core.models.components.destinations.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

/** Model for destinations map component. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class DestinationsMapModel extends AllDestinationsModel {


  /**
   * Reference of Saudi Tourism Configuration.
   */
  @Inject
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Map Api Key.
   */
  @Expose
  private String mapApiKey;

  /**
   * Show Map Border.
   */
  @Expose
  private boolean showMapBorder;

  /**
   * CTA Link.
   */
  @ChildResource
  @Expose
  private Link link;

  @PostConstruct
  void init() {
    super.init();
    mapApiKey = saudiTourismConfigs.getGoogleMapsKey();
    showMapBorder = saudiTourismConfigs.getShowMapBorder();
  }
}
