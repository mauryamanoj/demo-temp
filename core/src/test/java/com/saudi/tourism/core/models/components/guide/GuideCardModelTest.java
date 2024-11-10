package com.saudi.tourism.core.models.components.guide;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class GuideCardModelTest {
    private static final String CONTENT_PATH = "/content/sauditourism/en";


    @BeforeEach
    public void setUp(AemContext context) {
        context.load().json("/components/guide-cards/content.json", CONTENT_PATH);
    }

    @Test
    public void testGuideCardsModel(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/guide_cards";
        GuideCards guideCards =
            context.currentResource( RESOURCE_PATH).adaptTo(GuideCards.class);
        assertEquals(2, guideCards.getCards().size());
      assertEquals("DEMO", guideCards.getTitle());
      assertEquals("/content/dam/sauditourism/app_apple.png", guideCards.getBackgroundImage());
      assertEquals("GuideCardsItem", guideCards.getType());
      assertEquals("guide_card", guideCards.getComponentId());
      testCardSlidesData(guideCards);
    }


    /**
     * Test card slide specific data.
     * @param guideCards GuideCards
     */
    void testCardSlidesData(GuideCards guideCards){
        for (Card card : guideCards.getCards()) {
            Assertions.assertNotNull(card.getImage());
          Assertions.assertNotNull(card.getCtaData().getCtaEventName());
          assertEquals("/content/sauditourism/en", card.getCardCtaLink());
          assertEquals("ctaEventName", card.getCtaData().getCtaEventName());
          Assertions.assertNotNull(card.getCtaData().getPageCategory());
          assertEquals("pageCategory", card.getCtaData().getPageCategory());
          Assertions.assertNotNull(card.getCtaData().getPageSubCategory());
          assertEquals("pageSubCategory", card.getCtaData().getPageSubCategory());
          Assertions.assertNotNull(card.getCtaData().getSectionName());
          assertEquals("sectionName", card.getCtaData().getSectionName());
          Assertions.assertNotNull(card.getCardCtaLabel());
            Assertions.assertNotNull(card.getCardCtaLink());
        }
    }
}
