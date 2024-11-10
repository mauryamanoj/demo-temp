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
 * Contact information model for the App Cruise details.
 */
@Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
          extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = AppCruiseContactInformation.RT_APP_CRUISE_CONTACT_INFO)
public class AppCruiseContactInformation implements Serializable {

  /**
   * Resource type for this model.
   */
  static final String RT_APP_CRUISE_CONTACT_INFO =
      "apps/sauditourism/components/app-content/cruise/v1/app-cruise-contact-information";

  /**
   * Contact Information - Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Contact Information - Message.
   */
  @ValueMapValue
  private String message;

  /**
   * Contact Information - phone.
   */
  @ValueMapValue
  private String phone;

}
