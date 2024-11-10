package com.saudi.tourism.core.models.components.textimage.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.RunModeService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class TextImageModelTest {

  @Mock
  private RunModeService runModeService;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(TextImageModel.class);
    context.registerService(RunModeService.class, runModeService);
  }

  @Test
  public void testTextAndImageWorkingAsExpected(final AemContext context) {
    //Arrange
    context
      .load()
      .json(
        "/components/text-image/text-image.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/text_image");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/text_image")
        .adaptTo(TextImageModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, TextImageModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Test title", data.getTitle());
    assertTrue(data.getWhiteBackground());
    assertTrue(data.getSkipMargin());
    assertEquals("<p>This is a small description</p>", data.getDescription());
    assertEquals("left", data.getPosition());
    assertEquals("text_image", data.getComponentId());
    assertEquals("View more", data.getLink().getText());
    assertEquals("/content/sauditourism/en/live-the-moment", data.getLink().getUrl());
    assertEquals(true, data.getLink().isTargetInNewWindow());
    assertEquals("/content/dam/sauditourism/red-sea.jpeg", data.getImage().getFileReference());
    assertEquals("Alt test", data.getImage().getAlt());
    assertEquals("/content/dam/sauditourism/healthcare ministry.png", data.getLogo().getFileReference());
    assertEquals("/content/dam/sauditourism/healthcare ministry.png", data.getLogo().getS7fileReference());
    assertEquals("Alt logo test", data.getLogo().getAlt());
    assertEquals("cta event page category", data.getCtaData().getPageCategory());
    assertEquals("cta event page sub category", data.getCtaData().getPageSubCategory());
    assertEquals("cta event section name", data.getCtaData().getSectionName());
    assertEquals("cta event name", data.getCtaData().getCtaEventName());
  }
}
