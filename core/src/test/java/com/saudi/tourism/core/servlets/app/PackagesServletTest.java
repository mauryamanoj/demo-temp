package com.saudi.tourism.core.servlets.app;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import io.wcm.testing.mock.aem.junit5.JcrOakAemContext;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.servlet.MockRequestPathInfo;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class PackagesServletTest {
  private PackagesServlet servlet;
  private ResourceBundleProvider i18nProvider;
  private ResourceBundle i18nBundle;
  private UserService userService;

  @BeforeEach public void setup(final AemContext context) {
    servlet = new PackagesServlet();
    context.load().json("/servlets/app/en.json", "/content/sauditourism/app/en");
    context.currentResource("/content/sauditourism/app/en");

    i18nProvider = mock(ResourceBundleProvider.class);
    i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    doReturn(i18nBundle).when(i18nProvider).getResourceBundle(any());
    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");

    userService = mock(UserService.class);
    ResourceResolver mockResourceResolver = mock(ResourceResolver.class);
    TagManager tagManager = mock(TagManager.class);
    Tag tag = mock(Tag.class);
    when(tag.getTitle(any())).thenReturn("tag title");
    when(tagManager.resolve(any())).thenReturn(tag);
    when(mockResourceResolver.adaptTo(TagManager.class)).thenReturn(tagManager);

    when(userService.getResourceResolver()).thenReturn(mockResourceResolver);

    context.registerService(UserService.class, userService);

  }

  @Test public void testValidPath(final AemContext context) throws IOException {
    MockSlingHttpServletRequest request = context.request();
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/en");

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
  }

  @Test public void testInvalidLocale(final AemContext context) throws IOException {
    MockSlingHttpServletRequest request = context.request();

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
  }

  @Test public void testException(final AemContext context) throws IOException {
    MockSlingHttpServletRequest request = Mockito.spy(context.request());
    MockRequestPathInfo requestPathInfo = (MockRequestPathInfo) request.getRequestPathInfo();
    requestPathInfo.setSuffix("/en");

    Mockito.when(request.getResourceResolver()).thenThrow(new RuntimeException());

    servlet.doGet(request, context.response());
    Assertions.assertNotNull(context.response().getOutputAsString());
  }
}
