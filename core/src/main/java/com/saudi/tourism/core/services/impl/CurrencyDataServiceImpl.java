package com.saudi.tourism.core.services.impl;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.CurrencyDataService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.DataUtils;
import com.saudi.tourism.core.utils.RestHelper;

import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * The Currency Data Service.
 */
@Slf4j
@Component(service = CurrencyDataService.class,
           immediate = true)
public class CurrencyDataServiceImpl implements CurrencyDataService {

  /**
   * InMemory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;


  @Override public Object getCurrencyDataFromApi(final String currency) throws IOException {
    // Retrieve data from the external api
    String currencyApiData = getCurrencyDataViaApiUrl(currency);
    return DataUtils.filterRequiredCurrencyData(currencyApiData, currency);
  }

  /**
   * This method is used to get currency data
   * based on request params and config.
   *
   * @param currency the currency
   * @return the currency data via api url
   * @throws IOException .
   */
  private String getCurrencyDataViaApiUrl(String currency) throws IOException {

    ResponseMessage responseMessage = RestHelper
        .executeMethodGet(getCurrencyAppExternalUrlWithParams(currency), StringUtils.EMPTY, false);

    return responseMessage.getMessage();
  }

  /**
   * This method is used to construct the URL.
   *
   * @param currency the currency
   * @return String .
   */
  private String getCurrencyAppExternalUrlWithParams(String currency) {

    return String.format(saudiTourismConfig.getCurrencyExternalAppURL(), currency);
  }
}

