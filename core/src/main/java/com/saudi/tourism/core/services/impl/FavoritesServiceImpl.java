package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.RoadTripCustomResponse;
import com.saudi.tourism.core.servlets.nativeapp.model.roadtrip.scenario.response.NativeAppCustomCardResponse;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/** The Favorites Service. */
@Slf4j
@Component(service = FavoritesService.class, immediate = true)
public class FavoritesServiceImpl implements FavoritesService {

  /**
   * API Get Favorites suffix.
   */
  private static final String API_GET_FAVORITES = "user/get.favorites?locale=%s";

  /**
   * API Update Favorites suffix.
   */
  private static final String API_UPDATE_FAVORITES = "user/update.favorites";

  /**
   * API Delete Favorites suffix.
   */
  private static final String API_DELETE_FAVORITES = "user/delete.favorites";

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  /** Experience favorite ID prefix. */
  private static final String EXPERIENCE_FAVORITE_ID_PREFIX = "experienceId=";
  /** Roadtrip api limit. */
  private static final int ROADTRIP_LIMIT = 0;
  /** Roadtrip api offset. */
  private static final int ROADTRIP_OFFSET = 0;


  /**
   * Experience service.
   */
  @Reference
  private ExperienceService experienceService;

  /**
   * Roadtrip service.
   */
  @Reference
  private RoadTripScenariosService roadTripScenariosService;

  /**
   * Resource exporter.
   */
  @Reference
  private ResourceExporterService resourceExporterService;
  /**
   * adminService.
   */
  @Reference
  private AdminSettingsService adminService;

  /**
   * Runmode Service.
   */
  @Reference
  private RunModeService runModeService;


  /**
   * Get the favorite type from the favorite id.
   *
   * @param favoritePath The favorite id.
   * @return  The calculated favorite type.
   */
  public FavoriteType getFavoriteType(@NonNull final String favoritePath) {
    if (StringUtils.contains(favoritePath, EXPERIENCE_FAVORITE_ID_PREFIX)) {
      return FavoriteType.EXPERIENCE;
    }
    if (favoritePath.startsWith(Constants.ROADTRIP_PREFIX)) {
      return FavoriteType.ROADTRIP;
    }

    if (LinkUtils.isExternalLink(favoritePath)) {
      return FavoriteType.EXTERNAL_LINK;
    }

    return FavoriteType.INTERNAL_LINK;
  }

  /**
   * Get the Hallayala Experience ID from the favoritePath.
   *
   * @param favoritePath The favorite path stored in SSID.
   * @return The Experience ID.
   */
  String getExperienceId(@NonNull final String favoritePath) {
    final FavoriteType favoriteType = getFavoriteType(favoritePath);
    if (FavoriteType.EXPERIENCE.equals(favoriteType)) {
      return StringUtils.substringAfter(favoritePath, EXPERIENCE_FAVORITE_ID_PREFIX);
    }
    return null;
  }

  /**
   * Get the scenario ID  of the roadtrip from the favoritePath.
   *
   * @param favoritePath The favorite path stored in SSID.
   * @return The scenario ID.
   */
  String getScenarioId(@NonNull final String favoritePath) {
    final FavoriteType favoriteType = getFavoriteType(favoritePath);
    if (FavoriteType.ROADTRIP.equals(favoriteType)) {
      return favoritePath.split(Constants.ROADTRIP_PREFIX)[1];
    }
    return null;
  }

  @Override
  public List<Object> getFavoritesData(
      @NonNull ResourceResolver resourceResolver,
      @NonNull String locale,
      @NonNull List<String> favoritePaths) {
    final List<String> experienceFavoritePaths =
        favoritePaths.stream()
        .filter(favoritePath -> FavoriteType.EXPERIENCE.equals(getFavoriteType(favoritePath)))
        .collect(Collectors.toList());

    final List<String> internalLinkFavoritePath =
        favoritePaths.stream()
        .filter(
          favoritePath -> FavoriteType.INTERNAL_LINK.equals(getFavoriteType(favoritePath)))
        .collect(Collectors.toList());

    final List<String> roadtripFavoritePaths =
        favoritePaths.stream()
        .filter(
          favoritePath -> FavoriteType.ROADTRIP.equals(getFavoriteType(favoritePath)))
        .collect(Collectors.toList());

    final List<String> externalLinkFavoritePaths =
        favoritePaths.stream()
        .filter(
          favoritePath -> FavoriteType.EXTERNAL_LINK.equals(getFavoriteType(favoritePath)))
        .collect(Collectors.toList());

    final List<Object> experienceFavoriteData =
        createFavoriteExperienceData(resourceResolver, locale, experienceFavoritePaths);
    final List<Object> internalLinkFavoriteData =
        createFavoriteInternalLinkData(resourceResolver, locale, internalLinkFavoritePath);


    final List<Object> externalLinkFavoriteData =
        createFavoriteExternalLinkData(locale, externalLinkFavoritePaths);


    return Stream.of(experienceFavoriteData, internalLinkFavoriteData, externalLinkFavoriteData)
      .flatMap(Collection::stream)
      .collect(Collectors.toList());
  }


  /**
   * Create favorite model for InternalLink Favorite.
   *
   * @param resolver Resource resolver
   * @param locale Current Local (example: en)
   * @param favoritePaths A list of favorite paths
   * @return List of models for Experience Favorite
   */
  private List<Object> createFavoriteExperienceData(@NonNull ResourceResolver resolver,
      @NonNull final String locale, @NonNull final List<String> favoritePaths) {

    final Map<String, Object> queryParams = new HashMap<>();
    queryParams.put("lang", locale);

    final List<String> experienceIds =
        favoritePaths.stream().map(this::getExperienceId).collect(Collectors.toList());

    try {
      final Object experiencesDetails =
          experienceService.getExperiencesDetails(queryParams, experienceIds);

      if (experiencesDetails == null) {
        LOGGER.warn(
            "The experiences={} not found on halayalla api", String.join(",", experienceIds));
        return Collections.emptyList();
      }

      final JsonObject experiencesDetailsAsJsonObject =
          ((JsonElement) experiencesDetails).getAsJsonObject();

      if (!experiencesDetailsAsJsonObject.has("data")) {
        LOGGER.warn(
            "Halayalla api returned no data for experiences={}", String.join(",", experienceIds));
        return Collections.emptyList();
      }

      final JsonArray experienceDetailDataAsJsonArray =
          experiencesDetailsAsJsonObject.getAsJsonArray("data");

      if (null != experienceDetailDataAsJsonArray
          && experienceDetailDataAsJsonArray.size() == 0) {
        LOGGER.warn(
            "Halayalla api returned empty data for experiences={}",
            String.join(",", experienceIds));
        return Collections.emptyList();
      }

      final String packageDetailsPageFromConfig = adminService.getPackageDetailsPage(locale);

      // We will stream on the experienceDetailDataAsJsonArray by streaming on an IntStream with the
      // same size as the experienceDetailDataAsJsonArray
      return IntStream.range(0, experienceDetailDataAsJsonArray.size())
        .mapToObj(
          i -> {
            final JsonObject experienceData =
                experienceDetailDataAsJsonArray.get(i).getAsJsonObject();
            final String experienceId = experienceData.get("id").getAsString();

            try {
                  /* if in favorites, for the same ExperienceID wse save it with web and app format
                   it will take the first one
                  */
              final String favoritePath =
                  favoritePaths.stream()
                  .filter(path -> StringUtils.equals(getExperienceId(path), experienceId))
                  .findFirst()
                  .get();
              String link =
                  StringUtils.join(Constants.FORWARD_SLASH_CHARACTER, locale, favoritePath);

              final JSONObject model = new JSONObject(experienceData.toString());
              if (experienceData.has("background_image")) {
                model.put(
                    "cardImage",
                    experienceData.get("background_image").getAsString());
              }
              if (experienceData.has("name")) {
                model.put("title", experienceData.get("name").getAsString());
              }
              // reconstruct link for favorite coming from native app, experiences only contains experienceId
              if (favoritePath.startsWith(EXPERIENCE_FAVORITE_ID_PREFIX)) {
                link =
                  LinkUtils.getAuthorPublishUrl(resolver, packageDetailsPageFromConfig,
                    runModeService.isPublishRunMode()) + Constants.QUESTION_MARK + favoritePath;
              }
              model.put("path", link);
              model.put("link", link);
              model.put("favId", favoritePath);
              model.put("favCategory", Constants.PACKAGES_SEARCH_CATEGORY);
              model.put("experienceId", experienceId);
              model.remove("duration"); // workaround until app can handle it.

              return model;
            } catch (JSONException e) {
              LOGGER.warn("Couldn't create JSONObject for experienceId={}", experienceId, e);
            }

            return null;
          })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    } catch (IOException e) {
      LOGGER.warn(
          "IOException when fetching details of experiences={} from halayalla api",
          String.join(",", experienceIds),
          e);
    }

    return Collections.emptyList();
  }


  /**
   * Create favorite model for roadtrip Favorite.
   *
   * @param locale Current Local (example: en)
   * @param favoritePaths A list of favorite paths
   * @return List of models for roadtrip Favorite
   */
  private List<Object> createFavoriteRoadtripData(
      @NonNull final String locale, @NonNull final List<String> favoritePaths) {


    List<String> scenarioIds = favoritePaths.stream().map(this::getScenarioId).filter(Objects::nonNull).collect(
        Collectors.toList());
    final JsonObject body = new JsonObject();
    final JsonArray scenariosIdsArray = new JsonArray();
    scenarioIds.forEach(scenarioId -> scenariosIdsArray.add(scenarioId));
    body.add("scenario_ids", scenariosIdsArray);

    try {
      RoadTripCustomResponse roadTripCustomResponse =
          roadTripScenariosService.getRoadTripScenariosNativeApp(ROADTRIP_LIMIT, ROADTRIP_OFFSET, locale,
          body.toString());

      if (roadTripCustomResponse == null) {
        LOGGER.warn(
            "The scearions={} not found on routes api", String.join(",", scenarioIds));
        return Collections.emptyList();
      }

      List<NativeAppCustomCardResponse> trips = roadTripCustomResponse.getTrips();
      return trips.stream().map(scenario -> {
        final JSONObject model =  new JSONObject(scenario);

        try {
          model.put("favCategory", Constants.ROADTRIPS_SEARCH_CATEGORY);
          model.put("scenarioId", scenario.getScenarioId());
          model.remove("duration"); // workaround until app can handle it.
          model.remove("image"); // workaround until app can handle it.


          return model;
        } catch (JSONException e) {
          LOGGER.warn("Couldn't create JSONObject for scenariod={}", scenario.getScenarioId(), e);
        }
        return null;
      }).collect(Collectors.toList());

    } catch (IOException e) {
      LOGGER.warn(
          "IOException when fetching details of scenarios={} from routes api",
          String.join(",", scenarioIds),
          e);
    }

    return Collections.emptyList();

  }

  /**
   * Create favorite model for external link Favorite.
   *
   * @param locale Current Local (example: en)
   * @param favoritePaths A list of favorite paths
   * @return List of models for external link Favorite
   */
  private List<Object> createFavoriteExternalLinkData(
      @NonNull final String locale, @NonNull final List<String> favoritePaths) {


    return favoritePaths.stream().map(favoritePath -> {
      try {
        final JSONObject model = new JSONObject();
        model.put("favCategory", Constants.MEET_SAUDI_SEARCH_CATEGORY);
        model.put("url", favoritePath);
        return model;
      } catch (JSONException e) {
        LOGGER.warn("Couldn't create JSONObject for favoritePath={}", favoritePath, e);
      }
      return null;
    }).collect(Collectors.toList());

  }

  /**
   * Create favorite model for InternalLink Favorites.
   *
   * @param resourceResolver A ResourceResolver
   * @param locale Current Local (example: en)
   * @param favoritePaths List of favorite paths
   * @return List of models for InternalLink Favorites
   */
  private List<Object> createFavoriteInternalLinkData(
      @NonNull final ResourceResolver resourceResolver,
      @NonNull final String locale,
      @NonNull final List<String> favoritePaths) {
    if (CollectionUtils.isEmpty(favoritePaths)) {
      return Collections.emptyList();
    }
    return favoritePaths.stream()
      .map(
        resourcePath ->
          resourceExporterService.exportResource(
            resourceResolver, Constants.FORWARD_SLASH_CHARACTER + locale + resourcePath))
      .filter(StringUtils::isNotEmpty)
      .filter(Objects::nonNull)
      .map(
        modelString -> {
          try {
            return new JSONObject(modelString);
          } catch (JSONException e) {
            LOGGER.warn("Couldn't create JSONObject from {}", modelString, e);
            return null;
          }
        })
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  @Override
  public FavoritesApiEndpoints computeFavoritesApiEndpoints(@NonNull final String locale) {
    final String getFavUrl = String.format(new StringBuilder(saudiTourismConfig.getFavBaseUrl())
          .append(API_GET_FAVORITES).toString(), locale);
    final String updateFavUrl = new StringBuilder(saudiTourismConfig.getFavBaseUrl())
          .append(API_UPDATE_FAVORITES).toString();
    final String deleteFavUrl = new StringBuilder(saudiTourismConfig.getFavBaseUrl())
          .append(API_DELETE_FAVORITES).toString();

    return FavoritesApiEndpoints.builder()
      .getFavUrl(getFavUrl)
      .updateFavUrl(updateFavUrl)
      .deleteFavUrl(deleteFavUrl)
      .build();
  }
}
