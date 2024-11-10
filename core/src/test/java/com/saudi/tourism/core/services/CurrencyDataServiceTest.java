package com.saudi.tourism.core.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.cache.InMemoryCache;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.impl.CurrencyDataServiceImpl;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class) public class CurrencyDataServiceTest {

  private static EventsRequestParams eventsRequestParams;
  private static Cache memCache;
  private static CurrencyDataServiceImpl currencyDataService;

  private static SaudiTourismConfigs saudiTourismConfigs;
  private static String strFilteredCurrencyData = null;
  private static String strFullCurrencyData = null;

  @BeforeAll static void beforeAll() {
    memCache = new InMemoryCache();
    currencyDataService = Mockito.mock(CurrencyDataServiceImpl.class, Mockito.CALLS_REAL_METHODS);
    saudiTourismConfigs = Mockito.mock(SaudiTourismConfigs.class);

    strFilteredCurrencyData = (""
        + "{\n"
        + "  \"source\": \"USD\",\n"
        + "  \"currencies\": {\n"
        + "    \"USDEUR\": 0.897304,\n"
        + "    \"USDGBP\": 0.814996,\n"
        + "    \"USDCAD\": 1.38415,\n"
        + "    \"USDPLN\": 3.94035,\n"
        + "    \"USDLKR\": 184.398304,\n"
        + "    \"USDINR\": 73.95904\n"
        + "  }\n"
        + "}"
    ).replaceAll("\\s+", "");

    strFullCurrencyData = (""
        + "{\n"
        + "  \"success\": true,\n"
        + "  \"terms\": \"https://currencylayer.com/terms\",\n"
        + "  \"privacy\": \"https://currencylayer.com/privacy\",\n"
        + "  \"timestamp\": 1584290046,\n"
        + "  \"source\": \"USD\",\n"
        + "  \"quotes\": {\n"
        + "    \"USDEUR\": 0.897304,\n"
        + "    \"USDGBP\": 0.814996,\n"
        + "    \"USDCAD\": 1.38415,\n"
        + "    \"USDPLN\": 3.94035,\n"
        + "    \"USDLKR\": 184.398304,\n"
        + "    \"USDINR\": 73.95904\n"
        + "  }\n"
        + "}"
    ).replaceAll("\\s+", "");

    eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setSource("USD");
    eventsRequestParams.setCurrencies("EUR,GBP,CAD,PLN,LKR,INR");

    when(saudiTourismConfigs.getCurrencyExternalAppURL()).thenReturn(String
        .format("http://dummy-url?source=%s&currencies=%s", eventsRequestParams.getSource(),
            eventsRequestParams.getCurrencies()));

    ResponseMessage response = Mockito.mock(ResponseMessage.class);
    when(response.getMessage()).thenReturn(strFullCurrencyData);

    Utils.setInternalState(currencyDataService, "memCache", memCache);
  }

  @Test void testDataUtilsFilterRequiredCurrencyData() {
    JsonParser parser = new JsonParser();
    JsonElement jsonTree = parser.parse(strFullCurrencyData);
    Assertions.assertNotNull(jsonTree);
    Assertions.assertTrue(jsonTree.isJsonObject());
    JsonObject filteredJsonObject = DataUtils.filterRequiredCurrencyData(strFullCurrencyData,
        "EUR,GBP,CAD,PLN,LKR,INR");
    Assertions.assertNotNull(filteredJsonObject);
    Assertions.assertTrue(jsonTree.isJsonObject());
    Assertions.assertNotNull(filteredJsonObject.get(Constants.CONST_STR_CURRENCIES));
    JsonObject currencyData =
        filteredJsonObject.get(Constants.CONST_STR_CURRENCIES).getAsJsonObject();
    Assertions.assertEquals("0.897304", currencyData.get("USDEUR").getAsString());
    Assertions.assertEquals("0.814996", currencyData.get("USDGBP").getAsString());
    Assertions.assertEquals("1.38415", currencyData.get("USDCAD").getAsString());
    Assertions.assertEquals("3.94035", currencyData.get("USDPLN").getAsString());
    Assertions.assertEquals("184.398304", currencyData.get("USDLKR").getAsString());
    Assertions.assertEquals("73.95904", currencyData.get("USDINR").getAsString());
  }
}
