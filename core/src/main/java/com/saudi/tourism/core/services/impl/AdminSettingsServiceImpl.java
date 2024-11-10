package com.saudi.tourism.core.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.AdminPageAlert;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.AttractionMapFiltersSettings;
import com.saudi.tourism.core.models.components.CalendarAppModel;
import com.saudi.tourism.core.models.components.ChatbotConfigModel;
import com.saudi.tourism.core.models.components.EventsFiltersSettings;
import com.saudi.tourism.core.models.components.HyPackageFilterModel;
import com.saudi.tourism.core.models.components.IdValueModel;
import com.saudi.tourism.core.models.components.ItemFilterModel;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.models.components.RoadTripFilterModel;
import com.saudi.tourism.core.models.components.RoadTripSettings;
import com.saudi.tourism.core.models.components.SeasonKeys;
import com.saudi.tourism.core.models.components.SeasonsListConfigModel;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.models.components.UserFeedbackConfigModel;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.EventFilterModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.models.components.greenTaxi.DownloadAppModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.mobile.v1.filters.FilterCategories;
import com.saudi.tourism.core.services.mobile.v1.filters.FiltersIdTitleModel;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripFilterResponseModel;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Generated;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.services.impl.AdminSettingsServiceImpl.SERVICE_DESCRIPTION;
import static com.saudi.tourism.core.utils.Constants.CONFIG_URL;

/**
 * The implementation of the AdminSettingsService - service to provide admin page options.
 */
@Component(service = AdminSettingsService.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class AdminSettingsServiceImpl implements AdminSettingsService {

  /**
   * This service description.
   */
  static final String SERVICE_DESCRIPTION = "Admin Settings Service";
  /**
   * The memory cache key prefix for seasons.
   */
  public static final String KEY_PREFIX_SEASONS = "seasons:";
  /**
   * The constant Chatbot_Path.
   */
  public static final String SEASONS_INFO_PATH = CONFIG_URL
      + "/admin/jcr:content/seasons";

  /**
   * Lang param for halayalla api.
   */
  public static final String PARAM_LANG = "lang";
  /**
   * CONTENT.
   */
  public static final String CONTENT = "/content/";
  /**
   * LANGUAGE_CONFIGS.
   */
  public static final String LANGUAGE_CONFIGS = "/{language}/Configs";
  /**
   * The constant DEFAULT_PACKAGE_DETAILS.
   */
  public static final String DEFAULT_PACKAGE_DETAILS =
      Constants.ROOT_CONTENT_PATH + "/{language}/summer-vibes/package-details";

  /**
   * Global in-memory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * The service to provide read-only resource resolver.
   */
  @Reference
  private UserService resolverProvider;

  /**
   * The Event service.
   */
  @Reference
  private EventService eventService;

  /**
   * The service to Experience service.
   */

  /**
   * Experience Service.
   */
  @Reference
  private transient ExperienceService service;

  /**
   * RoadTrip Service.
   */
  @Reference
  private transient RoadTripScenariosService roadTripService;

  /**
   * Activation of this service.
   */
  @Activate
  @Generated
  protected void activate() {
    AdminUtil.setAdminSettingsService(this);
  }

  /**
   * This service deactivation.
   */
  @Deactivate
  @Generated
  protected void deactivate() {
    AdminUtil.setAdminSettingsService(null);
  }

  @Override
  public AdminPageOption getAdminOptions(String language, String site) {
    final String cacheKey;
    if (StringUtils.isNotBlank(site)) {
      cacheKey = Constants.KEY_PREFIX_ADMIN_OPTIONS + site + language;
    } else {
      cacheKey = Constants.KEY_PREFIX_ADMIN_OPTIONS + Constants.PROJECT_ID + language;
    }

    // Get from cache
    AdminPageOption adminPageOption = (AdminPageOption) memCache.get(cacheKey);
    if (adminPageOption != null) {
      return adminPageOption;
    }

    // Get admin page resource
    final Resource resource;
    final String adminOptionsPath;
    if (StringUtils.isNotBlank(site)) {
      adminOptionsPath = StringUtils
        .replaceEach(CONTENT + site + Constants.ADMIN_CONFIG_OPTIONS_PATH,
          new String[] {Constants.LANGUAGE_PLACEHOLDER}, new String[] {language});
    } else {
      adminOptionsPath = StringUtils
        .replaceEach(Constants.ADMIN_OPTIONS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    }

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(adminOptionsPath);

      if (resource != null) {
        adminPageOption = resource.adaptTo(AdminPageOption.class);
        if (adminPageOption == null) {
          LOGGER.error("Couldn't adapt admin config {} to model", adminOptionsPath);
        }
      } else {
        LOGGER.error("Admin config resource not found by path {}", adminOptionsPath);
      }
    }

    if (adminPageOption != null) {
      // Store object to cache
      memCache.add(cacheKey, adminPageOption);
    } else {
      // If resource is not found or can't be adapted - use default
      adminPageOption = new AdminPageOption();
    }

    // Default values if couldn't adapt or property is empty
    checkInitValues(adminPageOption, language, site);

    return adminPageOption;
  }

  /**
   * Check each property and fill with default value when it's empty.
   * @param site site name.
   * @param adminPageOption admin page options
   * @param language        language to create default paths
   */
  private void checkInitValues(final AdminPageOption adminPageOption, final String language, String site) {
    if (StringUtils.isBlank(adminPageOption.getEventsPath()) && StringUtils.isBlank(site)) {
      adminPageOption.setEventsPath(
          Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
          + Constants.FORWARD_SLASH_CHARACTER + "events");
    } else if (StringUtils.isBlank(adminPageOption.getEventsPath()) && StringUtils.isNotBlank(site)) {
      adminPageOption.setEventsPath(
          CONTENT + site + Constants.FORWARD_SLASH_CHARACTER + language
          + Constants.FORWARD_SLASH_CHARACTER + "events");
    }
    if (StringUtils.isBlank(adminPageOption.getPackagesPath()) && StringUtils.isBlank(site)) {
      adminPageOption.setPackagesPath(
          Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
          + Constants.FORWARD_SLASH_CHARACTER + "packages");
    } else if (StringUtils.isBlank(adminPageOption.getPackagesPath()) && StringUtils.isNotBlank(site)) {
      adminPageOption.setPackagesPath(
          CONTENT + site + Constants.FORWARD_SLASH_CHARACTER + language
          + Constants.FORWARD_SLASH_CHARACTER + "packages");
    }
    final String confPath;
    if (StringUtils.isNotBlank(site)) {
      confPath = CommonUtils.getResourcePath(language, CONTENT + site + LANGUAGE_CONFIGS)
        + Constants.FORWARD_SLASH_CHARACTER;
    } else {
      confPath = CommonUtils.getResourcePath(language, CONFIG_URL)
        + Constants.FORWARD_SLASH_CHARACTER;
    }

    if (StringUtils.isBlank(adminPageOption.getHeaderPath())) {
      adminPageOption.setHeaderPath(confPath + "header");
    }
    if (StringUtils.isBlank(adminPageOption.getFooterPath())) {
      adminPageOption.setFooterPath(confPath + "footer");
    }
    if (StringUtils.isBlank(adminPageOption.getSitemapPath())) {
      adminPageOption.setSitemapPath(confPath + "sitemap");
    }

    if (StringUtils.isBlank(adminPageOption.getActivitiesPath())) {
      adminPageOption.setActivitiesPath(MessageFormat.format(Constants.ACTIVITIES_PATH, language));
    }
    if (StringUtils.isBlank(adminPageOption.getTripPlansPath())) {
      adminPageOption.setTripPlansPath(MessageFormat.format(Constants.TRIP_PLANS_PATH, language));
    }
  }

  @Override
  public AdminPageAlert getAdminAlert(String language, String site) {
    final String cacheKey;
    if (StringUtils.isNotBlank(site)) {
      cacheKey = Constants.KEY_PREFIX_ADMIN_ALERTS + site + language;
    } else {
      cacheKey = Constants.KEY_PREFIX_ADMIN_ALERTS + language;
    }
    // Get from cache
    AdminPageAlert adminPageAlert = (AdminPageAlert) memCache.get(cacheKey);
    if (adminPageAlert != null) {
      return adminPageAlert;
    }

    // Get admin alerts resource
    final Resource resource;
    String adminAlertPath;
    if (StringUtils.isNotBlank(site)) {
      adminAlertPath = StringUtils
        .replaceEach(CONTENT + site + Constants.ADMIN_ALERT_RAW_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    } else {
      adminAlertPath = StringUtils
        .replaceEach(Constants.ADMIN_ALERT_PATH, new String[]{Constants.LANGUAGE_PLACEHOLDER},
          new String[]{language});
    }
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(adminAlertPath);

      if (resource != null) {
        adminPageAlert = resource.adaptTo(AdminPageAlert.class);
        if (adminPageAlert == null) {
          LOGGER.error("Couldn't adapt alert config {} to model", adminAlertPath);
        }
      } else {
        LOGGER.error("Admin alert resource not found by path {}", adminAlertPath);
      }
    }

    if (adminPageAlert != null) {
      // Store object to cache
      memCache.add(cacheKey, adminPageAlert);
    } else {
      adminPageAlert = new AdminPageAlert();
    }

    return adminPageAlert;
  }

  @Override
  public List<FiltersSettings> getThingsToDoFilters(final String language) {
    List<FiltersSettings> filtersSettingsList = new ArrayList<>();

    String thingsToDoFiltersPath = StringUtils.replaceEach(
        Constants.THINGS_TO_DO_TYPES_FILTERS_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER},
        new String[] {language}
    );

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      final Resource resource = resolver.getResource(thingsToDoFiltersPath);

      if (resource != null) {
        for (Resource childResource : resource.getChildren()) {
          FiltersSettings settings = childResource.adaptTo(FiltersSettings.class);
          if (settings != null) {
            filtersSettingsList.add(settings);
          } else {
            LOGGER.warn("Couldn't adapt child resource at {} to ThingsToDoFiltersSettings model",
                childResource.getPath());
          }
        }
      } else {
        LOGGER.warn("Resource not found by path {}", thingsToDoFiltersPath);
      }
    }

    return filtersSettingsList;
  }

  @Override
  public FiltersSettings getStoriesFilters(final String language) {
    FiltersSettings filtersSetting = new FiltersSettings();

    String storiesFiltersPath = StringUtils.replaceEach(
        Constants.STORIES_FILTERS_PATH,
        new String[] {Constants.LANGUAGE_PLACEHOLDER},
        new String[] {language}
    );

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      final Resource resource = resolver.getResource(storiesFiltersPath);
      if (resource != null) {
        filtersSetting = resource.adaptTo(FiltersSettings.class);
      } else {
        LOGGER.warn("Resource not found by path {}", storiesFiltersPath);
      }
    }

    return filtersSetting;
  }

  @Override
  public EventsFiltersSettings getEventsFilters(final String language) {

    final String cacheKey = Constants.KEY_PREFIX_EVENTS_FILTERS + language;

    final Resource resource;
    EventFilterModel eventFilterModel = null;

    EventsFiltersSettings eventsFiltersSettings = (EventsFiltersSettings) memCache.get(cacheKey);
    String eventsfiltersPath =
        StringUtils.replaceEach(Constants.EVENTS_FILTERS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(eventsfiltersPath);
      if (resource != null) {
        eventsFiltersSettings = resource.adaptTo(EventsFiltersSettings.class);
        List<ItemFilterModel> filters = eventsFiltersSettings.getFilters();
        if (null != filters) {
          EventsRequestParams eventsRequestParams = new EventsRequestParams();
          eventsRequestParams.setLocale(language);
          try {
            eventFilterModel = eventService.getEventFilters(eventsRequestParams);
          } catch (RepositoryException e) {
            LOGGER.warn("Error during fetching events filters", e);
          }
          EventFilterModel finalEventFilterModel = eventFilterModel;
          filters.forEach(filter -> {
            if (finalEventFilterModel != null) {
              List<IdValueModel> data = getListIdValueFromType(finalEventFilterModel, filter.getKey());
              filter.setData(data);
            }
          });
        }
      }
    }

    return eventsFiltersSettings;
  }

  @Override
  public List<String> fetchWhitelistedAttractionCategories(final String language) {
    List<String> whitelistedCategories = new ArrayList<>();

    String attractionMapFiltersPath = Constants.ATTRACTION_MAP_FILTERS_PATH
        .replace(Constants.LANGUAGE_PLACEHOLDER, language);

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      Resource resource = resolver.getResource(attractionMapFiltersPath);
      if (resource == null) {
        LOGGER.warn("No resource found at path: {}", attractionMapFiltersPath);
        return whitelistedCategories;
      }

      AttractionMapFiltersSettings filtersSetting = resource.adaptTo(AttractionMapFiltersSettings.class);
      if (filtersSetting == null) {
        LOGGER.warn("Failed to adapt resource to AttractionMapFiltersSettings at path: {}", attractionMapFiltersPath);
        return whitelistedCategories;
      }

      whitelistedCategories.addAll(filtersSetting.getWhitelistedCategories());
    } catch (Exception e) {
      LOGGER.error("Error obtaining resource resolver or processing resource: {}", e.getMessage(), e);
    }

    return whitelistedCategories;
  }

  @Override
  public String getPackageDetailsPage(final String language) {
    // Get packagePageSettings resource
    final Resource resource;
    String packageSettingsPath = StringUtils
        .replaceEach(Constants.PACKAGE_SETTINGS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});


    PackagePageSettings packagePageSettings = null;

    String packageDetailsPage = StringUtils
        .replaceEach(DEFAULT_PACKAGE_DETAILS, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});

    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(packageSettingsPath);

      if (resource != null) {
        packagePageSettings = resource.adaptTo(PackagePageSettings.class);
        if (packagePageSettings == null) {
          LOGGER.warn("getPackageDetailsPage: Couldn't adapt alert config {} to model");
        } else {
          if (StringUtils.isNotBlank(packagePageSettings.getPackageDetailsPath())) {
            packageDetailsPage = packagePageSettings.getPackageDetailsPath();
          }
        }
      }

    }

    return packageDetailsPage;
  }




  @Override
  public PackagePageSettings getPackagePageSettings(final String language) {
    final String cacheKey = Constants.KEY_PREFIX_PACKAGE_SETTINGS + language;

    // Get from cache
    PackagePageSettings packagePageSettings = (PackagePageSettings) memCache.get(cacheKey);
    if (Objects.nonNull(packagePageSettings)) {
      return packagePageSettings;
    }

    // Get packagePageSettings resource
    final Resource resource;
    String packageSettingsPath = StringUtils
        .replaceEach(Constants.PACKAGE_SETTINGS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(packageSettingsPath);

      if (resource != null) {
        packagePageSettings = resource.adaptTo(PackagePageSettings.class);
        if (packagePageSettings == null) {
          LOGGER.error("Couldn't adapt alert config {} to model", packagePageSettings);
        } else {
          List<HyPackageFilterModel> filters = packagePageSettings.getFilters();
          List<FilterCategories> mobileFilters = packagePageSettings.getMobileFilters();
          if (null != filters) {
            Object categoriesRes = null;
            try {
              Map<String, Object> reqMap = new HashMap<>();
              reqMap.put(PARAM_LANG, language);
              categoriesRes = service.getExperienceCategories(reqMap);
            } catch (IOException ex) {
              LOGGER.error("Error in Package Filter categories : {}",
                  ex.getLocalizedMessage(), ex);
            }
            Object finalCategoriesRes = categoriesRes;
            filters.forEach(filter -> {
              if (filter.getKey().equals("sub_category")) {
                final List<IdValueModel> data = getDataForSubCategory(finalCategoriesRes, language);
                filter.setData(data);
              }

              if (filter.getKey().equals("categories")) {
                final List<IdValueModel> data = getDataForCategories(finalCategoriesRes, language);
                filter.setData(data);
              }
            });
          }
          if (null != mobileFilters) {
            Object categoriesRes = null;
            try {
              Map<String, Object> reqMap = new HashMap<>();
              reqMap.put(PARAM_LANG, language);
              categoriesRes = service.getExperienceCategories(reqMap);
            } catch (IOException ex) {
              LOGGER.error("Error in Package Filter categories : {}", ex.getLocalizedMessage(), ex);
            }
            Object finalCategoriesRes = categoriesRes;
            mobileFilters.forEach(
                mobileFilter -> {
                  if (mobileFilter.getId().equals("categories")) {
                    final List<IdValueModel> categories = getDataForCategories(finalCategoriesRes, language);
                    if (CollectionUtils.isNotEmpty(categories)) {
                      final var items = categories.stream()
                              .filter(Objects::nonNull)
                              .map(
                                  city -> {
                                    FiltersIdTitleModel idTitleModel = new FiltersIdTitleModel();
                                    idTitleModel.setId(city.getId());
                                    idTitleModel.setTitle(city.getValue());
                                    idTitleModel.setIconUrl(StringUtils.EMPTY);
                                    return idTitleModel;
                                  })
                              .filter(Objects::nonNull)
                              .collect(Collectors.toList());
                      mobileFilter.setItems(items);
                    }
                  }
                });
          }
        }
      } else {
        LOGGER.error("Admin alert resource not found by path {}", packagePageSettings);
      }
    }

    if (packagePageSettings != null) {
      // Store object to cache
      memCache.add(cacheKey, packagePageSettings);
    } else {
      packagePageSettings = new PackagePageSettings();
    }

    return packagePageSettings;
  }

  @Override
  public RoadTripSettings getRoadTripSettings(final String language) {
    final String cacheKey = Constants.KEY_PREFIX_ROADTRIP_SETTINGS + language;

    // Get from cache
    RoadTripSettings roadTripSettings = (RoadTripSettings) memCache.get(cacheKey);
    if (Objects.nonNull(roadTripSettings)) {
      return roadTripSettings;
    }

    // Get roadTripSettings resource
    final Resource resource;
    String roadTripSettingsPath = StringUtils
        .replaceEach(Constants.ROADTRIP_SETTINGS_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(roadTripSettingsPath);

      if (resource != null) {
        roadTripSettings = resource.adaptTo(RoadTripSettings.class);
        if (roadTripSettings == null) {
          LOGGER.error("Couldn't adapt alert config {} to model", roadTripSettings);
        } else {
          List<RoadTripFilterModel> filters = roadTripSettings.getFilters();
          if (null != filters) {
            try {
              RoadTripFilterResponseModel filterResponse = (RoadTripFilterResponseModel)
                  roadTripService.getRoadTripScenariosFilterNativeApp(language);
              filters.forEach(filter -> {
                if (filter.getKey().equals("transport")) {
                  final List<IdValueModel> data = new ArrayList<>();
                  if (null != filterResponse.getTransport()) {
                    filterResponse.getTransport().forEach(trans -> {
                      IdValueModel model = new IdValueModel();
                      model.setId(trans);
                      model.setValue(trans);
                      data.add(model);
                    });
                    filter.setData(data);
                  }
                }
                if (filter.getKey().equals("categories")) {
                  if (null != filterResponse.getCategories()) {
                    final List<IdValueModel> catData = new ArrayList<>();
                    filterResponse.getCategories().forEach(cat -> {
                      IdValueModel model = new IdValueModel();
                      model.setId(cat.getAlias());
                      model.setValue(cat.getName());
                      catData.add(model);
                    });
                    filter.setData(catData);
                  }
                }
                if (filter.getKey().equals("start_city")) {
                  if (null != filterResponse.getStartCity()) {
                    final List<IdValueModel> cityData = new ArrayList<>();
                    filterResponse.getStartCity().forEach(city -> {
                      IdValueModel model = new IdValueModel();
                      model.setId(city.getId());
                      model.setValue(city.getName());
                      cityData.add(model);
                    });
                    filter.setData(cityData);
                  }
                }
                if (filter.getKey().equals("duration")) {
                  if (null != filterResponse.getDuration()) {
                    final List<IdValueModel> durationData = new ArrayList<>();
                    filterResponse.getDuration().forEach(dur -> {
                      IdValueModel model = new IdValueModel();
                      model.setId(dur);
                      model.setValue(dur);
                      durationData.add(model);
                    });
                    durationData.sort(Comparator.comparing(IdValueModel::getValue));
                    filter.setData(durationData);
                  }
                }
                if (filter.getKey().equals("minDistance,maxDistance")) {
                  final List<IdValueModel> distanceData = new ArrayList<>();
                  IdValueModel model = new IdValueModel();
                  model.setId("minDistance");
                  model.setValue(filter.getMinDistance());
                  distanceData.add(model);
                  IdValueModel model1 = new IdValueModel();
                  model1.setId("maxDistance");
                  model1.setValue(filter.getMaxDistance());
                  distanceData.add(model1);
                  filter.setData(distanceData);
                }
              });
            } catch (IOException ex) {
              LOGGER.error("Error in Road trip Filter : {}",
                  ex.getLocalizedMessage());
            }
          } else {
            LOGGER.error("Admin alert resource not found by path {}", roadTripSettings);
          }
        }
      }
    }
    if (roadTripSettings != null) {
      // Store object to cache
      memCache.add(cacheKey, roadTripSettings);
    } else {
      roadTripSettings = new RoadTripSettings();
    }
    return roadTripSettings;
  }

  /**
   * Check each property and fill with default value when it's empty.
   *
   * @param eventFilterModel eventFiltersModel
   * @param type             type of filters
   * @return list of idValueModel
   */
  private List<IdValueModel> getListIdValueFromType(final EventFilterModel eventFilterModel, final String type) {
    if (eventFilterModel == null || StringUtils.isBlank(type)) {
      return Collections.emptyList();
    }

    Function<EventFilterModel, List<AppFilterItem>> getterFunc;

    switch (type) {
      case "city":
        getterFunc = EventFilterModel::getCity;
        break;
      case "region":
        getterFunc = EventFilterModel::getRegion;
        break;
      case "category":
        getterFunc = EventFilterModel::getCategory;
        break;
      case "target":
        getterFunc = EventFilterModel::getTarget;
        break;
      case "freePaid":
        getterFunc = EventFilterModel::getFreePaid;
        break;
      case "season":
        getterFunc = EventFilterModel::getSeason;
        break;
      case "date":
        getterFunc = EventFilterModel::getDate;
        break;
      default:
        getterFunc = null;
        break;
    }

    if (getterFunc == null) {
      return Collections.emptyList();
    } else {
      List<IdValueModel> data = getterFunc.apply(eventFilterModel).stream().map(temp -> {
        IdValueModel obj = new IdValueModel();
        obj.setValue(temp.getValue());
        obj.setId(temp.getId());
        return obj;
      }).collect(Collectors.toList());
      return data;
    }
  }

  @Override
  public CalendarAppModel getCalendarAppData(String language) {
    final String cacheKey = Constants.KEY_PREFIX_CALENDAR_DATA + language;
    final Resource resource;
    CalendarAppModel calendarAppModel = (CalendarAppModel) memCache.get(cacheKey);
    String calendarInfoPath =
        StringUtils.replaceEach(Constants.CALENDAR_INFO_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(calendarInfoPath);
      if (resource != null) {
        calendarAppModel = resource.adaptTo(CalendarAppModel.class);
      } else {
        calendarAppModel = new CalendarAppModel(); // return default model
      }
    } catch (Exception e) {
      LOGGER.warn("Error during fetching Calendar Info", e);
      calendarAppModel = new CalendarAppModel(); // return default model
    }
    return calendarAppModel;
  }

  /**
   * get Download app settings for green taxi.
   *
   * @param language
   * @return
   */
  @Override
  public DownloadAppModel getGreenTaxiDownloadAppSettings(final String language) {
    final Resource resource;
    DownloadAppModel downloadAppModel = null;
    String downloadAppConfigPath =
        StringUtils.replaceEach(Constants.GREEN_TAXI_DOWNLOAD_APP_CONFIG_PATH,
          new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(downloadAppConfigPath);
      if (resource != null) {
        downloadAppModel = resource.adaptTo(DownloadAppModel.class);
      }
    }
    return downloadAppModel;
  }

  @Override
  public ChatbotConfigModel getChatbotData(String language) {
    final String cacheKey = Constants.KEY_PREFIX_CHATBOT + language;
    final Resource resource;
    ChatbotConfigModel chatbotConfigModel = (ChatbotConfigModel) memCache.get(cacheKey);
    String calendarInfoPath =
        StringUtils.replaceEach(Constants.CHATBOT_INFO_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(calendarInfoPath);
      if (resource != null) {
        chatbotConfigModel = resource.adaptTo(ChatbotConfigModel.class);
      }
    } catch (Exception e) {
      LOGGER.warn("Error during fetching Chatbot Info", e);
    }
    return chatbotConfigModel;
  }

  @Override
  public List<SeasonKeys> getSeasonsList(String language) {
    final String cacheKey = KEY_PREFIX_SEASONS + language;
    final Resource resource;
    SeasonsListConfigModel seasonsListConfigModel = (SeasonsListConfigModel) memCache.get(cacheKey);
    String seasonsInfoPath =
        StringUtils.replaceEach(SEASONS_INFO_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(seasonsInfoPath);
      if (resource != null) {
        seasonsListConfigModel = resource.adaptTo(SeasonsListConfigModel.class);
      }
    } catch (Exception e) {
      LOGGER.warn("Error during fetching Seasons", e);
    }
    return seasonsListConfigModel.getListSeasons();
  }

  /**
   * Check data for filter key subCategory.
   * @param finalCategoriesRes finalCategoriesRes
   * @param language language
   * @return data
   */
  private List<IdValueModel> getDataForSubCategory(final Object finalCategoriesRes, final String language) {
    final List<IdValueModel> data = new ArrayList<>();
    if (finalCategoriesRes != null) {
      Map<String, Object> reqMap = new HashMap<>();
      JsonParser parser = new JsonParser();
      JsonObject categoriesObj = parser.parse(finalCategoriesRes.toString()).getAsJsonObject();
      if (null != categoriesObj) {
        JsonArray categoryData = categoriesObj.getAsJsonArray("data");
        if (null != categoryData) {
          final Map<String, String> vals = new HashMap<>();
          categoryData.forEach(catData -> {
            JsonObject dataObj = (JsonObject) catData;
            JsonArray subCategories = dataObj.getAsJsonArray("sub_categories");
            if (null != subCategories) {
              subCategories.forEach(subCat -> {
                JsonObject subCatObj = (JsonObject) subCat;
                JsonObject title = subCatObj.getAsJsonObject("title");
                if (null != title && null != title.get(language)) {
                  final String titleVal = title.get(language).getAsString();
                  if (!vals.containsKey(titleVal)) {
                    IdValueModel model = new IdValueModel();
                    if (null != subCatObj.get("slug")) {
                      final String id = subCatObj.get("slug").getAsString();
                      model.setId(id);
                      model.setValue(titleVal);
                      vals.put(titleVal, id);
                      data.add(model);
                    }
                  } else {
                    if (null != subCatObj.get("slug")) {
                      String newValue = vals.get(titleVal) + "," + subCatObj.get("slug").getAsString();
                      vals.put(titleVal, newValue);
                      Optional<IdValueModel> model = data.stream()
                          .filter(item -> item.getValue().equals(titleVal)).findFirst();
                      if (model.isPresent()) {
                        model.get().setId(vals.get(titleVal));
                      }
                    }
                  }
                }
              });
            }
          });
        }
      }
    }
    return data;
  }

  /**
   * Check data for filter key Categories.
   *
   * @param finalCategoriesRes finalCategoriesRes
   * @param language           language
   * @return data
   */
  private List<IdValueModel> getDataForCategories(final Object finalCategoriesRes, final String language) {
    final List<IdValueModel> data = new ArrayList<>();
    if (finalCategoriesRes != null) {
      Map<String, Object> reqMap = new HashMap<>();
      JsonParser parser = new JsonParser();
      JsonObject categoriesObj = parser.parse(finalCategoriesRes.toString()).getAsJsonObject();
      if (null != categoriesObj) {
        JsonArray categoryData = categoriesObj.getAsJsonArray("data");
        if (null != categoryData) {
          final Map<String, String> vals = new HashMap<>();
          categoryData.forEach(catData -> {
            JsonObject dataObj = (JsonObject) catData;
            String title = Constants.BLANK;
            if (dataObj.has("title")) {
              title = dataObj.get("title").getAsString();
            }

            String id = Constants.BLANK;
            if (dataObj.has("slug")) {
              id = dataObj.get("slug").getAsString();
            }
            IdValueModel model = new IdValueModel();
            model.setId(id);
            model.setValue(title);
            data.add(model);

          });
        }
      }
    }
    return data;
  }

  @Override
  public UserFeedbackConfigModel getUserFeedbackConfig(String language) {
    final String cacheKey = Constants.KEY_PREFIX_USER_FEEDBACKS + language;
    final Resource resource;
    UserFeedbackConfigModel userFeedbackConfigModel = (UserFeedbackConfigModel) memCache.get(cacheKey);
    String userFeedbackInfoPath =
        StringUtils.replaceEach(Constants.USER_FEEDBACKS_INFO_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
            new String[] {language});
    try (ResourceResolver resolver = resolverProvider.getResourceResolver()) {
      resource = resolver.getResource(userFeedbackInfoPath);
      if (resource != null) {
        userFeedbackConfigModel = resource.adaptTo(UserFeedbackConfigModel.class);
      }
    } catch (Exception e) {
      LOGGER.warn("Error during fetching User feedback Info", e);
    }
    return userFeedbackConfigModel;
  }
}
