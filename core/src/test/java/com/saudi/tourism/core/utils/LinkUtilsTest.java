package com.saudi.tourism.core.utils;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.jcr.RepositoryException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * Unit Test Class to test link utils.
 */
@ExtendWith(AemContextExtension.class)
public class LinkUtilsTest {

  private ResourceResolver resourceResolver;

  @BeforeEach
  public void setUp(AemContext context) throws RepositoryException {
    resourceResolver = spy(context.resourceResolver());
  }


  private static final String TEST_PATH = "/content/sauditourism/en/test";

  private static final String EXTENSION_TEST_PATH = TEST_PATH + Constants.HTML_EXTENSION;

  @Test
  public void testIsInternalLinkWithNullPath() {
    Assertions.assertFalse(LinkUtils.isInternalLink(null));
  }

  @Test public void testIsInternalLinkWithEmptyPath() {
    Assertions.assertFalse(LinkUtils.isInternalLink(""));
  }

  @Test public void testIsInternalLinkWithPageUrl() {
    Assertions.assertFalse(LinkUtils.isInternalLink(EXTENSION_TEST_PATH));
  }
  @Test public void testIsInternalLinkWithPageUrlHtml() {
    Assertions.assertFalse(LinkUtils.isInternalLink("/content/sauditourism/en.html"));
  }

  @Test public void testIsInternalLinkWithPagePath() {
    Assertions.assertTrue(LinkUtils.isInternalLink(TEST_PATH));
  }

  @Test public void testIsInternalLinkWithExternalUrl() {
    Assertions.assertFalse(LinkUtils.isInternalLink("https://www.dummy-url.com"));
  }

  @Test public void testGetExtensionUrlWithNull() {
    Assertions.assertNull(LinkUtils.getUrlWithExtension(null));
  }

  @Test public void testGetExtensionUrlWithEmptyUrl() {
    Assertions.assertEquals("", LinkUtils.getUrlWithExtension(""));
  }

  @Test public void testGetExtensionUrlWithPagePath() {
    Assertions.assertEquals(EXTENSION_TEST_PATH, LinkUtils.getUrlWithExtension(TEST_PATH));
  }

  @Test public void testGetExtensionUrlWithPageUrl() {
    Assertions.assertEquals(EXTENSION_TEST_PATH, LinkUtils.getUrlWithExtension(EXTENSION_TEST_PATH));
  }

  @Test public void testGetExtensionUrlWithExternalUrl() {
    Assertions.assertEquals("https://www.dummy-url.com",
        LinkUtils.getUrlWithExtension("https://www.dummy-url.com"));
  }

  @Test public void transformUrl() {
    when(resourceResolver.map("/content/sauditourism/en/do")).thenReturn("/en/do");
    Assertions.assertEquals("/en/do", LinkUtils.transformUrl("/content/sauditourism/en/do.html",
        true));

    Assertions.assertEquals("/en/do",LinkUtils.getAuthorPublishUrl(resourceResolver, "/content/sauditourism/en/do",
        "true"));
  }
}
