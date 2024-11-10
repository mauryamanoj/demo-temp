package com.saudi.tourism.core.login.services.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.login.OauthTokenModel;
import com.saudi.tourism.core.login.services.SaudiLoginConfig;
import com.saudi.tourism.core.login.services.TokenService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * The Token Service.
 */
@Slf4j
@Component(service = TokenService.class,
           immediate = true)
public class TokenServiceImpl implements TokenService {

  /**
   * The constant ACCESS_TOKEN.
   */
  public static final String ACCESS_TOKEN = "access_token";
  /**
   * InMemory cache.
   */
  @Reference
  private Cache memCache;

  /**
   * Saudi Login Configurations.
   */
  @Reference
  private SaudiLoginConfig saudiLoginConfig;


  @Override public String getMachineToMachineToken() throws IOException {
    String token = (String) memCache.get(ACCESS_TOKEN);
    if (token != null) {
      return token;
    }
    Map<String, String> headerMap = new HashMap<>();
    headerMap.put("Content-Type", "application/json");
    OauthTokenModel oauthTokenModel = OauthTokenModel.builder().
        clientId(saudiLoginConfig.getClientId()).clientSecret(saudiLoginConfig.getClientSecret())
        .audience(saudiLoginConfig.getAudience()).grantType("client_credentials").build();
    ResponseMessage responseMessage = RestHelper.executeMethodPost(
        saudiLoginConfig.getAuth0BaseUrl() + saudiLoginConfig.getAuth0OauthTokenEndpoint(),
        new Gson().toJson(oauthTokenModel), null, headerMap);
    JsonParser parser = new JsonParser();
    JsonObject rootObj = parser.parse(responseMessage.getMessage()).getAsJsonObject();

    token = rootObj.get(ACCESS_TOKEN).getAsString();
    memCache.add(ACCESS_TOKEN, token);

    return token;
  }
}
