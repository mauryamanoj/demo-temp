package com.saudi.tourism.core.models.app.location;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.saudi.tourism.core.models.app.common.AppPageRequestResultModel;
import com.saudi.tourism.core.models.app.content.RelatedModel;
import com.saudi.tourism.core.models.app.page.ComponentInfo;
import com.saudi.tourism.core.models.app.page.Search;
import com.saudi.tourism.core.models.common.ImageCaption;
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
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * App location detail page model.
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
public class AppLocationPageModel extends AppPageRequestResultModel {

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
   * Name.
   */
  @ValueMapValue
  private String descriptionTitle;

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
   * City instance.
   */
  private RegionCity cityObj;

  /**
   * CTA Text.
   */
  @ValueMapValue
  private String ctaText;

  /**
   * CTA URL.
   */
  @ValueMapValue
  private String ctaUrl;

  /**
   * Short description.
   */
  @ValueMapValue
  private String shortDescription;

  /**
   * Long description.
   */
  @ValueMapValue
  private String description;

  /**
   * Featured.
   */
  @ValueMapValue
  private boolean featured;

  /**
   * Categories / tags for this attraction.
   */
  @ValueMapValue
  private String[] tags;

  /**
   * Featured Image.
   */
  @ValueMapValue
  private String featuredImage;

  /**
   * Featured Image caption.
   */
  @ChildResource
  private ImageCaption featuredImageCaption;

  /**
   * Thumbnail Image.
   */
  @ValueMapValue
  private String thumbnail;

  /**
   * Related object.
   */
  @ChildResource
  private RelatedModel related;

  /**
   * Publish Date.
   */
  @ValueMapValue
  private String publishDate;

  /**
   * All locations for this page.
   */
  @ChildResource
  private List<LocationItemModel> locations;

  /**
   * Special locations for this page.
   */
  @ChildResource
  private List<LocationItemModel> specialLocations;

  /**
   * All info Items for this page.
   */
  @ChildResource
  private List<InfoItemModel> infoItems;

  /**
   * isArticleDetail page.
   */
  @ValueMapValue
  private boolean isArticleDetail;

  /**
   * All Articles for this page.
   */
  @ChildResource
  private List<LocationDetailModel> articles;

  /**
   * Components in parsys.
   */
  private List<ComponentInfo> components;

  /**
   * Search Tags for LocationServlet.
   */
  @ChildResource
  private Search search;

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
   * Cities service (to get cities by id).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient RegionCityService citiesService;

  /**
   * Sling settings service to check if the current environment is author or publish
   * (clear after PostConstruct!).
   */
  @JsonIgnore
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private transient SlingSettingsService settingsService;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
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
    // Correct link to the corresponding content page
    if (StringUtils.isNotBlank(webMapping)) {
      try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
        setLink(LinkUtils.getAuthorPublishUrl(resourceResolver, webMapping,
            Boolean.toString(settingsService.getRunModes().contains(Externalizer.PUBLISH))));
      }
    }

    // Localize needed fields
    if (StringUtils.isNotBlank(cityId)) {
      this.cityObj = citiesService.getRegionCityById(cityId, language);
      if (cityObj != null) {
        setCity(cityObj.getName());
      }
    }
    if (StringUtils.isNotBlank(this.region)) {
      final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
      // Workaround if the region text was broken when we switched to dropdown then reverted
      this.region = i18nBundle.getString(this.region);
    }

    if (CollectionUtils.isNotEmpty(locations)) {
      locations.forEach(location -> location.setId(AppUtils.stringToID(location.getTitle())));
    }
    if (CollectionUtils.isNotEmpty(specialLocations)) {
      specialLocations.forEach(specialLocation -> specialLocation
          .setId(AppUtils.stringToID(specialLocation.getTitle())));
    }

    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      if (ArrayUtils.isNotEmpty(tags)) {
        setCategory(CommonUtils.getCategoryFromTagName(tags, resourceResolver, language));
      }
      // Fill components from the page contents
      this.setComponents(AppUtils.getComponentInfo(resourceResolver, pagePath));
    }

    // Cleanup
    this.userService = null;
    this.i18nProvider = null;
    this.citiesService = null;
    this.settingsService = null;
  }

  /**
   * Get latitude from the first location point, workaround for the old version of app. Can be
   * removed when we don't need this.
   *
   * @return latitude for this attraction
   */
  public String getLatitude() {
    if (org.apache.commons.collections4.CollectionUtils.isEmpty(locations)) {
      return null;
    }

    return locations.get(0).getLatitude();
  }

  /**
   * Get longitude from the first location point, workaround for the old version of app. Can be
   * removed when we don't need this.
   *
   * @return longitude for this attraction
   */
  public String getLongitude() {
    if (org.apache.commons.collections4.CollectionUtils.isEmpty(locations)) {
      return null;
    }

    return locations.get(0).getLongitude();
  }
}
