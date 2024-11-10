package com.saudi.tourism.core.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.CategoriesI18n;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripCustomResponse;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripCustomResponseModel;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripFilterResponseModel;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.AuthorDetails;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.authorDetails.User;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.customAuthorDetails.CustomUserDetails;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.Scenario;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.ScenarioDetails;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.RouteBody;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.customeRouteBody.days.CustomDays;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.RouteBodyCustom;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.category.Category;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.Day;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.image.Image;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response.ImageResponse;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response.NativeAppCustomCardResponse;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Currency Data Service.
 */
@Slf4j
@Component(service = RoadTripScenariosService.class, immediate = true)
public class RoadTripScenariosServiceImpl implements RoadTripScenariosService {
  /**
   * InMemory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  @Override
  public Object getRoadTripScenariosFromApi(Integer limit) throws IOException {
    // Retrieve data from the external api
    String loadedRoadTripScenarios = getRoadTripScenariosViaApiUrl();
    List<String> stringIds = DataUtils
        .filterRequiredRoadTripScenariosApiUrl(loadedRoadTripScenarios, limit, StringUtils.EMPTY);
    JsonParser parser = new JsonParser();
    JsonArray routes = new JsonArray();

    for (String scenarioId : stringIds) {

      ResponseMessage responseMessage = RestHelper
          .executeMethodGet(getRoadTripScenariosExternalUrlWithParams()
              + "visitsaudi/scenario?scenario_id=" + scenarioId, StringUtils.EMPTY, false);

      JsonElement routeJson = parser.parse(responseMessage.getMessage());

      routes.add(routeJson);

    }

    return routes;
  }

  /**
   * @param limit  number of road trips required.
   * @param offset Pass the offset in numbers.
   * @param locale Pass the locale in numbers.
   * @param body   Pass the body of the trips request.
   * @return responseMessage.getMessage().
   * @throws IOException .
   */
  public RoadTripCustomResponse getRoadTripScenariosNativeApp(Integer limit, Integer offset, String locale,
                                              String body) throws IOException {
    String sSuffixUrl = "scenarios/search";
    String aApiEndPoint = getRoadTripScenariosExternalUrlWithParams() + sSuffixUrl + Constants.QUESTION_MARK
        + "limit" + Constants.EQUAL + limit + Constants.CONST_AMPERSAND + "offset" + Constants.EQUAL + offset;
    HttpEntity entity = new StringEntity(body, ContentType.APPLICATION_FORM_URLENCODED);
    ResponseMessage responseMessage = RestHelper.executeMethodPost(
        aApiEndPoint, entity, null, false);

    RoadTripCustomResponseModel roadTripCustomResponseModel = new Gson()
        .fromJson(responseMessage.getMessage(), RoadTripCustomResponseModel.class);
    if (!roadTripCustomResponseModel.getScenarios().isEmpty()
        && roadTripCustomResponseModel.getScenarios().size() >= 1) {
      return customResponseForRoadTripNativeApp(roadTripCustomResponseModel, limit, offset, locale);
    }
    return null;
  }

  /**
   * @param locale Pass the locale in numbers.
   * @return responseMessage.getMessage().
   * @throws IOException .
   */
  public Object getRoadTripScenariosFilterNativeApp(String locale) throws IOException {
    String sSuffixUrl = "visitsaudi/scenario/filters";
    String aApiEndPoint = getRoadTripScenariosExternalUrlWithParams() + sSuffixUrl + Constants.QUESTION_MARK
        + "lang" + Constants.EQUAL + locale;
    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        aApiEndPoint, StringUtils.EMPTY, false);
    RoadTripFilterResponseModel roadTripFilterResponseModel = new Gson()
        .fromJson(responseMessage.getMessage(), RoadTripFilterResponseModel.class);
    return roadTripFilterResponseModel;
  }


  /**
   * @param roadTripCustomResponseModel roadTripCustomResponseModel.
   * @param limit                       limit.
   * @param offset                      offset.
   * @param locale                      locale.             .
   * @return return Map.
   */
  private RoadTripCustomResponse customResponseForRoadTripNativeApp(
      RoadTripCustomResponseModel roadTripCustomResponseModel,
      Integer limit, Integer offset, String locale) {
    List<NativeAppCustomCardResponse> trips = new ArrayList<>();
    NativeAppCustomCardResponse nativeAppCustomCardResponse;
    for (Scenario scenario : roadTripCustomResponseModel.getScenarios()) {
      nativeAppCustomCardResponse = new NativeAppCustomCardResponse();
      nativeAppCustomCardResponse.setDistance(scenario.getSumDistance());
      nativeAppCustomCardResponse.setDuration(scenario.getDuration());
      nativeAppCustomCardResponse.setId(scenario.getUid());
      nativeAppCustomCardResponse.setTransport(scenario.getTransport());
      nativeAppCustomCardResponse.setStartCity(scenario.getStartCity());
      nativeAppCustomCardResponse.setCategories(scenario.getCategories());
      nativeAppCustomCardResponse.setScenarioId(scenario.getScenarioId());
      nativeAppCustomCardResponse.setGuidance(scenario.getGuidance());
      nativeAppCustomCardResponse.setCategoriesI18n(
          sortedResponseOnBasisOfCategories(
          scenario, locale));
      nativeAppCustomCardResponse.setImage(getImages(scenario));
      setResponseOnBasisOfLocale(scenario, locale, nativeAppCustomCardResponse);
      trips.add(nativeAppCustomCardResponse);
    }
    Pagination pagination = new Pagination();
    pagination.setLimit(limit);
    pagination.setOffset(offset);
    pagination.setTotal(roadTripCustomResponseModel.getCount());


    return RoadTripCustomResponse.builder().trips(trips).pagination(pagination).build();
  }

  /**
   * @param images images.
   * @return imageList .
   */
  private List<ImageResponse> getImages(Scenario images) {
    List<ImageResponse> imageList = new ArrayList<>();
    for (Image image : images.getImages()) {
      ImageResponse imageResponse = new ImageResponse();

      imageResponse.setId(image.getId());
      imageResponse.setDay(image.getDay());
      imageResponse.setUid(imageResponse.getUid());
      imageList.add(imageResponse);
    }
    return imageList;
  }

  /**
   * @param scenario scenrio object.
   * @param locale   locale.
   * @return return List<String>.
   */
  private List<String> sortedResponseOnBasisOfCategories(
      Scenario scenario,
      String locale) {
    List<String> categoriesi18nList = new ArrayList<>();
    for (CategoriesI18n categoriesI18n : scenario.getCategoriesI18n()) {
      categoriesi18nList.add(setCategoriesLocale(categoriesI18n, locale));
    }
    return categoriesi18nList;
  }


  /**
   * @param categoriesI18n categoriedi18n.
   * @param locale         locale.
   * @return String.
   */
  private String setCategoriesLocale(CategoriesI18n categoriesI18n, String locale) {
    if (locale.equals("de")) {
      return categoriesI18n.getDe();
    }
    if (locale.equals("es")) {
      return categoriesI18n.getEs();
    }
    if (locale.equals("ru")) {
      return categoriesI18n.getRu();
    }
    if (locale.equals("pt")) {
      return categoriesI18n.getPt();
    }
    if (locale.equals("ar")) {
      return categoriesI18n.getAr();
    }
    if (locale.equals("ja")) {
      return categoriesI18n.getJa();
    } else {
      return categoriesI18n.getEn();
    }
  }

  /**
   * @param scenario                    scenario.
   * @param nativeAppCustomCardResponse roadTripCustomModel.
   * @param locale                      locale.
   */
  private void setResponseOnBasisOfLocale(Scenario scenario, String locale,
                                          NativeAppCustomCardResponse nativeAppCustomCardResponse) {
    if (locale.equals("de")) {
      if (null != scenario.getLocalNames()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getDe().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getDe().getText());
      }
    }
    if (locale.equals("es")) {
      if (null != scenario.getLocalNames()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getEs().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getEs().getText());
      }
    }
    if (locale.equals("ru")) {
      if (null != scenario.getLocalNames()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getRu().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getRu().getText());
      }
    }
    if (locale.equals("pt")) {
      if (null != scenario.getLocalNames()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getPt().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getPt().getText());
      }
    }
    if (locale.equals("ar")) {
      if (null != scenario.getLocalNames()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getAr().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getAr().getText());
      }
    } else {
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setName(scenario.getLocalNames().getEn().getText());
      }
      if (null != scenario.getStartCityName()) {
        nativeAppCustomCardResponse.setStartCityName(scenario.getStartCityName().getEn().getText());
      }
    }
  }

  /**
   * This method is used to get road trip data.
   *
   * @return the road trip data via api url
   * @throws IOException .
   */
  private String getRoadTripScenariosViaApiUrl() throws IOException {

    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        getRoadTripScenariosExternalUrlWithParams() + "scenarios/search", StringUtils.EMPTY, false);

    return responseMessage.getMessage();
  }

  /**
   * This method is used to construct the URL.
   *
   * @return String .
   */
  private String getRoadTripScenariosExternalUrlWithParams() {

    return String.format(saudiTourismConfig.getRoadTripScenariosExternalAppURL());
  }

  /**
   * @param scenarioId scenarioId.
   * @param locale     locale.
   * @return object .
   * @throws IOException IOException.
   */
  public Object getRoadTripScenarioDetailByIdNativeApp(String locale, String scenarioId) throws IOException {
    String sSuffixUrl = "visitsaudi/scenario";
    String aApiEndPoint = getRoadTripScenariosExternalUrlWithParams() + sSuffixUrl + Constants.QUESTION_MARK
        + "scenario_id" + Constants.EQUAL + scenarioId;
    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        aApiEndPoint, StringUtils.EMPTY, false);

    ScenarioDetails scenarioDetails = new Gson()
        .fromJson(responseMessage.getMessage(), ScenarioDetails.class);

    if (!scenarioDetails.getRouteBody().isEmpty()
        && scenarioDetails.getRouteBody().size() >= 1) {
      return customScenarioResponseByLocale(scenarioDetails, locale);
    }
    return null;
  }

  /**
   * @param scenarioDetails scenarioDetails.
   * @param locale          locale.
   * @return object .
   */
  private Object customScenarioResponseByLocale(ScenarioDetails scenarioDetails, String locale) {
    HashMap<String, Object> hashMap = new HashMap<>();

    if (scenarioDetails.getRouteBody() != null && scenarioDetails.getRouteBody().size() >= 1) {
      RouteBody customScenarioDetailsByLocale = null;
      for (RouteBodyCustom routeBodyCustom : scenarioDetails.getRouteBody()) {
        customScenarioDetailsByLocale = new RouteBody();
        customScenarioDetailsByLocale.setMetadata(routeBodyCustom.getMetadata());
        customScenarioDetailsByLocale.setPhoto(routeBodyCustom.getPhoto());
        customScenarioDetailsByLocale.setTags(routeBodyCustom.getTags());
        customScenarioDetailsByLocale.setStartCity(routeBodyCustom.getStartCity());
        customScenarioDetailsByLocale.setStartCityCoordinates(routeBodyCustom.getStartCityCoordinates());
        customScenarioDetailsByLocale.setPageViews(routeBodyCustom.getPageViews());
        customScenarioDetailsByLocale.setAuthorBusiness(routeBodyCustom.getAuthor().getBusiness());
        customScenarioDetailsByLocale.setAuthorImage(routeBodyCustom.getAuthor().getImage());
        customScenarioDetailsByLocale.setCategories(getCategoriesByLocale(routeBodyCustom, locale));
        sortedByLocaleScenarioDetais(customScenarioDetailsByLocale, routeBodyCustom, locale);
        customScenarioDetailsByLocale.setDays(sortedDaysDetailsByLocale(
            customScenarioDetailsByLocale, routeBodyCustom, locale));
      }
      hashMap.put("routeBody", customScenarioDetailsByLocale);
      return hashMap;
    }
    return null;
  }

  /**
   * @param routeBodyCustom routeBodyCustom.
   * @param locale          locale.
   * @return Object .
   */
  private List<String> getCategoriesByLocale(RouteBodyCustom routeBodyCustom, String locale) {
    List<String> categoryList = null;
    for (Category category : routeBodyCustom.getCategories()) {
      categoryList = new ArrayList<>();
      if (locale.equals("ar")) {
        categoryList.add(category.getAr());
      }
      if (locale.equals("pt")) {
        categoryList.add(category.getPt());
      }
      if (locale.equals("ru")) {
        categoryList.add(category.getRu());
      }
      if (locale.equals("de")) {
        categoryList.add(category.getDe());
      }
      if (locale.equals("ja")) {
        categoryList.add(category.getJa());
      } else if (categoryList.isEmpty()) {
        categoryList.add(category.getEn());
      }
    }
    return categoryList;
  }


  /**
   * @param customScenarioDetailsByLocale customScenarioDetailsByLocale.
   * @param routeBodyCustom               routeBodyCustom.
   * @param locale                        locale.
   * @return Object.
   */
  private RouteBody sortedByLocaleScenarioDetais(RouteBody customScenarioDetailsByLocale,
                                                 RouteBodyCustom routeBodyCustom, String locale) {

    if (locale.equals("ar")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getAr());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getAr());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getAr());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getAr());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getAr());
      return customScenarioDetailsByLocale;
    }
    if (locale.equals("es")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getEs());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getEs());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getEs());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getEs());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getEs());
      return customScenarioDetailsByLocale;
    }
    if (locale.equals("ja")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getJa());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getJa());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getJa());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getJa());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getJa());
      return customScenarioDetailsByLocale;
    }
    if (locale.equals("de")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getDe());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getDe());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getDe());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getDe());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getDe());
      return customScenarioDetailsByLocale;
    }
    if (locale.equals("pt")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getPt());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getPt());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getPt());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getPt());
      return customScenarioDetailsByLocale;
    }
    if (locale.equals("ru")) {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getRu());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getRu());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getRu());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getRu());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getRu());
      return customScenarioDetailsByLocale;
    } else {
      customScenarioDetailsByLocale.setDescription(routeBodyCustom.getDescription().getEn());
      customScenarioDetailsByLocale.setHighlights(routeBodyCustom.getHighlights().getEn());
      customScenarioDetailsByLocale.setStartCityName(routeBodyCustom.getStartCityName().getEn());
      customScenarioDetailsByLocale.setName(routeBodyCustom.getName().getEn());
      customScenarioDetailsByLocale.setAuthorName(routeBodyCustom.getAuthor().getName().getEn());
      return customScenarioDetailsByLocale;
    }
  }

  /**
   * @param customScenarioDetailsByLocale customScenarioDetailsByLocale.
   * @param routeBodyCustom               routeBodyCustom.
   * @param locale                        locale.
   * @return List<List   <   customDay>>.
   */
  private List<List<CustomDays>> sortedDaysDetailsByLocale(RouteBody customScenarioDetailsByLocale,
                                                           RouteBodyCustom routeBodyCustom, String locale) {
    List<List<CustomDays>> customResponseDaysFinalList = null;

    if (routeBodyCustom.getDays() != null & routeBodyCustom.getDays().size() >= 1) {
      customResponseDaysFinalList = new ArrayList();
      List<CustomDays> daysList = null;

      for (List<Day> day : routeBodyCustom.getDays()) {
        daysList = new ArrayList<>();
        for (Day routeBodyDays : day) {
          CustomDays customResponseDays = new CustomDays();
          if (routeBodyDays.getMetadata() != null) {
            customResponseDays.setMetadata(routeBodyDays.getMetadata());
          }
          if (null != routeBodyDays.getCategories() && routeBodyDays.getCategories().size() >= 1) {
            customResponseDays.setCategory(routeBodyDays.getCategory());
          }
          if (null != routeBodyDays.getId()) {
            customResponseDays.setId(routeBodyDays.getId());
          }
          sortedDaysFieldsByLocale(routeBodyDays, locale, customResponseDays);

          if (null != routeBodyDays.getPoint()) {
            customResponseDays.setPoint(routeBodyDays.getPoint());
          }
          if (null != routeBodyDays.getPhotos()) {
            customResponseDays.setPhotos(routeBodyDays.getPhotos());
          }
          if (null != routeBodyDays.getRoutes()) {
            customResponseDays.setRoutes(routeBodyDays.getRoutes());
          }
          customResponseDays.setCategories(sortedRouteBodyCategoriesByLocale(routeBodyDays, locale));
          daysList.add(customResponseDays);
        }
        customResponseDaysFinalList.add(daysList);
      }
    }
    return customResponseDaysFinalList;
  }

  /**
   * @param routeBody routeBody.
   * @param locale    locale.
   * @return List<String> .
   */
  private List<String> sortedRouteBodyCategoriesByLocale(Day routeBody, String locale) {
    List<String> customCategorylist = new ArrayList<>();
    if (routeBody.getCategories() != null && routeBody.getCategories().size() >= 1) {
      for (com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.details.routebody.days.category.
            Category category : routeBody.getCategories()) {
        if (locale.equals("ar")) {
          customCategorylist.add(category.getName().getAr());
        }
        if (locale.equals("es")) {
          customCategorylist.add(category.getName().getEs());
        }
        if (locale.equals("ru")) {
          customCategorylist.add(category.getName().getRu());
        }
        if (locale.equals("de")) {
          customCategorylist.add(category.getName().getDe());
        }
        if (locale.equals("pt")) {
          customCategorylist.add(category.getName().getPt());
        } else if (locale.equals("en")) {
          customCategorylist.add(category.getName().getEn());
        }
      }
    }
    return customCategorylist;
  }

  /**
   * @param routeBodyDays      routeBodyDays.
   * @param locale             locale.
   * @param customResponseDays customResponseDays.
   * @return CustomDays .
   */

  private CustomDays sortedDaysFieldsByLocale(Day routeBodyDays, String locale, CustomDays customResponseDays) {

    if (locale.equals("ar")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getAr());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getAr().getText());
      }
      if (null != routeBodyDays.getName().getAr()) {
        customResponseDays.setName(routeBodyDays.getName().getAr());
      }
    }
    if (locale.equals("es")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getEs());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getEs().getText());
      }
      if (null != routeBodyDays.getName().getEs()) {
        customResponseDays.setName(routeBodyDays.getName().getEs());
      }
    }
    if (locale.equals("ru")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getRu());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getRu().getText());
      }
      if (null != routeBodyDays.getName().getRu()) {
        customResponseDays.setName(routeBodyDays.getName().getRu());
      }
    }
    if (locale.equals("de")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getDe());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getDe().getText());
      }
      if (null != routeBodyDays.getName().getDe()) {
        customResponseDays.setName(routeBodyDays.getName().getDe());
      }
    }
    if (locale.equals("pt")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getPt());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getPt().getText());
      }
      if (null != routeBodyDays.getName().getPt()) {
        customResponseDays.setName(routeBodyDays.getName().getPt());
      }
    }
    if (locale.equals("fr")) {
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getFr().getText());
      }
    }
    if (locale.equals("it")) {
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getIt().getText());
      }
    }
    if (locale.equals("pt-br")) {
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getPtBr().getText());
      }
    }
    if (locale.equals("ru")) {
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getRu().getText());
      }
      if (null != routeBodyDays.getName().getRu()) {
        customResponseDays.setName(routeBodyDays.getName().getRu());
      }
    } else if (locale.equals("en")) {
      if (null != routeBodyDays.getDescription()) {
        customResponseDays.setDescription(routeBodyDays.getDescription().getEn());
      }
      if (null != routeBodyDays.getTitle()) {
        customResponseDays.setTitle(routeBodyDays.getTitle().getEn().getText());
      }
      if (null != routeBodyDays.getName().getEn()) {
        customResponseDays.setName(routeBodyDays.getName().getEn());
      }
    }
    return customResponseDays;
  }

  /**
   * @param scenarioId scenarioId.
   * @param language   language.
   * @param pathStyle  pathStyle.
   * @param pinStyle   pinStyle.
   * @param day        day.
   * @return Object.
   * @throws IOException IOException.
   */
  @Override
  public Object getRoadTripViewOnMapUrl(String scenarioId, String language, String pathStyle,
                                        String pinStyle, Integer day, String size) throws IOException {
    String sSuffixUrl = "scenario/staticmap";
    String encodedPathStyle = URLEncoder.encode(pathStyle);
    String encodedPinStyle = URLEncoder.encode(pinStyle);
    String aApiEndPoint = getRoadTripScenariosExternalUrlWithParams() + sSuffixUrl + Constants.QUESTION_MARK
        + "scenario_id" + Constants.EQUAL + scenarioId + Constants.CONST_AMPERSAND + "language"
        + Constants.EQUAL + language
        + Constants.CONST_AMPERSAND + "pathStyle" + Constants.EQUAL
        + encodedPathStyle + Constants.CONST_AMPERSAND
        + "pinStyle" + Constants.EQUAL + encodedPinStyle
        + Constants.CONST_AMPERSAND + "day" + Constants.EQUAL + day
        + Constants.CONST_AMPERSAND + "size" + Constants.EQUAL + size;
    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        aApiEndPoint, StringUtils.EMPTY, false);
    JsonParser parser = new JsonParser();
    JsonElement jsonElement = parser.parse(responseMessage.getMessage());
    return jsonElement;
  }

  /**
   * @param locale locale.
   * @param userID userID.
   * @return Object .
   * @throws IOException   IOException
   * @throws JSONException jsonException.
   */
  public Object getRoadTripAuthorDetails(String userID, String locale) throws IOException, JSONException {
    String sSuffixUrl = "user/scenarios";
    String aApiEndPoint = getRoadTripScenariosExternalUrlWithParams() + sSuffixUrl + Constants.QUESTION_MARK
        + "user_id" + Constants.EQUAL + userID;
    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        aApiEndPoint, StringUtils.EMPTY, false);
    AuthorDetails authorDetails = new Gson().fromJson(responseMessage.getMessage(), AuthorDetails.class);
    Map<String, Object> customMap = new HashMap<>();
    if (authorDetails.getUsers().size() >= 1) {
      customMap.put("users", getCustomAuthorDetails(locale, authorDetails));
    }
    if (authorDetails.getScenarios().size() >= 1) {
      customMap.put("scenarios_user", getCustomerDetailsScenarios(authorDetails, locale));
    }
    return customMap;
  }

  /**
   * @param locale        locale.
   * @param authorDetails authorDetails.
   * @return List CustomUserDetails.
   */
  private List<CustomUserDetails> getCustomAuthorDetails(String locale, AuthorDetails authorDetails) {
    List<CustomUserDetails> userList = new ArrayList<>();
    CustomUserDetails customUserDetails = null;
    for (User userDetails : authorDetails.getUsers()) {
      customUserDetails = new CustomUserDetails();
      customUserDetails.setAffiliate(userDetails.getAffiliate());
      customUserDetails.setAllow(userDetails.getAllow());
      customUserDetails.setAnonymous(userDetails.getAnonymous());
      customUserDetails.setBookmarks(userDetails.getBookmarks());
      customUserDetails.setCurrency(userDetails.getCurrency());
      customUserDetails.setId(userDetails.getId());
      customUserDetails.setBusiness(userDetails.getBusiness());
      customUserDetails.setEmailUnconfirmed(userDetails.getEmailUnconfirmed());
      customUserDetails.setGeoid(userDetails.getGeoid());
      customUserDetails.setImages(userDetails.getImages());
      customUserDetails.setLang(userDetails.getLang());
      customUserDetails.setVwCampaignAccept(userDetails.getVwCampaignAccept());
      customUserDetails.setVerified(userDetails.getVerified());
      customUserDetails.setUnitSystem(userDetails.getUnitSystem());
      customUserDetails.setSubscription(userDetails.getSubscription());
      customUserDetails.setSafebutton(userDetails.getSafebutton());
      customUserDetails.setResidence(userDetails.getResidence());
      customUserDetails.setEmail(userDetails.getEmail());
      customUserDetails.setPhoneUnconfirmed(userDetails.getPhoneUnconfirmed());
      customUserDetails.setNotificationsCount(userDetails.getNotificationsCount());
      customUserDetails.setLinkSpecial(userDetails.getLinkSpecial());
      customUserDetails.setLinks(userDetails.getLinks());
      customUserDetails.setPhone(userDetails.getPhone());
      customUserDetails.setMailings(userDetails.getMailings());
      customUserDetails.setRegistrationCode(userDetails.getRegistrationCode());
      customUserDetails.setGeo(userDetails.getGeo());
      sortedUserDetailsByLocale(locale, userDetails, customUserDetails);
    }
    userList.add(customUserDetails);
    return userList;
  }

  /**
   * @param locale            locale.
   * @param customUserDetails customUser.
   * @param user              user.
   * @return CustomUserDetails.
   */
  private CustomUserDetails sortedUserDetailsByLocale(String locale, User user, CustomUserDetails customUserDetails) {

    if (locale.equals("ar")) {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getAr().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getAr().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getAr().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getAr().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getAr()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getAr().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("de")) {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getDe().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getDe().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getDe().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getDe().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getDe()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getDe().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("ru")) {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getRu().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getRu().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getRu().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getRu().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getRu()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getRu().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("es")) {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getEs().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getEs().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getEs().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getEs().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getEs()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getEs().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("pt")) {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getPt().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getPt().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getPt().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getPt().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getPt()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getPt().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("ja")) {
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getJa().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getJa().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getJa()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getJa().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("ptBr")) {
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getPtBr().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getPtBr().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("fr")) {
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getFr().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getFr().getText());
      }
      return customUserDetails;
    }
    if (locale.equals("it")) {
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getIt().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getIt().getText());
      }
      return customUserDetails;
    } else {
      if (null != user.getFirstname()) {
        customUserDetails.setFirstname(user.getFirstname().getEn().getText());
      }
      if (null != user.getLastname()) {
        customUserDetails.setLastname(user.getLastname().getEn().getText());
      }
      if (null != user.getResidenceName()) {
        customUserDetails.setResidenceName(user.getResidenceName().getEn().getText());
      }
      if (null != user.getAbout()) {
        customUserDetails.setAbout(user.getAbout().getEn().getText());
      }
      if (null != user.getVisitedPlaces() && null != user.getVisitedPlaces().getEn()) {
        customUserDetails.setVisitedPlaces(user.getVisitedPlaces().getEn().getText());
      }
    }

    return customUserDetails;
  }

  /**
   * @param authorDetails authorDetails.
   * @param locale        locale.
   * @return List.
   */
  private List<NativeAppCustomCardResponse> getCustomerDetailsScenarios(AuthorDetails authorDetails, String locale) {
    List<NativeAppCustomCardResponse> trips = new ArrayList<>();
    NativeAppCustomCardResponse nativeAppCustomCardResponse = null;
    for (Scenario scenario : authorDetails.getScenarios()) {
      nativeAppCustomCardResponse = new NativeAppCustomCardResponse();
      nativeAppCustomCardResponse.setDistance(scenario.getSumDistance());
      nativeAppCustomCardResponse.setDuration(scenario.getDuration());
      nativeAppCustomCardResponse.setId(scenario.getUid());
      nativeAppCustomCardResponse.setTransport(scenario.getTransport());
      nativeAppCustomCardResponse.setStartCity(scenario.getStartCity());
      nativeAppCustomCardResponse.setCategories(scenario.getCategories());
      nativeAppCustomCardResponse.setScenarioId(scenario.getScenarioId());
      nativeAppCustomCardResponse.setGuidance(scenario.getGuidance());
      nativeAppCustomCardResponse.setCategoriesI18n(
          sortedResponseOnBasisOfCategories(
          scenario, locale));
      nativeAppCustomCardResponse.setImage(getImages(scenario));
      setResponseOnBasisOfLocale(scenario, locale, nativeAppCustomCardResponse);
      trips.add(nativeAppCustomCardResponse);
    }
    return trips;
  }
}
