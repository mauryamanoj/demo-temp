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
public class ContactServletTest {

  private ContactServlet servlet;

  @BeforeEach
  public void setup(AemContext context) {
    servlet = new ContactServlet();
  }

  @Test
  public void testWithLocale(AemContext context) throws  IOException {

    context.load().json("/servlets/app/contact.json", "/content/sauditourism/app/en/contact");
    context.currentResource("/content/sauditourism/app/en/contact");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("en");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

  @Test
  public void tesNoLocale(AemContext context) throws IOException {
    context.load().json("/servlets/app/contact.json", "/temp");
    context.currentResource("/temp");
    MockSlingHttpServletRequest request = context.request();

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }


  @Test
  public void tesInvalidModel(AemContext context) throws IOException {
    context.load().json("/servlets/app/contact.json", "/temp");
    context.currentResource("/temp");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("en");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }


}
