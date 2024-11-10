package com.saudi.tourism.core.models.app.cruise;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.app.common.AppPageRequestResultModel;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Exporters;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Model for handling Cruise data for APP (model is for app-cruise-page component).
 */
@Exporters({
               @Exporter(name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
                      extensions = ExporterConstants.SLING_MODEL_EXTENSION),
               @Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
                         name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
                         extensions = ExporterConstants.SLING_MODEL_EXTENSION)
})
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = Constants.RT_APP_CRUISE_PAGE)
@Slf4j
public class AppCruiseModel extends AppPageRequestResultModel {

  /**
   * Cruise title, jcr:title from page properties.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  private String title;

  /**
   * The Feature Image (main image for the cruise).
   */
  @ValueMapValue
  private String featuredImage;

  /**
   * Cruise CTA text.
   */
  @ValueMapValue
  private String ctaText;

  /**
   * Cruise CTA URL.
   */
  @ValueMapValue
  private String ctaUrl;

  /**
   * Cruise PDF CTA.
   */
  @ValueMapValue
  private String pdfCta;

  /**
   * Cruise PDF Link.
   */
  @ValueMapValue
  private String pdfLink;

  /**
   * Cruise subtitle, from page properties.
   */
  @ValueMapValue
  private String subtitle;

  /**
   * Cruise description, jcr:description from page properties.
   */
  @ValueMapValue(name = JcrConstants.JCR_DESCRIPTION)
  private String description;

  /**
   * Cruise details.
   */
  @ChildResource
  private AppCruiseDetails details;

  /**
   * Cruise itineraries.
   */
  @ChildResource
  private AppCruiseItineraries itineraries;

  /**
   * Cruise contact information.
   */
  @ChildResource
  private AppCruiseContactInformation contactInformation;

}
