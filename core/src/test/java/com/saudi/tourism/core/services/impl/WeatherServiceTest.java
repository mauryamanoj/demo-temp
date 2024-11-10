package com.saudi.tourism.core.services.impl;

import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for WeatherService (WeatherServiceImpl.class).
 */
@ExtendWith(AemContextExtension.class)
class WeatherServiceTest {

  @Language("JSON")
  private static final String STR_FULL_WEATHER_DATA =
      //@formatter:off
      "{\n"
          + "  \"coord\": {\n"
          + "    \"lon\": -0.13,\n"
          + "    \"lat\": 51.51\n"
          + "  },\n"
          + "  \"weather\": [\n"
          + "    {\n"
          + "      \"id\": 500,\n"
          + "      \"main\": \"Rain\",\n"
          + "      \"description\": \"light rain\",\n"
          + "      \"icon\": \"10n\"\n"
          + "    }\n"
          + "  ],\n"
          + "  \"base\": \"stations\",\n"
          + "  \"main\": {\n"
          + "    \"temp\": 9.96,\n"
          + "    \"feels_like\": 6.25,\n"
          + "    \"temp_min\": 8.33,\n"
          + "    \"temp_max\": 11.67,\n"
          + "    \"pressure\": 1008,\n"
          + "    \"humidity\": 87\n"
          + "  },\n"
          + "  \"visibility\": 6000,\n"
          + "  \"wind\": {\n"
          + "    \"speed\": 4.6,\n"
          + "    \"deg\": 250\n"
          + "  },\n"
          + "  \"rain\": {\n"
          + "    \"1h\": 0.25\n"
          + "  },\n"
          + "  \"clouds\": {\n"
          + "    \"all\": 90\n"
          + "  },\n"
          + "  \"dt\": 1583887735,\n"
          + "  \"sys\": {\n"
          + "    \"type\": 1,\n"
          + "    \"id\": 1414,\n"
          + "    \"country\": \"GB\",\n"
          + "    \"sunrise\": 1583907779,\n"
          + "    \"sunset\": 1583949486\n"
          + "  },\n"
          + "  \"timezone\": 0,\n"
          + "  \"id\": 2643743,\n"
          + "  \"name\": \"London\",\n"
          + "  \"cod\": 200\n"
          + "}";
         //@formatter:on

  @Language("JSON")
  private static final String STR_FILTERED_WEATHER_DATA =
      //@formatter:off
      "{\n"
          + "  \"weather\": \"Rain\",\n"
          + "  \"temp\": 9.96,\n"
          + "  \"tempMin\": 8.33,\n"
          + "  \"tempMax\": 11.67,\n"
          + "  \"city\": \"London\"\n"
          + "}";
      //@formatter:on

  private static final String LAT = "12345";
  private static final String LON = "54321";
  private static final String IMPERIAL = "imperial";
  private static final String DUMMY_ONE_CALL = "http://dummy-one-call-url?APPID=dummyAppId";
  private static final String DUMMY_GROUP_CALL = "http://dummy-group-call-url?APPID=dummyAppId";

  private WeatherRequestParams weatherRequestParams;
  private Cache memCache;
  private WeatherServiceImpl testService;
  private SaudiTourismConfigs saudiTourismConfig;
  private InputStream inputStreamFullData = null;
  private InputStream inputStreamFilteredData = null;
  private String response;
  private ResourceBundleProvider i18nProvider;
  private ResourceBundle i18nBundle;

  @BeforeEach
  private void setUp(AemContext context) throws RepositoryException, IOException {
    memCache = mock(Cache.class);
    saudiTourismConfig = mock(SaudiTourismConfigs.class);

    i18nBundle = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    i18nProvider = mock(ResourceBundleProvider.class);
    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any(Locale.class));
    context.registerService(ResourceBundleProvider.class, i18nProvider);

    when(saudiTourismConfig.getSimpleWeatherExternalAppUrl())
        .thenReturn("http://dummy-url?APPID=dummyAppId");
    when(saudiTourismConfig.getGroupCallWeatherExternalAppUrl()).thenReturn(DUMMY_GROUP_CALL);
    when(saudiTourismConfig.getOneCallWeatherExternalAppUrl()).thenReturn(DUMMY_ONE_CALL);

    inputStreamFullData = DataUtils.getDataValues(STR_FULL_WEATHER_DATA);
    inputStreamFilteredData = DataUtils.getDataValues(STR_FULL_WEATHER_DATA);

    testService = new WeatherServiceImpl();
    JsonParser parser = new JsonParser();
    Utils.setInternalState(testService, "memCache", memCache);
    Utils.setInternalState(testService, "saudiTourismConfig", saudiTourismConfig);

    weatherRequestParams = new WeatherRequestParams();
    weatherRequestParams.setCity(Collections.singletonList("london"));
    weatherRequestParams.setLatitude(LAT);
    weatherRequestParams.setLongitude(LON);
    weatherRequestParams.setLocale("en");
  }


  // TODO Test methods need to be created

  @Test
  void getSimpleWeatherSingleCity() {
    assertTrue(true);
  }

  @Test
  void getSimpleWeatherGroupOfCities() {
    assertTrue(true);
  }

  @Test
  void getExtendedWeather() {
    assertTrue(true);
  }

  @Test
  void getSingleCityFromWeatherRequest() {
    assertTrue(true);
  }

  @Test
  void requestExtendedWeather() {
    assertTrue(true);
  }

  @Test
  void requestSimpleWeatherGroup() {
    assertTrue(true);
  }

  @Test
  void localizeData() {
    assertTrue(true);
  }

  @Test
  void testLocalizeData() {
    assertTrue(true);
  }

  @Test
  void getOpenWeatherOneCallUrl() {
    assertEquals(DUMMY_ONE_CALL + "&lat=" + LAT + "&lon=" + LON + "&lang=en" + "&units=" + IMPERIAL,
        testService.getOpenWeatherOneCallUrl(LAT, LON, IMPERIAL));
  }

  @Test
  void getWeatherGroupCallUrl() throws UnsupportedEncodingException {
    assertEquals(
        DUMMY_GROUP_CALL + "&id=" + URLEncoder.encode("1,2", "UTF-8") + "&lang=en" + "&units="
            + IMPERIAL, testService.getWeatherGroupCallUrl(Arrays.asList("1", "2"), IMPERIAL));
  }

  @Test
  void getMeasurementUnit() {
    assertEquals("metric", WeatherServiceImpl.getMeasurementUnit(null));
    assertEquals("metric", WeatherServiceImpl.getMeasurementUnit(StringUtils.EMPTY));
    assertEquals("metric", WeatherServiceImpl.getMeasurementUnit("unknown"));

    assertEquals("metric", WeatherServiceImpl.getMeasurementUnit("METRIC"));
    assertEquals("metric", WeatherServiceImpl.getMeasurementUnit("metric"));
    assertEquals("imperial", WeatherServiceImpl.getMeasurementUnit("imperial"));
    assertEquals("imperial", WeatherServiceImpl.getMeasurementUnit("IMPERIAL"));
    assertEquals("standard", WeatherServiceImpl.getMeasurementUnit("standard"));
    assertEquals("standard", WeatherServiceImpl.getMeasurementUnit("STANDARD"));
  }
}
