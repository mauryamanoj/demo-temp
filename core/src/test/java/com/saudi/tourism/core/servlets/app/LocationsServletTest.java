package com.saudi.tourism.core.servlets.app;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

@ExtendWith(AemContextExtension.class)
public class LocationsServletTest {

  private LocationsServlet servlet;

  @BeforeEach
  public void setup(AemContext context) {
    servlet = new LocationsServlet();

  }

  @Test
  public void testValidPath(AemContext context) throws  IOException {

    context.load().json("/servlets/app/en.json", "/content/sauditourism/app/en");
    context.currentResource("/content/sauditourism/app/en");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/en");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

  @Test
  public void testInvalidLocale(AemContext context) throws  IOException {

    context.load().json("/servlets/app/en.json", "/content/sauditourism/app/en");
    context.currentResource("/content/sauditourism/app/en");
    MockSlingHttpServletRequest request = context.request();

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

}
