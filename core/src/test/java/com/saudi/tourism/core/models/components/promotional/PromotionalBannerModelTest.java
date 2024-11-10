package com.saudi.tourism.core.models.components.promotional;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class PromotionalBannerModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/promotional-section/content-promotion-banner.json", CONTENT_PATH);
  }

  @Test
  public void testCategoriesCardsModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/promotional_banner";
    PromotionalBannerModel banner =
      context.currentResource( RESOURCE_PATH).adaptTo(PromotionalBannerModel.class);
    assertEquals(2, banner.getCards().size());
    assertEquals(true,banner.getHideImageBrush());
    assertEquals(false,banner.isNotOnTop());
    assertEquals("large",banner.getHeight());
    testCardSlidesData(banner);
  }


  /**
   * Test PromotionalBanner specific data.
   * @param banner PromotionalBannerModel
   */
  void testCardSlidesData(PromotionalBannerModel banner) {
    int i = 0;
    for (PromotionalSectionBannerModel card : banner.getCards()) {
      Assertions.assertNotNull(card.getImage());
      assertEquals("/content/dam/static-images/resources/flags/png/051-cayman-islands.png", card.getImage().getS7fileReference());
      assertEquals("/content/dam/static-images/resources/flags/png/051-cayman-islands-thumbnail.png", card.getThumbnail().getS7fileReference());
      Assertions.assertNotNull(card.getTitle());
      assertEquals("TEST", card.getTitle());
      Assertions.assertNotNull(card.getSubTitle());
      Assertions.assertNotNull(card.getLink());
      assertEquals("/content/sauditourism/dmeo", card.getLink().getUrl());
      assertEquals("https://scth.scene7.com/is/image/scth/video.mp4", card.getVideo().getS7videoFileReference());
      assertEquals("/content/dam/sauditourism/video.mp4", card.getVideo().getVideoFileReference());
      assertTrue(card.getVideo().isAutoplay());
      assertTrue(card.getVideo().isAutorerun());
      assertEquals("CTA Label", card.getLink().getCopy());
      assertEquals("cta event page category", card.getCtaData().getPageCategory());
      assertEquals("cta event page sub category", card.getCtaData().getPageSubCategory());
      assertEquals("cta event section name", card.getCtaData().getSectionName());
      assertEquals("cta event name", card.getCtaData().getCtaEventName());
      i++;
    }
  }
}
