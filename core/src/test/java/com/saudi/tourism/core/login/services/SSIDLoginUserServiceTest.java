package com.saudi.tourism.core.login.services;

import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.SSIDFunctionnalException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.login.services.impl.SSIDLoginUserServiceImpl;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.services.SSIDTripPlanService;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SSIDLoginUserServiceTest {
  private final String SSID_TOKEN =
      "eyJraWQiOiJzdGEiLCJhbGciOiJSUzI1NiJ9.eyJpc3MiOiJodHRwczovL3VhdC1zc2lkLnZpc2l0c2F1ZGkuY29tIiwic3ViIjoiU0lELTE0MjEwOC0yMDIyLWVmNzdmMTAwLTA2MWQtNGMzNS04NzI4LTdjYWNiOTdlY2ZhMSIsImF1ZCI6IjY1NDAyMGNiY2RjMDZkMTVlNTE3ZmU0NzdlNDNhYzI2MDE5YzY3YTkwMjhjOGQ1NmJhYmJiMDlkN2JkZjg2NWYiLCJleHAiOjE2NjYyNzYzMjYsImlhdCI6MTY2MTA5MjMyNiwic2NvcGUiOiJwcm9maWxlIiwibm9uY2UiOiJiMDVmZGJhNTBkOGQ0MGUwYjBiOGFjNzkwYzI2YzgzZiJ9.TZxBp2pdrSccsxQTSswZqqyXV6jzLvAROIWrK1ZHEAFWex57tzwXDBqWCjmgCDxGI9molKziaGtoMYph099GBjTii5fzofYPodo2UNIP12dykbQus3cb7fL-WP-MGEEEkHC-OXlu5GKZHfHOSPSbw54m_fW5vpj4jZeVo89PTB9kINCQdiXo21mYh_61EEy6UlHNkNeCsbl2vbIfWxoY3ro6-x76IxKiDM3Qty7Vx41-Gr-S8PQlo5aVY1-QY7b99nk5Q_zw3zis2puY_5pbybkP5WgBqHlGC0r_IwwhfbjaN71pvEUVSTL3TRQJQWNBm8LXH3D09CVJyzu099uhkA";

  private final String SSID_FAVORITES_RESPONSE =
      "{\"email\":\"john.doe@gmail.com\",\"name\":\"John DOE\",\"picture\":\"\",\"email_verified\":true,\"user_metadata\":{\"favorites\":[\"/see/highlights/jeddah-alshallal-park\",\"/summer-vibes/package-details?experienceId=60d824d098336644843e74f7\"],\"trips\":[],\"profile\":{\"smartPassId\":\"SID-142108-2022-ef77f100-061d-4c35-8728-7cacb97ecfa1\",\"ageRange\":\"\",\"firstName\":\"Souhayl\",\"lastName\":\"ABDOUNI\",\"genderCode\":\"\",\"tripCount\":\"0\",\"visaNumber\":\"\",\"interests\":[],\"favoritesCount\":\"1\",\"birthDate\":\"\",\"email\":\"abdouni.souhayl@gmail.com\",\"travelPartner\":\"\",\"mobileNumber\":\"\",\"countryCode\":\"\",\"halaYallaUserId\":\"\",\"hasDiscoveredLoyaltyTooltips\":false,\"isMobileVerified\":false}}}";
  @Mock
  private SSIDFavouritesTripsConfig ssidFavouritesTripsConfig;

  @Mock
  private ResourceExporterService resourceExporterService;

  @Mock
  private SSIDTripPlanService tripPlanService;

  @Mock
  private SaudiSSIDConfig ssidConfig;

  @Mock
  private TokenService tokenService;

  @Mock
  private UserService userService;

  @Mock
  private HttpClientBuilder httpClientBuilder;

  @Mock
  private CloseableHttpClient closeableHttpClient;

  @Mock
  private CloseableHttpResponse closeableHttpResponse;

  @Mock
  private StatusLine statusLine;

  @Mock
  private HttpEntity httpEntity;

  @Mock
  private FavoritesService favoritesService;

  private ResourceResolver resourceResolver;

  private SSIDLoginUserServiceImpl ssidLoginUserService;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    ssidLoginUserService = new SSIDLoginUserServiceImpl();

    aemContext.registerService(SSIDFavouritesTripsConfig.class, ssidFavouritesTripsConfig);
    aemContext.registerService(ResourceExporterService.class, resourceExporterService);
    aemContext.registerService(SSIDTripPlanService.class, tripPlanService);
    aemContext.registerService(SaudiSSIDConfig.class, ssidConfig);
    aemContext.registerService(TokenService.class, tokenService);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerService(FavoritesService.class, favoritesService);
    aemContext.registerInjectActivateService(ssidLoginUserService);

    resourceResolver = aemContext.resourceResolver();
    when(userService.getResourceResolver()).thenReturn(resourceResolver);

    when(ssidFavouritesTripsConfig.getMiddlewareDomain())
        .thenReturn("https://middleware-qa.visitsaudi.com/v1/ssid");
    when(ssidFavouritesTripsConfig.getFetchFavoritesTripsEndpoint())
        .thenReturn("/auth/userDetails");
  }

  @Test
  public void getUserDetailsFullShouldReturnFavoritesDetails()
      throws IOException, JSONException, MiddlewareException {
    try (MockedStatic<HttpClientBuilder> mockedHttpClientBuilder =
        mockStatic(HttpClientBuilder.class)) {
      // Arrange
      UserIDToken userIDToken = new UserIDToken();
      userIDToken.setToken(SSID_TOKEN);
      userIDToken.setLocale("en");

      Map<String, Object> filterParams = new HashMap<>();
      filterParams.put("locale", "en");

      mockedHttpClientBuilder.when(() -> HttpClientBuilder.create()).thenReturn(httpClientBuilder);
      when(httpClientBuilder.build()).thenReturn(closeableHttpClient);
      when(closeableHttpClient.execute(any(HttpGet.class))).thenReturn(closeableHttpResponse);
      when(closeableHttpResponse.getStatusLine()).thenReturn(statusLine);
      when(closeableHttpResponse.getEntity()).thenReturn(httpEntity);
      when(statusLine.getStatusCode()).thenReturn(200);
      when(httpEntity.getContent())
          .thenReturn(IOUtils.toInputStream(SSID_FAVORITES_RESPONSE, "UTF-8"));

      final JSONObject internalLinkData = new JSONObject();
      internalLinkData.put(
          "cardImage", "/content/dam/jeddah/attractions-in-jeddah/Alshallal-roller-coaster.jpeg");
      internalLinkData.put(
          "path", "/content/sauditourism/en/see/highlights/jeddah-alshallal-park.html");
      internalLinkData.put("favCategory", "FestivalsEvents");
      internalLinkData.put(
          "link", "/content/sauditourism/en/see/highlights/jeddah-alshallal-park.html");
      internalLinkData.put("title", "Al Shallal Theme Park");
      internalLinkData.put("favId", "/see/highlights/jeddah-alshallal-park");

      final JSONObject hallayalaExperienceData = new JSONObject();
      hallayalaExperienceData.put(
        "cardImage",
        "https://agool-new.s3.me-south-1.amazonaws.com/dmc/venue-images/dmc_venue_62861da195386.jpg");
      hallayalaExperienceData.put(
        "path", "/en/summer-vibes/package-details?experienceId=62861da576aebc4253669860");
      hallayalaExperienceData.put(
        "link", "/en/summer-vibes/package-details?experienceId=62861da576aebc4253669860");
      hallayalaExperienceData.put("title", "3 Days Beach Adventure in KAEC");
      hallayalaExperienceData.put(
        "favId", "/summer-vibes/package-details?experienceId=62861da576aebc4253669860");

      when(favoritesService.getFavoritesData(eq(resourceResolver), eq("en"), any(List.class)))
          .thenReturn(Arrays.asList(new JSONObject[]{internalLinkData, hallayalaExperienceData}));

      // Act
      final String result =
          ssidLoginUserService.getUserDetailsFull(userIDToken, "favorites", filterParams);

      // Assert
      final JSONObject favorites = new JSONObject(result);
      final JSONArray details = favorites.getJSONArray("details");
      assertNotNull(details);

      final JSONObject internalLinkDetail = details.getJSONObject(0);
      final JSONObject hallayalaDetail = details.getJSONObject(1);

      assertEquals(
          "/content/dam/jeddah/attractions-in-jeddah/Alshallal-roller-coaster.jpeg",
          internalLinkDetail.get("cardImage"));
      assertEquals(
          "/content/sauditourism/en/see/highlights/jeddah-alshallal-park.html",
          internalLinkDetail.get("path"));
      assertEquals("FestivalsEvents", internalLinkDetail.get("favCategory"));
      assertEquals(
          "/content/sauditourism/en/see/highlights/jeddah-alshallal-park.html",
          internalLinkDetail.get("link"));
      assertEquals("Al Shallal Theme Park", internalLinkDetail.get("title"));
      assertEquals("/see/highlights/jeddah-alshallal-park", internalLinkDetail.get("favId"));

      assertEquals(
          "https://agool-new.s3.me-south-1.amazonaws.com/dmc/venue-images/dmc_venue_62861da195386.jpg",
          hallayalaDetail.get("cardImage"));
      assertEquals(
          "/en/summer-vibes/package-details?experienceId=62861da576aebc4253669860",
          hallayalaDetail.get("path"));
      assertEquals(
          "/en/summer-vibes/package-details?experienceId=62861da576aebc4253669860",
          hallayalaDetail.get("link"));
      assertEquals("3 Days Beach Adventure in KAEC", hallayalaDetail.get("title"));
      assertEquals("/summer-vibes/package-details?experienceId=62861da576aebc4253669860", hallayalaDetail.get("favId"));
    }
  }
}
