package com.saudi.tourism.core.models.components.packages;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Model contains configuration for Packages Signup Form.
 */
@Getter
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = PackagesSignupFormConfig.RT_ADMIN_PACKAGE_FORM_CONFIG)
@Slf4j
public class PackagesSignupFormConfig implements Serializable {

  /**
   * Resource type for package-form-config component.
   */
  public static final String RT_ADMIN_PACKAGE_FORM_CONFIG =
      "sauditourism/components/structure/admin/package-form-config";

  /**
   * Form title.
   */
  @ValueMapValue
  private String title;

  /**
   * Form subtitle.
   */
  @ValueMapValue
  private String subtitle;
  /**
   * Form subtitle.
   */
  @ValueMapValue
  private String submit;
  /**
   * Form requiredTip.
   */
  @ValueMapValue
  private String requiredTip;
  /**
   * Form termsAgreementCopy.
   */
  @ValueMapValue
  private String termsAgreementCopy;
  /**
   * Form subtitle.
   */
  @ChildResource(name = "confirmationCopies")
  private LeadFormSuccessError lead;
  /**
   * Form subtitle.
   */
  @ChildResource
  private InputsModel inputs;
  /**
   * Form fields.
   */
  private List<InputModel> fields;

  /**
   * List of countries built from countries datasource, localized
   * (/apps/sauditourism/components/content/utils/country).
   */
  private List<Map<String, Object>> countries;

  /**
   * Json configuration for confirmation.
   */
  private String confirmationCopies;

  /**
   * The current resource.
   */
  @JsonIgnore
  @Self(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient Resource currentResource;

  /**
   * The read only resource resolver provider.
   */
  @JsonIgnore
  @OSGiService
  private transient UserService resourceResolverProvider;

  /**
   * I18n resource bundle provider.
   */
  @JsonIgnore
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
               injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (nullified in PostConstruct).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * This model after construct initialization.
   *
   * @throws JsonProcessingException if can't produce json for confirmation config
   */
  @PostConstruct private void init() throws JsonProcessingException {
    final String language = CommonUtils.getLanguageForPath(currentResource.getPath());

    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));

    // Prepare countries map
    // TODO move to service and cache countries list in memory, as it's slow iteration
    try (ResourceResolver resolver = resourceResolverProvider.getResourceResolver()) {
      final String datasourcePath = "sauditourism/components/content/utils/country";
      final Resource datasourceResource = resolver.getResource(datasourcePath);
      assert datasourceResource != null;

      final Stream<Resource> datasourceItemsStream =
          StreamSupport.stream(datasourceResource.getChildren().spliterator(), false);

      this.countries = datasourceItemsStream.map(Resource::getValueMap).map(vm -> {
        final HashMap<String, Object> props = new HashMap<>(vm);
        props.remove(JcrConstants.JCR_PRIMARYTYPE);
        props.compute("text",
            (key, value) -> Optional.ofNullable(value).map(v -> i18n.getString(value.toString()))
                .orElse(null));
        return props;
      }).collect(Collectors.toList());
    }

    // Prepare other fields
    prepareFormInputs();
    prepareConfirmationConfig();

    // Cleanup
    this.i18nProvider = null;
    this.resourceResolverProvider = null;
    this.currentResource = null;
    this.settingsService = null;
  }

  /**
   * @throws JsonProcessingException if can't produce json for confirmation config
   */
  private void prepareConfirmationConfig() throws JsonProcessingException {
    final Resource confirmationResource = this.currentResource.getChild("confirmationCopies");
    if (confirmationResource == null) {
      LOGGER.error("Packages Signup Form is not configured!");

      // Return default config
      this.confirmationCopies = "{\"exit\":{\"subtitle\":\"Are you sure to exit?\","
          + "\"denyCopy\":\"No\",\"confirmCopy\":\"Yes\",\"title\":\"Exit\"},"
          + "\"success\":{\"cta\":{\"copy\":\"Explore All Packages\",\"href\":"
          + "\"/content/sauditourism/en/about-e-visa\"},\"subtitle\":\"Thanks for Request.\","
          + "\"copy\":\"<p>The agent will get in touch with you soon. If you haven’t received a"
          + " response within 48hrs please contact the provider."
          + "</p>\\r\\n\",\"title\":\"Booking Successful!\"},\"error\":{\"cta\":{\"copy\":"
          + "\"Explore All Packages\",\"href\":\"#\"},\"subtitle\":\"Error Occurred.\",\"copy\":"
          + "\"<p>There was an error, please try again. or contact directly\\r\\n\","
          + "\"title\":\"Oops\"}}";
      return;
    }

    // Build Map of properties for child resources and produce json
    Map<String, Object> confirmationCopiesMap = getDataMap(confirmationResource);

    this.confirmationCopies =
        RestHelper.getObjectMapper().writeValueAsString(confirmationCopiesMap);
  }

  /**
   * Visit recursively and build data map.
   *
   * @param resource resource
   * @return datamap of node and properties
   */
  private Map<String, Object> getDataMap(final Resource resource) {
    Map<String, Object> dataMap = new HashMap<>();
    if (Objects.nonNull(resource) && resource.hasChildren()) {
      Iterator<Resource> resourceIterator = resource.listChildren();
      while (resourceIterator.hasNext()) {
        Resource childResource = resourceIterator.next();
        ValueMap props = childResource.getValueMap();
        Map<String, Object> childMap = new HashMap<>(props);
        childMap.remove(JcrConstants.JCR_PRIMARYTYPE);
        // iterate upto level 2 only
        if (childResource.hasChildren()) {
          Iterator<Resource> subResourceIterator = childResource.listChildren();
          while (subResourceIterator.hasNext()) {
            Resource subResource = subResourceIterator.next();
            ValueMap subProps = subResource.getValueMap();
            Map<String, Object> subChildMap = new HashMap<>(subProps);
            subChildMap.remove(JcrConstants.JCR_PRIMARYTYPE);
            childMap.put(subResource.getName(), subChildMap);
          }
        }
        dataMap.put(childResource.getName(), childMap);
      }
    }
    return dataMap;
  }

  /**
   * Prepares all form inputs.
   */
  private void prepareFormInputs() {
    fields = new ArrayList<>();
    fields.add(inputs.getFirstName());
    fields.add(inputs.getLastName());
    fields.add(inputs.getEmail());
    fields.add(inputs.getPhone());
    fields.add(inputs.getNationality());
    fields.add(inputs.getComment());
  }

}
