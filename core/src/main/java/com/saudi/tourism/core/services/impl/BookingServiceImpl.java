package com.saudi.tourism.core.services.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.BookingService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * The Currency Data Service.
 */
@Slf4j
@Component(service = BookingService.class, immediate = true)
public class BookingServiceImpl implements BookingService {
  /**
   * for handling json format in response.
   */
  private JsonParser parser = new JsonParser();
  /**
   * Saudi Tourism Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  @Override
  public Object getAllBookings(Map<String, Object> queryStrings) throws IOException {
    StringBuilder sb = new StringBuilder(getHalayallaEndPointUrl());
    sb.append("bookings");
    ResponseMessage responseMessage = RestHelper.executeMethodGetWithQueryParametersandHeaders(
        sb.toString(), queryStrings, getRequestHeaders());
    JsonElement json = parser.parse(responseMessage.getMessage());
    return json;
  }

  /**
   * This method is used to construct the URL.
   * @return String .
   */
  private String getHalayallaEndPointUrl() {

    return String.format(saudiTourismConfig.getHalayallaEndPointUrl()) + "/api/vs/";
  }

  /**
   * This method is used to construct the headers.
   * @return Map<String,Object> .
   */
  private Map<String, Object> getRequestHeaders() {

    Map<String, Object> headers = new HashMap<String, Object>();
    headers.put(Constants.TOKEN_HEADER_STR, saudiTourismConfig.getHalayallaToken());
    headers.put(Constants.CLIENTKEY_HEADER_STR, saudiTourismConfig.getHalayallaClientKey());
    headers.put(Constants.CLIENTSECRET_HEADER_STR, saudiTourismConfig.getHalayallaClientSecret());
    return headers;
  }

}
