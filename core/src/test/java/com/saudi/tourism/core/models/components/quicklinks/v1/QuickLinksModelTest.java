package com.saudi.tourism.core.models.components.quicklinks.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(AemContextExtension.class)
public class QuickLinksModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/quick-links";

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/c-01-quick-links/content.json", CONTENT_PATH);
  }

  @Test
  public void testContent(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/quick-links/jcr:content/root/responsivegrid/c01_quick_links";

    QuickLinksModel model = context.currentResource(RESOURCE_PATH).adaptTo(QuickLinksModel.class);

    assertEquals("My Title", model.getComponentTitle());
    assertEquals(2, model.getLinks().size());
    assertEquals("Google", model.getLinks().get(0).getCopy());
    assertEquals("https://www.google.com", model.getLinks().get(0).getUrl());
    assertTrue(model.getLinks().get(0).isTargetInNewWindow());
    assertEquals("h2", model.getWeight());
  }

}
