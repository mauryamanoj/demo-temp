package com.saudi.tourism.core.models.components.articlecard.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.ImageCaption;
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
class ArticleCardModelTest {
  private final Gson gson = new GsonBuilder().create();

  private final AemContext aemContext = new AemContext();

  @Mock
  private SlingSettingsService settingsService;


  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SlingSettingsService.class, settingsService);

    aemContext.addModelsForClasses(ArticleCardModel.class, Image.class, ImageCaption.class);
    aemContext.load().json("/components/c13-article-card/article-card.json", "/article-card");
  }

  @Test
  public void shouldReturnHeaderConfiguration() {
    //Arrange
    aemContext.currentResource("/article-card");

    //Act
    final ArticleCardModel model = aemContext.request().getResource().adaptTo(ArticleCardModel.class);
    final String json = model.getJson();

    //Assert
    final ArticleCardModel data = gson.fromJson(json, ArticleCardModel.class);
    assertEquals(null, data.getTitle());
    assertEquals("h3", data.getTitleWeight());
    assertEquals("<h5>Ashar Resort</h5>\r\n" +
      "<p>You’ll find high-end villas, gourmet restaurants and a spa at the Ashar Resort<i> - </i>opening mid 2021. </p>\r\n", data.getDescription());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg:crop-1160x650?defaultImage=placeholder.jpg", data.getImage().getDesktopImage());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg:crop-375x280?defaultImage=placeholder.jpg", data.getImage().getMobileImage());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getImage().getFileReference());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg", data.getImage().getS7fileReference());
    assertEquals("Ashar Resort", data.getImage().getAlt());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getImage().getMobileImageReference());
    assertEquals("https://scene7.adobe.com/sauditourism/placeholder.jpg", data.getImage().getS7mobileImageReference());
    assertEquals("Caption Title", data.getCaption().getCopy());
    assertEquals("internal", data.getCaption().getLinkType());
  }
}