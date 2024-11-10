package com.saudi.tourism.core.login.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.net.HttpHeaders;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.saudi.tourism.core.login.Auth0Exception;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.LoginUserService;
import com.saudi.tourism.core.login.services.SaudiLoginConfig;
import com.saudi.tourism.core.login.services.TokenService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanConstants;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.services.TripPlanService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ProfileConstants;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static com.saudi.tourism.core.utils.ProfileConstants.CP_APPLE;
import static com.saudi.tourism.core.utils.ProfileConstants.CP_TWITTER;
import static com.saudi.tourism.core.utils.ProfileConstants.DEFAULT_EMAIL_KEY;
import static com.saudi.tourism.core.utils.ProfileConstants.DEFAULT_LAST_NAME_KEY;
import static com.saudi.tourism.core.utils.ProfileConstants.FAVORITES_COUNT;
import static com.saudi.tourism.core.utils.ProfileConstants.ITEMS;
import static com.saudi.tourism.core.utils.ProfileConstants.PN_FIRST_NAME;
import static com.saudi.tourism.core.utils.ProfileConstants.PN_LAST_NAME;
import static com.saudi.tourism.core.utils.ProfileConstants.TRIP_COUNT;
import static com.saudi.tourism.core.utils.ProfileConstants.UNSUPPORTED_TYPE_MESSAGE;
import static com.saudi.tourism.core.utils.ProfileConstants.USER_METADATA_KEY;
import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * The Token Service.
 */
@Slf4j
@Component(service = LoginUserService.class,
           immediate = true)
public class LoginUserServiceImpl implements LoginUserService {

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
   * Saudi Login Configurations.
   */
  @Reference
  private SaudiLoginConfig saudiLoginConfig;

  /**
   * The service for working with trip plans.
   */
  @Reference
  private TripPlanService tripPlanService;

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

  @Override
  public String getUserDetails(final UserIDToken userIDToken, final String type)
      throws IOException {
    return getUserDetails(userIDToken, type, false, null);
  }

  @Override
  public String getUserDetailsFull(final UserIDToken userIDToken, final String type,
      final Map<String, Object> filterParams) throws IOException {
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
   */
  public String getUserDetails(final UserIDToken userIDToken, final String type,
      final boolean populateData, final Map<String, Object> filterParams)
      throws IOException {
    String id =
        URLEncoder.encode(userIDToken.getUser(), com.adobe.granite.rest.Constants.DEFAULT_CHARSET);
    String userEndpoint = StringUtils
        .replaceEach(saudiLoginConfig.getAuth0GetUserEndpoint(), new String[] {Constants.ID},
            new String[] {id});
    ResponseMessage responseMessage = RestHelper
        .executeMethodGet(saudiLoginConfig.getAuth0BaseUrl() + userEndpoint,
            getAccessToken(userIDToken.getToken()), true);

    return handleUserProfileFilterResponse(type, responseMessage, userIDToken, populateData,
        filterParams);
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
   */
  @Nullable
  public String handleUserProfileFilterResponse(final String type,
      final ResponseMessage responseMessage, final UserIDToken userIDToken,
      final boolean populateData, final Map<String, Object> filterParams) {
    JSONObject jsonRespObject;
    if (responseMessage.getStatusCode() != SC_OK) {
      throw new Auth0Exception(responseMessage.getMessage());
    }
    try {
      jsonRespObject = new JSONObject(responseMessage.getMessage());

      JSONObject userMetaData = getUserMetadata(jsonRespObject, userIDToken);
      switch (type) {
        case PROFILE:
          return userMetaData.getJSONObject(type).toString();
        case "all":
          return userMetaData.toString();
        case FAVORITES:
          JSONArray items = userMetaData.getJSONArray(type);
          JSONObject favoritesObject = createFavoritesObject(
              populateData, items, userIDToken, filterParams);
          return favoritesObject.toString();
        case TRIPS:
          final JSONArray jsonArray = userMetaData.getJSONArray(type);

          if (populateData) {
            // Filter array before processing
            final JSONArray metadataTripsArray =
                CommonUtils.filterJsonArrByProp(jsonArray, filterParams);

            return RestHelper.getObjectMapper().writeValueAsString(
                tripPlanService.fromJson(metadataTripsArray, userIDToken.getLocale()));
          }

          // Filter by id (propName/propValue)
          return CommonUtils.filterJsonArrByProp(jsonArray, filterParams).toString();
        default:
          return UNSUPPORTED_TYPE_MESSAGE;
      }
    } catch (JSONException | JsonProcessingException e) {
      LOGGER.error("Couldn't process JSON {}", responseMessage.getMessage(), e);
      throw new IllegalStateException("User profile metadata error: Could not process JSON", e);
    }
  }

  /**
   * Create Favorites Object.
   * @param populateData is populateData
   * @param items items
   * @param userIDToken userIDToken
   * @param filterParams filterParams
   * @return Favorites Object
   * @throws JSONException - error
   */
  private JSONObject createFavoritesObject(boolean populateData, JSONArray items,
      UserIDToken userIDToken, Map<String, Object> filterParams) throws JSONException {
    JSONObject favoritesObject = new JSONObject();

    if (!populateData) {
      favoritesObject.put(ITEMS, items);
      return favoritesObject;
    }
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final JSONArray jsonArray =
          processFavoritesArray(items, resourceResolver, userIDToken.getLocale());
      if (filterParams != null && !filterParams.isEmpty() && !(filterParams.size() == 1
          && filterParams.containsKey(Constants.LOCALE))) {
        final JSONArray filteredDetails =
            CommonUtils.filterJsonArrByProp(jsonArray, filterParams);
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

        favoritesObject.put("details", filteredDetails);
        favoritesObject.put("items", filteredItems);

      } else {
        // Do not filter
        favoritesObject.put("details", jsonArray);
        favoritesObject.put("items", items);
      }
    }
    return favoritesObject;
  }

  /**
   * Collect all favorite ids from the populated json array.
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
   */
  public JSONObject getUserMetadata(final JSONObject jsonRespObject, final UserIDToken userIDToken)
      throws JSONException {
    JSONObject metaData = null;
    if (jsonRespObject.has(USER_METADATA_KEY)) {
      metaData = jsonRespObject.getJSONObject(USER_METADATA_KEY);
    }

    if (Objects.isNull(metaData) || "{}".equals(metaData.toString())) {
      // Initialise meta_data for new user
      metaData = new JSONObject();

      JSONObject profile = new JSONObject();

      String connectionProvider = getConnectionProvider(jsonRespObject);
      profile.put(PN_FIRST_NAME,
          getJSONObjString(jsonRespObject, getProviderKeyName(PN_FIRST_NAME, connectionProvider)));
      profile.put(PN_LAST_NAME,
          getJSONObjString(jsonRespObject, getProviderKeyName(PN_LAST_NAME, connectionProvider)));
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
      patchUserMetadata(userIDToken, metaJsonObject.toString());
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
  private String getJSONObjString(final JSONObject jsonRespObject, final String key)
      throws JSONException {
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
  private JSONArray processFavoritesArray(JSONArray favorites, ResourceResolver resourceResolver,
      String locale) throws JSONException {
    JSONArray favoriteDetails = new JSONArray();
    for (int i = 0; i < favorites.length(); i++) {
      Object favorite = favorites.get(i);
      if (favorite instanceof String) {
        Object model = getFavoriteModel(favorite.toString(), resourceResolver, locale);
        if (Objects.nonNull(model)) {
          favoriteDetails.put(model);
        }
      }
    }
    return favoriteDetails;
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
    return Optional.ofNullable(favorite).map(resourcePath -> resourceExporterService
        .exportResource(resourceResolver,
            Constants.FORWARD_SLASH_CHARACTER + locale + resourcePath))
        .filter(StringUtils::isNotEmpty).map(modelString -> {
          try {
            return new JSONObject(modelString);
          } catch (JSONException e) {
            LOGGER.error("Couldn't create JSONObject from {}", modelString, e);
            return null;
          }
        }).orElse(null);
  }

  /**
   * Fill data param for each favorite item. Recursion is required to fill nested JSONArray objects.
   *
   * @param favorite         favorite item
   * @param resourceResolver resource resolver
   * @throws JSONException generic JSON exception
   */
  private void fillDataByPath(@NonNull final Object favorite, ResourceResolver resourceResolver)
      throws JSONException {
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
      throws IOException {
    final JsonObject metaJsonObject = new JsonObject();
    final JsonObject typeJsonObject = new JsonObject();
    String fullMetadata = getUserDetails(userIDToken, "all");

    switch (type) {
      case FAVORITES:
        typeJsonObject.add(type, getUpdatedFavoritesDelete(body, userIDToken));
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
    String id = URLEncoder.encode(userIDToken.getUser(), "UTF-8");
    String userEndpoint = StringUtils
        .replaceEach(saudiLoginConfig.getAuth0GetUserEndpoint(), new String[] {Constants.ID},
            new String[] {id});
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put(HttpHeaders.CONTENT_TYPE, com.adobe.granite.rest.Constants.CT_JSON);

    ResponseMessage responseMessage = RestHelper
        .executeMethodPatch(saudiLoginConfig.getAuth0BaseUrl() + userEndpoint,
            metaJsonObject.toString(), getAccessToken(userIDToken.getToken()), true, headerMap);
    return handleUserProfileFilterResponse(type, responseMessage, userIDToken, false, null);
  }

  @Override
  public String updateUserMetadata(final String body, final UserIDToken userIDToken,
      final String type) throws IOException {
    Map<String, Object> filterParams = null;
    JsonObject typeJsonObject = new JsonObject();
    String fullMetadata = getUserDetails(userIDToken, "all");
    switch (type) {
      case PROFILE:
        typeJsonObject.add(type, getUpdatedProfile(body, userIDToken));
        break;
      case FAVORITES:
        typeJsonObject.add(type, getUpdatedFavorites(body, userIDToken));
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
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString());

    if (Objects.nonNull(responseMessage)) {
      return handleUserProfileFilterResponse(type, responseMessage, userIDToken, true,
          filterParams);
    } else {
      throw new Auth0Exception(
          "ResponseMessage is null. updateUserMetadata() :: patchUserMetadata");
    }
  }

  @Override
  public String saveTripPlan(final String tripPlanJson, final UserIDToken userIDToken)
      throws IOException {

    JsonObject typeJsonObject = new JsonObject();

    final UpdateObjectsResult saveResult = getUpdatedTrips(null, tripPlanJson, userIDToken);
    typeJsonObject.add(TRIPS, saveResult.getAllObjectsJsonArray());

    String fullMetadata = getUserDetails(userIDToken, "all");
    typeJsonObject.add(PROFILE, updateTripFavCount(fullMetadata, typeJsonObject));

    JsonObject metaJsonObject = new JsonObject();
    metaJsonObject.add("user_metadata", typeJsonObject);
    ResponseMessage responseMessage = patchUserMetadata(userIDToken, metaJsonObject.toString());

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
   * @return the response message
   */
  private ResponseMessage patchUserMetadata(final UserIDToken userIDToken, final String metaJson) {

    try {
      String id = URLEncoder.encode(userIDToken.getUser(), "UTF-8");
      String userEndpoint = StringUtils
          .replaceEach(saudiLoginConfig.getAuth0GetUserEndpoint(), new String[] {Constants.ID},
              new String[] {id});

      Map<String, String> headerMap = new HashMap<>();
      headerMap.put("Content-Type", "application/json");

      return RestHelper
          .executeMethodPatch(saudiLoginConfig.getAuth0BaseUrl() + userEndpoint, metaJson,
              getAccessToken(userIDToken.getToken()), true, headerMap);
    } catch (IOException e) {
      LOGGER.error("Error in patchUserMetadata");
    }
    return null;
  }

  /**
   * Gets updated favorites.
   *
   * @param favorite    the favorite
   * @param userIDToken the user id
   * @return the updated favorites
   * @throws IOException if can't request User's data from Auth0
   */
  private JsonArray getUpdatedFavorites(final String favorite, final UserIDToken userIDToken)
      throws IOException {
    final String favorites = getUserDetails(userIDToken, FAVORITES);
    final JsonParser typeParser = new JsonParser();

    final JsonArray favJsonArray =
        typeParser.parse(favorites).getAsJsonObject().getAsJsonArray(ITEMS);

    // If there's already such element, remove it before adding updated
    final JsonElement existingFav =
        CommonUtils.searchJsonArrByProp(favJsonArray, Constants.PATH_PROPERTY, favorite);
    if (existingFav != null) {
      favJsonArray.remove(existingFav);
    }

    favJsonArray.add(favorite);

    return favJsonArray;
  }

  /**
   * Gets updated favorites.
   *
   * @param favorite    the favorite
   * @param userIDToken the user id
   * @return the updated favorites
   * @throws IOException if can't request user's data from Auth0
   */
  private JsonArray getUpdatedFavoritesDelete(final String favorite, final UserIDToken userIDToken)
      throws IOException {
    final JsonParser typeParser = new JsonParser();
    // Already stored favorites in user's meta
    final String favorites = getUserDetails(userIDToken, FAVORITES);
    // Favorites array from user's meta
    final JsonArray favJsonArray =
        typeParser.parse(favorites).getAsJsonObject().getAsJsonArray(ITEMS);

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
   */
  private UpdateObjectsResult getUpdatedTrips(final String userMetadata, final String tripToUpdate,
      final UserIDToken userIDToken) throws IOException {
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
      JsonElement previousTripPlanWithSameId =
          CommonUtils.searchJsonArrByProp(tripJsonArray, Constants.PN_ID, tripId);

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
   * @param newMeta the newMeta
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
   */
  private JsonObject getUpdatedProfile(final String updatedProfile, final UserIDToken userIDToken) {

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
}

