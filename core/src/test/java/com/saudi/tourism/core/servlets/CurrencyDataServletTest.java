package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.CurrencyDataService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class CurrencyDataServletTest {

  private CurrencyDataServlet currencyDataServlet;

  private EventsRequestParams eventsRequestParams;

  private SaudiTourismConfigs saudiTourismConfig;

  private CurrencyDataService currencyDataService;

  private Cache memCache;

  String strFilteredCurrencyData = null;
  String strFullCurrencyData = null;

  @BeforeEach public void setUp(AemContext context) throws IOException {

    strFilteredCurrencyData = "{\n" + "  \"source\": \"USD\",\n" + "  \"currencies\": {\n"
        + "    \"USDEUR\": 0.897304,\n" + "    \"USDGBP\": 0.814996,\n"
        + "    \"USDCAD\": 1.38415,\n" + "    \"USDPLN\": 3.94035,\n"
        + "    \"USDLKR\": 184.398304,\n" + "    \"USDINR\": 73.95904\n" + "  }\n" + "}";

    strFullCurrencyData =
        "{\n" + "  \"success\": true,\n" + "  \"terms\": \"https://currencylayer.com/terms\",\n"
            + "  \"privacy\": \"https://currencylayer.com/privacy\",\n"
            + "  \"timestamp\": 1584290046,\n" + "  \"source\": \"USD\",\n" + "  \"quotes\": {\n"
            + "    \"USDEUR\": 0.897304,\n" + "    \"USDGBP\": 0.814996,\n"
            + "    \"USDCAD\": 1.38415,\n" + "    \"USDPLN\": 3.94035,\n"
            + "    \"USDLKR\": 184.398304,\n" + "    \"USDINR\": 73.95904\n" + "  }\n" + "}";

    eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setSource("USD");
    eventsRequestParams.setCurrencies("EUR,GBP,CAD,PLN,LKR,INR");

    saudiTourismConfig = Mockito.mock(SaudiTourismConfigs.class);
    when(saudiTourismConfig.getCurrencyExternalAppURL()).thenReturn(
        String.format("http://dummy-url?source=%s&currencies=%s",
            eventsRequestParams.getSource(), eventsRequestParams.getCurrencies()));

    currencyDataService = Mockito.mock(CurrencyDataService.class);
    when(currencyDataService.getCurrencyDataFromApi(any(String.class)))
        .thenReturn(String.valueOf(DataUtils.getDataValues(strFullCurrencyData)));

    currencyDataServlet = new CurrencyDataServlet();
    Utils.setInternalState(currencyDataServlet, "currencyDataService", currencyDataService);
  }

  @Test public void testMandatoryParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Assertions.assertNotNull(saudiTourismConfig.getCurrencyExternalAppURL());
    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertEquals(400 , context.response().getStatus());

  }

  @Test public void testNoCityParam(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testException(AemContext context) throws ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("source", "USD");
    parameters.put("currencies", "EUR,GBP,CAD,PLN,LKR,INR");
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);

    when(saudiTourismConfig.getCurrencyExternalAppURL()).thenThrow(NullPointerException.class);

    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testNoUrlParam(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("source", "USD");
    parameters.put("currencies", "EUR,GBP,CAD,PLN,LKR,INR");
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);

    when(saudiTourismConfig.getCurrencyExternalAppURL()).thenReturn("");
    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testCityData(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("source", "USD");
    parameters.put("currencies", "EUR,GBP,CAD,PLN,LKR,INR");
    parameters.put("currency", "EUR");
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);
    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
    parameters.remove("locale");
    context.request().setParameterMap(parameters);

    currencyDataServlet.doGet(context.request(), context.response());

  }

  @Test public void testError(AemContext context) throws ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("currency", "EUR");
    context.request().setParameterMap(parameters);

    Utils.setInternalState(currencyDataServlet, "currencyDataService", null);
    currencyDataServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

}
