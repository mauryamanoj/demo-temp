package com.saudi.tourism.core.models.components;

import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import org.apache.sling.api.resource.Resource;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.day.crx.JcrConstants;

/**
 * class for static data for weather-widget.
 */
@Model(adaptables = {SlingHttpServletRequest.class, Resource.class},
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class WeatherWidgetStaticModel {

  /**
   * I18n Resource Bundle provider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;
  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;
  /**
   * saudiIANATimeZone.
   */
  @Expose
  private String saudiIANATimeZone;
  /**
   * decimalQuantity.
   */
  @Expose
  private int decimalQuantity;
  /**
   * currentWeatherText.
   */
  @Expose
  private Map<String, Object> currentWeatherText;
  /**
   * weekWeatherText.
   */
  @Expose
  private Map<String, String> weekWeatherText;
  /**
   * meteorologicalInformationText.
   */
  private Map<String, Object> meteorologicalInformationText;
  /**
   * windSpeedMeasure.
   */
  private Map<String, String> windSpeedMeasure;
  /**
   * temperatureByHourSliderText.
   */
  private Map<String, String> temperatureByHourSliderText;
  /**
   * dayTranslation.
   */
  @Expose
  private Map<String, String> dayTranslation;
  /**
   * monthTranslation.
   */
  @Expose
  private Map<String, String> monthTranslation;
  /**
   * localeCode.
   */
  private String localeCode;
  /**
   * bigTemperatureCardText.
   */
  private Map<String, Object> bigTemperatureCardText;
  /**
   * method for getting the translated values.
   */
  @PostConstruct
  protected void init() {

    InheritanceValueMap ivm =
        new HierarchyNodeInheritanceValueMap(getCurrentPage().getContentResource());
    localeCode = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);
    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(localeCode));
    saudiIANATimeZone = "Asia/Riyadh";
    decimalQuantity = 1;
    currentWeatherText = new HashMap<>();
    currentWeatherText.put("title", CommonUtils.getI18nString(i18n, "today"));
    bigTemperatureCardText = new HashMap<>();
    bigTemperatureCardText.put("title", CommonUtils.getI18nString(i18n, "Now"));
    bigTemperatureCardText.put("feelsLike", CommonUtils.getI18nString(i18n, "Feels like"));
    bigTemperatureCardText.put("celsiusSymbol", CommonUtils.getI18nString(i18n, "°C"));
    bigTemperatureCardText.put("fahrenheitSymbol", CommonUtils.getI18nString(i18n, "°F"));
    meteorologicalInformationText = new HashMap<>();
    meteorologicalInformationText.put("sunriseTitle", CommonUtils.getI18nString(i18n, "Sunrise"));
    meteorologicalInformationText.put("maxTitle", CommonUtils.getI18nString(i18n, "High"));
    meteorologicalInformationText.put("minTitle", CommonUtils.getI18nString(i18n, "Low"));
    meteorologicalInformationText.put("humidityTitle", CommonUtils.getI18nString(i18n, "Humidity"));
    meteorologicalInformationText.put("windSpeedTitle", CommonUtils.getI18nString(i18n, "Wind"));
    meteorologicalInformationText.put("sunsetTitle", CommonUtils.getI18nString(i18n, "Sunset"));
    windSpeedMeasure = new HashMap<>();
    windSpeedMeasure.put("celsius", CommonUtils.getI18nString(i18n, "m/s"));
    windSpeedMeasure.put("fahrenheit", CommonUtils.getI18nString(i18n, "mph"));

    meteorologicalInformationText.put("windSpeedMeasure", windSpeedMeasure);
    temperatureByHourSliderText = new HashMap<>();
    temperatureByHourSliderText.put("firstPillTitleCopy", CommonUtils.getI18nString(i18n, "Now"));

    currentWeatherText.put("bigTemperatureCardText", bigTemperatureCardText);
    currentWeatherText.put("meteorologicalInformationText", meteorologicalInformationText);
    currentWeatherText.put("temperatureByHourSliderText", temperatureByHourSliderText);
    weekWeatherText = new HashMap<>();
    weekWeatherText.put("title", CommonUtils.getI18nString(i18n, "this-week"));
    dayTranslation = new HashMap<>();
    dayTranslation.put("sunday", CommonUtils.getI18nString(i18n, "Sun"));
    dayTranslation.put("monday", CommonUtils.getI18nString(i18n, "Mon"));
    dayTranslation.put("tuesday", CommonUtils.getI18nString(i18n, "Tue"));
    dayTranslation.put("wednesday", CommonUtils.getI18nString(i18n, "Wed"));
    dayTranslation.put("thursday", CommonUtils.getI18nString(i18n, "Thu"));
    dayTranslation.put("friday", CommonUtils.getI18nString(i18n, "Fri"));
    dayTranslation.put("saturday", CommonUtils.getI18nString(i18n, "Sat"));
    monthTranslation = new HashMap<>();
    monthTranslation.put("January", CommonUtils.getI18nString(i18n, "January"));
    monthTranslation.put("February", CommonUtils.getI18nString(i18n, "February"));
    monthTranslation.put("March", CommonUtils.getI18nString(i18n, "March"));
    monthTranslation.put("April", CommonUtils.getI18nString(i18n, "April"));
    monthTranslation.put("May", CommonUtils.getI18nString(i18n, "May"));
    monthTranslation.put("June", CommonUtils.getI18nString(i18n, "June"));
    monthTranslation.put("July", CommonUtils.getI18nString(i18n, "July"));
    monthTranslation.put("August", CommonUtils.getI18nString(i18n, "August"));
    monthTranslation.put("September", CommonUtils.getI18nString(i18n, "September"));
    monthTranslation.put("October", CommonUtils.getI18nString(i18n, "October"));
    monthTranslation.put("November", CommonUtils.getI18nString(i18n, "November"));
    monthTranslation.put("December", CommonUtils.getI18nString(i18n, "December"));

  }
}
