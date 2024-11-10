package com.saudi.tourism.core.models.components.hero.v1;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.impl.SaudiTourismConfigImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class SecondaryHeroTest {
  private static final String CONTENT_PATH =
      "/content/sauditourism/en/homepage-usa/first-level/secondary-hero/";

  @Mock
  private SaudiTourismConfigs saudiTourismConfig;

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/h-02-secondary-hero/content.json", CONTENT_PATH);

    context.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
  }

  @Test public void testContent(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/homepage-usa/first-level/secondary-hero/jcr:content/root/responsivegrid/h02_secondary_hero";

    CommonHeroModel model = context.currentResource(RESOURCE_PATH).adaptTo(CommonHeroModel.class);

    assertEquals("Title", model.getTitle());
    assertEquals("Subtitle", model.getSubtitle());
    assertEquals("/content/dam/sauditourism/full-bleed-slider/bleed-slide-02.jpg",
        model.getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/card-01.jpg",
        model.getImage().getMobileImageReference());
    assertEquals("alt", model.getImage().getAlt());
    assertEquals("/content/dam/video.mp4", model.getVideoPath());

  }
}
