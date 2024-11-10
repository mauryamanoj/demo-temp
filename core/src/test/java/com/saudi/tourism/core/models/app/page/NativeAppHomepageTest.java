package com.saudi.tourism.core.models.app.page;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class NativeAppHomepageTest {

  private static final String CONTENT_PATH = "/content/sauditourism/app1/en";

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/pages/app-explore-deals-page"
      + ".json", CONTENT_PATH);
  }
  @Test
  public void testExploreDealsCards(AemContext aemContext) {
    String RESOURCE_PATH = "/content/sauditourism/app1/en/jcr:content";

    NativeAppHomepage homePage = aemContext.currentResource(RESOURCE_PATH).adaptTo(NativeAppHomepage.class);
    assertEquals(1, homePage.getExploreDeals().size());
    assertEquals("Explore Deals Title", homePage.getExploreDeals().get(0).getTitle());
    assertEquals("Explore Deals Description", homePage.getExploreDeals().get(0).getDescription());
    assertEquals("internal", homePage.getExploreDeals().get(0).getLinkType());
    assertEquals("www.abc.cmo", homePage.getExploreDeals().get(0).getLink());
    assertEquals("Explore deals CTA text", homePage.getExploreDeals().get(0).getCtaText());
  }

}
