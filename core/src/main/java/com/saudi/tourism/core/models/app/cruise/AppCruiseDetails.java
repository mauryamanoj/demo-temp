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
 * Cruise details model.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = AppCruiseDetails.RT_APP_CRUISE_DETAILS)
public class AppCruiseDetails implements Serializable {

  /**
   * Resource type for this model.
   */
  static final String RT_APP_CRUISE_DETAILS =
      "sauditourism/components/app-content/cruise/v1/app-cruise-details";

  /**
   * Title for details.
   */
  @ValueMapValue
  private String title;

  /**
   * Cruse details items.
   */
  @ChildResource
  private List<AppCruiseDetail> items;

}
