package com.saudi.tourism.core.models.components.destinations.v1;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.CustomFloatDeserializer;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Model for All destinations component. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class AllDestinationsModel {

  /** Resource Resolver. */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /**
   * Current resource.
   */
  @Self
  private Resource resource;

  /**
   * Weather service.
   */
  @OSGiService
  private WeatherService weatherService;

  /** Title. */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Destinations.
   */
  @Inject
  @Named("destinations")
  @JsonIgnore
  private List<DestinationItem> destinationsCFPaths;

  /** Destinations. */
  @Expose
  private List<Destination> destinations;

  @PostConstruct
  void init() {
    if (CollectionUtils.isNotEmpty(destinationsCFPaths)) {
      String locale = CommonUtils.getLanguageForPath(resource.getPath());
      destinations = destinationsCFPaths.stream()
        .map(DestinationItem::getDestinationCFPath)
        .filter(StringUtils::isNotEmpty)
        .map(p -> Optional.ofNullable(resourceResolver.getResource(p))
          .map(r -> r.adaptTo(DestinationCFModel.class))
          .orElse(null))
        .filter(Objects::nonNull)
        .map(destinationCFModel -> {
          DynamicMediaUtils.setAllImgBreakPointsInfo(
              destinationCFModel.getBannerImage(),
              "crop-660x337", "crop-375x280", "1280", "420",
                resourceResolver, resource);
          DynamicMediaUtils.setAllImgBreakPointsInfo(
              destinationCFModel.getPopUpImage(),
              "crop-660x337", "crop-375x280", "1280", "420",
                resourceResolver, resource);
          return Destination.fromCFModel(destinationCFModel);
        })
        .peek(destination -> {
          if (StringUtils.isNotEmpty(destination.getId()) || (StringUtils.isNotEmpty(destination.getLatitude())
                && StringUtils.isNotEmpty(destination.getLongitude()))) {
            WeatherRequestParams weatherRequest = new WeatherRequestParams();
            weatherRequest.setLocale(locale);
            weatherRequest.setCity(Collections.singletonList(destination.getId()));
            weatherRequest.setLatitude(destination.getLatitude());
            weatherRequest.setLongitude(destination.getLongitude());

            safelyFetchWeather(weatherRequest).ifPresent(destination::setWeather);
          }
        })
        .collect(Collectors.toList());
    }
  }

  /**
   * Safely fetch weather.
   * @param params
   * @return Optional of SimpleWeatherMinMax
   */
  private Optional<SimpleWeatherMinMax> safelyFetchWeather(WeatherRequestParams params) {
    try {
      return Optional.ofNullable(weatherService.getSimpleWeatherSingleCity(params));
    } catch (Exception e) {
      LOGGER.error("Error fetching weather for city: {}", params.getCity(), e);
      return Optional.empty();
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.registerTypeAdapter(Float.class, new CustomFloatDeserializer()).create();
    return gson.toJson(this);
  }

  /** Questions By ContryType model. */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class DestinationItem {
    /** Destination CF Path. */
    @ValueMapValue
    private String destinationCFPath;
  }
}
