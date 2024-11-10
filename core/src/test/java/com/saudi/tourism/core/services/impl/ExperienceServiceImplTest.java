package com.saudi.tourism.core.services.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.RestHelper;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ExperienceServiceImplTest {

  @Mock private SaudiTourismConfigs saudiTourismConfig;

  private ExperienceServiceImpl experienceServiceImpl;

  private String experiencesAsString;

  @BeforeEach
  void setUp(final AemContext aemContext) throws IOException {
    experienceServiceImpl = new ExperienceServiceImpl();

    Map<String, Object> props = new HashMap<>();
    props.put("immediate", "true");
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
    aemContext.registerInjectActivateService(experienceServiceImpl, props);

    final Path path =
        Paths.get(
            ExperienceServiceImplTest.class
                .getResource("/services/experiences-service/experiences.json")
                .getPath());
    experiencesAsString = new String(Files.readAllBytes(path), "UTF-8");
  }

  @Test
  void getExperiencesDetailsShouldCallHalayallaApiWithTheRightParams() throws IOException {
    // Mock scope
    try (MockedStatic<RestHelper> restHelper = mockStatic(RestHelper.class)) {
      // Arrange
      restHelper
          .when(
              () ->
                  RestHelper.executeMethodPost(
                      anyString(), anyString(), nullable(String.class), anyMap()))
          .thenReturn(new ResponseMessage(200, experiencesAsString));
      when(saudiTourismConfig.getHalayallaEndPointUrl())
          .thenReturn("https://vs-test.halayalla.rocks");
      when(saudiTourismConfig.getHalayallaToken())
          .thenReturn("ngkQU1y1YqCUfWM5tFF9xttXErsz48GLFZ7mNZcBss5HUBG0i5KSPdfRURj1vjFq");

      final Map<String, Object> queryStrings = new HashMap<>();
      queryStrings.put("lang", "en");

      // Act
      final JsonObject experiences =
          (JsonObject)
              experienceServiceImpl.getExperiencesDetails(
                  queryStrings,
                  Arrays.asList(
                      "60dafb83ae9265354b6eb208",
                      "60e078fa118cbe2add34ed97",
                      "60daecc729b46a18534e331f",
                      "60d5f855e00a8169e523780d",
                      "60cf8b1418d74a2d1f05668a",
                      "62b914f976b72f597c58167b",
                      "62b1e37fef9fed368b2ae155",
                      "612b8cfcd49d62377104a34a"));

      // Assert
      final JsonArray data = experiences.getAsJsonArray("data");
      assertTrue(data != null);
      assertEquals(8, data.size());
    }
  }
}
