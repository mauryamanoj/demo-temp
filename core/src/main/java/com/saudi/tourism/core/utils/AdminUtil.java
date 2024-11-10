package com.saudi.tourism.core.utils;

import com.saudi.tourism.core.models.components.AdminPageAlert;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.EventsFiltersSettings;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.models.components.RoadTripSettings;
import com.saudi.tourism.core.services.AdminSettingsService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.text.StrSubstitutor;

import java.util.Map;

/**
 * The type Admin util.
 */
@Slf4j
public final class AdminUtil {

  /**
   * Service for providing admin page settings.
   */
  @Getter
  @Setter
  private static AdminSettingsService adminSettingsService;

  /**
   * Instantiates a admin utils.
   */
  private AdminUtil() {
  }

  /**
   * Returns admin page options by language.
   * @param site sitename.
   * @param language language for getting options
   * @return admin page options instance
   */
  public static AdminPageOption getAdminOptions(final String language, final String site) {
    return adminSettingsService.getAdminOptions(language, site);
  }

  /**
   * use new getAdminAlert.
   * @param site site name.
   * @param language the language
   * @return the use new Header or Footer
   */
  public static AdminPageAlert getAdminAlert(String language, String site) {
    return adminSettingsService.getAdminAlert(language, site);
  }

  /**
   * use new getPackagePageSettings.
   *
   * @param language the language
   * @return the use new PackagePageSettings
   */
  public static PackagePageSettings getPackagePageSettings(String language) {
    return adminSettingsService.getPackagePageSettings(language);
  }

  /**
   * use new getEventsFilters.
   *
   * @param language the language
   * @return the use new tEventsFilters
   */
  public static EventsFiltersSettings getEventsFilters(String language) {
    return adminSettingsService.getEventsFilters(language);
  }




  /**
   * use new getRoadTripPageSettings.
   *
   * @param language the language
   * @return the use new roadTripSettings
   */
  public static RoadTripSettings getRoadTripPageSettings(String language) {
    return adminSettingsService.getRoadTripSettings(language);
  }

  /**
   * Replace vendor placeholders string.
   *
   * @param value              the value
   * @param vendorPlaceholders the vendor placeholders
   * @return the string
   */
  public static String replaceVendorPlaceholders(String value,
      Map<String, String> vendorPlaceholders) {
    try {
      StrSubstitutor substitute = new StrSubstitutor(vendorPlaceholders, "{{", "}}");
      value = substitute.replace(value);
      StrSubstitutor sub2 = new StrSubstitutor(vendorPlaceholders, "%7B%7B", "%7D%7D");
      value = sub2.replace(value);
    } catch (Exception e) {
      LOGGER.error("error in replaceVendorPlaceholders {}", e);
    }
    return value;
  }
}
