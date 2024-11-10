package com.saudi.tourism.core.models.components;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.models.components.bigslider.v1.BigSlide;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.favorites.v1.FavoritesApiEndpointsModel;
import com.saudi.tourism.core.models.components.packages.PackageDetail;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
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
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jcr.RepositoryException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.saudi.tourism.core.utils.Constants.I18N_MULTIPLE_DESTINATION_KEY;
import static com.saudi.tourism.core.utils.Constants.ONE;
import static com.saudi.tourism.core.utils.Constants.PACKAGE_DETAIL_RES_TYPE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_VARIANT;
import static com.saudi.tourism.core.utils.Constants.ZERO;
import static com.saudi.tourism.core.utils.NumberConstants.CONST_TWO;
import static com.saudi.tourism.core.utils.NumberConstants.SEVEN;

import static com.saudi.tourism.core.utils.I18nConstants.SAVE_TO_FAV_TEXT;
import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_SAVED_TO_FAV;

/**
 * Text Cards Model.
 */
@Model(adaptables = { Resource.class,
    SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
@JsonInclude(Include.NON_NULL)
public class TextCardsModel {

  /** The Constant NN_SEASONS_ITEMS. */
  private static final String NN_SEASONS_ITEMS = "items";

  /** The Constant NN_SEASONS_ITEMS. */
  private static final String SEASONS_CARDS = "c-seasons-cards";
  /** The Constant G_ROAD_TRIP_CARDS. */
  public static final String G_ROAD_TRIP_CARDS = "g-road-trip-cards";
  /** The Constant A_GENERAL_CARDS. */
  public static final String A_GENERAL_CARDS = "a-general-cards";
  /** The Constant TOP_ATTRACTIONS_CARDS. */
  public static final String TOP_ATTRACTIONS_CARDS = "top-attractions";

  /**
   * header.
   */
  @ChildResource
  @Expose
  private ComponentHeading header;

  /**
   * List of cards for c25-text-cards component.
   */
  @ChildResource
  @Expose
  private List<CardModel> cards;

  /**
   * Background for Article Cards.
   */
  @ValueMapValue
  @Expose
  private String background;

  /**
   * Background Ornament ID for Article Cards.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String backgroundOrnamentId;

  /**
   * Ornaments for Article Cards.
   */
  @ValueMapValue
  @Expose
  private String ornaments;

  /**
   * Pattern for Text Cards.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String pattern;

  /**
   * Checkbox to hide description field from dialog and page.
   */
  @ValueMapValue
  @Expose
  private boolean hideDescription;
  /**
   * from label.
   */
  @ValueMapValue
  @Expose
  private String from;
  /**
   * currency.
   */
  @ValueMapValue
  @Expose
  private String sar;
  /**
   * off label.
   */
  @ValueMapValue
  @Expose
  private String off;
  /**
   * person label.
   */
  @ValueMapValue
  @Expose
  private String person;
  /**
   * not available label.
   */
  @ValueMapValue
  @Expose
  private String notAvailable;
  /**
   * free label.
   */
  @ValueMapValue
  @Expose
  private String free;
  /**
   * book now label.
   */
  @ValueMapValue
  @Expose
  private String bookNow;

  /**
   * analytics event label.
   */
  @ValueMapValue
  @Expose
  private String analyticsEventLabel;

  /**
   * check dates label.
   */
  @ValueMapValue
  @Expose
  private String checkDates;

   /**
   * Link Type.
   */
  @ValueMapValue
  @Expose
  private String linkType;

  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /**
   * Checkbox set if Color Variant.
   */
  @Setter
  @ValueMapValue
  @Expose
  private Boolean isColorVariant;

  /**
   * Component Type.
   */
  @ValueMapValue
  @Expose
  private String componentType;

  /**
   * Summer Packages Cards City.
   */
  @ValueMapValue
  @Expose
  private String summerPackagesCardsCityId;

  /**
   * full ornament.
   */
  @ValueMapValue
  @Expose
  private String enableFullOrnament;

  /**
   * Boolean to check if we should show the fav icon on all the cards.
   * For now used only for the summer packages cards.
   */
  @ValueMapValue
  @Expose
  private Boolean showFav;

  /**
   * listsize for loadmore button in mod-feed-09 component.
   */
  @Expose
  private int listSize;

  /**
   * Card Json Data.
   */
  @Expose
  private String cardsJsonData;

  /**
   * Road Trip End Point.
   */
  @Expose
  private String roadTripEndpoint;

  /**
   * Is publisher.
   */
  @Expose
  private boolean isPublish;

  /**
   * i18nProvider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The package Service.
   */
  @OSGiService
  private PackageService packageService;

  /**
   * The Resource resolver.
   */
  @SlingObject
  private SlingHttpServletRequest request;

  /**
   * Sling settings service to check if the current environment is author or
   * publish (nullified in PostConstruct).
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  /**
   * Saudi Tourism Configurations.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * summerPackagesApiEndpoint.
   */
  @Expose
  private String summerPackagesApiEndpoint;
  /**
   * package Details Path.
   */
  @Expose
  private String packageDetailsPath;
  /**
   * domainServerUrl.
   */
  @Expose
  private String domainServerUrl;
  /**
   * city.
   */
  @Expose
  private String city;

  /** Favorites Endpoint Model. */
  @Self
  private FavoritesApiEndpointsModel favoritesApiEndpointsModel;

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
   * Save To Favorite Text.
   */
  @Expose
  private String saveToFavoritesText;

  /**
   * Saved To Favorite Text.
   */
  @Expose
  private String savedToFavoritesText;

  /**
   *
   * Resource resolver.
   */
  @SlingObject(injectionStrategy = InjectionStrategy.REQUIRED)
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * Injecting card view all button text.
   */
  @Expose
  @ValueMapValue
  private String viewAllText;

  /**
   * Injecting card view all button link.
   */
  @Expose
  @ValueMapValue
  private String viewAllLink;

  /**
   * css margin class.
   */
  @Expose
  @ValueMapValue
  private String cssMarginClass;

  /**
   * Post construct init method.
   */
  @PostConstruct
  protected void init() {
    domainServerUrl = saudiTourismConfig.getDomainServerUrlKey();
    summerPackagesApiEndpoint = saudiTourismConfig.getSummerPackagesApiEndpoint();
    isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    packageDetailsPath = saudiTourismConfig.getPackageDetailsPath();
    updateFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getUpdateFavUrl();
    deleteFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getDeleteFavUrl();
    getFavUrl = favoritesApiEndpointsModel.getFavoritesApiEndpoints().getGetFavUrl();
    city = summerPackagesCardsCityId;
    if (null != request && null != request.getResource()) {
      String lang = CommonUtils.getLanguageForPath(request.getResource().getPath());
      Locale locale = new Locale(lang);
      ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
      saveToFavoritesText = i18nBundle.getString(SAVE_TO_FAV_TEXT);
      savedToFavoritesText = i18nBundle.getString(I18_KEY_SAVED_TO_FAV);
    }
    if (null != componentType && componentType.equals(G_ROAD_TRIP_CARDS)) {
      roadTripEndpoint = saudiTourismConfig.getRoadTripEndpoint();
    }
    if (Objects.nonNull(cards) && !cards.isEmpty()) {
      setListSize(cards.size());
      setCardsJsonData(populateCardJsonData());
    } else if (Objects.nonNull(request)) {
      Resource currentResource = request.getResource();
      PageManager pageManager = request.getResourceResolver().adaptTo(PageManager.class);
      Page page = pageManager.getContainingPage(currentResource);
      if (Objects.nonNull(page)
          && page.getContentResource().isResourceType(PACKAGE_DETAIL_RES_TYPE)) {
        initForPackageDetailPage(page, currentResource);
      }
    }
    if (null != componentType && componentType.equals(SEASONS_CARDS)) {
      LOGGER.debug("Seasons cards initialized");
      String lang = CommonUtils.getLanguageForPath(request.getResource().getPath());
      String path = MessageFormat.format(Constants.SEASONS_PATH, lang);
      LOGGER.debug("seasons path -  {}", path);
      Resource seasonsResource = request.getResourceResolver().getResource(path);
      final Resource itemsResource = seasonsResource.getChild(NN_SEASONS_ITEMS);
      if (itemsResource != null && itemsResource.hasChildren()) {
        final Iterable<Resource> children = itemsResource.getChildren();
        cards = new ArrayList<>();
        for (final Resource seasonsItemResource : children) {
          final ValueMap valueMap = seasonsItemResource.getValueMap();
          final String startDate = valueMap.get("date", String.class);
          final String seasonName = valueMap.get(Constants.CONST_NAME, String.class);
          final String desc = valueMap.get("description", String.class);
          final String fileReference = valueMap.get("fileReference", String.class);
          LOGGER.debug("start date for card {}", startDate);
          LOGGER.debug("season name for card {}", seasonName);
          LOGGER.debug("desc for card {}", desc);
          LOGGER.debug("imagePath for card {}", fileReference);
          CardModel cardModel = new CardModel();
          cardModel.setTitle(seasonName);
          cardModel.setDescription(desc);
          cardModel.setStartDate(startDate);
          Image img = new Image();
          img.setFileReference(fileReference);
          cardModel.setImage(img);
          cards.add(cardModel);
        }
        LOGGER.debug("cards size {} ", cards.size());
      }

    }
    if (null != componentType && (componentType.equals(A_GENERAL_CARDS) || componentType.equals(TOP_ATTRACTIONS_CARDS))
        && null != getHeader() && null != getHeader().getLink()) {

      getHeader().getLink().setUrl(LinkUtils.getAuthorPublishUrl(resolver, viewAllLink, isPublish));
      getHeader().getLink().setCopy(viewAllText);
      getHeader().getLink().setTargetInNewWindow(
          LinkUtils.isExternalLink(viewAllLink));
    }
  }

  /**
   * Initialize cards by package-detail pages.
   * @param page current page
   * @param currentResource current resource
   */
  private void initForPackageDetailPage(Page page, Resource currentResource) {
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    Locale locale = new Locale(lang);
    ResourceBundle i18nBundle = i18nProvider.getResourceBundle(locale);
    try {
      PackageDetail packageDetail = page.getContentResource().adaptTo(PackageDetail.class);
      List<PackageDetail> packageDetails = packageService.getRelatedPackages(request, lang,
          Boolean.FALSE, packageDetail);
      if (packageDetails.isEmpty()) {
        PackagesRequestParams packagesRequestParams = new PackagesRequestParams();
        packagesRequestParams.setLocale(lang);
        packagesRequestParams.setPath(packageDetail.getPath());
        packagesRequestParams.setLimit(Constants.EVENTS_SLIDER_COUNT);
        packageDetails = packageService.getFilteredPackages(packagesRequestParams).getData();
      }

      cards = new ArrayList<>(packageDetails.size());
      for (PackageDetail detail : packageDetails) {
        CardModel card = new CardModel();
        card.setTitle(detail.getTitle());
        Image image = new Image();
        image.setFileReference(detail.getBannerImage());
        image.setS7fileReference(detail.getS7bannerImage());
        card.setImage(image);

        String copy = Optional.ofNullable(detail.getPrice())
            .map(s -> StringUtils.replaceEach(
                CommonUtils.getI18nString(i18nBundle, Constants.FROM3_SAR_I18KEY),
                new String[] {"{0}"}, new String[] {s}))
            .orElse(CommonUtils.getI18nString(i18nBundle, "learnmore"));
        Link link = new Link(LinkUtils.getAuthorPublishUrl(resolver, detail.getPath(),
            settingsService.getRunModes().contains(Externalizer.PUBLISH)), copy, false);
        link.setUrlSlingExporter(detail.getFavId());
        card.setLink(link);

        processCities(detail, card, i18nBundle);

        card.setFeaturedTagLineFeaturedCopy(
            Optional.ofNullable(detail.getDuration()).map(AppFilterItem::getValue).orElse(null));

        cards.add(card);
      }

      // init header
      header = new ComponentHeading();
      header.setHeading(
          new Heading(CommonUtils.getI18nString(i18nBundle, "More packages you may like")));
      header.setLink(new Link("/content/sauditourism/" + lang + "/packages",
          CommonUtils.getI18nString(i18nBundle, "View all"), false));
      header.setShowUnderline(true);

      hideDescription = true;
      componentType = "a-general-cards";
    } catch (RepositoryException e) {
      LOGGER.error("Error during initialization", e);
    }
  }

  /**
   * Process cities of package.
   * @param detail package detail
   * @param card card
   * @param i18nBundle i18n bundle
   */
  private void processCities(PackageDetail detail, CardModel card, ResourceBundle i18nBundle) {
    int size = Optional.ofNullable(detail.getCities()).filter(list -> !list.isEmpty())
        .map(List::size).orElse(ZERO);
    String key = null;
    switch (size) {
      case ZERO:
        break;
      case ONE:
        key = detail.getCities().get(0);
        break;
      default:
        key = I18N_MULTIPLE_DESTINATION_KEY;
    }
    if (Objects.nonNull(key)) {
      card.setSimpleTagLinesList(
          Collections.singletonList(CommonUtils.getI18nString(i18nBundle, key)));
    }
  }

  /**
   * Populate card json data to expose.
   * @return cardJsonData
   */
  protected String populateCardJsonData() {
    List<Map<String, Object>> exposeCardsData = new ArrayList<>();

    for (CardModel cardModel : cards) {
      Map<String, Object> resultMap = new HashMap<>();
      Map<String, String> linkData = getLinkData(cardModel);
      Map<String, String> imageData = getImageData(cardModel);
      Map<String, Object> labels = getLabels(cardModel);

      resultMap.put("link", linkData);
      resultMap.put(PN_IMAGE, imageData);
      resultMap.put("labels", labels);
      resultMap.put(PN_VARIANT, "with-cta");
      exposeCardsData.add(resultMap);
    }

    Gson gson = new Gson();
    return gson.toJson(exposeCardsData);
  }

  /**
   * Get labels data.
   *
   * @param cardModel cards
   * @return labels
   */
  protected Map<String, Object> getLabels(final CardModel cardModel) {
    Map<String, Object> labels = new HashMap<>();
    Map<String, String> weatherLabels = new HashMap<>();
    weatherLabels.put(Constants.PN_ICON, cardModel.getWeatherIcon());
    weatherLabels.put("copy", cardModel.getWeatherTemperature());

    List<Map<String, String>> simpleLabels = new ArrayList<>();
    if (Objects.nonNull(cardModel.getLabelList())) {
      for (String label : cardModel.getLabelList()) {
        Map<String, String> copy = new HashMap<>();
        copy.put("copy", label);
        simpleLabels.add(copy);
      }
    }

    labels.put("weather-label", weatherLabels);
    labels.put("simple-labels", simpleLabels);
    return labels;
  }

  /**
   * Get Image data.
   *
   * @param cardModel cards
   * @return image
   */
  protected Map<String, String> getImageData(final CardModel cardModel) {
    Map<String, String> imageData = new HashMap<>();
    imageData.put("desktopImage", StringUtils.defaultIfBlank(
        cardModel.getImage().getS7fileReference(), cardModel.getImage().getFileReference()));
    return imageData;
  }

  /**
   * Get Link data.
   *
   * @param cardModel cards
   * @return link
   */
  protected Map<String, String> getLinkData(final CardModel cardModel) {
    Map<String, String> linkData = new HashMap<>();
    linkData.put("copy", cardModel.getLink().getCopy());
    linkData.put("url", LinkUtils.getAuthorPublishUrl(resolver,
        cardModel.getLink().getUrl(), isPublish));
    return linkData;
  }

  /**
   * getSummerPackagesApiEndpoint.
   * @return summerPackagesApiEndpoint
   */
  public String getSummerPackagesApiEndpoint() {
    return summerPackagesApiEndpoint;
  }

  /**
   * getDomainServerUrl.
   * @return link
   */
  public String getDomainServerUrl() {
    return domainServerUrl;
  }

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
   * is showed component.
   */
  @Expose
  private boolean show;

  /**
   * Constructor.
   * @param saudiTourismConfigs config
   * @param authorSlides authored slids
   * @param eventService event service
   * @param currentResource current resource
   * @param i18nProvider i18nProvider
   * @param regionCityService regionCityService
   */
  @Inject
  public TextCardsModel(
      @OSGiService SaudiTourismConfigs saudiTourismConfigs,
      @ChildResource @Named("authorSlides") List<Slide> authorSlides,
      @OSGiService EventService eventService,
      @SlingObject @Named("currentResource") Resource currentResource,
      @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
          ResourceBundleProvider i18nProvider,
      @OSGiService RegionCityService regionCityService
  ) {
    this.variant = currentResource.getValueMap().get(PN_VARIANT, String.class);
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(lang));
    //boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    switch (Optional.ofNullable(variant).orElse(StringUtils.EMPTY)) {
      case "event":
        slides = loadEventSlides(currentResource.getResourceResolver(), eventService, lang, isPublish, i18n,
          currentResource);
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
      case "attraction":
        slides = Optional.ofNullable(authorSlides).orElse(Collections.emptyList()).stream().map(
            slide -> BigSlide.slideToBigSlideModel(slide, currentResource.getResourceResolver(), currentResource))
                .filter(Objects::nonNull).collect(Collectors.toList());
        setOrnamentId(Optional.ofNullable(ornamentId)
            .orElse(Constants.SIMPLE_SLIDER_DEFAULT_ORNAMENT));
        break;
      default:
        slides = Collections.emptyList();
    }

    if (Objects.nonNull(slides) && slides.size() > CONST_TWO) {
      this.show = true;
    }
  }

  /**
   * Load event slides.
   * @param resolver resolver
   * @param eventService eventService
   * @param lang         lang
   * @param isPublish    isPublish
   * @param i18n         i18n
   * @param currentResource currentResource
   * @return list of event slides
   *
   */
  private List<BigSlide> loadEventSlides(ResourceResolver resolver, EventService eventService, final String lang,
                                         boolean isPublish, ResourceBundle i18n, Resource currentResource) {
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

  /**
   * Instantiates a new text cards model.
   */
  TextCardsModel() {
  };

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
