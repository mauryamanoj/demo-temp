package com.saudi.tourism.core.login.services.impl;

import java.io.IOException;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentResponse;
import com.saudi.tourism.core.login.models.LoyaltyEnrollmentStatus;
import com.saudi.tourism.core.login.services.SSIDService;
import com.saudi.tourism.core.login.services.SaudiSSIDConfig;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.SSIDRestHelper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, AemContextExtension.class})
public class SSIDServiceImplTest {
  @Mock
  private SaudiSSIDConfig saudiSSIDConfig;

  @Mock
  private Cache memCache;

  private SSIDService service = new SSIDServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    service = new SSIDServiceImpl();

    aemContext.registerService(SaudiSSIDConfig.class, saudiSSIDConfig);
    aemContext.registerService(Cache.class, memCache);
    aemContext.registerInjectActivateService(service);

    when(saudiSSIDConfig.getSSIDBaseUrl()).thenReturn("https://uat-ssid.visitsaudi.com/ssid/api");
    when(saudiSSIDConfig.getSSIDGetUserProfileEndpoint()).thenReturn("/v2/userinfo");
    lenient().when(saudiSSIDConfig.getSSIDUpdateProfileWithLoyaltyEndpoint()).thenReturn("/v2/userinfo/updateProfileWithLoyalty");
  }

  @Test
  void enrollToLoyaltyProgramShouldHandleNoUserDataFromSSID() throws IOException {
    //Arrange
    LoyaltyEnrollmentResponse result = null;
    final var userInfoResponse = new ResponseMessage(500, StringUtils.EMPTY);

    //Act
    try (MockedStatic<SSIDRestHelper> mockedStatic = mockStatic(SSIDRestHelper.class)) {
      mockedStatic.when(() -> SSIDRestHelper.executeMethodGet(anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(userInfoResponse);
      result = service.enrollToLoyaltyProgram(StringUtils.EMPTY);
    }


    //Assert
    assertNotNull(result);
    assertEquals(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED, result.getStatus());
    assertEquals("No UserInfo returned from SSID while enabling Loyalty Program", result.getMessage());
  }

  @Test
  void enrollToLoyaltyProgramShouldHandleNoSidDataFromSSID() throws IOException {
    //Arrange
    LoyaltyEnrollmentResponse result = null;
    final var userInfoResponse = new ResponseMessage(200, "{\n" +
        "   \"loyaltyId\":\"\",\n" +
        "   \"lastName\":\"DOE\",\n" +
        "   \"gender\":\"\",\n" +
        "   \"mobileNumber\":\"\",\n" +
        "   \"pictureUrl\":\"\",\n" +
        "   \"birthDate\":\"\",\n" +
        "   \"isEmailVerified\":\"Y\",\n" +
        "   \"sid\":\"\",\n" +
        "   \"firstName\":\"John\",\n" +
        "   \"countryCode\":\"\",\n" +
        "   \"name\":\"John DOE\",\n" +
        "   \"isMobileVerified\":\"N\",\n" +
        "   \"interests\":\"\",\n" +
        "   \"email\":\"john.doe@sta.sa\",\n" +
        "   \"travelPartner\":\"\"\n" +
        "}");

    //Act
    try (MockedStatic<SSIDRestHelper> mockedStatic = mockStatic(SSIDRestHelper.class)) {
      mockedStatic.when(() -> SSIDRestHelper.executeMethodGet(anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(userInfoResponse);
      result = service.enrollToLoyaltyProgram(StringUtils.EMPTY);
    }


    //Assert
    assertNotNull(result);
    assertEquals(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED, result.getStatus());
    assertEquals("SID not returned from SSID", result.getMessage());
  }

  @Test
  void enrollToLoyaltyProgramShouldHandleUserWithALoyaltyID() throws IOException {
    //Arrange
    LoyaltyEnrollmentResponse result = null;
    final var userInfoResponse = new ResponseMessage(200, "{\n" +
        "   \"loyaltyId\":\"e7af115b-1286-4b4a-abf0-afe111266544\",\n" +
        "   \"lastName\":\"DOE\",\n" +
        "   \"gender\":\"\",\n" +
        "   \"mobileNumber\":\"\",\n" +
        "   \"pictureUrl\":\"\",\n" +
        "   \"birthDate\":\"\",\n" +
        "   \"isEmailVerified\":\"Y\",\n" +
        "   \"sid\":\"99560f7c-7c0d-4cbc-8c38-24e5c3022460\",\n" +
        "   \"firstName\":\"John\",\n" +
        "   \"countryCode\":\"\",\n" +
        "   \"name\":\"John DOE\",\n" +
        "   \"isMobileVerified\":\"N\",\n" +
        "   \"interests\":\"\",\n" +
        "   \"email\":\"john.doe@sta.sa\",\n" +
        "   \"travelPartner\":\"\"\n" +
        "}");

    //Act
    try (MockedStatic<SSIDRestHelper> mockedStatic = mockStatic(SSIDRestHelper.class)) {
      mockedStatic.when(() -> SSIDRestHelper.executeMethodGet(anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(userInfoResponse);
      result = service.enrollToLoyaltyProgram(StringUtils.EMPTY);
    }


    //Assert
    assertNotNull(result);
    assertEquals(LoyaltyEnrollmentStatus.ALREADY_ENROLLED, result.getStatus());
    assertNull(result.getMessage());
  }

  @Test
  void enrollToLoyaltyProgramShouldHandleUserWithNoLoyaltyIDResponseOKFromSSID() throws IOException {
    //Arrange
    LoyaltyEnrollmentResponse result = null;
    final var userInfoResponse = new ResponseMessage(HttpStatus.SC_ACCEPTED, "{\n" +
        "   \"loyaltyId\":\"\",\n" +
        "   \"lastName\":\"DOE\",\n" +
        "   \"gender\":\"\",\n" +
        "   \"mobileNumber\":\"\",\n" +
        "   \"pictureUrl\":\"\",\n" +
        "   \"birthDate\":\"\",\n" +
        "   \"isEmailVerified\":\"Y\",\n" +
        "   \"sid\":\"99560f7c-7c0d-4cbc-8c38-24e5c3022460\",\n" +
        "   \"firstName\":\"John\",\n" +
        "   \"countryCode\":\"\",\n" +
        "   \"name\":\"John DOE\",\n" +
        "   \"isMobileVerified\":\"N\",\n" +
        "   \"interests\":\"\",\n" +
        "   \"email\":\"john.doe@sta.sa\",\n" +
        "   \"travelPartner\":\"\"\n" +
        "}");

    final var loyaltyResponse = new ResponseMessage(HttpStatus.SC_OK, "{\n" +
        "   \"status\":\"succes\"\n" +
        "}");

    //Act
    try (MockedStatic<SSIDRestHelper> mockedStatic = mockStatic(SSIDRestHelper.class)) {
      mockedStatic.when(() -> SSIDRestHelper.executeMethodGet(anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(userInfoResponse);
      mockedStatic.when(() -> SSIDRestHelper.executeMethodPatch(anyString(), any(HttpEntity.class), anyString(), anyBoolean())).thenReturn(loyaltyResponse);
      result = service.enrollToLoyaltyProgram(StringUtils.EMPTY);
    }


    //Assert
    assertNotNull(result);
    assertEquals(LoyaltyEnrollmentStatus.SUCCESSFULLY_ENROLLED, result.getStatus());
    assertNull(result.getMessage());
  }

  @Test
  void enrollToLoyaltyProgramShouldHandleUserWithNoLoyaltyIDResponseKOFromSSID() throws IOException {
    //Arrange
    LoyaltyEnrollmentResponse result = null;
    final var userInfoResponse = new ResponseMessage(200, "{\n" +
        "   \"loyaltyId\":\"\",\n" +
        "   \"lastName\":\"DOE\",\n" +
        "   \"gender\":\"\",\n" +
        "   \"mobileNumber\":\"\",\n" +
        "   \"pictureUrl\":\"\",\n" +
        "   \"birthDate\":\"\",\n" +
        "   \"isEmailVerified\":\"Y\",\n" +
        "   \"sid\":\"99560f7c-7c0d-4cbc-8c38-24e5c3022460\",\n" +
        "   \"firstName\":\"John\",\n" +
        "   \"countryCode\":\"\",\n" +
        "   \"name\":\"John DOE\",\n" +
        "   \"isMobileVerified\":\"N\",\n" +
        "   \"interests\":\"\",\n" +
        "   \"email\":\"john.doe@sta.sa\",\n" +
        "   \"travelPartner\":\"\"\n" +
        "}");

    final var loyaltyResponse = new ResponseMessage(500, "{\n" +
        "   \"status\":\"failure\"\n" +
        "}");

    //Act
    try (MockedStatic<SSIDRestHelper> mockedStatic = mockStatic(SSIDRestHelper.class)) {
      mockedStatic.when(() -> SSIDRestHelper.executeMethodGet(anyString(), anyString(), anyBoolean(), anyBoolean())).thenReturn(userInfoResponse);
      mockedStatic.when(() -> SSIDRestHelper.executeMethodPatch(anyString(), any(HttpEntity.class), anyString(), anyBoolean())).thenReturn(loyaltyResponse);
      result = service.enrollToLoyaltyProgram(StringUtils.EMPTY);
    }


    //Assert
    assertNotNull(result);
    assertEquals(LoyaltyEnrollmentStatus.UNSUCCESSFULLY_ENROLLED, result.getStatus());
    assertNull(result.getMessage());
  }
}
