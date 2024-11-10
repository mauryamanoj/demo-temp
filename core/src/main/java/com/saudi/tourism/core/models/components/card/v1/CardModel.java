package com.saudi.tourism.core.models.components.card.v1;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.components.footnote.v1.FootnoteModel;
import com.saudi.tourism.core.models.components.listcomponent.v1.ListTableItem;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Locale;

import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.utils.PrimConstants.HORIZONTAL_LABEL;
import static com.saudi.tourism.core.utils.PrimConstants.VERTICAL_LABEL;

/**
 * C-03 Card Model.
 *
 * @author cbarrios
 */
@Model(adaptables = {
    Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class CardModel implements Serializable {

  /**
   * The Weather Data service.
   */
  @JsonIgnore
  @OSGiService
  private transient WeatherService weatherDataService;

  /**
   * Component title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Component discountLabel.
   */
  @ValueMapValue
  @Expose
  private String discountLabel;
  /**
   * hide image brush.
   */
  @ValueMapValue
  @Expose
  private String hideImageBrush;
  /**
   * Component location.
   */
  @ValueMapValue
  @Expose
  private String location;

  /**
   * Rating field.
   */
  @ValueMapValue
  @Expose
  private String rating;

  /**
   * Component subtitle.
   */
  @ValueMapValue
  @Expose
  private String subtitle;

  /**
   * Component description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Image position.
   */
  @ValueMapValue
  @Expose
  private String imagePosition;

  /**
   * Variable of image.
   */
  @ChildResource
  @Setter
  @Expose
  private Image image = new Image();

  /**
   * Image caption.
   */
  @ChildResource
  @Expose
  private ImageCaption caption;

  /**
   * Video Path.
   */
  @ValueMapValue
  @Expose
  private String videoPath;

  /**
   * Variable to videoType.
   */
  @ValueMapValue
  @Expose
  private String videoType;

  /**
   * Variable to youtubeReference.
   */
  @ValueMapValue
  @Expose
  private String youtubeReference;

  /**
   * Mobile Video Path.
   */
  @ValueMapValue
  @Expose
  private String mobileVideoPath;

  /**
   * Image position.
   */
  @ValueMapValue
  @Expose
  private String fullWidthVideo;

  /**
   * Variable of image.
   */
  @ChildResource
  @Expose
  private Image videoBackgroundImage;

  /**
   * Apply link.
   */
  @ChildResource
  @Expose
  private Link link = new Link();

  /**
   * CTA Type.
   */
  @ValueMapValue
  @Expose
  private String ctaType;

  /**
   * Footnote.
   */
  @ChildResource
  @Expose
  private FootnoteModel footnote;

  /**
   * Apply link.
   */
  @ChildResource
  @Expose
  private List<Link> linkList;

  /**
   * Simple Labels authored for c27-teaser with Cards.
   */
  @ValueMapValue
  @Expose
  private String[] labels;

  /**
   * Label List for html for c27-teaser with Cards.
   */
  @Expose
  private List<String> simpleLabelList;

  /**
   * Label List for html for Cities and City Cards components to pull from Global
   * Configs.
   */
  @Expose
  private List<String> labelList;

  /**
   * Region Label List for html for Cities and City Cards components to pull from
   * Global Configs.
   */
  @Expose
  private List<String> regionLabelList;

  /**
   * Tag Lines authored.
   */
  @ValueMapValue
  @Expose
  private String[] simpleTagLines;

  /**
   * Tag Lines List for html.
   */
  @Expose
  private List<String> simpleTagLinesList;

  /**
   * Cuisines authored.
   */
  @ValueMapValue
  @Expose
  private String[] cuisines;

  /**
   * Cuisines List for html.
   */
  @Expose
  private List<String> cuisinesList;

  /**
   * featuredTagLine.
   */
  @ValueMapValue
  @Expose
  private String featuredTagLineFeaturedCopy;

  /**
   * Tagline for Article Cards.
   */
  @ValueMapValue
  @Expose
  private String tagline;

  /**
   * is highlight.
   */
  @ValueMapValue
  @Expose
  private Boolean highlight;

  /**
   * isFeatured.
   */
  @ValueMapValue
  @Expose
  private Boolean isFeatured;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String titleWeight;

  /**
   * Variable description weight.
   */
  @ValueMapValue
  @Expose
  private String descriptionWeight;

  /**
   * Variable of position.
   */
  @ValueMapValue
  @Expose
  private String position;

  /**
   * Variable of background.
   */
  @ValueMapValue
  @Expose
  private String background;

  /**
   * Variable of card background.
   */
  @ValueMapValue
  @Expose
  private String cardBackground;

  /**
   * hide Favorite icon.
   */
  @ValueMapValue(injectionStrategy = InjectionStrategy.OPTIONAL)
  @Expose
  private boolean hideFav;

  /**
   * Variable heading weight.
   */
  @ValueMapValue
  @Expose
  private String subtitleWeight;

  /**
   * Variable to hold list of tableItems.
   */
  @ChildResource
  @Expose
  private List<ListTableItem> tableItems;

  /**
   * icon.
   */
  @ValueMapValue
  @Expose
  private String icon;

  /**
   * Region id.
   */
  @ValueMapValue
  @Expose
  private String regionId;

  /**
   * City id.
   */
  @ValueMapValue
  @Expose
  private String cityId;

  /**
   * analyticsDataCopy.
   */
  @ValueMapValue
  @Expose
  private String analyticsDataCopy;


  /**
   * highlightOrnamentId.
   */
  @ValueMapValue
  @Expose
  private String highlightOrnamentId;

  /**
   * highlightOrnamentId.
   */
  @ValueMapValue
  @Expose
  private String ornamentBarId;

  /**
   * Service to get city by id.
   */
  @OSGiService
  @JsonIgnore
  private transient RegionCityService citiesService;

  /**
   * Variable is activities flag.
   */
  @Expose
  private boolean activities;

  /**
   * Variable of region.
   */
  @Expose
  private String region;

  /**
   * Variable of city.
   */
  @Expose
  private String city;

  /**
   * Variable of activity.
   */
  @Expose
  private String activity;

  /**
   * weatherLabel for mod-te-10 component.
   */
  @Expose
  private String weatherTemperature;

  /**
   * weatherIcon for mod-te-10 component.
   */
  @Expose
  private String weatherIcon;

  /**
   * City obj.
   */
  @Expose
  private RegionCityExtended cityObj;

  /**
   * Latitude coordinate for this city.
   */
  @Expose
  private String latitude;

  /**
   * Longitude coordinate for this city.
   */
  @Expose
  private String longitude;

  /**
   * Activity Service for Activity type in Article Cards.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.OPTIONAL)
  @JsonIgnore
  private transient ActivityService activityService;

  /**
   * Activity model if this card is for displaying activity.
   */
  @JsonIgnore
  private Activity activityModel;

  /**
   * Component type Manual or Activity.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * Set link values from ActivityModel.
   */
  @ValueMapValue
  @Expose
  private boolean useActivityLink;

  /**
   * Link for activity.
   */
  @ValueMapValue
  @Expose
  private String activityReference;

  /** The start date. */
  @Expose
  private String startDate;

  /**
   * favoritePath.
   */
  @Expose
  @Getter
  private String favoritePath;


  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;
  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;
  /**
   * Resource Bundle Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;
  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;
  /**
   * Locale.
   */
  private String locale;
  /**
   * Best For Label.
   */
  @Expose
  private String bestForLabel;
  /**
   * layout.
   */
  @Expose
  private String layout;
  /**
   * Model Initializer.
   */
  @PostConstruct
  protected void init() {
    final ResourceResolver resourceResolver = resource.getResourceResolver();
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    if (Objects.isNull(pageManager)) {
      this.resource = null;
      return;
    }
    if (Objects.nonNull(highlight) && highlight.equals("true")) {
      layout = HORIZONTAL_LABEL;
    } else {
      layout = VERTICAL_LABEL;
    }
    DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080", "crop-375x667",
        "1280", "420", resourceResolver, currentResource);
    Page currentPage = pageManager.getContainingPage(resource);
    String path = currentPage.getPath();
    String language = CommonUtils.getLanguageForPath(path);

    String vendorName = CommonUtils.getVendorName(currentPage);
    String packageName = CommonUtils.getPackageName(currentPage);
    locale = CommonUtils.getLanguageForPath(path);
    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    bestForLabel = i18n.getString(I18nConstants.I18_KEY_BEST_FOR);

    if (link != null && StringUtils.isNotEmpty(link.getUrl())) {
      favoritePath = LinkUtils.getFavoritePath(link.getUrl());
    }

    if (Objects.nonNull(labels)) {
      simpleLabelList = Arrays.asList(labels);
    }

    if (Objects.nonNull(simpleTagLines)) {
      simpleTagLinesList = Arrays.asList(simpleTagLines);
    }

    if (Objects.nonNull(cuisines)) {
      cuisinesList = Arrays.asList(cuisines);
    }

    if (Objects.nonNull(cityId)) {
      cityObj = citiesService.getRegionCityExtById(cityId, language);

      if (Objects.nonNull(cityObj)) {
        setLabelList(
            Optional.ofNullable(cityObj.getDestinationFeatureTags()).map(Collection::stream)
                .orElse(Stream.empty()).map(CategoryTag::getCopy).collect(Collectors.toList()));
        setCity(Optional.ofNullable(cityObj.getName())
            .orElse(WordUtils.capitalize(cityId.replace("-", " "))));
        latitude = cityObj.getLatitude();
        longitude = cityObj.getLongitude();
      }
    }

    if (Objects.nonNull(regionId)) {
      RegionCityExtended regionObj = citiesService.getRegionCityExtById(regionId, language);
      if (Objects.nonNull(regionObj)) {
        setRegion(regionObj.getNavigationTitle());
        setRegionLabelList(
            Optional.ofNullable(regionObj.getDestinationFeatureTags()).map(Collection::stream)
                .orElse(Stream.empty()).map(CategoryTag::getCopy).collect(Collectors.toList()));
      }
    }

    link.setAppEventData(CommonUtils.getAnalyticsEventData(
        Constants.DEFAULT_TOUR_PACKAGE_HERO_EVENT, LinkUtils.getAppFormatUrl(link.getUrl()),
        StringUtils.defaultIfBlank(link.getTitle(), link.getCopy()), vendorName, packageName));

    if (StringUtils.isNotBlank(cityId) && Objects.nonNull(weatherDataService)) {
      populateWeatherData(language);
    } else if (StringUtils.isBlank(getLink().getCopy())) {
      // if link copy is not available in case mod-feed-09 & mod-te-10, set title as
      // link copy
      link.setCopy(title);
    }
    if  (getLink().getUrl() != null && !getLink().getUrl().isEmpty()) {
      getLink().setUrl(LinkUtils.getAuthorPublishUrl(resourceResolver, getLink().getUrl(),
          settingsService.getRunModes().contains(Externalizer.PUBLISH)));
    }

    setActitityFields();

    // Cleanup
    this.resource = null;
  }

  /**
   * Populate Weather data.
   * @param language page language.
   */
  private void populateWeatherData(String language) {
    final WeatherRequestParams weatherRequestParams = new WeatherRequestParams();
    weatherRequestParams.setCity(Collections.singletonList(cityId));
    weatherRequestParams.setLocale(language);

    try {
      final SimpleWeatherModel weatherModel = weatherDataService
          .getSimpleWeatherSingleCity(weatherRequestParams);
      if (Objects.nonNull(weatherModel)) {
        weatherIcon = weatherModel.getIcon();
        weatherTemperature = String.format("%.1f", weatherModel.getTemp());
        // if link copy is not available in case mod-feed-09 & mod-te-10, set title as
        // link copy
        if (StringUtils.isBlank(getLink().getCopy())) {
          link.setCopy(weatherModel.getCity());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in Card Model for weather", e);
    }
  }

  /**
   * Method for setting fields in case if type for Article Cards is Activity not
   * Manual.
   */
  private void setActitityFields() {
    if (StringUtils.equals(type, Constants.ACTIVITY)) {
      if (StringUtils.isBlank(activityReference)) {
        LOGGER.error("Activity reference is not set for card, please configure");
        return;
      }

      activityModel = activityService.getActivityByPath(activityReference,
          CommonUtils.getLanguageForPath(resource.getPath()));
      if (Objects.isNull(activityModel)) {
        LOGGER.error("Error in adapting activity {}", activityReference);
        return;
      }
      setTitle(activityModel.getTitle());
      setDescription(activityModel.getDescription());
      setImage(activityModel.getImage());
    }

    if (this.activityModel != null && Boolean.TRUE.equals(useActivityLink)) {
      if (StringUtils.isBlank(link.getCopy())) {
        link.setCopy(activityModel.getTitle());
      }

      link.setUrl(activityModel.getLink());
      link.setUrlWithExtension(activityModel.getLink());
      link.setUrlSlingExporter(activityModel.getFavId());
    }
  }

  /**
   * Sets the title.
   *
   * @param title the new title
   */
  public void setTitle(String title) {
    this.title = title;
  }

  /**
   * Sets the description.
   * @param description the new description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Sets the image.
   * @param image the new image
   */
  public void setImage(Image image) {
    this.image = image;
  }

  /**
   * Sets the start date.
   * @param startDate the new start date
   */
  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }
}
