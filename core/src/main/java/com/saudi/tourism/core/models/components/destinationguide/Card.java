package com.saudi.tourism.core.models.components.destinationguide;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Card  Model.
 */
@Model(adaptables = {Resource.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class Card {
  /**
   * Weather service.
   */
  @OSGiService
  private WeatherService weatherService;

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Card title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Card type.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * Card description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Link.
   */
  @ChildResource
  @Getter
  @Expose
  private Link link;

  /**
   * Items.
   */
  @ChildResource
  @Setter
  @Expose
  private List<Item> items;

  /**
   * Logo.
   */
  @ChildResource
  @Setter
  @Expose
  private Image logo;

  /**
   * Card City ID.
   */
  @ValueMapValue
  @Expose
  private String cityId;

  /**
   * Card Text Weather.
   */
  @Expose
  private String weather;

  /**
   * Card Temp Weather.
   */
  @Expose
  private Float temp;

  /**
   * Card Icon Weather.
   */
  @Expose
  private String icon;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    if (StringUtils.isNotBlank(cityId)) {
      var weatherRequest = new WeatherRequestParams();
      List<String> citiesId = new ArrayList<>();
      citiesId.add(cityId);
      weatherRequest.setCity(citiesId);
      final String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
      weatherRequest.setLocale(locale);
      var weatherResponse = weatherService.getSimpleWeatherGroupOfCities(weatherRequest);
      if (weatherResponse != null) {
        SimpleWeatherModel simpleWeatherModel = weatherResponse.get(0);
        if (simpleWeatherModel != null) {
          weather = simpleWeatherModel.getWeather();
          icon = simpleWeatherModel.getIcon();
          temp = simpleWeatherModel.getTemp();
          if (ObjectUtils.isNotEmpty(temp)) {
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            String formattedTemperatureStr = decimalFormat.format(temp).replace(",", ".");
            temp = Float.parseFloat(formattedTemperatureStr);
          }
        }
      }
    }
    final ResourceResolver resourceResolver = currentResource.getResourceResolver();
    DynamicMediaUtils.setAllImgBreakPointsInfo(logo, "crop-600x600", "crop-600x600",
        "1280", "420", currentResource.getResourceResolver(), currentResource);
    link.setUrl(LinkUtils.getAuthorPublishUrl(resourceResolver, link.getUrl(),
        settingsService.getRunModes().contains(Externalizer.PUBLISH)));

    if (ObjectUtils.isNotEmpty(items)) {
      for (Item item : items) {
        if ("weather".equals(type)) {
          item.setTitle(item.getWeatherTitle());
        } else if ("item".equals(type)) {
          item.setTitle(item.getItemTitle());
        }
      }
    }
  }
}
