package com.saudi.tourism.core.models.common;

import com.day.cq.commons.Externalizer;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import pl.pojo.tester.internal.utils.CollectionUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;

@ExtendWith(AemContextExtension.class)
class SmartCropsInfoModelTest {

  private String CONTENT_PATH = "/content/sauditourism/en/see";
  private String RESOURCE_PATH =
      CONTENT_PATH + "/jcr:content/root/responsivegrid/h02_secondary_hero";
  private String DAM_IMAGE_PATH = "/content/dam/sauditourism/media/test.jpg";
  private String S7_IMAGE_PATH =
      "http://s7g10.scene7.com/is/image/scth/woman-in-black-hijab-taking-selfie";

  @BeforeEach
  public void setUp(AemContext context) {
    MockitoAnnotations.initMocks(this);
    final SlingSettingsService settingsService = Mockito.mock(SlingSettingsService.class);
    context.registerService(SlingSettingsService.class, settingsService);
    doReturn(CollectionUtils.asSet(Externalizer.PUBLISH)).when(settingsService).getRunModes();
  }

  @Test
  void testDefaultImageSrcForDam(AemContext context) {
    SmartCropsInfoModel model = getModelForTest(context, DAM_IMAGE_PATH);
    assertNotNull(model);
    assertEquals(null, model.getDefImgSrc());
  }

  @Test
  void testDefaultImageSrcForS7(AemContext context) {
    SmartCropsInfoModel model = getModelForTest(context, S7_IMAGE_PATH);
    assertNotNull(model);
    assertEquals(null, model.getDefImgSrc());
  }

  private SmartCropsInfoModel getModelForTest(AemContext context, String s7ImageSrc) {
    context.request().setAttribute("imageSrc", s7ImageSrc);
    context.requestPathInfo().setResourcePath(RESOURCE_PATH);
    context.runMode("author");
    return context.request().adaptTo(SmartCropsInfoModel.class);
  }
}