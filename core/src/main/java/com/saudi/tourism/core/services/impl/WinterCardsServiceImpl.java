package com.saudi.tourism.core.services.impl;

import com.google.gson.Gson;
import com.saudi.tourism.core.beans.RoadTripData;
import com.saudi.tourism.core.beans.RoadTripResponse;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WinterCardsService;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Service Impl for Winter Cards .
 */
@Slf4j
@Component(service = WinterCardsService.class, immediate = true)
public class WinterCardsServiceImpl implements WinterCardsService {

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  /**
   * Constant road trip end point .
   */
  private static final String ROAD_TRIP_END_POINT = "scenarios/search";

  /**
   * Constant road trip end point with scenario id.
   */
  private static final String ROAD_TRIP_END_POINT_SCENARIO_ID = "visitsaudi/scenario?scenario_id=";

  /**
   * List of String.
   */
  private final List<RoadTripData> list = new ArrayList<RoadTripData>();

  /**
   * RoadTripResponse object.
   */
  private RoadTripResponse roadTripResponse;

  /**
   * Constant DURATION_ONE.
   */
  private static final Integer DURATION_ONE =  60;

  /**
   * Constant DURATION_TWO.
   */
  private static final Integer DURATION_TWO =  24;

  /**
   * Method to get the Experience Categories .
   */
  @Override
  public String getExperienceCategories(Map<String, Object> queryString) throws IOException {
    String url = saudiTourismConfig.getWinter2021CardsApiEndpoint();
    String token = getToken();
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(url,
        token, queryString);
    return responseMessage.getMessage();
  }

  /**
   * Method to get the Experience Categories .
   */
  @Override
  public String getExperience(Map<String, Object> queryString) throws IOException {
    String url = saudiTourismConfig.getWinterCardsExperienceApiEndpoint();
    String token = getToken();
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(url,
        token, queryString);
    return responseMessage.getMessage();
  }

  @Override
  public String getExperienceDetails(Map<String, Object> queryString, String experienceId)
      throws IOException {
    String url = saudiTourismConfig.getWinterCardsExperienceApiEndpoint();
    String newUrl = url + SpecialCharConstants.FORWARD_SLASH + experienceId;
    String token = getToken();
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParameters(newUrl,
        token, queryString);
    return responseMessage.getMessage();
  }

  @Override
  public List getRoadTripScenariosFromApi(Integer limit, String locale, String city) throws IOException {
    list.clear();
    String loadedRoadTripScenarios = getRoadTripScenariosViaApiUrl();
    List<String> stringIds = DataUtils
        .filterRequiredRoadTripScenariosApiUrl(loadedRoadTripScenarios, limit, city);

    for (String scenarioId : stringIds) {
      RoadTripData data = new RoadTripData();
      ResponseMessage responseMessage = RestHelper
          .executeMethodGet(getRoadTripScenariosExternalUrlWithParams()
          + ROAD_TRIP_END_POINT_SCENARIO_ID + scenarioId, StringUtils.EMPTY, false);
      Gson gson = new Gson();
      String roadTripAPIResponse = responseMessage.getMessage();
      roadTripResponse = gson.fromJson(roadTripAPIResponse, RoadTripResponse.class);
      extractRoadTripDetails(locale, data);
    }
    return list;
  }

  /**
   * This method is used to extract the road trip details.
   * @param locale .
   * @param data .
   */
  private void extractRoadTripDetails(String locale, RoadTripData data) {
    String photo = roadTripResponse.getRouteBody().get(0).getPhoto();
    String id = roadTripResponse.getRouteBody().get(0).getId();
    String tripNameEN = roadTripResponse.getRouteBody().get(0).getName().getEn();
    String tripNameAR = roadTripResponse.getRouteBody().get(0).getName().getAr();
    String tripNameES = roadTripResponse.getRouteBody().get(0).getName().getEs();
    String tripNamePT = roadTripResponse.getRouteBody().get(0).getName().getPt();
    String tripNameRU = roadTripResponse.getRouteBody().get(0).getName().getRu();
    String tripNameDE = roadTripResponse.getRouteBody().get(0).getName().getDe();

    String authorBusiness = roadTripResponse.getRouteBody().get(0).getAuthor().getBusiness();

    String authorNameEN = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getEn();
    String authorNameAR = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getAr();
    String authorNameDE = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getDe();
    String authorNameES = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getEs();
    String authorNamePT = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getPt();
    String authorNameRU = roadTripResponse.getRouteBody().get(0).getAuthor().getName().getRu();

    String authorImage = roadTripResponse.getRouteBody().get(0).getAuthor().getImage();

    String tripDescriptionEN = roadTripResponse.getRouteBody().get(0).getDescription().getEn();
    String tripDescriptionAR = roadTripResponse.getRouteBody().get(0).getDescription().getAr();
    String tripDescriptionES = roadTripResponse.getRouteBody().get(0).getDescription().getEs();
    String tripDescriptionPT = roadTripResponse.getRouteBody().get(0).getDescription().getPt();
    String tripDescriptionRU = roadTripResponse.getRouteBody().get(0).getDescription().getRu();
    String tripDescriptionDE = roadTripResponse.getRouteBody().get(0).getDescription().getDe();

    String tripCityEN = roadTripResponse.getRouteBody().get(0).getCity().getEn();
    String tripCityAR = roadTripResponse.getRouteBody().get(0).getCity().getAr();
    String tripCityES = roadTripResponse.getRouteBody().get(0).getCity().getEs();
    String tripCityPT = roadTripResponse.getRouteBody().get(0).getCity().getPt();
    String tripCityRU = roadTripResponse.getRouteBody().get(0).getCity().getRu();
    String tripCityDE = roadTripResponse.getRouteBody().get(0).getCity().getDe();
    Integer duration = roadTripResponse.getRouteBody().get(0).getMetadata().getDuration();
    Integer date = duration / DURATION_ONE;
    Integer days = date / DURATION_TWO;

    data.setDays(days);
    data.setPhoto(photo);
    data.setId(id);
    data.setAuthorBusiness(authorBusiness);
    data.setAuthorImage(authorImage);

    data.setAuthorNameEN(authorNameEN);
    data.setAuthorNameAR(authorNameAR);
    data.setAuthorNameES(authorNameES);
    data.setAuthorNamePT(authorNamePT);
    data.setAuthorNameRU(authorNameRU);
    data.setAuthorNameDE(authorNameDE);

    data.setTripNameEN(tripNameEN);
    data.setTripNameENModified(StringUtils
        .lowerCase(tripNameEN.replace(" ", "-"), new Locale(locale)));

    data.setTripNameAR(tripNameAR);
    data.setTripNameARModified(StringUtils
        .lowerCase(tripNameAR.replace(" ", "-"), new Locale(locale)));

    data.setTripNameES(tripNameES);
    data.setTripNameESModified(StringUtils
        .lowerCase(tripNameES.replace(" ", "-"), new Locale(locale)));

    data.setTripNamePT(tripNamePT);
    data.setTripNamePTModified(StringUtils
        .lowerCase(tripNamePT.replace(" ", "-"), new Locale(locale)));

    data.setTripNameRU(tripNameRU);
    data.setTripNameRUModified(StringUtils
        .lowerCase(tripNameRU.replace(" ", "-"), new Locale(locale)));

    data.setTripNameDE(tripNameDE);
    data.setTripNameDEModified(StringUtils
        .lowerCase(tripNameDE.replace(" ", "-"), new Locale(locale)));

    data.setTripDescriptionEN(tripDescriptionEN);
    data.setTripDescriptionAR(tripDescriptionAR);
    data.setTripDescriptionES(tripDescriptionES);
    data.setTripDescriptionPT(tripDescriptionPT);
    data.setTripDescriptionRU(tripDescriptionRU);
    data.setTripDescriptionDE(tripDescriptionDE);

    data.setTripCityEN(tripCityEN);
    data.setTripCityAR(tripCityAR);
    data.setTripCityES(tripCityES);
    data.setTripNamePT(tripCityPT);
    data.setTripCityRU(tripCityRU);
    data.setTripCityDE(tripCityDE);

    list.add(data);
  }

  /**
   * This method is used to get road trip data.
   *
   * @return the road trip data via api url
   * @throws IOException .
   */
  private String getRoadTripScenariosViaApiUrl() throws IOException {
    ResponseMessage responseMessage = RestHelper.executeMethodGet(
        getRoadTripScenariosExternalUrlWithParams() + ROAD_TRIP_END_POINT, StringUtils.EMPTY, false);
    return responseMessage.getMessage();
  }

  /**
   * This method is used to construct the URL.
   *
   * @return String .
   */
  private String getRoadTripScenariosExternalUrlWithParams() {
    return saudiTourismConfig.getRoadTripScenariosExternalAppURL();
  }

  /**
   * Method to get the HalaYalla token .
   * @return String
   */
  private String getToken() {
    return saudiTourismConfig.getHalayallaToken();
  }

  /**
   * This method is used to construct the URL.
   * @return String .
   */
  private String getHalaYallaEndPointUrl() {
    return saudiTourismConfig.getHalayallaEndPointUrl() + "/api/vs/";
  }
}
