package com.saudi.tourism.core.models.app.page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class CardsTest {

  private static final String CONTENT_PATH = "/content/sauditourism/app/en";

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/pages/app-travelessentials-flydeal-page"
      + ".json", CONTENT_PATH);
  }
  @Test
  public void testTravelEssentialCards(AemContext aemContext) {
    String RESOURCE_PATH = "/content/sauditourism/app/en/jcr:content";

    AppHomepage homePage = aemContext.currentResource(RESOURCE_PATH).adaptTo(AppHomepage.class);
    assertEquals(1, homePage.getTravelEssentials().size());
    assertEquals("TravelEssential Title", homePage.getTravelEssentials().get(0).getTitle());
    assertEquals("travel essential card 1", homePage.getTravelEssentials().get(0).getDescription());
    assertEquals("external", homePage.getTravelEssentials().get(0).getLinkType());
    assertEquals("www.abc.cmo", homePage.getTravelEssentials().get(0).getLink());
  }

  @Test
  public void testFlyDealCards(AemContext aemContext) {
    String RESOURCE_PATH = "/content/sauditourism/app/en/jcr:content";

    AppHomepage homePage = aemContext.currentResource(RESOURCE_PATH).adaptTo(AppHomepage.class);
    assertEquals(1, homePage.getPartners().size());
    assertEquals("Flydeal Title", homePage.getPartners().get(0).getTitle());
    assertEquals("/con/mad/flydeal/bigPic.jpg", homePage.getPartners().get(0).getImagePath());
    assertEquals("click this", homePage.getPartners().get(0).getCtaText());
    assertEquals("/con/mad/icon.png", homePage.getPartners().get(0).getIconPath());
  }
}
