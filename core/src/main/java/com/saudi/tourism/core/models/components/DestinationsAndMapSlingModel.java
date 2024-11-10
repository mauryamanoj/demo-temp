package com.saudi.tourism.core.models.components;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.RegionCityExtended;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import org.apache.sling.api.resource.ResourceResolver;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Locale;

import static com.saudi.tourism.core.utils.Constants.API_V1_BASE_PATH;
import static com.saudi.tourism.core.utils.Constants.CONST_AMPERSAND;
import static com.saudi.tourism.core.utils.Constants.EQUAL;
import static com.saudi.tourism.core.utils.Constants.LOCALE;
import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_VARIANT;
import static com.saudi.tourism.core.utils.I18nConstants.LOAD_MORE_TEXT;

/**
 * Sling Model for a component 'DestinationsAndMap'.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class DestinationsAndMapSlingModel extends TextCardsModel {

  /**
   * Reference of Saudi Tourism Configuration.
   */
  @OSGiService
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Map Api Key.
   */
  @Expose
  private String mapApiKey;
  /**
   * Google Map Api Key.
   */
  @Expose
  private String googleMapsKey;
  /**
   * Request Json Data.
   */
  @Expose
  private String requestJsonData;

  /**
   * currentResource.
   */
  @Self
  private Resource currentResource;
  /**
   * Resource Bundle Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private transient ResourceBundleProvider i18nProvider;
  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  /**
   * Subheading of the component.
   */
  @ValueMapValue
  @Expose
  private String subHeading;
  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resourceResolver;
  /**
   * Map Type.
   */
  @ValueMapValue
  @Expose
  private String mapType;

  /**
   * Load More button.
   */
  @ValueMapValue
  @Expose
  private String isLoadMore;

  /**
   * Type.
   */
  @ValueMapValue
  @Expose
  private String type;

  /**
   * Card Orientation.
   */
  @ValueMapValue
  @Expose
  private String cardOrientation;
  /**
   * loadMoreText.
   */
  @Expose
  private String loadMoreText;
  /**
   * Image.
   */
  private Image image;
  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

  /**
   * Post construct init method.
   */
  @PostConstruct
  @Override
  protected void init() {
    super.init();
    mapApiKey = saudiTourismConfigs.getMapKey();
    googleMapsKey = saudiTourismConfigs.getGoogleMapsKey();
    InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentResource);
    String locale = CommonUtils.getLanguageForPath(currentResource.getPath());
    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    loadMoreText = i18n.getString(LOAD_MORE_TEXT);
    if (Objects.nonNull(getCards()) && !getCards().isEmpty()) {
      setRequestJsonData(populateRequestJsonData());
    }
  }

  /**
   * Populate card json data to expose.
   * @return cardJsonData
   */
  @Override
  protected String populateCardJsonData() {
    List<Map<String, Object>> exposeCardsData = new ArrayList<>();

    for (CardModel cardModel : getCards()) {
      Map<String, Object> resultMap = new HashMap<>();
      Map<String, String> linkData = getLinkData(cardModel);
      Map<String, String> imageData = getImageData(cardModel);
      Map<String, Object> labels = getLabels(cardModel);
      image = Optional.ofNullable(cardModel.getCityObj())
        .map(RegionCityExtended::getImage).orElse(null);
      DynamicMediaUtils.setAllImgBreakPointsInfo(image, "crop-1920x1080",
          "crop-375x667", "1280", "420",
          resourceResolver, currentResource);
      resultMap.put("link", linkData);
      resultMap.put(PN_IMAGE, image);
      resultMap.put("labels", labels);
      resultMap.put(PN_VARIANT, "with-cta");
      resultMap.put("city", cardModel.getCityId());
      resultMap.put("latitude", cardModel.getLatitude());
      resultMap.put("longitude", cardModel.getLongitude());
      exposeCardsData.add(resultMap);
    }

    Gson gson = new Gson();
    return gson.toJson(exposeCardsData);
  }


  /**
   * Populate card json data to expose.
   * @return cardJsonData
   */
  private String populateRequestJsonData() {
    String lang = CommonUtils.getLanguageForPath(currentResource.getPath());
    Map<String, String> requestData = new HashMap<>();
    requestData.put("basePath", API_V1_BASE_PATH);
    requestData.put("extendedWeatherEndpoint", "weather-extended");
    requestData.put("weatherEndpoint", "weather");
    requestData.put("extendedWeatherParameters", "city=riyadh");

    if (!getCards().isEmpty()) {
      requestData.put("weatherParameters",
          LOCALE + EQUAL + lang + CONST_AMPERSAND
          + String.join(CONST_AMPERSAND, getCards().stream()
          .map(CardModel::getCityObj).filter(Objects::nonNull)
          .map(cityObj -> "city=" + cityObj.getId()).toArray(String[]::new)));

    }

    Gson gson = new Gson();
    return gson.toJson(requestData);
  }

}
