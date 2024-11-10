package com.saudi.tourism.core.models.components.footnote.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class FootnoteTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/footnote";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/c98-footnote/content.json", CONTENT_PATH);
  }

  @Test public void testFooterConfig(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/footnote/jcr:content/root/responsivegrid/c98_footnote";
    FootnoteModel footnoteModel = context.currentResource(RESOURCE_PATH).adaptTo(FootnoteModel.class);
    Assertions.assertEquals("Sample title", footnoteModel.getHeading());
    Assertions.assertEquals("Link", footnoteModel.getLink().getCopy());
    Assertions.assertEquals("#", footnoteModel.getLink().getUrl());
  }

}
