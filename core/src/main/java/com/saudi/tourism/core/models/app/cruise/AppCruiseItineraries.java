package com.saudi.tourism.core.models.app.cruise;

import com.adobe.cq.export.json.ExporterConstants;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * Itineraries for the App Cruise details.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = AppCruiseItineraries.RT_APP_CRUISE_ITINERARIES)
public class AppCruiseItineraries implements Serializable {

  /**
   * Resource type for this model.
   */
  static final String RT_APP_CRUISE_ITINERARIES =
      "sauditourism/components/app-content/cruise/v1/app-cruise-itineraries";

  /**
   * Title for the itineraries.
   */
  @ValueMapValue
  private String title;

  /**
   * Items of cruise itineraries.
   */
  @ChildResource
  private List<AppCruiseItinerary> items;

}
