package com.saudi.tourism.core.models.components.eventpagehero.v1;


import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class EventPageHeroModelTest {
  private static final String CONTENT_PATH =
      "/content/sauditourism/en/temp/event-page-hero";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/h-04-event-page-hero/content.json", CONTENT_PATH);
  }

  @Test public void testContent(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/temp/event-page-hero/jcr:content/root/responsivegrid/h04_event_page_hero";

    EventPageHeroModel model =
        context.currentResource(RESOURCE_PATH).adaptTo(EventPageHeroModel.class);

    Assertions.assertEquals(3, model.getEvents().size());

  }
}
