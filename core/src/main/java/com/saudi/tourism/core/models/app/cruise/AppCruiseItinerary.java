package com.saudi.tourism.core.models.app.cruise;

import com.adobe.cq.export.json.ExporterConstants;
import lombok.Getter;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Itinerary item for App Cruise information.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class AppCruiseItinerary implements Serializable {

  /**
   * Title of the itinerary item.
   */
  @ValueMapValue
  private String title;

  /**
   * Description of the itinerary item.
   */
  @ValueMapValue
  private String description;

  /**
   * Image for the itinerary item.
   */
  @ValueMapValue
  private String image;

  /**
   * Start point.
   */
  @ValueMapValue
  private String from;

  /**
   * Finish point.
   */
  @ValueMapValue
  private String to;

}
