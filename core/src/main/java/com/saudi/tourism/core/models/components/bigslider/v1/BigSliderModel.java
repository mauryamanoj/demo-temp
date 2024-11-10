package com.saudi.tourism.core.models.components.bigslider.v1;

import com.adobe.cq.wcm.style.ComponentStyleInfo;
import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.saudi.tourism.core.utils.Constants.ONE;
import static com.saudi.tourism.core.utils.Constants.PN_VARIANT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;
import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_TWO;
import static com.saudi.tourism.core.utils.NumberConstants.SEVEN;

/**
 * Big slider component sling model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class BigSliderModel {

  /**
   * Saudi Tourism Config.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Events Service.
   */
  @OSGiService
  private transient EventService eventService;

  /**
   * i18n Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private transient ResourceBundleProvider i18nProvider;

  /**
   * Settings Service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * RegionCity Service.
   */
  @OSGiService
  private transient RegionCityService regionCityService;

  /** Favorites Endpoint Model. */
  @Self
  private transient FavoritesApiEndpointsModel favoritesApiEndpointsModel;

  /**
   * Component Style.
   */
  @Self
  private transient ComponentStyleInfo componentStyleInfo;

  /**
   * Child Slides.
   */
  @ChildResource
  @Named("authorSlides")
  private  List<Slide> authorSlides;

  /**
   * Current Resource.
   */
  @SlingObject
  @Named("currentResource")
  private transient Resource currentResource;

  /**
   * Variable to hold component title.
   */
  @ChildResource
  @Expose
  private ComponentHeading componentHeading;

  /**
   * Variable to hold list of slides.
   */
  @Expose
  private List<BigSlide> slides;

  /**
   * Slider variant.
   */
  @Expose
  private String variant;

  /**
   * Ornament ID.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String ornamentId;
  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /**
   * is showed component.
   */
  @Expose
  private boolean show;

  /** Servlet endpoint to update favorites. */
  @Expose
  private String updateFavUrl;

  /** Servlet endpoint to delete a favorite. */
  @Expose
  private String deleteFavUrl;

  /** Servlet endpoint to fetch favorites. */
  @Expose
  private String getFavUrl;

  /**
   * Component Style : Applied CSS classes.
   */
  @Expose
  private String appliedCssClasses;

  /**
   * Save To Favorite Text.
   */
  @Expose
  private String saveToFavoritesText;

  /**
   * Saved To Favorite Text.
   */
  @Expose
  private String savedToFavoritesText;


  /** init method of sling model. */
  @PostConstruct
  public void init() {
    this.variant = currentResource.getValueMap().get(PN_VARIANT, String.class);
    updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
    deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
    getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(lang));
    saveToFavoritesText = i18n.getString(SAVE_TO_FAV_TEXT);
    savedToFavoritesText = i18n.getString(I18_KEY_SAVED_TO_FAV);
    boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    switch (Optional.ofNullable(variant).orElse(StringUtils.EMPTY)) {
      case "event":
        slides = loadEventSlides(currentResource.getResourceResolver(), eventService,
            lang, isPublish, i18n, currentResource);
        setOrnamentId(Optional.ofNullable(ornamentId)
            .orElse(Constants.EVENT_SLIDER_DEFAULT_ORNAMENT));
        break;
      case "simple":
        slides = Optional.ofNullable(authorSlides).orElse(Collections.emptyList()).stream().map(
            slide -> BigSlide.slideToBigSlideModel(slide, currentResource.getResourceResolver(), currentResource))
                .filter(Objects::nonNull).collect(Collectors.toList());
        setOrnamentId(Optional.ofNullable(ornamentId)
            .orElse(Constants.SIMPLE_SLIDER_DEFAULT_ORNAMENT));
        break;
      case "map":
        slides = loadMapSlides(currentResource, regionCityService, lang,
            saudiTourismConfigs.getScene7Domain(), DynamicMediaUtils.isCnServer(settingsService));
        setOrnamentId(Optional.ofNullable(ornamentId)
            .orElse(Constants.MAP_SLIDER_DEFAULT_ORNAMENT));
        break;
      case "category":
        slides = loadCategorySlides(currentResource, regionCityService, isPublish, i18n,
            saudiTourismConfigs.getScene7Domain(), DynamicMediaUtils.isCnServer(settingsService));
        setOrnamentId(Optional.ofNullable(ornamentId)
            .orElse(Constants.CATEGORY_SLIDER_DEFAULT_ORNAMENT));
        break;
      default:
        slides = Collections.emptyList();
    }

    if (Objects.nonNull(slides) && slides.size() > CONST_TWO) {
      this.show = true;
    }

    if (Objects.nonNull(componentStyleInfo)) {
      appliedCssClasses = componentStyleInfo.getAppliedCssClasses();
    }
  }

  /**
   * Load event slides.
   *
   * @param resolver resolver
   * @param eventService eventService
   * @param lang         lang
   * @param isPublish    isPublish
   * @param i18n         i18n
   * @param currentResource currentResource
   * @return list of event slides
   */
  private List<BigSlide> loadEventSlides(ResourceResolver resolver, EventService eventService,
                                         final String lang, boolean isPublish,
                                         ResourceBundle i18n, Resource currentResource) {
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(ONE);
    LocalDate next = today.plusDays(SEVEN);

    EventsRequestParams eventParams = new EventsRequestParams();
    eventParams.setLocale(lang);
    eventParams.setStartDate(tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE));
    eventParams.setEndDate(next.format(DateTimeFormatter.ISO_LOCAL_DATE));

    try {
      EventListModel listModel = eventService.getFilteredEvents(eventParams);
      return listModel.getData().stream()
          .map(detail -> BigSlide.ofEventDetail(resolver, detail, isPublish, i18n, currentResource))
          .collect(Collectors.toList());
    } catch (RepositoryException e) {
      LOGGER.error("Unable to get events for this week: ", e);
    }
    return Collections.emptyList();
  }

  /**
   * Load category slides.
   *
   * @param currentResource   currentResource
   * @param regionCityService regionCityService
   * @param isPublish         isPublish
   * @param i18n              i18n
   * @param scene7Domain      scene7Domain
   * @param isCnServer        isCnServer
   * @return list of category slides
   */
  private List<BigSlide> loadCategorySlides(final Resource currentResource,
      RegionCityService regionCityService, boolean isPublish, ResourceBundle i18n,
      String scene7Domain, boolean isCnServer) {
    String[] paths = currentResource.getValueMap().get("path", String[].class);
    Stream<String> stream = Optional.ofNullable(paths).map(Arrays::stream).orElse(Stream.empty());
    return stream.map(path -> BigSlide
        .ofCategory(path, regionCityService, currentResource.getResourceResolver(), isPublish, i18n,
          currentResource)).filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * Load map slides.
   *
   * @param currentResource   currentResource
   * @param regionCityService regionCityService
   * @param lang              lang
   * @param scene7Domain      scene7Domain
   * @param isCnServer        isCnServer
   * @return map slide
   */
  private List<BigSlide> loadMapSlides(final Resource currentResource,
      RegionCityService regionCityService, final String lang, String scene7Domain,
      boolean isCnServer) {
    Resource mapSlides = currentResource.getChild("mapSlides");
    Stream<Resource> resourceStream = Optional.ofNullable(mapSlides).map(Resource::getChildren)
        .map(r -> StreamSupport.stream(r.spliterator(), false)).orElse(Stream.empty());
    return resourceStream
        .map(r -> BigSlide.ofMap(r, regionCityService, lang, scene7Domain, isCnServer,
          currentResource.getResourceResolver(), currentResource))
        .filter(Objects::nonNull).collect(Collectors.toList());
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
