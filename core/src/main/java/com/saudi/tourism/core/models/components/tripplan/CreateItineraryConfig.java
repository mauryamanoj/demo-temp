package com.saudi.tourism.core.models.components.tripplan;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.servlets.SSIDTripPlanFilterServlet;
import com.saudi.tourism.core.servlets.SSIDTripPlanServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static com.saudi.tourism.core.models.components.tripplan.TripPlannerUtils.getTripPlannerDetailPageUrl;
import static com.saudi.tourism.core.utils.Constants.JSON_EXTENSION;

/**
 * Handles configuration for create itinerary (trip plan) wizard modal window.
 * <p>
 * This model is used only for creating Json output, so we don't need classes for child items,
 * HashMap&lt;String, String&gt; is used instead everywhere here. Can be refactored when we need
 * them.
 */
@Getter
@Model(adaptables = SlingHttpServletRequest.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class CreateItineraryConfig implements Serializable {

  /**
   * Resource type for create-itinerary-config.
   */
  public static final String RT_ADMIN_CREATE_ITINERARY_CONFIG =
      "sauditourism/components/structure/admin/create-itinerary-config";

  /**
   * Background image for the wizard.
   */
  @Expose
  private Map<String, String> backgroundImage = new HashMap<>();

  /**
   * Url to view/edit trip plan.
   */
  @Expose
  private String tripDetailsUrl;

  /**
   * Endpoint url to create trip plan.
   */
  @Expose
  private String createTripPlanEndpoint = SSIDTripPlanServlet.SERVLET_PATH;

  /**
   * Endpoint url to get cities.
   */
  @Expose
  private String citiesEndpoint = SSIDTripPlanFilterServlet.SERVLET_PATH + JSON_EXTENSION;

  /**
   * Steps copies.
   */
  @Expose
  private Map<String, TripStep> stepsCopies = new HashMap<>();

  /**
   * Modal copies.
   */
  @Expose
  private Map<String, ModalCopy> modalCopies = new HashMap<>();

  /**
   * Json representation for this model.
   */
  @JsonIgnore
  private transient String json;

  /**
   * The current resource.
   */
  @JsonIgnore
  @Self(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingHttpServletRequest request;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * This model after construct initialization.
   */
  @PostConstruct
  private void init() {
    Resource resource = request.getResource();
    final String language = CommonUtils.getLanguageForPath(resource.getPath());

    // Check if this model was adapted not from create itinerary config resource
    // (i.e. page component, in footer-config.html)
    if (!resource.isResourceType(RT_ADMIN_CREATE_ITINERARY_CONFIG)) {
      final ResourceResolver resolver = request.getResourceResolver();
      String createItineraryConfigResourcePath = StringUtils
          .replaceEach(Constants.ADMIN_ITINERARY_CONFIG_PATH,
              new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language});
      resource = resolver.getResource(createItineraryConfigResourcePath);
    }

    if (resource == null) {
      final String errorMessage =
          "Could not find create-itinerary-config resource for the request " + request
              .getPathInfo();
      LOGGER.debug(errorMessage);
      this.json = null;
      return;
    }

    extractDataFromResource(resource, language);

    // Store json output for the model. Currently it is produced only in this method, so model
    // changes won't update json, can be refactored when we need re-creating it.
    final ObjectMapper objectMapper = new ObjectMapper();
    try {
      this.json = objectMapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      final String errorMessage =
          "Could not produce json for create-itinerary-config resource " + resource.getPath()
              + StringUtils.SPACE + e.getMessage();
      LOGGER.error(errorMessage, e);
      final ResponseMessage errorResponse =
          new ResponseMessage(MessageType.ERROR.getType(), errorMessage);

      this.json = new Gson().toJson(errorResponse);
    }

    // Clear request before caching
    this.request = null;
    this.settingsService = null;
  }

  /**
   * Extracts all necessary data from the ValueMap of the provided resource.
   *
   * @param resource create-itinerary-config resource for the current locale
   * @param language the current locale
   */
  private void extractDataFromResource(final Resource resource, final String language) {
    // Resource props
    final ValueMap resourceValueMap = resource.getValueMap();
    this.backgroundImage.put("desktopImage", resourceValueMap.get("desktopImage", String.class));
    this.backgroundImage.put("mobileImage", resourceValueMap.get("mobileImage", String.class));
    this.backgroundImage.put("imageAlt", resourceValueMap.get("imageAlt", String.class));
    final ResourceResolver resolver = request.getResourceResolver();
    this.tripDetailsUrl = getTripPlannerDetailPageUrl(resolver,
        resourceValueMap.get("tripDetailsUrl", String.class),
        settingsService.getRunModes().contains(Externalizer.PUBLISH), language);

    // Fill steps
    final Resource stepsResource = resource.getChild("steps");
    if (stepsResource != null) {
      stepsCopies.put("tripNameStep", stepsResource.getChild("step1").adaptTo(TripNameStep.class));
      stepsCopies.put("citiesStep", stepsResource.getChild("step2").adaptTo(TripCitiesStep.class));
      stepsCopies
          .put("dateSelectionStep", stepsResource.getChild("step3").adaptTo(TripDatesStep.class));

      loadModalCopyToModalCopiesMap(resource, "loginModal", "login");
      loadModalCopyToModalCopiesMap(resource, "errorModal", "error");
      loadModalCopyToModalCopiesMap(resource, "closeModal", "close");
    }
  }

  /**
   * Load and put in map ModalCopy.
   * @param parent parent
   * @param nodeName nodeName
   * @param key key
   */
  private void loadModalCopyToModalCopiesMap(Resource parent, String nodeName, String key) {
    Resource res = parent.getChild(nodeName);
    if (res != null) {
      ModalCopy modalCopy;
      if (key.equals("close")) {
        modalCopy = res.adaptTo(ModalCloseCopy.class);
      } else {
        modalCopy = res.adaptTo(ModalCopy.class);
      }
      modalCopies.put(key, modalCopy);
    }
  }

  /**
   * TripStep.
   */
  @Data
  public static class TripStep implements Serializable {
    /**
     * label.
     */
    @Expose
    @ValueMapValue
    private String label;

    /**
     * title.
     */
    @Expose
    @ValueMapValue
    private String title;

    /**
     * buttonTitle.
     */
    @Expose
    @ValueMapValue
    private String buttonTitle;
  }

  /**
   * TripNameStep.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class TripNameStep extends TripStep {
    /**
     * inputPlaceholder.
     */
    @Expose
    @ValueMapValue
    private String inputPlaceholder;
  }

  /**
   * TripCitiesStep.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class TripCitiesStep extends TripStep {
    /**
     * multipleSelectionLabel.
     */
    @Expose
    @ValueMapValue
    private String multipleSelectionLabel;
  }

  /**
   * TripDatesStep.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class TripDatesStep extends TripStep {
    /**
     * dateLabel.
     */
    @Expose
    @ValueMapValue
    private String dateLabel;

    /**
     * Description.
     */
    @Expose
    @ValueMapValue
    private String description;
  }


  /**
   * ModalCopy.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class ModalCopy implements Serializable {
    /**
     * Title.
     */
    @Expose
    @ValueMapValue
    private String title;

    /**
     * Paragraph.
     */
    @Expose
    @ValueMapValue
    private String paragraph;

    /**
     * Primary button label.
     */
    @Expose
    @ValueMapValue
    private String primaryButtonLabel;
  }

  /**
   * Modal Close Copy.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class ModalCloseCopy extends ModalCopy {
    /**
     * Subtitle.
     */
    @Expose
    @ValueMapValue
    private String subtitle;

    /**
     * Secondary button label.
     */
    @Expose
    @ValueMapValue
    private String secondaryButtonLabel;
  }
}
