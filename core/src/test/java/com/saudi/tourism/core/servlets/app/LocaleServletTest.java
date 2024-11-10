package com.saudi.tourism.core.servlets.app;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(AemContextExtension.class)
public class LocaleServletTest {

  private LocaleServlet servlet;

  @BeforeEach
  public void setup(AemContext context) {
    context.load().json("/servlets/app/contact.json", "/content/sauditourism/app");
    context.currentResource("/content/sauditourism/app");
    servlet = new LocaleServlet();
  }

  @Test
  public void testLocales(AemContext context) throws IOException {

    MockSlingHttpServletRequest request = context.request();
    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }
}
