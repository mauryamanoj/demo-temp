package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.app.i18n.I18nDetail;
import com.saudi.tourism.core.models.app.i18n.I18nObject;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(AemContextExtension.class)
public class I18nServletTest {

  private I18nServlet servlet;

  @BeforeEach
  public void setup(AemContext context) {
    context.load().json("/servlets/app/i18n.json", "/apps/sauditourism/i18n/en");
    context.currentResource("/apps/sauditourism/i18n/en");
    servlet = new I18nServlet();
  }

  @Test
  public void testI18N(AemContext context) throws IOException {

    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("en");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

    List<I18nDetail> resultList = new ArrayList<>();
    resultList.add(new I18nDetail("key", "value"));
    I18nObject result = new I18nObject("en", resultList);
    Assertions.assertNotNull(result);

  }

  @Test
  public void testI18NNoLocale(AemContext context) throws IOException {

    MockSlingHttpServletRequest request = context.request();
    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }
}
