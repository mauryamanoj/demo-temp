package com.sta.core.sitemap;

import com.sta.core.sitemap.model.SitemapEntry;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.LoginException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SitemapServiceImplTest {

  @Mock
  private SitemapServiceImpl.Configuration configuration;

  private SitemapServiceImpl service = new SitemapServiceImpl();

  @BeforeEach
  void setUp(final AemContext context) throws LoginException {
    when(configuration.rootPath()).thenReturn("/content/sauditourism");
    when(configuration.includeTemplates()).thenReturn(
      new String[] {"/conf/sauditourism/settings/wcm/templates/content-page",
        "/conf/sauditourism/settings/wcm/templates/event-details"});
    when(configuration.excludePaths()).thenReturn(
      new String[] {"/content/sauditourism/en/Configs", "/content/sauditourism/es/Configs",
        "/content/sauditourism/ru/Configs",
        "/content/sauditourism/ja/Configs", "/content/sauditourism/de/Configs", "/content/sauditourism/fr/Configs",
        "/content/sauditourism/ar/Configs", "/content/sauditourism/zh/Configs"});

    context.load().json("/sitemap/en.json", "/content/sauditourism/en");
    context.load().json("/sitemap/seasons.json", "/content/sauditourism/en/seasons");
    context.load().json("/sitemap/configs.json", "/content/sauditourism/en/Configs");
    context.load().json("/sitemap/trip-plans.json", "/content/sauditourism/en/Configs/trip-plans");

    context.registerService(SitemapServiceImpl.Configuration.class, configuration);
    context.registerInjectActivateService(service);
    service.activate(configuration);
  }

  @Test
  public void shouldExcludePagesUnderExcludedPaths(final AemContext context) throws ServiceUserException {
    //Arrange
    final SitemapWriter writer = new SitemapWriter("/content/sauditourism/en", context.resourceResolver());
    final var page = context.pageManager().getPage("/content/sauditourism/en");

    //Act
    final var entries = service.createEntries(page, writer, context.resourceResolver());

    //Assert
    assertNotNull(entries);
    assertEquals(1, entries.size());

    final var sitemapEntry = (SitemapEntry) entries.get(0);
    assertEquals("/content/sauditourism/en/seasons", sitemapEntry.getLoc());
  }
}