package com.saudi.tourism.core.models.components.packages;

import com.day.cq.wcm.api.Page;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.servlets.PackageFormServlet;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.SaudiConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_TITLE;

/**
 * Model for packages-signup (Package Contact Form) component.
 */
@Getter
@Model(adaptables = SlingHttpServletRequest.class,
       cache = true)
@Slf4j
public class PackagesSignupFormModel implements Serializable {

  /**
   * Form title.
   */
  private String title;

  /**
   * Form Subtitle.
   */
  private String subtitle;
  /**
   * Form submit Label.
   */
  private String submit;
  /**
   * Form termsAgreementCopy Text.
   */
  private String termsAgreementCopy;

  /**
   * Api path for form post request (/bin/api/v1/package-form).
   */
  private String apiPath = PackageFormServlet.POST_URL;

  /**
   * List of countries built from countries datasource
   * (/apps/sauditourism/components/content/utils/country).
   */
  private List<Map<String, Object>> countryList;

  /**
   * Fields of signup form.
   */
  private Map<String, Map<String, String>> inputs;

  /**
   * Confirmation json config.
   */
  private String confirmationCopies;

  /**
   * card json config.
   */
  private String card;

  /**
   * The Current page.
   */
  @ScriptVariable
  private transient Page currentPage;

  /**
   * The read only resource resolver provider.
   */
  @JsonIgnore
  @OSGiService
  private transient UserService resourceResolverProvider;

  /**
   * Package request form configuration model.
   */
  private PackagesSignupFormConfig config = null;
  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Model initialization.
   */
  @PostConstruct private void init() {
    String language = CommonUtils.getLanguageForPath(currentPage.getPath());
    buildConfigForLanguage(language);
    if (config != null) {
      // Just copy all fields from config
      this.title = config.getTitle();
      this.subtitle = config.getSubtitle();
      this.submit = config.getSubmit();
      this.termsAgreementCopy = config.getTermsAgreementCopy();
      this.countryList = config.getCountries();
      Map<String, String> vendorPlaceholders = new HashMap<>();
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        String vendor = SaudiConstants.PACKAGE_DMC;
        Resource jcrRes = currentPage.getContentResource();
        if (jcrRes.getValueMap().containsKey(SaudiConstants.PACKAGE_DMC)) {

          vendor = jcrRes.getValueMap().get(SaudiConstants.PACKAGE_DMC).toString();

        } else  {
          CommonUtils.readDmcFromPath(null, currentPage.getPath());

        }
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
        vendorPlaceholders.put(SaudiConstants.PACKAGE_DMC, i18nBundle.getString(vendor));
        vendorPlaceholders
            .put(SaudiConstants.PACKAGE_DMC + "-link", i18nBundle.getString(vendor + "-link"));
        setupCardData(i18nBundle, language);
      }
      String copies =
          AdminUtil.replaceVendorPlaceholders(config.getConfirmationCopies(), vendorPlaceholders);

      this.confirmationCopies = AdminUtil.replaceVendorPlaceholders(copies, vendorPlaceholders);
    }


    // Cleanup
    this.currentPage = null;
    this.resourceResolverProvider = null;
  }

  /**
   * SetUp Card data.
   *
   * @param i18nBundle i18nBundle
   * @param language   langauge
   */
  private void setupCardData(final ResourceBundle i18nBundle, final String language) {
    try {
      PackageDetail packageDetail = currentPage.getContentResource().adaptTo(PackageDetail.class);
      if (Objects.nonNull(packageDetail)) {
        String image = packageDetail.getCardImage();
        if (StringUtils.isBlank(image)) {
          image = packageDetail.getBannerImage();
        }
        Map<String, String> cardMap = new HashMap<>();
        cardMap.put(PN_TITLE, packageDetail.getTitle());
        cardMap.put("description", getAreaName(packageDetail, i18nBundle, language));
        cardMap.put("priceCopy", packageDetail.getPriceDisplay());
        cardMap.put("daysCopy", packageDetail.getDuration().getValue());
        cardMap.put(PN_IMAGE, image);
        this.card = RestHelper.getObjectMapper().writeValueAsString(cardMap);
      }
    } catch (Exception ex) {
      LOGGER.error("Exception Occurred in Setup Card data for packages.");
    }
  }

  /**
   * Get AreaName for Card.
   *
   * @param packageDetail PackageDetail.
   * @param i18nBundle 18nBundle
   * @param language   language
   * @return Area Name. if single name than return city name else multiple location
   */
  private String getAreaName(final PackageDetail packageDetail, final ResourceBundle i18nBundle,
      final String language) {
    String areaName = "";
    String[] areas = packageDetail.getPackageAreaTags();
    if (Objects.nonNull(areas)) {
      try (ResourceResolver resolver = resourceResolverProvider.getResourceResolver()) {
        List<String> areaList = CommonUtils
            .getCategoryFromTagName(areas, resolver, language);
        if (areaList.size() == NumberConstants.CONST_ONE) {
          areaName = areaList.get(0);
        } else if (areaList.size() > NumberConstants.CONST_ONE) {
          areaName = CommonUtils.getI18nString(i18nBundle, Constants.I18N_MULTIPLE_DESTINATION_KEY);
        }
      } catch (Exception ex) {
        LOGGER.error("Exception occured while getting area list.", ex);
      }
    }
    return areaName;
  }

  /**
   * Create PackagesSignupFormConfig model from the admin config resource according to language.
   * If config is not found for the current language, it returns config for "en".
   *
   * @param language language to get config resource
   */
  private void buildConfigForLanguage(final String language) {
    ResourceResolver resolver = null;
    try {
      resolver = resourceResolverProvider.getResourceResolver();
      final String configResourcePath = StringUtils
          .replaceEach(Constants.ADMIN_PACKAGE_FORM_CONFIG_PATH,
              new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language});

      final Resource configResource = resolver.getResource(configResourcePath);
      if (configResource == null) {
        LOGGER.error("Package form config resource not found {}", configResourcePath);
      } else {
        config = configResource.adaptTo(PackagesSignupFormConfig.class);

        if (config == null) {
          LOGGER.error("Couldn't adapt config resource to package form config {}",
              configResourcePath);
        }
      }

      // Try "en" language if failed
      if (config == null && !Constants.DEFAULT_LOCALE.equals(language)) {
        LOGGER.debug("Trying to get config from {}", Constants.DEFAULT_LOCALE);
        buildConfigForLanguage(Constants.DEFAULT_LOCALE);
      }
    } catch (Exception ex) {
      LOGGER.error("Exception Occurred while building config for language.", ex);
    } finally {
      if (resolver.isLive()) {
        resolver.close();
      }
    }
  }
}
