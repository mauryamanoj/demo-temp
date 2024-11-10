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
public class PageServletTest {

  private PageServlet servlet;

  @BeforeEach
  public void setup(AemContext context) {
    servlet = new PageServlet();
  }

  @Test
  public void testValidPath(AemContext context) throws  IOException {

    context.load().json("/servlets/app/content-page.json",
        "/content/sauditourism/app/en/content-page");
    context.currentResource("/content/sauditourism/app/en/content-page");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/content-page");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

  @Test
  public void tesInvalidPath(AemContext context) throws IOException {
    context.load().json("/servlets/app/content-page.json", "/temp");
    context.currentResource("/temp");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/location-page");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

}
