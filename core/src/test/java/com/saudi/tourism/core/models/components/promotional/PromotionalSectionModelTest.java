package com.saudi.tourism.core.models.components.promotional;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Image;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PromotionalSectionModelTest {
  private final Gson gson = new GsonBuilder().create();

  private final AemContext aemContext = new AemContext();
  private static final String CONTENT_PATH = "/content/sauditourism/en/activity";

  @Mock
  private SlingSettingsService settingsService;


  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SlingSettingsService.class, settingsService);

    aemContext.addModelsForClasses(PromotionalSectionModel.class, Image.class);
    aemContext.load().json("/components/promotional-section/content.json", CONTENT_PATH);
  }

  @Test
  public void shouldReturnHeaderConfiguration() {
    //Arrange
    String RESOURCE_PATH = "/content/sauditourism/en/activity/jcr:content/root/responsivegrid"
      + "/promotional_section";

    //PromotionalSection
    final PromotionalSectionModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(PromotionalSectionModel.class);
    final String json = model.getJson();

    //Assert
    final PromotionalSectionModel data = gson.fromJson(json, PromotionalSectionModel.class);
    assertEquals("DEMO", data.getTitle());
    assertEquals("Sub Title", data.getSubTitle());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg:crop-275x275?defaultImage=placeholder.jpg", data.getImage().getDesktopImage());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg:crop-275x275?defaultImage=placeholder.jpg", data.getImage().getMobileImage());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getImage().getFileReference());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg", data.getImage().getS7fileReference());
    assertEquals("Ashar Resort", data.getImage().getAlt());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getImage().getMobileImageReference());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg", data.getImage().getS7mobileImageReference());
    assertEquals("Cta Label", data.getCtaLabel());
    assertEquals("/content/sauditourism/en", data.getCtaLink());
    assertEquals("cta event page category", data.getCtaData().getPageCategory());
    assertEquals("cta event page sub category", data.getCtaData().getPageSubCategory());
    assertEquals("cta event section name", data.getCtaData().getSectionName());
    assertEquals("cta event name", data.getCtaData().getCtaEventName());
  }
}