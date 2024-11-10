package com.saudi.tourism.core.services.impl;

import com.day.cq.commons.Externalizer;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.LanguageLink;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.ArticleItem;
import com.saudi.tourism.core.models.components.nav.v2.NavigationHeader;
import com.saudi.tourism.core.models.components.nav.v2.NavigationTab;
import com.saudi.tourism.core.models.components.nav.v3.NavigationLink;
import com.saudi.tourism.core.models.components.search.SearchConfigModel;
import com.saudi.tourism.core.services.NavigationService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NavItemUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The Class NavigationServiceImpl.
 */
@Component(service = NavigationService.class,
           immediate = true)
@Slf4j
public class NavigationServiceImpl implements NavigationService {

  /**
   * The Cache.
   */
  @Reference
  private Cache cache;
  /**
   * Settings service.
   */
  @Reference
  private SlingSettingsService settingsService;

  @Override public NavigationHeader getNavigationHeader(SlingHttpServletRequest request,
      ResourceResolver resourceResolver, String language, String path, String site) {

    NavigationHeader header = null;
    String cacheKey;
    if (StringUtils.isNotBlank(site)) {
      cacheKey = Constants.HEADER_KEY + Constants.COLON_SLASH_CHARACTER + site + Constants.MINUS + language;
    } else {
      cacheKey = Constants.HEADER_KEY + Constants.MINUS + language;
    }

    header = (NavigationHeader) cache.get(cacheKey);

    if (header != null) {
      return header;
    }

    String contentPage = path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT;
    Resource resource = resourceResolver.getResource(contentPage);
    if (resource != null) {
      header = resource.adaptTo(NavigationHeader.class);
      if (header != null) {
        try {
          List<NavigationTab> navigationTabs = buildConfHeader(resourceResolver, path);
          if (StringUtils.isNotBlank(site)) {
            header.setLanguages(getChildItems(resourceResolver, "/content/" + site + "/languages", LanguageLink.class));
          } else {
            header.setLanguages(getChildItems(resourceResolver, Constants.LANGS_PATH, LanguageLink.class));
          }
          header.setNavigationTabs(navigationTabs);

          header.setSearchConfigModel(populateSearchConfigModel(header, resourceResolver, language));

          // Store to cache
          cache.add(cacheKey, header, Constants.HEADER_CACHE_TIME);
        } catch (Exception e) {
          LOGGER.error("Error in building header/navigation objects", e);
        }
      } else {
        LOGGER.error("NavigationHeader adapting error {}", contentPage);
      }
    }

    return header;
  }

  /**
   * Gets SearchConfigModel json.
   *
   * @param header           NavigationHeader
   * @param resourceResolver resource resolver
   * @param language         language
   * @return json config
   */
  private SearchConfigModel populateSearchConfigModel(NavigationHeader header, ResourceResolver resourceResolver,
      String language) {
    SearchConfigModel searchConfigModel = new SearchConfigModel();
    searchConfigModel.setSearchPagePath(header.getSearchPath() + "#search=");
    searchConfigModel.setSearchPlaceholder(header.getSearchPlaceholder());
    searchConfigModel.setCancelLabel(header.getCancelLabel());
    searchConfigModel.setClearLabel(header.getClearLabel());
    searchConfigModel.setSuggestionsEndpoint("/bin/api/solr/search");

    searchConfigModel.setTrendingBlock(
        SearchUtils.getSearchTrendings(header.getTrendingBlockTitle(), resourceResolver,
            language, Constants.TRENDING_PAGE));
    searchConfigModel.setFeatured(
        SearchUtils.getSearchTrendings(header.getFeaturedTitle(), resourceResolver,
            language, Constants.FEATURED_PAGE));

    searchConfigModel
        .setPillBlock(SearchUtils.getSearchPills(header.getPillBlockTitle(),
            resourceResolver, language));

    searchConfigModel
        .setSearchPage(SearchUtils.getSearchPage(
            resourceResolver, language, "searchPage", settingsService.getRunModes().contains(
            Externalizer.PUBLISH)));

    return searchConfigModel;
  }

  /**
   * Sets the menu items.
   *
   * @param resourceResolver resource resolver
   * @param path             resource path
   * @param clazz            required class
   * @param <T>              sub-class of {@link Link}
   * @return list of adapted values
   */
  private <T extends Link> List<T> getChildItems(ResourceResolver resourceResolver, String path,
      Class<T> clazz) {
    Resource resource = resourceResolver.resolve(path);

    Iterator<Resource> itemsIterator = resource.getChildren().iterator();
    List<T> items = new ArrayList<>();
    while (itemsIterator.hasNext()) {
      Resource child = itemsIterator.next();

      if (child.getName().equals(JcrConstants.JCR_CONTENT)) {
        continue;
      }

      T link = CommonUtils.getLinkInPageProperties(child, clazz);
      if (link != null) {
        items.add(link);
      }
    }

    return items;
  }

  /**
   * Build Configuration Header.
   *
   * @param resourceResolver resourceResolver
   * @param path             path
   * @return List of navigation tab
   */
  private List<NavigationTab> buildConfHeader(ResourceResolver resourceResolver, String path) {

    List<NavigationTab> navigationTabs = new ArrayList<>();
    Resource resource = resourceResolver.getResource(path);

    if (Objects.isNull(resource)) {
      return navigationTabs;
    }

    // For each tabs
    Iterator<Resource> headerIterator = resource.getChildren().iterator();
    while (headerIterator.hasNext()) {

      // navigation tab
      resource = headerIterator.next();

      if (resource.getName().equals(JcrConstants.JCR_CONTENT)) {

        continue;
      }

      path = resource.getPath();

      NavigationTab navigationTab = adaptToNavigationTab(resource);
      if (navigationTab != null) {
        PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
        if (NavItemUtils.NAV_TYPE_ARTICLES.equals(navigationTab.getNavType())) {
          navigationTab.setArticleItem(NavItemUtils
              .getArticleItem(pageManager, navigationTab.getArticlePath()));
        }

        navigationTab
            .setLinksColumn(new LinkedList<>(getChildItems(resourceResolver, path,
                NavigationLink.class)));

        navigationTab.getLinksColumn().stream().filter(NavigationLink::hasArticle)
            .forEach(link -> {
              ArticleItem article = NavItemUtils.getArticleItem(pageManager, link.getArticlePath());
              link.setArticleItem(article);
            });

        navigationTabs.add(navigationTab);
      } else {
        LOGGER.error("NavigationTab adapting error {}", path);
      }
    }
    return navigationTabs;
  }

  /**
   * Adapts jcr:content of the resource to NavigationTab.
   *
   * @param resource the resource
   * @return NavigationTab or null
   */
  private NavigationTab adaptToNavigationTab(Resource resource) {
    Resource content = resource.getChild(JcrConstants.JCR_CONTENT);
    if (content != null) {
      return content.adaptTo(NavigationTab.class);
    }
    return null;
  }
}
