package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
class DynamicImageRefServletTest {

  private DynamicImageRefServlet servlet;

  private SaudiTourismConfigs saudiTourismConfig;

  @BeforeEach
  public void setup(AemContext context) {
    servlet = new DynamicImageRefServlet();

    saudiTourismConfig = Mockito.mock(SaudiTourismConfigs.class);
    when(saudiTourismConfig.getScene7Domain()).thenReturn(Constants.SCENE7_DOMAIN);
    Utils.setInternalState(servlet, "saudiTourismConfig", saudiTourismConfig);
  }

  @Test
  public void testS7PublishedImage(AemContext context) throws IOException {

    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String expectedOutput = "{\"s7ImageReference\":\"https://scth.scene7"
        + ".com/is/image/scth/woman-in-black-hijab-taking-selfie-4029925\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/dam/sauditourism/media/s7image.jpg");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
  }

  @Test
  public void testS7NonPublishedImage(AemContext context) throws IOException {

    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String unexpectedOutput = "{\"s7ImageReference\":\"https://scth.scene7"
        + ".com/is/image/scth/woman-in-black-hijab-taking-selfie-4029925\"}";
    String expectedOutput = "{\"s7ImageReference\":\"/content/dam/sauditourism/media/s7image-non-published.jpg\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/dam/sauditourism/media/s7image-non-published.jpg");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    // as S7File status is PublishIncomplete, it should show dam image
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
    Assertions.assertNotEquals(unexpectedOutput, context.response().getOutputAsString());
  }

  @Test
  public void testNullImagePath(AemContext context) throws IOException {
    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String expectedOutput = "{\"s7ImageReference\":\"\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
  }

  @Test
  public void testUnAvailableImagePath(AemContext context) throws IOException {
    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String expectedOutput = "{\"s7ImageReference\":\"/content/dam/sauditourism/media/unavailable-image.jpg\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/dam/sauditourism/media/unavailable-image.jpg");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
  }

  @Test
  public void testS7WithoutDomainProp(AemContext context) throws IOException {
    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String expectedOutput = "{\"s7ImageReference\":\"/content/dam/sauditourism/media/s7image-wo-domain-property.jpg\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/dam/sauditourism/media/s7image-wo-domain-property.jpg");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
  }

  @Test
  public void testS7WithoutFileNameProp(AemContext context) throws IOException {
    context.load().json("/servlets/app/dam-content.json", "/content/dam/sauditourism/media");
    context.currentResource("/content/dam/sauditourism/media");
    String expectedOutput = "{\"s7ImageReference\":\"/content/dam/sauditourism/media/s7image-wo-file-property.jpg\"}";
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/dam/sauditourism/media/s7image-wo-file-property.jpg");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
    Assertions.assertEquals(expectedOutput, context.response().getOutputAsString());
  }

}
