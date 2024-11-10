package com.saudi.tourism.core.servlets.app;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;

import static com.saudi.tourism.core.utils.Constants.PN_IMAGE;
import static com.saudi.tourism.core.utils.Constants.PN_TITLE;

@ExtendWith(AemContextExtension.class) public class LegalPageServletTest {

  private LegalPageServlet servlet;

  @BeforeEach public void setup(AemContext context) {
    servlet = new LegalPageServlet();
  }

  @Test public void testValidPath(AemContext context) throws IOException {

    context.load()
        .json("/servlets/app/legal-page.json", "/content/sauditourism/app/en/legal-info-page");
    context.currentResource("/content/sauditourism/app/en/legal-info-page");
    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/legal-info-page");
    servlet.doGet(request, context.response());
    String expectedResp =
        "{\"title\":\"legal-info-page\",\"description\":\"legal info page description\","
            + "\"image\":\"/content/sauditourism/app/en/legal-info-page.jpg\","
            + "\"imageCaption\":{\"linkType\":null,\"copy\":\"Legal info image caption\","
            + "\"link\":\"http://legal.info\"},"
            + "\"texts\":[{\"text\":\"text 1\",\"description\":null,\"title\":\"title 1\"},{\"text\":\"text 2\",\"description\":null,\"title\":\"title 2\"},{\"text\":\"text 3\",\"description\":null,\"title\":\"title 3\"}],\"search\":null,\"favId\":null}";
    testJsonData(expectedResp);
    Assertions.assertEquals(expectedResp, context.response().getOutputAsString());
  }

  void testJsonData(String expectedResp) {
    JsonParser parser = new JsonParser();
    JsonElement jsonTree = parser.parse(expectedResp);
    if (jsonTree.isJsonObject()) {
      JsonObject jsonObject = jsonTree.getAsJsonObject();
      Assertions.assertEquals("legal-info-page", jsonObject.get(PN_TITLE).getAsString());
      Assertions
          .assertEquals("legal info page description", jsonObject.get("description").getAsString());
      Assertions.assertEquals("/content/sauditourism/app/en/legal-info-page.jpg",
          jsonObject.get(PN_IMAGE).getAsString());
      JsonArray texts = jsonObject.getAsJsonArray("texts");
      for (JsonElement jsonElement : texts) {
        Assertions.assertNotNull(jsonElement.getAsJsonObject().get("text").getAsString());
        Assertions.assertNotNull(jsonElement.getAsJsonObject().get(PN_TITLE).getAsString());
      }

    }
  }

  @Test public void tesInvalidPath(AemContext context) throws IOException {
    context.load().json("/servlets/app/legal-page.json", "/temp");
    context.currentResource("/temp");
    MockSlingHttpServletRequest request = context.request();

    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/content/sauditourism/app/en/legal-info-page");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());

  }

}
