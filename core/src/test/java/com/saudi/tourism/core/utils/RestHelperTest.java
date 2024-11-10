package com.saudi.tourism.core.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for RestHelper.
 */
class RestHelperTest {

  private static final String UNKNOWN_HOST = "http://dummy-url/";

  @Test
  void getObjectMapper() {
    final ObjectMapper objectMapper = RestHelper.getObjectMapper();
    assertFalse(objectMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
    assertTrue(objectMapper.getDeserializationConfig()
        .isEnabled(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY));
  }

  @Disabled
  @Test
  void executeMethodGet_unknown_host() {
    assertThrows(UnknownHostException.class,
        () -> RestHelper.executeMethodGet(UNKNOWN_HOST, null, false));
  }

  @Disabled
  @Test
  void executeMethodPost_unknown_host() {
    assertThrows(UnknownHostException.class,
        () -> RestHelper.executeMethodPost(UNKNOWN_HOST, null, null, false));
  }

  @Disabled
  @Test
  void executeMethodPut_unknown_host() {
    assertThrows(UnknownHostException.class,
        () -> RestHelper.executeMethodPut(UNKNOWN_HOST, null, null, false));
  }
}
