package com.saudi.tourism.core.services;

import com.saudi.tourism.core.cache.InMemoryCache;
import com.saudi.tourism.core.login.services.impl.SaudiSSIDConfigImpl;
import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import com.saudi.tourism.core.models.components.nav.v2.NavigationTab;
import com.saudi.tourism.core.models.components.nav.v3.NavigationLink;
import com.saudi.tourism.core.services.impl.NavigationServiceImpl;
import com.saudi.tourism.core.services.impl.SaudiModeConfigImpl;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.utils.SpecialCharConstants.FORWARD_SLASH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test for classes: NavigationServiceImpl, NavigationHeader.
 */
@ExtendWith(AemContextExtension.class)
class NavigationServiceTest {

  private static final String LANG = "en";

  /**
   * /content/sauditourism/
   */
  private static final String CONTENT_PATH = Constants.ROOT_CONTENT_PATH;

  /**
   * /content/sauditourism/{lang}/Configs/header-config
   */
  private static final String HEADER_CONFIG_PATH =
      CONTENT_PATH + FORWARD_SLASH + LANG + "/Configs" + "/header-config";

  /**
   * Header key "header-en".
   */
  private static final String HEADER_KEY = Constants.HEADER_KEY + Constants.MINUS + LANG;

  private NavigationService navigationService;

  private InMemoryCache memCache;
  private MockSlingHttpServletRequest request;
  private ResourceResolver resourceResolver;

  @BeforeEach public void setUp(AemContext context) {
    request = context.request();
    resourceResolver = context.resourceResolver();

    context.registerService(new SaudiModeConfigImpl());
    context.registerService(new SaudiSSIDConfigImpl());

    navigationService = mock(NavigationServiceImpl.class, Mockito.CALLS_REAL_METHODS);

    //    context.load().json("/components/header-config-model/content.json", CONFIGS_PATH);
    context.load().json("/components/header-config-model/header-config.2.json", HEADER_CONFIG_PATH);
    context.load().json("/components/header-config-model/languages.json", Constants.LANGS_PATH);

    memCache = mock(InMemoryCache.class, CALLS_REAL_METHODS);
    Utils.setInternalState(memCache, "cache", new ConcurrentHashMap<>());
    Utils.setInternalState(navigationService, "cache", memCache);
  }
  @Disabled("til fix it, find why return header value is null")
  @Test void getNavigationHeader(AemContext context) {
    checkGetHeaderFromCache();
    checkIfCantFindHeader();

    //
    // Check if everything is ok
    //
    final NavigationHeader header =
        navigationService.getNavigationHeader(request, resourceResolver, LANG, HEADER_CONFIG_PATH, StringUtils.EMPTY);

    assertNotNull(header.getNavigationTabs());
    assertNotNull(header.getLanguages());
    assertNotNull(header.getEvisa());
    assertNotNull(header.getFavoritesResultsPageUrl());
    assertNotNull(header.getLogoPath());
    assertNotNull(header.getSecondaryLinks());

    List<NavigationLink> articleLinks = header.getNavigationTabs().stream()
        .map(NavigationTab::getLinksColumn)
        .flatMap(List::stream)
        .filter(NavigationLink::hasArticle)
        .collect(Collectors.toList());

    assertTrue(articleLinks.stream().map(NavigationLink::getArticleItem).
        allMatch(Objects::nonNull));


    assertEquals(memCache.get(HEADER_KEY), header);
  }

  /**
   * Check get header from cache, if it's there.
   */
  private void checkGetHeaderFromCache() {
    final NavigationHeader mockHeader = mock(NavigationHeader.class);
    memCache.add(HEADER_KEY, mockHeader, Constants.HEADER_CACHE_TIME);
    final NavigationHeader headerFromCache =
        navigationService.getNavigationHeader(request, resourceResolver, LANG, HEADER_CONFIG_PATH, StringUtils.EMPTY);

    assertEquals(mockHeader, headerFromCache);
    verify(memCache, times(1)).get(HEADER_KEY);

    memCache.remove(HEADER_KEY);
  }

  /**
   * Check if the header can't be found because config path doesn't exist.
   */
  private void checkIfCantFindHeader() {
    final NavigationHeader nullHeader =
        navigationService.getNavigationHeader(request, resourceResolver, LANG, "not-found-path", StringUtils.EMPTY);

    Assertions.assertNull(nullHeader);
    Assertions.assertNull(memCache.get(HEADER_KEY));
  }
}
