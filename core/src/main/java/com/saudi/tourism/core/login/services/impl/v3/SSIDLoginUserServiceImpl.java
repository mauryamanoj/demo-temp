package com.saudi.tourism.core.login.services.impl.v3;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.SsidException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.SSIDFavouritesTripsConfig;
import com.saudi.tourism.core.login.services.SaudiLoginConfig;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.login.services.TokenService;
import com.saudi.tourism.core.login.services.v3.SSIDLoginUserService;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.services.SSIDTripPlanService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.ProfileConstants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.SSIDRestHelper;
import com.saudi.tourism.core.utils.SearchUtils;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.saudi.tourism.core.utils.ProfileConstants.CP_APPLE;
import static com.saudi.tourism.core.utils.ProfileConstants.CP_TWITTER;
import static com.saudi.tourism.core.utils.ProfileConstants.DEFAULT_EMAIL_KEY;
import static com.saudi.tourism.core.utils.ProfileConstants.DEFAULT_LAST_NAME_KEY;
import static com.saudi.tourism.core.utils.ProfileConstants.FAVORITES_COUNT;
import static com.saudi.tourism.core.utils.ProfileConstants.ITEMS;
import static com.saudi.tourism.core.utils.ProfileConstants.PICTURE_KEY;
import static com.saudi.tourism.core.utils.ProfileConstants.PN_FIRST_NAME;
import static com.saudi.tourism.core.utils.ProfileConstants.PN_LAST_NAME;
import static com.saudi.tourism.core.utils.ProfileConstants.TRIP_COUNT;
import static com.saudi.tourism.core.utils.ProfileConstants.UNSUPPORTED_TYPE_MESSAGE;
import static com.saudi.tourism.core.utils.ProfileConstants.USER_METADATA_KEY;
import static javax.servlet.http.HttpServletResponse.SC_ACCEPTED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * The Token Service.
 */
@Slf4j
@Component(service = SSIDLoginUserService.class, immediate = true)
public class SSIDLoginUserServiceImpl implements SSIDLoginUserService {

  /**
   * The constant FAVORITES.
   */
  public static final String FAVORITES = "favorites";
  /**
   * The constant TRIPS.
   */
  public static final String TRIPS = "trips";
  /**
   * The constant PROFILE.
   */
  public static final String PROFILE = "profile";

  /**
   * The constant GAMES.
   */
  public static final String GAMES = "games";

  /**
   * Saudi Login Configurations.
   */
  @Reference
  private SaudiLoginConfig saudiLoginConfig;

  /**
   * The service for working with trip plans.
   */
  @Reference
  private SSIDTripPlanService tripPlanService;

  /**
   * The Token Service.
   */
  @Reference
  private TokenService tokenService;

  /**
   * Resource exporter.
   */
  @Reference
  private ResourceExporterService resourceExporterService;

  /**
   * User service.
   */
  @Reference
  private UserService userService;

  /**
   * SSIDFavouritesTripsConfigImpl config.
   */
  @Reference
  private transient SSIDFavouritesTripsConfig config;

  /**
   * SSID config.
   */
  @Reference
  private transient SaudiSSIDConfig ssidConfig;

  /**
   * Settings service.
   */
  @Reference
  private SlingSettingsService settingsService;
  /**
   * Favorites service.
   */
  @Reference
  private FavoritesService favoritesService;

  @Override
  public String getUserDetails(final UserIDToken userIDToken, final String type) throws IOException,
      MiddlewareException {
    return getUserDetails(userIDToken, type, false, null);
  }

  @Override
  public String getUserDetailsFull(final UserIDToken userIDToken, final String type,
                                   final Map<String, Object> filterParams) throws IOException, MiddlewareException {
    return getUserDetails(userIDToken, type, true, filterParams);
  }

  /**
   * Gets user details.
   *
   * @param userIDToken  the user id token
   * @param type         the type
   * @param populateData the populate data
   * @param filterParams properties map for filtering
   * @return the user details
   * @throws IOException the io exception
   * @throws MiddlewareException the io exception
   */
  public String getUserDetails(final UserIDToken userIDToken, final String type, final boolean populateData,
                               final Map<String, Object> filterParams) throws IOException, MiddlewareException {
    String userEndPoint = config.getMiddlewareDomain() + config.getNativeAppUserDetailsEndpoint();
    HttpGet request = new HttpGet(userEndPoint);
    request.addHeader("token", getAccessToken(userIDToken.getToken()));

    final HttpClient client = HttpClientBuilder.create().build();
    final HttpResponse response = client.execute(request);

    ResponseMessage responseMessage = new ResponseMessage(response.getStatusLine().getStatusCode(),
        readBody(response.getEntity().getContent()));
    if (responseMessage.getStatusCode() != SC_OK
        || StringUtils.isNotEmpty(getFaultStatusSSIDMiddleware(responseMessage.getMessage()))) {
      throw new MiddlewareException(responseMessage.getMessage());
    }
    return handleUserProfileFilterResponse(type, responseMessage, userIDToken, populateData, filterParams);
  }

  /**
   * Read the body and returnt the String version of the body.
   *
   * @param bodyStream the body stream
   * @return string
   * @throws IOException the io exception
   */
  private static String readBody(final InputStream bodyStream) throws IOException {
    BufferedReader rd = new BufferedReader(new InputStreamReader(bodyStream));
    StringBuilder body = new StringBuilder();
    String line;

    while ((line = rd.readLine()) != null) {
      body.append(line);
    }
    return body.toString();
  }

  /**
   * Gets access token.
   *
   * @param token the token
   * @return the access token
   * @throws IOException the io exception
   */
  private String getAccessToken(final String token) throws IOException {
    if (Objects.nonNull(token)) {
      return token;
    }
    return tokenService.getMachineToMachineToken();
  }

  /**
   * Handle user profile filter response string.
   *
   * @param type            the type
   * @param responseMessage the response message
   * @param userIDToken     the userIDToken
   * @param populateData    true to full fill data from CRX (if applied)
   * @param filterParams    properties map for filtering
   * @return the string
   * @throws IOException exception.
   */
  @Nullable
  public String handleUserProfileFilterResponse(final String type, final ResponseMessage responseMessage,
                                                final UserIDToken userIDToken, final boolean populateData,
                                                final Map<String, Object> filterParams)
      throws IOException {
    JSONObject jsonRespObject;
    if (responseMessage.getStatusCode() != SC_OK && responseMessage.getStatusCode() != SC_ACCEPTED) {
      throw new SsidException(responseMessage.getMessage());
    }
    try {
      jsonRespObject = new JSONObject(responseMessage.getMessage());

      JSONObject userMetaData = getUserMetadata(jsonRespObject, userIDToken);
      switch (type) {
        case PROFILE:
          return userMetaData.getJSONObject(type).toString();
        case "all":
          return userMetaData.toString();
        case GAMES:
          return userMetaData.getJSONArray(type).toString();
        case FAVORITES:
          JSONArray items = userMetaData.getJSONArray(type);
          JSONObject favoritesObject = createFavoritesObject(populateData, items, userIDToken, filterParams);
          return favoritesObject.toString();
        case TRIPS:
          final JSONArray jsonArray = userMetaData.getJSONArray(type);

          if (populateData) {
            // Filter array before processing
            final JSONArray metadataTripsArray = CommonUtils.filterJsonArrByProp(jsonArray, filterParams);

            return RestHelper.getObjectMapper()
                .writeValueAsString(tripPlanService.fromJson(metadataTripsArray, userIDToken.getLocale()));
          }

          // Filter by id (propName/propValue)
          return CommonUtils.filterJsonArrByProp(jsonArray, filterParams).toString();
        default:
          return UNSUPPORTED_TYPE_MESSAGE;
      }
    } catch (JSONException | JsonProcessingException | SSIDFunctionnalException | MiddlewareException e) {
      LOGGER.error("Couldn't process JSON {}", responseMessage.getMessage(), e);
      throw new IllegalStateException("User profile metadata error: Could not process JSON", e);
    }
  }

  /**
   * Create Favorites Object.
   *
   * @param populateData is populateData
   * @param items        items
   * @param userIDToken  userIDToken
   * @param filterParams filterParams
   * @return Favorites Object
   * @throws JSONException - error
   */
  private JSONObject createFavoritesObject(boolean populateData, JSONArray items, UserIDToken userIDToken,
                                           Map<String, Object> filterParams) throws JSONException {
    JSONObject favoritesObject = new JSONObject();

    if (!populateData) {
      favoritesObject.put(ITEMS, items);
      return favoritesObject;
    }
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      List<DictItem> types =
          SearchUtils.getSearchCategoriesFromTags(userService.getResourceResolver(), userIDToken.getLocale());
      final JSONArray jsonArray = processFavoritesArray(items, resourceResolver, userIDToken.getLocale());
      if (filterParams != null && !filterParams.isEmpty()
          && !(filterParams.size() == 1 && filterParams.containsKey(Constants.LOCALE))) {
        final JSONArray filteredDetails = CommonUtils.filterJsonArrByProp(jsonArray, filterParams);
        final JSONArray filteredItems = new JSONArray();

        if (filteredDetails.length() > 0) {
          final Set<String> favIdsInDetails = collectFavoriteIds(filteredDetails);
          for (int i = 0; i < items.length(); i++) {
            final Object oneFav = items.opt(i);
            if (oneFav instanceof String && favIdsInDetails.contains(oneFav)) {
              filteredItems.put(oneFav);
            }
          }
        }

        favoritesObject.put("details", groupByCategories(filteredDetails, types));
        favoritesObject.put("items", filteredItems);

      } else {
        // Do not filter
        favoritesObject.put("details", groupByCategories(jsonArray, types));
        favoritesObject.put("items", items);
      }
    }
    return favoritesObject;
  }

  /**
   * Collect all favorite ids from the populated json array.
   *
   * @param filteredDetails filteredDetails
   * @return all favorite ids
   * @throws JSONException error
   */
  private Set<String> collectFavoriteIds(JSONArray filteredDetails) throws JSONException {
    final Set<String> favIdsInDetails = new HashSet<>();
    for (int i = 0; i < filteredDetails.length(); i++) {
      final JSONObject jsonObject = filteredDetails.optJSONObject(i);
      if (jsonObject == null) {
        continue;
      }
      String favId = jsonObject.getString("favId");
      if (StringUtils.isBlank(favId)) {
        favId = jsonObject.getString(Constants.PN_ID);
      }
      if (StringUtils.isNotBlank(favId)) {
        favIdsInDetails.add(favId);
      }
    }
    return favIdsInDetails;
  }

  /**
   * Gets user metadata.
   *
   * @param jsonRespObject the json resp object
   * @param userIDToken    the userIDToken
   * @return the user metadata
   * @throws JSONException the json exception
   * @throws IOException   the IO exception
   * @throws SSIDFunctionnalException   the SSID exception
   * @throws MiddlewareException   the Middleware exception
   */
  public JSONObject getUserMetadata(final JSONObject jsonRespObject, final UserIDToken userIDToken)
      throws JSONException, IOException, SSIDFunctionnalException, MiddlewareException {
    JSONObject metaData = null;
    if (jsonRespObject.has(USER_METADATA_KEY)) {
      metaData = jsonRespObject.getJSONObject(USER_METADATA_KEY);
    }

    if (!Objects.isNull(metaData) && jsonRespObject.has(PICTURE_KEY) && metaData.has(PROFILE)) {
      JSONObject profileData = metaData.getJSONObject(PROFILE);
      profileData.put(PICTURE_KEY, jsonRespObject.optString(PICTURE_KEY, Constants.BLANK));
    }


    if (Objects.isNull(metaData) || "{}".equals(metaData.toString())) {
      // Initialise meta_data for new user
      metaData = new JSONObject();

      JSONObject profile = new JSONObject();

      String connectionProvider = getConnectionProvider(jsonRespObject);
      profile.put(PN_FIRST_NAME,
          getJSONObjString(jsonRespObject, getProviderKeyName(PN_FIRST_NAME, connectionProvider)));
      profile.put(PN_LAST_NAME, getJSONObjString(jsonRespObject, getProviderKeyName(PN_LAST_NAME, connectionProvider)));
      profile.put("email", getJSONObjString(jsonRespObject, DEFAULT_EMAIL_KEY));
      profile.put("languageCode", "");
      profile.put("birthDate", "");
      profile.put("genderCode", "");
      profile.put("ageRange", "");
      profile.put("visaNumber", "");
      profile.put("travelPartner", "");
      profile.put("interests", new JSONArray());
      profile.put(TRIP_COUNT, 0);
      profile.put(FAVORITES_COUNT, 0);
      metaData.put(PROFILE, profile);
      metaData.put(TRIPS, new JSONArray());
      metaData.put(FAVORITES, new JSONArray());
      JSONObject metaJsonObject = new JSONObject();
      metaJsonObject.put(USER_METADATA_KEY, metaData);
      patchUserMetadata(userIDToken, metaJsonObject.toString(), null);
    }

    return metaData;
  }

  /**
   * Get Connection provider from connection identity.
   *
   * @param jsonRespObject jsonResponse object
   * @return connection provider
   * @throws JSONException json exception
   */
  private String getConnectionProvider(final JSONObject jsonRespObject) throws JSONException {
    if (jsonRespObject.has("identities")) {
      JSONArray jsonArray = jsonRespObject.getJSONArray("identities");
      if (jsonArray.length() >= 1) {
        JSONObject identityObject = jsonArray.getJSONObject(0);
        return getJSONObjString(identityObject, "provider");
      }
    }
    return StringUtils.EMPTY;
  }

  /**
   * Provider key name.
   *
   * @param key      key
   * @param provider provider
   * @return key name according provider
   */
  private String getProviderKeyName(final String key, final String provider) {
    if (PN_FIRST_NAME.equals(key)) {
      if (CP_TWITTER.equals(provider)) {
        return "name";
      } else if (CP_APPLE.equals(provider)) {
        return "first_name";
      }
      return ProfileConstants.DEFAULT_FIRST_NAME_KEY;
    }
    if (PN_LAST_NAME.equals(key)) {
      if (CP_APPLE.equals(provider)) {
        return "last_name";
      }
      return DEFAULT_LAST_NAME_KEY;
    }
    return StringUtils.EMPTY;
  }

  /**
   * Gets json obj string.
   *
   * @param jsonRespObject the json resp object
   * @param key            the key
   * @return the json obj string
   * @throws JSONException the json exception
   */
  private String getJSONObjString(final JSONObject jsonRespObject, final String key) throws JSONException {
    if (jsonRespObject.has(key)) {
      return jsonRespObject.getString(key);
    }
    return "";
  }

  /**
   * Recursively process array of favorite items.
   *
   * @param favorites        favorites
   * @param resourceResolver resource resolver
   * @param locale           locale
   * @return favoriteDetails favoriteDetails
   * @throws JSONException JSONException
   */
  private JSONArray processFavoritesArray(
      final JSONArray favorites, final ResourceResolver resourceResolver, final String locale) {

    if (favorites == null || favorites.length() == 0) {
      return new JSONArray();
    }

    final List<String> favoritePaths =
        IntStream.range(0, favorites.length())
            .mapToObj(
                i -> {
                  try {
                    return favorites.get(i);
                  } catch (JSONException e) {
                    LOGGER.warn("Error while getting favorite path", e);
                  }
                  return null;
                })
            .filter(Objects::nonNull)
            .filter(f -> f instanceof String)
            .map(String.class::cast)
            .collect(Collectors.toList());

    final List<Object> favoritesData =
        favoritesService.getFavoritesData(resourceResolver, locale, favoritePaths);

    final JSONArray favoriteDetails = new JSONArray();

    // Add all the element from 'favoritesData' to 'favoriteDetails'
    Optional.of(favoritesData).orElse(Collections.emptyList()).stream()
        .filter(Objects::nonNull)
        .forEach(fd -> favoriteDetails.put(fd));

    return favoriteDetails;
  }

  /**
   * Create Favorite Roadtrip Item.
   *
   * @param resourcePath resourcePath
   * @return favorite item for roadtrip favorite
   */
  private String createFavoriteRoadtripAsString(String resourcePath) {
    String scenarioId = resourcePath.split(Constants.ROADTRIP_PREFIX)[1];
    try {
      JSONObject roadtripObj = new JSONObject();
      roadtripObj.put("favCategory", Constants.ROADTRIPS_SEARCH_CATEGORY);
      roadtripObj.put("scenarioId", scenarioId);
      return roadtripObj.toString();
    } catch (JSONException e) {
      return null;
    }
  }

  /**
   * Create Favorite Package Item.
   *
   * @param resourcePath resourcePath
   * @return favorite item for package favorite
   */
  private String createFavoritePackageAsString(String resourcePath) {
    String[] experienceIds = resourcePath.split("experienceId=");
    if (experienceIds.length < 1) {
      return null;
    }

    String experienceId = experienceIds[1];
    try {
      JSONObject experienceObj = new JSONObject();
      experienceObj.put("favCategory", Constants.PACKAGES_SEARCH_CATEGORY);
      experienceObj.put("experienceId", experienceId);
      return experienceObj.toString();
    } catch (JSONException e) {
      return null;
    }
  }

  /**
   * Create Favorite External link .
   *
   * @param resourcePath resourcePath
   * @return favorite item for package external link
   */
  private String createFavoriteExternalLinkAsString(String resourcePath) {
    try {
      JSONObject experienceObj = new JSONObject();
      experienceObj.put("favCategory", Constants.MEET_SAUDI_SEARCH_CATEGORY);
      experienceObj.put("url", resourcePath);
      return experienceObj.toString();
    } catch (JSONException e) {
      return null;
    }
  }

  /**
   * Adapt favorite json object to model, and return input if it is not adaptable.
   *
   * @param favorite         favorite
   * @param resourceResolver resource resolver
   * @param locale           locale
   * @return adapted object or json object
   */
  private Object getFavoriteModel(String favorite, ResourceResolver resourceResolver,
                                  String locale) {
    return Optional.ofNullable(favorite).map(resourcePath -> {
      if (resourcePath.startsWith(Constants.ROADTRIP_PREFIX)) {
        return createFavoriteRoadtripAsString(resourcePath);
      } else if (resourcePath.contains("experienceId")) {
        return createFavoritePackageAsString(resourcePath);
      } else if (LinkUtils.isExternalLink(resourcePath)) {
        return createFavoriteExternalLinkAsString(resourcePath);
      } else {
        String rewrittenPath = Constants.FORWARD_SLASH_CHARACTER + locale + resourcePath;
        return resourceExporterService
            .exportResource(resourceResolver, rewrittenPath
                           );
      }
    }).filter(StringUtils::isNotEmpty).map(modelString -> {
      try {
        return new JSONObject(modelString);
      } catch (JSONException e) {
        LOGGER.error("Couldn't create JSONObject from {}", modelString, e);
        return null;
      }
    }).orElse(null);

  }


  /**
   * Fill data param for each favorite item. Recursion is required to fill nested
   * JSONArray objects.
   *
   * @param favorite         favorite item
   * @param resourceResolver resource resolver
   * @throws JSONException generic JSON exception
   */
  private void fillDataByPath(@NonNull final Object favorite, ResourceResolver resourceResolver) throws JSONException {
    if (favorite instanceof JSONObject) {
      Object path = ((JSONObject) favorite).get(Constants.PATH_PROPERTY);
      if (path != null) {
        String model = resourceExporterService.exportResource(resourceResolver, path.toString());
        ((JSONObject) favorite).put("body", model);
      }
    } else if (favorite instanceof JSONArray) {
      for (int i = 0; i < ((JSONArray) favorite).length(); i++) {
        fillDataByPath(((JSONArray) favorite).get(i), resourceResolver);
      }
    }
  }

  @Override
  public String deleteFavTrip(final String body, final UserIDToken userIDToken, final String type)
      throws IOException, MiddlewareException, SSIDFunctionnalException {
    final JsonObject metaJsonObject = new JsonObject();
    final JsonObject typeJsonObject = new JsonObject();

    String fullMetadata = getUserDetails(userIDToken, "all");
    switch (type) {
      case FAVORITES:
        typeJsonObject.add(type, getUpdatedFavoritesDelete(fullMetadata, body, userIDToken));
        typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));
        break;
      case TRIPS:
        final UpdateObjectsResult updatedTrips = getUpdatedTrips(fullMetadata, body, userIDToken);
        typeJsonObject.add(type, updatedTrips.getAllObjectsJsonArray());
        typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));
        break;
      default:
        return UNSUPPORTED_TYPE_MESSAGE;
    }
    metaJsonObject.add(USER_METADATA_KEY, typeJsonObject);
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString(), null);

    return handleUserProfileFilterResponse(type, responseMessage, userIDToken, false, null);
  }

  @Override
  public String updateUserMetadata(final String body, final UserIDToken userIDToken, final String type)
      throws IOException, SSIDFunctionnalException, MiddlewareException {
    Map<String, Object> filterParams = null;
    JsonObject typeJsonObject = new JsonObject();
    String fullMetadata = getUserDetails(userIDToken, "all");
    switch (type) {
      case PROFILE:
        typeJsonObject.add(type, getUpdatedProfile(body, userIDToken));
        break;
      case FAVORITES:
        typeJsonObject.add(type, getUpdatedFavorites(fullMetadata, body, userIDToken));
        typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));
        break;
      case TRIPS:
        final UpdateObjectsResult updatedTrips = getUpdatedTrips(fullMetadata, body, userIDToken);
        typeJsonObject.add(type, updatedTrips.getAllObjectsJsonArray());
        typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));
        // Prepare filtering map with id param for filtering final result
        filterParams = new HashMap<>();
        filterParams.put(Constants.PN_ID, updatedTrips.getId());
        break;
      default:
        return UNSUPPORTED_TYPE_MESSAGE;
    }

    // Send patch request to Auth0 for updating data
    JsonObject metaJsonObject = new JsonObject();
    metaJsonObject.add(USER_METADATA_KEY, typeJsonObject);
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString(), type);
    boolean populateData = true;
    if (type.equals(FAVORITES)) {
      populateData = false;
    }
    if (Objects.nonNull(responseMessage)) {
      return handleUserProfileFilterResponse(type, responseMessage, userIDToken, populateData, filterParams);
    } else {
      throw new SsidException("ResponseMessage is null. updateUserMetadata() :: patchUserMetadata");
    }
  }

  @Override
  public String saveTripPlan(final String tripPlanJson, final UserIDToken userIDToken)
      throws IOException, SSIDFunctionnalException, MiddlewareException {

    JsonObject typeJsonObject = new JsonObject();

    final UpdateObjectsResult saveResult = getUpdatedTrips(null, tripPlanJson, userIDToken);
    typeJsonObject.add(TRIPS, saveResult.getAllObjectsJsonArray());

    String fullMetadata = getUserDetails(userIDToken, "all");
    typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));

    JsonObject metaJsonObject = new JsonObject();
    metaJsonObject.add("user_metadata", typeJsonObject);
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString(), null);

    if (responseMessage == null) {
      throw new IOException("Could not save Trip Plan object into User Storage (response is null)");
    }

    return saveResult.getId();
  }

  /**
   * Patch user metadata response message.
   *
   * @param userIDToken the user id token
   * @param metaJson    the type json object
   * @param type        the type of details
   * @return the response message
   * @throws IOException exception.
   * @throws SSIDFunctionnalException exception.
   * @throws MiddlewareException exception.
   */
  private ResponseMessage patchUserMetadata(final UserIDToken userIDToken, final String metaJson,
                                            final String type)
      throws IOException, SSIDFunctionnalException, MiddlewareException {
    if (StringUtils.isNotBlank(type) && type.equals("profile")) {
      String updateUserProfileEndpoint = ssidConfig.getSSIDUpdateUserProfileEndpoint();
      Map<String, String> headMap = new HashMap<>();
      headMap.put("Content-Type", "application/json");
      JsonParser typeParser = new JsonParser();
      JsonObject userDataObj = typeParser.parse(metaJson).getAsJsonObject();
      if (null != userDataObj && null != userDataObj.getAsJsonObject("user_metadata")
          && null != userDataObj.getAsJsonObject("user_metadata").getAsJsonObject("profile")) {
        JsonObject profileObj = userDataObj.getAsJsonObject("user_metadata").getAsJsonObject("profile");
        profileObj.remove(TRIP_COUNT);
        profileObj.remove(FAVORITES_COUNT);
        profileObj.remove("email");
        profileObj.add("gender", profileObj.get("genderCode"));
        profileObj.remove("genderCode");
        profileObj.remove("halaYallaUserId");
        profileObj.remove("unlockedCities");
        profileObj.remove("cityOfResidency");
        profileObj.remove("ageRange");
        profileObj.remove("languageCode");
        profileObj.remove("visaNumber");
        profileObj.remove("hasDiscoveredLoyaltyTooltips");
        if (null != profileObj.get("interests")
            && profileObj.get("interests").isJsonArray()) {
          String[] array = profileObj.get("interests").toString().split("\"");
          if (array.length == 1) {
            profileObj.remove("interests");
            if (StringUtils.isNotBlank(ssidConfig.getSSIDUpdateUserProfileVersion())
                && ssidConfig.getSSIDUpdateUserProfileVersion().equals("v2")) {
              profileObj.add("interests", new JsonPrimitive(""));
            }
          } else {
            List interestList = Arrays.asList(array);
            final StringBuilder interestValue = new StringBuilder();
            interestList.forEach(item -> {
              if (interestList.indexOf(item) % 2 == 1) {
                interestValue.append(item).append(",");
              }
            });
            profileObj.remove("interests");
            profileObj.add("interests", new JsonPrimitive(interestValue.substring(0,
                interestValue.toString().length() - 1)));
          }
        }
        if ((StringUtils.isBlank(ssidConfig.getSSIDUpdateUserProfileVersion()))
            || (StringUtils.isNotBlank(ssidConfig.getSSIDUpdateUserProfileVersion())
            && ssidConfig.getSSIDUpdateUserProfileVersion().equals("v1"))) {
          if (profileObj.get("isMobileVerified") != null
              && profileObj.get("isMobileVerified").toString().equals("true")) {
            profileObj.remove("isMobileVerified");
            profileObj.add("isMobileVerified", new JsonParser().parse("Y"));
          } else if (profileObj.get("isMobileVerified") != null) {
            profileObj.remove("isMobileVerified");
            profileObj.add("isMobileVerified", new JsonParser().parse("N"));
          }
          if (profileObj.get("isEmailVerified") != null
              && profileObj.get("isEmailVerified").toString().equals("true")) {
            profileObj.remove("isEmailVerified");
            profileObj.add("isEmailVerified", new JsonParser().parse("Y"));
          } else if (profileObj.get("isEmailVerified") != null) {
            profileObj.remove("isEmailVerified");
            profileObj.add("isEmailVerified", new JsonParser().parse("N"));
          }
          profileObj.entrySet().removeIf(entry -> ((entry.getValue() == null
              || entry.getValue().equals("null") || StringUtils.isBlank(entry.getValue().toString())
              || entry.getValue().toString().equals("\"\""))));
        } else if (StringUtils.isNotBlank(ssidConfig.getSSIDUpdateUserProfileVersion())
            && ssidConfig.getSSIDUpdateUserProfileVersion().equals("v2")) {
          profileObj.remove("countryCode");
          profileObj.remove("isEmailVerified");
          profileObj.remove("isMobileVerified");
          profileObj.remove("mobileNumber");
          profileObj.entrySet().removeIf(entry -> (!isOptionalField(entry.getKey())
              && (entry.getValue() == null
              || entry.getValue().equals("null") || StringUtils.isBlank(entry.getValue().toString())
              || entry.getValue().toString().equals("\"\""))));
        }
        ResponseMessage response = SSIDRestHelper
            .executeMethodPatch(ssidConfig.getSSIDBaseUrl() + updateUserProfileEndpoint, profileObj.toString(),
                getAccessToken(userIDToken.getToken()), true, headMap, isProdRunMode());
        if ((response.getStatusCode() == SC_OK || response.getStatusCode() == SC_ACCEPTED)
            && StringUtils.isEmpty(getFaultStatusSSIDMiddleware(response.getMessage()))) {
          LOGGER.info("Update user profile from ssid is successful with response:" + response.getMessage());
        } else {
          LOGGER.error("Bad response from Update User profile from SSID. Code:" + response.getStatusCode()
              + ", message: " + response.getMessage() + " Request:" + profileObj);
          throw new SSIDFunctionnalException(response.getMessage());
        }
      } else {
        LOGGER.error("Error in parsing the User profile details from SSID, details: " + metaJson);
        throw new IOException(StatusEnum.INTERNAL_SERVER_ERROR.toString());
      }
    }
    String userEndpoint = config.getMiddlewareDomain() + config.getModifyFavoritesTripsEndpoint();
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    headerMap.put("token", getAccessToken(userIDToken.getToken()));
    headerMap.put("SSID", userIDToken.getUser());
    ResponseMessage responseMessage = RestHelper.executeMethodPost(userEndpoint, metaJson, null, headerMap);
    if ((responseMessage.getStatusCode() == SC_OK || responseMessage.getStatusCode() == SC_ACCEPTED)
        && StringUtils.isEmpty(getFaultStatusSSIDMiddleware(responseMessage.getMessage()))) {
      return responseMessage;
    } else {
      LOGGER.error("Bad response from Update User profile from Middleware. Code:" + responseMessage.getStatusCode()
          + ", message: " + responseMessage.getMessage());
      throw new MiddlewareException(responseMessage.getMessage());
    }
  }

  /**
   * returns if optional field.
   *
   * @param fieldName field name.
   * @return is optional.
   */
  private boolean isOptionalField(final String fieldName) {
    final List optionalFields = new ArrayList<>(
        Arrays.asList("travelPartner", "interests"));
    return optionalFields.contains(fieldName);
  }

  /**
   * @param response response.
   * @return status code.
   */
  private String getFaultStatus(String response) {
    try {
      if (StringUtils.isNotBlank(response)) {
        JsonObject errorObj = new JsonParser().parse(response).getAsJsonObject();
        if (null != errorObj && null != errorObj.get("faultCode")) {
          if (String.valueOf(errorObj.get("faultCode").getAsInt()).equals("200")) {
            return StringUtils.EMPTY;
          }
          return String.valueOf(errorObj.get("faultCode").getAsInt());
        }
      }
    } catch (Exception ex) {
      return StringUtils.EMPTY;
    }
    return StringUtils.EMPTY;
  }

  /**
   * @param response response.
   * @return status code.
   */
  private String getFaultStatusSSIDMiddleware(String response) {
    try {
      if (StringUtils.isNotBlank(response)) {
        JsonParser parser = new JsonParser();
        JsonObject errorObj = parser.parse(response).getAsJsonObject();
        if (null != errorObj && null != errorObj.get("error")) {
          if (String.valueOf(errorObj.get("error").getAsInt()).equals("200")) {
            return StringUtils.EMPTY;
          }
          return String.valueOf(errorObj.get("error").getAsInt());
        }
        if (null != errorObj && null != errorObj.get("status")) {
          if (String.valueOf(errorObj.get("status").getAsInt()).equals("200")) {
            return StringUtils.EMPTY;
          }
          return String.valueOf(errorObj.get("status").getAsInt());
        }

      }
    } catch (Exception ex) {
      return StringUtils.EMPTY;
    }
    return StringUtils.EMPTY;
  }



  /**
   * Gets updated favorites.
   *
   * @param listFavorites    the favorite
   * @param userIDToken the user id
   * @param fullMetadata fullMetadata
   * @return the updated favorites
   * @throws IOException if can't request User's data from Auth0
   * @throws MiddlewareException Middleware exception
   */
  private JsonArray getUpdatedFavorites(final String fullMetadata, final String listFavorites,
                                        final UserIDToken userIDToken) throws IOException, MiddlewareException {


    String favorites = null;
    try {
      if (StringUtils.isNotBlank(fullMetadata)) {
        favorites = new JSONObject(fullMetadata).getJSONArray(FAVORITES).toString();
      }
    } catch (JSONException e) {
      LOGGER.warn("getUpdatedFavorites: Could not parse user's metadata json.", e);
    }
    // If we didn't pass metadata or there was an parsing error
    if (StringUtils.isBlank(favorites)) {
      favorites = getUserDetails(userIDToken, FAVORITES);
    }
    // Convert existing favorites array from JSONArray to Gson JsonArray
    JsonParser parser = new JsonParser();
    final JsonArray favJsonArray = parser.parse(favorites).getAsJsonArray();

    String[] listFavoritesAsArray = listFavorites.split(",");
    for (String favorite : listFavoritesAsArray) {
      // If there's already such element, remove it before adding updated
      final JsonElement existingFav = CommonUtils.searchJsonArrByProp(favJsonArray, Constants.PATH_PROPERTY, favorite);
      if (existingFav != null) {
        favJsonArray.remove(existingFav);
      }
      favJsonArray.add(favorite);
    }
    return favJsonArray;
  }

  /**
   * Gets favorites grouped by search categories.
   *
   * @param favoritesDetails the favorite
   * @param types            types  of tags
   * @return the grouped favorites
   */
  private JSONArray groupByCategories(final JSONArray favoritesDetails, List<DictItem> types) {

    HashMap<String, List<JSONObject>> favoritesGroupByCategories = new HashMap<String, List<JSONObject>>();
    JSONArray groupedFavorites = new JSONArray();

    for (int i = 0; i < favoritesDetails.length(); i++) {
      final JSONObject jsonObject = favoritesDetails.optJSONObject(i);
      if (jsonObject == null) {
        continue;
      }

      String pageCategory = jsonObject.optString("favCategory");
      if (StringUtils.isNotBlank(pageCategory)) {
        if (favoritesGroupByCategories.containsKey(pageCategory)) {
          favoritesGroupByCategories.get(pageCategory).add(jsonObject);

        } else {
          ArrayList<JSONObject> newCategory = new ArrayList<JSONObject>();
          newCategory.add(jsonObject);
          favoritesGroupByCategories.put(pageCategory, newCategory);
        }
      }
    }
    favoritesGroupByCategories.forEach((key, listDetails)
        -> {
      JSONObject sectionCategory = new JSONObject();
      JSONArray detailsSection = new JSONArray();
      for (int j = 0; j < listDetails.size(); j++) {
        detailsSection.put(listDetails.get(j));
      }
      try {

        sectionCategory.put("favorites", detailsSection);
        sectionCategory.put("section", key);
        sectionCategory.put("sectionTitle", types.stream().filter(d -> d.getCode().equalsIgnoreCase(key))
            .map(DictItem::getValue).findAny().orElse(Constants.BLANK));
        groupedFavorites.put(sectionCategory);

      } catch (JSONException e) {
        throw new RuntimeException(e);
      }

    });


    return groupedFavorites;
  }

  /**
   * Gets updated favorites to delete.
   *
   * @param listFavorites the list of favorite to delete
   * @param userIDToken   the user id
   * @param fullMetadata fullMetadata
   * @return the updated favorites
   * @throws IOException if can't request user's data from Auth0
   * @throws MiddlewareException Middleware exception
   */
  private JsonArray getUpdatedFavoritesDelete(final String fullMetadata, final String listFavorites,
                                              final UserIDToken userIDToken)
      throws IOException, MiddlewareException {
    String favorites = null;
    try {
      if (StringUtils.isNotBlank(fullMetadata)) {
        favorites = new JSONObject(fullMetadata).getJSONArray(FAVORITES).toString();
      }
    } catch (JSONException e) {
      LOGGER.warn("getUpdatedFavoritesDelete: Could not parse user's metadata json.", e);
    }
    // If we didn't pass metadata or there was an parsing error
    if (StringUtils.isBlank(favorites)) {
      favorites = getUserDetails(userIDToken, FAVORITES);
    }
    // Convert existing favorites array from JSONArray to Gson JsonArray
    JsonParser parser = new JsonParser();
    final JsonArray favJsonArray = parser.parse(favorites).getAsJsonArray();
    String[] listFavoritesAsArray = listFavorites.split(",");
    for (String favorite : listFavoritesAsArray) {
      // Look for existing stored favorite by path and remove from list
      if (StringUtils.isNotBlank(favorite)) {
        final JsonElement favToDelete =
            CommonUtils.searchJsonArrByProp(favJsonArray, Constants.PATH_PROPERTY, favorite);

        if (favToDelete != null) {
          favJsonArray.remove(favToDelete);
        } else {
          final String errorMessage = "Couldn't delete [" + favorite + "]";
          LOGGER.error(errorMessage);
          throw new IllegalArgumentException(errorMessage);
        }
      }
    }

    return favJsonArray;
  }

  /**
   * Gets updated favorites.
   *
   * @param userMetadata the full data received rom getUserDetails
   * @param tripToUpdate the trip json for updating
   * @param userIDToken  the user id
   * @return the updated favorites
   * @throws IOException if can't request user's data from Auth0
   * @throws MiddlewareException Middleware exception
   */
  private UpdateObjectsResult getUpdatedTrips(final String userMetadata, final String tripToUpdate,
                                              final UserIDToken userIDToken) throws IOException, MiddlewareException {
    final UpdateObjectsResult result = new UpdateObjectsResult();

    String trips = null;
    try {
      if (StringUtils.isNotBlank(userMetadata)) {
        trips = new JSONObject(userMetadata).getJSONArray(TRIPS).toString();
      }
    } catch (JSONException e) {
      LOGGER.error("Could not parse user's metadata json.", e);
    }
    // If we didn't pass metadata or there was an parsing error
    if (StringUtils.isBlank(trips)) {
      trips = getUserDetails(userIDToken, TRIPS);
    }

    final JsonParser typeParser = new JsonParser();
    final JsonObject parsedTripJsonObject = typeParser.parse(tripToUpdate).getAsJsonObject();
    // Convert existing trips array from JSONArray to Gson JsonArray
    final JsonArray tripJsonArray = typeParser.parse(trips).getAsJsonArray();

    // Id of the trip plan being stored/updated
    String tripId = CommonUtils.getJsonPropStr(parsedTripJsonObject, Constants.PN_ID);

    // Look for existing stored trip plan by id and remove from list
    if (StringUtils.isNotBlank(tripId) && !tripId.equals(TripPlanConstants.NEW_TRIP_PLAN_ID)) {
      JsonElement previousTripPlanWithSameId = CommonUtils.searchJsonArrByProp(tripJsonArray, Constants.PN_ID, tripId);

      if (previousTripPlanWithSameId != null) {
        tripJsonArray.remove(previousTripPlanWithSameId);
      } else {
        // Id was provided, but we didn't find the object to update
        final String errorMessage = "Couldn't update stored Trip Plan - couldn't find user's "
            + "Trip Plan with the provided id [" + tripId + "]";
        LOGGER.error(errorMessage);
        throw new IllegalArgumentException(errorMessage);
      }

    } else {
      // Id wasn't provided, so generate it
      tripId = UUID.randomUUID().toString();
      LOGGER.debug("The new Trip Plan object will be saved with id: {}", tripId);

      parsedTripJsonObject.add(Constants.PN_ID, new JsonPrimitive(tripId));
    }

    // Add only if there is not only id field in the request body
    if (parsedTripJsonObject.size() > 1) {
      // to display the last edited trip on top
      final JsonArray updatedTripJsonArray = new JsonArray();
      updatedTripJsonArray.add(parsedTripJsonObject);
      updatedTripJsonArray.addAll(tripJsonArray);

      result.setUpdatedObject(parsedTripJsonObject);
      result.setId(tripId);
      result.setAllObjectsJsonArray(updatedTripJsonArray);
    } else {
      result.setAllObjectsJsonArray(tripJsonArray);
    }

    return result;
  }

  /**
   * Gets updated favorites.
   *
   * @param fullMetadata the profJson
   * @param newMeta      the newMeta
   * @return the updated favorites
   */
  private JsonElement updateTripFavCount(final String fullMetadata, final JsonObject newMeta) {
    JSONObject jsonRespObject = null;
    JsonParser parser = new JsonParser();

    try {
      jsonRespObject = new JSONObject(fullMetadata).getJSONObject(PROFILE);
      if (newMeta.has(FAVORITES)) {
        jsonRespObject.put(FAVORITES_COUNT, newMeta.getAsJsonArray(FAVORITES).size());
      }
      if (newMeta.has(TRIPS)) {
        jsonRespObject.put(TRIP_COUNT, newMeta.getAsJsonArray(TRIPS).size());
      }
    } catch (JSONException e) {
      LOGGER.error("JSONException occurred in updateTripFavCount() {}", e.getMessage());
    }
    if (Objects.nonNull(jsonRespObject)) {
      return parser.parse(jsonRespObject.toString());
    }
    return null;
  }

  /**
   * Gets updated favorites.
   *
   * @param updatedProfile the updatedProfile
   * @param userIDToken    the user id
   * @return the updated favorites
   * @throws MiddlewareException MiddlewareException
   */
  private JsonObject getUpdatedProfile(final String updatedProfile, final UserIDToken userIDToken)
      throws MiddlewareException {

    try {
      String profile = getUserDetails(userIDToken, PROFILE);
      JsonParser typeParser = new JsonParser();

      JsonObject profileObj = typeParser.parse(profile).getAsJsonObject();
      JsonObject updatedProfileObj = typeParser.parse(updatedProfile).getAsJsonObject();
      updatedProfileObj.add(FAVORITES_COUNT, profileObj.get(FAVORITES_COUNT));
      updatedProfileObj.add(TRIP_COUNT, profileObj.get(TRIP_COUNT));
      return updatedProfileObj;
    } catch (IOException e) {
      LOGGER.error("Error in getUpdatedFavorites {}", e);
    }
    return new JsonObject();
  }

  /**
   * check if prod run mode.
   *
   * @return true/false
   */
  private boolean isProdRunMode() {
    if (null != settingsService && null != settingsService.getRunModes()
        && settingsService.getRunModes().contains("prod")) {
      return true;
    }
    return false;
  }

  /**
   * Internal wrapper class to store update results (it's used for trips for now).
   */
  @Data
  @NoArgsConstructor
  private static class UpdateObjectsResult {

    /**
     * New / updated object id.
     */
    private String id;

    /**
     * Updated object (parsed to JsonObject).
     */
    private JsonObject updatedObject;

    /**
     * Array of all objects of the same type from user's metadata.
     */
    private JsonArray allObjectsJsonArray;

  }

  /**
   *@param tripPlanJson trip plan json to save.
   * @param userIDToken  user id token .
   * @return ResponseMessage responseMessage.
   * @throws IOException IOException.
   * @throws MiddlewareException MiddlewareException.
   * @throws SSIDFunctionnalException SSIDFunctionnalException.
   */
  public ResponseMessage saveTripPlanNativeApp(final String tripPlanJson, final UserIDToken userIDToken)
      throws IOException, MiddlewareException, SSIDFunctionnalException {

    JsonObject typeJsonObject = new JsonObject();
    final com.saudi.tourism.core.login.services.impl.v3.SSIDLoginUserServiceImpl.UpdateObjectsResult saveResult =
        getUpdatedTrips(null, tripPlanJson, userIDToken);
    typeJsonObject.add(TRIPS, saveResult.getAllObjectsJsonArray());
    String fullMetadata = getUserDetails(userIDToken, "all");
    typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));
    JsonObject metaJsonObject = new JsonObject();
    metaJsonObject.add("user_metadata", typeJsonObject);
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString(), null);

    if (responseMessage == null) {
      throw new IOException("Could not save Trip Plan object into User Storage (response is null)");
    }
    responseMessage.setId(saveResult.getId());
    return responseMessage;
  }
}
