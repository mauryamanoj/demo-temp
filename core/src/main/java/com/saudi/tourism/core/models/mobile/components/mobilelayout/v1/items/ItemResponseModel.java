package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items;

import com.day.cq.commons.Externalizer;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.models.mobile.components.atoms.ToastWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.Date;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.atoms.TextTemplate;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.atoms.Weather;
import com.saudi.tourism.core.models.mobile.components.atoms.About;
import com.saudi.tourism.core.models.mobile.components.atoms.EventWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.GeneralInformationWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Expandable;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Optional;
import java.util.Map;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Sling Model for the Section component in AEM.
 */
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Slf4j
public class ItemResponseModel {
  /**
   * Weather icon Map.
   */
  private static final Map<String, Object> TEMPERATURE_ICON = new HashMap<>();
  /**
   * CONTENT_DAM_ICON_WEATHER.
   */
  public static final String CONTENT_DAM_ICON_WEATHER = "/content/dam/static-images/resources/weathers/";
  /**
   * PNG Extension.
   */
  public static final String PNG = ".png";
  /**
   * Weather Icons.
   */
  static {
    TEMPERATURE_ICON.put("Fallback", "weather-default");
    TEMPERATURE_ICON.put("Clear", "weather-clear-day");
    TEMPERATURE_ICON.put("Thunderstorm", "weather-cloud-storm");
    TEMPERATURE_ICON.put("Drizzle", "weather-cloud-rain");
    TEMPERATURE_ICON.put("Rain", "weather-cloud-rain-heavy");
    TEMPERATURE_ICON.put("Snow", "weather-cloud-snow");
    TEMPERATURE_ICON.put("Clouds", "weather-clouds");
    TEMPERATURE_ICON.put("Mist", "weather-mist");
    TEMPERATURE_ICON.put("Smoke", "weather-smoke");
    TEMPERATURE_ICON.put("Haze", "weather-haze");
    TEMPERATURE_ICON.put("Dust", "weather-dust");
    TEMPERATURE_ICON.put("Fog", "weather-fog");
    TEMPERATURE_ICON.put("Sand", "weather-dust");
    TEMPERATURE_ICON.put("Ash", "weather-dust");
    TEMPERATURE_ICON.put("Squall", "weather-cloud-windy");
    TEMPERATURE_ICON.put("Tornado", "weather-tornado");
  }

  /**
   * Weather service.
   */
  @JsonIgnore
  @OSGiService
  private WeatherService weatherService;

  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;

  /** Tag Manager. */
  @Inject
  @JsonIgnore
  private transient TagManager tagManager;

  /** Resource Resolver. */
  @Inject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /** language. */
  @JsonIgnore
  private String language = Constants.DEFAULT_LOCALE;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;


  /**
   * Defines the id of the item.
   */
  private String id;

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * is_dmc.
   */
  private String isDmc;

  /**
   * package_url.
   */
  private String packageUrl;

  /**
   * cities.
   */
  @ValueMapValue
  private List<String> cities;


  /**
   * IsSelected.
   */
  @ValueMapValue
  private Boolean isSelected;

  /**
   * Represents the media gallery configuration.
   */
  @ChildResource
  private List<MediaGallery> mediaGallery;

  /**
   * Represents the titles.
   */
  @ChildResource
  private Titles titles;

  /**
   * Represents the titles.
   */
  @ChildResource
  private CustomAction customAction;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class ButtonComponentStyle {

    /**
     * componentUIType.
     */
    @ValueMapValue
    private String componentUIType;

  }

  /**
   * categories.
   */
  @ChildResource
  private List<Category> categories;

  /**
   * Type.
   */
  @ValueMapValue
  private String type;

  /**
   * Category tags.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] categoryTags;

  /**
   * Poi Type tags.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] poiTypesTags;

  /**
   * Poi Type .
   */
  @JsonIgnore
  private List<String> poiTypes;

  /**
   * location.
   */
  @ChildResource
  private Location location;

  /**
   * Season CF path.
   * */
  @ValueMapValue
  @JsonIgnore
  private String seasonCFPath;

  /**
   * Season.
   */
  private Season season;

  /**
   * Season.
   */
  @Builder
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Season {

    /**
     * id for season.
     */
    private String id;

    /**
     * Season Title.
     */
    private String title;
  }

  /**
   * price.
   */
  @ChildResource
  private Price price;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Price {

    /**
     * currency.
     */
    @ValueMapValue
    private String currency;

    /**
     * discount.
     */
    @ValueMapValue(name = "discount")
    private String originalPrice;

    /**
     * start Price.
     */
    @ValueMapValue(name = "startPrice")
    private String finalPrice;

    /**
     * start priceLabel.
     */
    private String priceLabel;
  }

  /**
   * showFavorite.
   */
  @ValueMapValue
  private Boolean showFavorite;

  /**
   * weather.
   */
  @ChildResource
  private Weather weather;

  /**
   * primary Tag.
   */
  @ChildResource
  private PrimaryTag primaryTag;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class PrimaryTag {

    /**
     * show.
     */
    @ValueMapValue
    private Boolean show;

    /**
     * title.
     */
    @ValueMapValue
    private String title;

    @PostConstruct
    void init() {
      if (StringUtils.isNotBlank(title)) {
        setTitle(title.toUpperCase());
      }
    }
  }

  /**
   * secondary Tag.
   */
  @ChildResource
  private SecondaryTag secondaryTag;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class SecondaryTag {

    /**
     * show.
     */
    @ValueMapValue
    private Boolean show;

    /**
     * title.
     */
    @ValueMapValue
    private String title;

    @PostConstruct
    void init() {
      if (StringUtils.isNotBlank(title)) {
        setTitle(title.toUpperCase());
      }
    }
  }

  /**
   * rating.
   */
  @ChildResource
  private Rating rating;

  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class Rating {

    /**
     * show.
     */
    @ValueMapValue
    private Boolean show;

    /**
     * value.
     */
    @ValueMapValue
    private String value;
  }

  /**
   * date.
   */
  @ChildResource
  private Date date;
  /**
   * about.
   */
  @ChildResource
  private About about;

  /**
   * toast widget.
   */
  private ToastWidget toastWidget;

  /**
   * expandable.
   */
  @ChildResource
  private Expandable expandable;

  /**
   * eventWidget.
   */
  @ChildResource
  private EventWidget eventWidget;

  /**
   * GeneralInformationWidget.
   */
  @ChildResource
  private GeneralInformationWidget generalInformationWidget;

  /**
   * includeChecklist.
   */
  @ChildResource
  private IncludeChecklist includeChecklist;

  /**
   * TExt template.
   */
  @ChildResource
  private TextTemplate textTemplate;

  /**
   * Inner class representing the item IncludeChecklist.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class IncludeChecklist {


    /**
     * Titles Include Checklist.
     */
    @ValueMapValue
    private List<String> titles;


    /**
     * included.
     */
    @ValueMapValue
    private Boolean included;

  }

  /**
   * Inner class representing the Titles Include Checklist.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class TitlesIncludeChecklist {

    /**
     * title.
     */
    @ValueMapValue
    private String title;
  }

  /**
   * phoneNumbers.
   */
  @ChildResource
  private List<PhoneNumbers> phoneNumbers;

  /**
   * Inner class representing the PhoneNumbers.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class PhoneNumbers {

    /**
     * title.
     */
    @ChildResource
    private String title;

    /**
     * phone.
     */
    @ValueMapValue
    private String phone;
  }

  /** published date.
   */
  @JsonIgnore
  private transient Calendar publishedDate;

  /** init method. */
  @PostConstruct
  private void init() {
    processItemDestination(resolver);
    var destination = location.getDestination();
    try {
      if (destination != null) {
        processBuildDegreeAndIcon(destination);
      }
    } catch (Exception e) {
      LOGGER.error("Error in Service for weather", e);
    }
    final ResourceResolver resourceResolver = resource.getResourceResolver();
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    if (pageManager == null) {
      return;
    }
    Page currentPage = pageManager.getContainingPage(resource);
    if (Objects.nonNull(currentPage)) {
      id = MobileUtils.extractItemId(currentPage.getPath());
    }

    language = CommonUtils.getLanguageForPath(currentPage.getPath());

    processCategoriesTags();
    processItemSeason(resourceResolver);
  }

  private void processBuildDegreeAndIcon(Destination destination) {
    var locale = CommonUtils.getLanguageForPath(resource.getPath());
    var weatherRequest = new WeatherRequestParams();
    weatherRequest.setLocale(locale);
    weatherRequest.setCity(Collections.singletonList(destination.getWeatherCityId()));
    weatherRequest.setLatitude(destination.getLat());
    weatherRequest.setLongitude(destination.getLng());
    Optional.ofNullable(safelyFetchWeather(weatherRequest))
        .map(Optional::get)
        .ifPresent(weatherMinMax -> {
          String icon = generateIconUrl(weatherMinMax);
          weather.setWeatherIconURL(icon);
          setDegreesIfPresent(weather, weatherMinMax.getTemp());
        });
  }

  /** This method Fetch Weather Safely.
   * @return Optional<SimpleWeatherMinMax>
   * @param params
   */
  private Optional<SimpleWeatherMinMax> safelyFetchWeather(WeatherRequestParams params) {
    try {
      return Optional.ofNullable(weatherService.getSimpleWeatherSingleCity(params));
    } catch (Exception e) {
      LOGGER.error("Error fetching weather for city: {}", params.getCity(), e);
      return Optional.empty();
    }
  }
  /** This method aims to prepare item categories tags. */
  public void processCategoriesTags() {
    if (ArrayUtils.isNotEmpty(categoryTags)) {
      categories =
          Arrays.stream(categoryTags)
              .flatMap(p -> TagUtils.getTagOrChildren(p, tagManager, resolver, language).stream())
              .peek(tag -> TagUtils.processTagsForMobile(tag, resolver, settingsService))
              .map(
                  category -> {
                    if (StringUtils.isNotBlank(category.getTitle())) {
                      category.setTitle(category.getTitle().toUpperCase());
                    }
                    return category;
                  })
              .collect(Collectors.toList());
    }
  }

  /**
   * This method aims to prepare destination object of item location.
   *
   * @param resourceResolver
   */
  private void processItemDestination(ResourceResolver resourceResolver) {
    if (location.getDestinationCFPath() != null) {
      final var destinationCF = resourceResolver.getResource(location.getDestinationCFPath());
      if (destinationCF == null) {
        return;
      }
      final var destinationCFModel = destinationCF.adaptTo(DestinationCFModel.class);
      if (destinationCFModel == null) {
        return;
      }

      String destinationTitle = destinationCFModel.getTitle();
      if (StringUtils.isNotBlank(destinationTitle)) {
        destinationTitle = destinationCFModel.getTitle().toUpperCase();
      }

      location.setDestination(
          Destination.builder()
              .id(StringUtils.substringAfterLast(location.getDestinationCFPath(), "/"))
              .weatherCityId(destinationCFModel.getId())
              .title(destinationTitle)
              .lat(destinationCFModel.getLatitude())
              .lng(destinationCFModel.getLongitude())
              .build());
    }
  }

  /**
   * This method aims to prepare item season object.
   *
   * @param resourceResolver
   */
  private void processItemSeason(ResourceResolver resourceResolver) {
    if (StringUtils.isNotEmpty(seasonCFPath)) {
      final var seasonCF = resourceResolver.getResource(seasonCFPath);
      if (seasonCF == null) {
        return;
      }
      final var seasonCFModel = seasonCF.adaptTo(SeasonCFModel.class);
      if (seasonCFModel == null) {
        return;
      }
      season =
          Season.builder()
              .id(StringUtils.substringAfterLast(seasonCFPath, "/"))
              .title(seasonCFModel.getTitle())
              .build();
    }
  }

  private String generateIconUrl(SimpleWeatherMinMax weatherResponse) {
    Object iconKey = weatherResponse.getIcon();
    String iconFileName = TEMPERATURE_ICON.getOrDefault(iconKey, "weather-default").toString();
    return LinkUtils.getAuthorPublishAssetUrl(
      resolver,
      CONTENT_DAM_ICON_WEATHER + iconFileName + PNG,
      settingsService.getRunModes().contains(Externalizer.PUBLISH)
    );
  }

  private void setDegreesIfPresent(Weather weather, Float temperature) {
    Optional.ofNullable(temperature)
        .ifPresent(value -> weather.setDegrees(value.intValue()));
  }
}
