package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.Via;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.via.ChildResource;
import org.apache.sling.settings.SlingSettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.day.cq.commons.jcr.JcrConstants.JCR_CONTENT;

/**
 * Destination page model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class DestinationPageModel implements Serializable {

  /**
   * Region id.
   */
  @Expose
  private final String regionId;

  /**
   * Region label.
   */
  @Expose
  private final String i18nRegionLabel;

  /**
   * City id.
   */
  @Expose
  private final String cityId;

  /**
   * City label.
   */
  @Expose
  private final String i18nCityLabel;

  /**
   * Path to resource.
   */
  @Expose
  private final String url;

  /**
   * cityNavCode.
   */
  @Expose
  private final String cityNavCode;

  /**
   * regionNavCode.
   */
  @Expose
  private final String regionNavCode;

  /**
   * Translated list of tags (destination/category tags for city or region).
   */
  @Expose
  private final List<String> bestFor = new LinkedList<>();

  /**
   * Constructor.
   *
   * @param region          region
   * @param city            city
   * @param currentResource current resource
   * @param citiesSvc       RegionCityService
   * @param settingsService SettingsService
   * @param i18nProvider i18nProvider
   */
  @Inject
  public DestinationPageModel(
      @ValueMapValue(injectionStrategy = InjectionStrategy.REQUIRED)
      @Via(value = JCR_CONTENT, type = ChildResource.class)
      @Named("region") String region,
      @ValueMapValue
      @Via(value = JCR_CONTENT, type = ChildResource.class)
      @Named("city") String city,
      @Named("currentResource") Resource currentResource,
      @OSGiService final RegionCityService citiesSvc,
      @OSGiService SlingSettingsService settingsService,
      @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET,
        injectionStrategy = InjectionStrategy.REQUIRED)
      ResourceBundleProvider i18nProvider
  ) {
    // Path here is a path to see/break-free page, without jcr:content
    String path = currentResource.getPath();
    this.url = LinkUtils.getAuthorPublishUrl(currentResource.getResourceResolver(),
        path, settingsService.getRunModes().contains(Externalizer.PUBLISH));
    String language = CommonUtils.getLanguageForPath(path);

    cityId = AppUtils.stringToID(city);
    this.cityNavCode =
        Optional.ofNullable(cityId).map(id -> citiesSvc.getRegionCityExtById(id, language))
            .map(cityExt -> {
              // Get tags from the city
              final List<CategoryTag> cityDestinationTags = cityExt.getDestinationFeatureTags();
              if (CollectionUtils.isNotEmpty(cityDestinationTags)) {
                cityDestinationTags.stream().map(CategoryTag::getCopy)
                    .collect(Collectors.toCollection(() -> bestFor));
              }

              return StringUtils.defaultIfBlank(cityExt.getNavigationValue(), cityId);
            }).orElse(city);
    final ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));
    this.i18nCityLabel = i18nBundle.getString(this.cityNavCode);
    regionId = AppUtils.stringToID(region);
    this.regionNavCode =
        Optional.ofNullable(regionId).map(id -> citiesSvc.getRegionCityExtById(id, language))
            .map(regionExt -> {
              // Get tags from the region if it wasn't populated from city
              final List<CategoryTag> regionCategories = regionExt.getDestinationFeatureTags();
              if (CollectionUtils.isEmpty(bestFor) && CollectionUtils
                  .isNotEmpty(regionCategories)) {
                regionCategories.stream().map(CategoryTag::getCopy)
                    .collect(Collectors.toCollection(() -> bestFor));
              }

              return StringUtils.defaultIfBlank(regionExt.getNavigationValue(), regionId);
            }).orElse(region);
    this.i18nRegionLabel = i18nBundle.getString(this.regionNavCode);
  }
}
