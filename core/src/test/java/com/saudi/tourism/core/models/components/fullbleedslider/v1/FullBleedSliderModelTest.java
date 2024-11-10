package com.saudi.tourism.core.models.components.fullbleedslider.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class FullBleedSliderModelTest {


  private static final String CONTENT_PATH = "/content/sauditourism/en/full-bleed-slider";


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/c-04-full-bleed-slider/content.json", CONTENT_PATH);
  }

  @Test
  public void testSimpleSliderModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/full-bleed-slider/jcr:content/root/responsivegrid/c04_full_bleed_slide";

    FullBleedSliderModel model =
        context.currentResource(RESOURCE_PATH).adaptTo(FullBleedSliderModel.class);

    assertEquals(1, model.getSlides().size());
    assertEquals("Top Text Header", model.getComponentHeading().getHeading().getText());

    assertEquals("Link Header", model.getComponentHeading().getLink().getCopy());
    assertEquals("/content/sauditourism/en.html",
        model.getComponentHeading().getLink().getUrlWithExtension());
    assertEquals("/content/sauditourism/en", model.getComponentHeading().getLink().getUrl());
    assertEquals(true, model.getComponentHeading().getLink().isTargetInNewWindow());

    //slide info
    assertEquals("Slide", model.getSlides().get(0).getTitle());
    assertEquals("subtitle", model.getSlides().get(0).getSubtitle());
    assertEquals("description", model.getSlides().get(0).getDescription());

    //slide link
    assertEquals("Link", model.getSlides().get(0).getLink().getCopy());
    assertEquals("/content/sauditourism/en", model.getSlides().get(0).getLink().getUrl());
    assertEquals(false, model.getSlides().get(0).getLink().isTargetInNewWindow());

    //slide image
    assertEquals("alt", model.getSlides().get(0).getImage().getAlt());
    assertEquals("/content/dam/sauditourism/card-01.jpg",
        model.getSlides().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/card-mobile-02.jpg",
        model.getSlides().get(0).getImage().getMobileImageReference());


  }


}
