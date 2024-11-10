package com.saudi.tourism.core.models.app.location;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saudi.tourism.core.models.app.common.AppPageRequestResultModel;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Native App location detail page model.
 */
@Model(adaptables = Resource.class,
    resourceType = {Constants.APP_LOCATION_RESOURCE_TYPE},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(selector = Constants.MODEL_EXPORTER_SELECTOR,
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
          value = Constants.STR_TRUE)})
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Setter
@Slf4j
public class NativeAppLocationPageModel extends AppPageRequestResultModel {

  /**
   * Title.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  private String title;

  /**
   * Name.
   */
  @ValueMapValue
  private String name;


  /**
   * Type.
   */
  @ValueMapValue
  private String type;

  /**
   * Link to the corresponding attraction page (url with extension).
   */
  private String link;


  /**
   * Region Id.
   */
  private String regionId;

  /**
   * Region.
   */
  @ValueMapValue
  private String region;

  /**
   * City id.
   */
  @ValueMapValue(name = Constants.CITY)
  private String cityId;

  /**
   * City name (localized).
   */
  @ValueMapValue
  private String city;

  /**
   * Categories / tags for this attraction.
   */
  @ValueMapValue
  private String[] tags;

  /**
   * Thumbnail Image.
   */
  @JsonIgnore
  @ValueMapValue
  private String thumbnail;

  /**
   * Featured.
   */
  @ValueMapValue
  private boolean featured;

  /**
   * Image.
   */
  private String image;

  /**
   * Category tags.
   */
  private List<String> category;

  /**
   * The User service.
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient UserService userService;

  /**
   * ResourceBundleProvider.
   */
  @JsonIgnore
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
      injectionStrategy = InjectionStrategy.REQUIRED)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;


  /**
   * Cities service (to get cities by id).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient RegionCityService citiesService;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    this.image = this.thumbnail;
    final String pagePath = getPath();
    final String language = CommonUtils.getLanguageForPath(pagePath);

    final String favoriteId = this.getWebMappingPath();
    // Fix webMappingPath from favoriteId
    final String webMapping;
    if (StringUtils.isNotBlank(favoriteId)) {
      webMapping =
        Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language + favoriteId;
    } else {
      webMapping = null;
    }

    if (StringUtils.isNotBlank(this.region)) {
      final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
      // Workaround if the region text was broken when we switched to dropdown then reverted
      this.region = i18nBundle.getString(this.region);
    }

    if (StringUtils.isNotBlank(cityId)) {
      RegionCity cityObj = citiesService.getRegionCityById(cityId, language);
      if (cityObj != null) {
        setCity(cityObj.getName());
      }
    }
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      if (ArrayUtils.isNotEmpty(tags)) {
        setCategory(CommonUtils.getCategoryFromTagName(tags, resourceResolver, language));
        List<String> tagNames = CommonUtils.getTagNames(tags, resourceResolver);
        List<String> keys = new ArrayList<>();
        tagNames.forEach(key -> keys.add(AppUtils.stringToID(key)));
        Object[] keyArr = keys.toArray();
        setTags(Arrays.copyOf(keyArr, keyArr.length, String[].class));
      }
      // Correct link to the corresponding content page
      if (StringUtils.isNotBlank(webMapping)) {
        setLink(LinkUtils.getAuthorPublishUrl(resourceResolver, webMapping,
            Boolean.toString(settingsService.getRunModes().contains(Externalizer.PUBLISH))));
      }
    }

    // Cleanup
    this.userService = null;
    this.i18nProvider = null;
    this.citiesService = null;
    this.settingsService = null;
  }
}
