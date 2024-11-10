package com.saudi.tourism.core.models.components.bannerslider.v1;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class BannerSliderModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/banner-slider.v1/content.json", CONTENT_PATH);
  }

  @Test
  public void testCategoriesCardsModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/banner-slider";
    BannerSliderModel banner =
      context.currentResource( RESOURCE_PATH).adaptTo(BannerSliderModel.class);
    assertEquals(1, banner.getCards().size());
    testCardSlidesData(banner);
  }


  /**
   * Test card slide specific data.
   * @param banner Banner
   */
  void testCardSlidesData(BannerSliderModel banner) {
    for (Card card : banner.getCards()) {
      Assertions.assertNotNull(card.getImage());
      assertEquals("/content/dam/sauditourism/app_apple.png", card.getImage().getS7fileReference());
      Assertions.assertNotNull(card.getTitle());
      assertEquals("TITLE", card.getTitle());
      Assertions.assertNotNull(card.getTabTitle());
      assertEquals("Sub TITLE", card.getTabTitle());
      assertEquals("DESCRIPTION", card.getDescription());
      Assertions.assertNotNull(card.getLink());
      assertEquals("/content/sauditourism/app1", card.getLink().getUrl());
      assertEquals("APP", card.getLink().getCopy());
      assertEquals(true, card.getLink().isTargetInNewWindow());
      assertEquals("{\"event\":\"EVNT OK\",\"linkURL\":\"https://www.visitsaudi.com/app1\",\"linkTitle\":\"APP\",\"vendorName\":null,\"packageName\":\"\"}", card.getLink().getAppEventData());
      assertEquals("https://scth.scene7.com/is/image/scth/video.mp4", card.getVideo().getS7videoFileReference());
      assertEquals("/content/dam/sauditourism/video.mp4", card.getVideo().getVideoFileReference());
      assertEquals(true, card.getVideo().isAutorerun());
      assertEquals(true, card.getVideo().isAutoplay());
      assertEquals("cta event page category", card.getCtaData().getPageCategory());
      assertEquals("cta event page sub category", card.getCtaData().getPageSubCategory());
      assertEquals("cta event section name", card.getCtaData().getSectionName());
      assertEquals("cta event name", card.getCtaData().getCtaEventName());
    }
  }
}
