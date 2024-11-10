package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.WeatherRequestParams;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

/**
 * Unit test class for weather API servlet.
 */
@ExtendWith(AemContextExtension.class)
class WeatherServletTest {

  private WeatherServlet testServlet;

  private WeatherRequestParams weatherRequestParams;

  private WeatherService weatherService;

  public static final String STR_ORIG_WEATHER_DATA =
      //@formatter:off
      "{\n"
          + "  \"coord\": {\n"
          + "    \"lon\": -0.13,\n"
          + "    \"lat\": 51.51\n"
          + "  },\n"
          + "  \"weather\": {\n"
          + "    \"id\": 800,\n"
          + "    \"main\": \"Clear\",\n"
          + "    \"description\": \"clear sky\",\n"
          + "    \"icon\": \"01n\"\n"
          + "  },\n"
          + "  \"base\": \"stations\",\n"
          + "  \"main\": {\n"
          + "    \"temp\": 278.31,\n"
          + "    \"feels_like\": 270.23,\n"
          + "    \"temp_min\": 277.04,\n"
          + "    \"temp_max\": 279.26,\n"
          + "    \"pressure\": 987,\n"
          + "    \"humidity\": 57\n"
          + "  },\n"
          + "  \"id\": 2643743,\n"
          + "  \"name\": \"London\",\n"
          + "  \"cod\": 200\n"
          + "}";
      //@formatter:on

  public static final String STR_WEATHER_DATA =
      //@formatter:off
      "{\n"
          + "  \"weather\": \n"
          + "    {\n"
          + "      \"id\": 802,\n"
          + "      \"main\": \"Clouds\",\n"
          + "      \"description\": \"scattered clouds\",\n"
          + "      \"icon\": \"03n\"\n"
          + "    }\n"
          + "  ,\n"
          + "  \"temperature\": {\n"
          + "    \"temp\": 278.14,\n"
          + "    \"feels_like\": 270.82,\n"
          + "    \"temp_min\": 277.04,\n"
          + "    \"temp_max\": 279.26,\n"
          + "    \"pressure\": 987,\n"
          + "    \"humidity\": 60\n"
          + "  },\n"
          + "  \"city\": \"London\"\n"
          + "}";
      //@formatter:on

  @BeforeEach
  public void setUp(AemContext context) throws IOException {
    weatherRequestParams = new WeatherRequestParams();
    weatherRequestParams.setCity(Collections.singletonList("london"));
    weatherRequestParams.setLatitude("12345");
    weatherRequestParams.setLongitude("54321");
    weatherRequestParams.setLocale("en");

    weatherService = Mockito.mock(WeatherService.class);

    testServlet = new WeatherServlet();
    Utils.setInternalState(testServlet, "weatherService", weatherService);
  }

  @Test
  void testParams(AemContext context) throws IllegalArgumentException, IOException {
    testServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
    Assertions.assertEquals(500, context.response().getStatus());
  }

  @Test
  void testNoCityNoCoordinates(AemContext context) throws IllegalArgumentException, IOException {
    weatherRequestParams.setCity(null);
    weatherRequestParams.setLatitude(null);
    weatherRequestParams.setLongitude(null);
    testServlet.doGet(context.request(), context.response());

    Assertions.assertNotNull(context.response());
    Assertions.assertEquals(500, context.response().getStatus());
  }


  @Test
  void testServiceException(AemContext context) throws IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("city", "london");
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);

    doThrow(IllegalStateException.class).when(weatherService).getExtendedWeather(any());

    testServlet.doGet(context.request(), context.response());
    final String outputAsString = context.response().getOutputAsString();
    Assertions.assertNotNull(outputAsString);
    Assertions.assertEquals(500, context.response().getStatus());
    assertTrue(outputAsString.contains("IllegalStateException"));
  }

}
