package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class DynamicMediaMapperModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/see";
  @Mock SaudiTourismConfigs saudiTourismConfigs;
  @Mock SlingSettingsService settingsService;
  private DynamicMediaMapperModel model = new DynamicMediaMapperModel();

  @BeforeEach public void setUp(AemContext context) {
    MockitoAnnotations.initMocks(this);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(SlingSettingsService.class, settingsService);
    context.load().json("/components/dynamic-media/page-content.json", CONTENT_PATH);
    context.load().json("/components/dynamic-media/image-profile.json",
        "/conf/global/settings/dam/adminui-extension/imageprofile");
    String RESOURCE_PATH =
        "/content/sauditourism/en/see/jcr:content/root/responsivegrid/traveller_quotes";
    context.currentResource(RESOURCE_PATH);
    when(saudiTourismConfigs.getDynamicMediaProfile()).thenReturn("Test-IP");
    //    Utils.setInternalState(model, "saudiTourismConfig", saudiTourismConfigs);
    Utils.setInternalState(model, "settingsService", settingsService);
  }

  @Test void testContentImage(AemContext context) {
    DynamicMediaMapperModel model =
        getModelForTest(context, "/content/dam/sauditourism/media/test.jpg", "Large");
    assertNotNull(model);
    assertEquals("/content/dam/sauditourism/media/test.jpg", model.getSrc());
  }

  @Test void testS7Image(AemContext context) {
    DynamicMediaMapperModel model = getModelForTest(context,
        "http://s7g10.scene7.com/is/image/scth/woman-in-black-hijab-taking-selfie-4029925",
        "Large");
    assertNotNull(model);
    assertEquals("http://s7g10.scene7.com/is/image/scth/woman-in-black-hijab-taking-selfie"
        + "-4029925:Large?defaultImage=woman-in-black-hijab-taking-selfie-4029925", model.getSrc());
  }

  private DynamicMediaMapperModel getModelForTest(AemContext context, String imagePath,
      String profile) {
    context.request().setAttribute("imageSrc", imagePath);
    context.request().setAttribute("componentProfile", profile);
    context.request().setAttribute("enableImageSharpen", false);
    return context.request().adaptTo(DynamicMediaMapperModel.class);
  }

}