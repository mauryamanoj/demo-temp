package com.saudi.tourism.core.models.components.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.SaudiConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;

/**
 * The Class AccountInputsModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class AccountInputsModel implements Serializable {

  /**
   * firstName.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel firstName;

  /**
   * lastName.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel lastName;

  /**
   * genderCode.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel genderCode;

  /**
   * ageRange.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel ageRange;

  /**
   * travelPartner.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel travelPartner;

  /**
   * interests.
   */
  @ChildResource
  @Expose
  private AccountPropertiesModel interests;

  /**
   * The User service.
   */
  @Inject
  @JsonIgnore
  private transient UserService userService;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * currentResource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct public void init() {
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        final String language = CommonUtils.getLanguageForPath(currentResource.getPath());
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

        interests.setOptions(
            getAppDialogItems(resolver, SaudiConstants.INTERESTS_CONTENT_UTILS_PATH, i18nBundle,
                true));
        travelPartner.setOptions(
            getAppDialogItems(resolver, SaudiConstants.TRAVEL_PARTNERS_CONTENT_UTILS_PATH,
                i18nBundle, true));
        ageRange.setOptions(
            getAppDialogItems(resolver, SaudiConstants.AGE_RANGES_CONTENT_UTILS_PATH, i18nBundle,
                true));
        genderCode.setOptions(
            getAppDialogItems(resolver, SaudiConstants.GENDERS_CONTENT_UTILS_PATH, i18nBundle,
                true));
      }
    } catch (Exception e) {
      LOGGER.error("Errror {}", e);
    }

  }

  /**
   * Gets app dialog items.
   *
   * @param resolver        the current resource resolver
   * @param dialogItemsPath the dialog items path
   * @param i18n            the current locale resource bundle
   * @param camelCase       should id be formatted to camel case
   * @return the app dialog items
   */
  private List<InputOptionsModel> getAppDialogItems(final ResourceResolver resolver,
      final String dialogItemsPath, final ResourceBundle i18n, final boolean camelCase) {

    try {
      Stream<Resource> resourceStream =
          Optional.ofNullable(dialogItemsPath).map(path -> dialogItemsPath)
              .filter(StringUtils::isNotBlank).map(resolver::getResource).map(Resource::getChildren)
              .map(res -> StreamSupport.stream(res.spliterator(), false)).orElse(Stream.empty());

      return resourceStream
          .map(resource -> this.getAppFilterItem(resource, i18n, camelCase))
          .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error in APP filter listing");

    }
    return new ArrayList<>();
  }

  /**
   * Gets app filter item.
   *
   * @param resource   the value map
   * @param i18nBundle i18n bundle for the current language
   * @param camelCase  should id be formatted to camel case
   * @return the app filter item
   */
  private InputOptionsModel getAppFilterItem(final Resource resource,
      final ResourceBundle i18nBundle, boolean camelCase) {
    @NotNull ValueMap valueMap = resource.getValueMap();
    String label = valueMap.get("text").toString();
    String value = valueMap.get("value").toString();
    if ("".equals(value)) {
      value = AppUtils.stringToID(label, camelCase);
    }
    if (Objects.nonNull(i18nBundle)) { // update city to i18n value
      label = i18nBundle.getString(value);
    }
    @Nullable Resource image = resource.getChild(PN_IMAGE);
    InputOptionsModel input = new InputOptionsModel(label, value);
    if (image != null) {
      input.setImage(image.adaptTo(Image.class));
    }

    return input;
  }

}
