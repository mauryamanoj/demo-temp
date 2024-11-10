package com.saudi.tourism.core.services.article.v1;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.jcr.Session;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.article.v1.filters.ArticlesFiltersChain;
import com.saudi.tourism.core.services.events.v1.Pagination;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Articles Service. */
@Component(service = ArticlesCFService.class, immediate = true)
@Slf4j
public class ArticlesCFServiceImpl implements ArticlesCFService {

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;


  /**
   * Saudi Tourism Configs.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

/**
   * Articles Filters Chain.
   */
  @Reference
  private ArticlesFiltersChain articlesFiltersChain;

  @Override
  public FetchArticlesResponse getArticles(@NonNull final FetchArticlesRequest request) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var allArticlesCF = fetchAllArticles(request, resourceResolver);
      // Check if allArticlesCF is null or empty before proceeding
      if (CollectionUtils.isEmpty(allArticlesCF)) {
        LOGGER.info("No articles found for the provided request.");
        return FetchArticlesResponse.builder()
          .data(Collections.emptyList())
          .build();
      }

      final var filteredArticlesCF =
          CollectionUtils.emptyIfNull(allArticlesCF).stream()
          .filter(e -> articlesFiltersChain.doFilter(request, e))
          .collect(Collectors.toList());

      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(request.getLocale());
      final var filteredArticles = convertToArticleSummaries(filteredArticlesCF, resourceResolver, locale, tagManager);

      List<ArticleSummary> paginatedArticles = null;
      if (request.getLimit() > 0) {
        paginatedArticles =
          CollectionUtils.emptyIfNull(filteredArticles).stream()
            .skip(request.getOffset())
            .limit(request.getLimit())
            .collect(Collectors.toList());
      } else {
        paginatedArticles =
          CollectionUtils.emptyIfNull(filteredArticles).stream()
            .skip(request.getOffset())
            .collect(Collectors.toList());
      }

      final var pagination =
          Pagination.builder()
          .total(CollectionUtils.size(filteredArticles))
          .limit(request.getLimit())
          .offset(request.getOffset())
          .build();

      return FetchArticlesResponse.builder()
          .data(paginatedArticles)
          .pagination(pagination)
          .build();
    }
  }

  @Override
  public FetchArticlesHomeResponse getArticlesHome(@NonNull final FetchArticlesRequest request) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var allArticlesCF = fetchAllArticles(request, resourceResolver);
      // Check if allArticlesCF is null or empty before proceeding
      if (CollectionUtils.isEmpty(allArticlesCF)) {
        LOGGER.info("No articles found for the provided request.");
        return FetchArticlesHomeResponse.builder()
          .featuredArticles(Collections.emptyList())
          .newArticles(Collections.emptyList())
          .trendingArticles(Collections.emptyList())
          .build();
      }

      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(request.getLocale());
      final var articles = convertToArticleSummaries(allArticlesCF, resourceResolver, locale, tagManager);

      // Filter articles based on category
      List<ArticleSummary> featuredArticles = filterArticlesByCategory(articles, "featured");
      List<ArticleSummary> newArticles = filterArticlesByCategory(articles, "new");
      List<ArticleSummary> trendingArticles = filterArticlesByCategory(articles, "trending");

      // Construct FetchArticlesHomeResponse
      return FetchArticlesHomeResponse.builder()
        .featuredArticles(featuredArticles)
        .newArticles(newArticles)
        .trendingArticles(trendingArticles)
        .build();
    }
  }

  @Override
  public FetchArticlesTagsResponse getArticlesTags(FetchArticlesRequest fetchArticlesRequest) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final Resource articlesTagsRoot = resourceResolver.getResource(saudiTourismConfigs.getArticleTagsPath());
      if (articlesTagsRoot == null) {
        LOGGER.error("Articles Tags Root node not found.");
        return null;
      }

      Iterator<Resource> childResources = articlesTagsRoot.listChildren();
      if (!childResources.hasNext()) {
        return FetchArticlesTagsResponse.builder().data(Collections.emptyList()).build();
      }

      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(StringUtils.defaultIfEmpty(fetchArticlesRequest.getLocale(), "en"));

      List<ArticleTag> tags = CommonUtils.iteratorToStream(childResources)
          .map(resource -> tagManager.resolve(resource.getPath()))
          .filter(Objects::nonNull)
          .map(tag -> ArticleTag.builder().id(tag.getTagID()).title(tag.getTitle(locale)).build())
          .collect(Collectors.toList());

      return FetchArticlesTagsResponse.builder().data(tags).build();
    }
  }


  /**
   * Filter Articles By Category.
   *
   * @param articles
   * @param category
   * @return List of Article Summaries.
   */
  private List<ArticleSummary> filterArticlesByCategory(List<ArticleSummary> articles, String category) {
    return articles.stream()
      .filter(article -> category.equalsIgnoreCase(article.getCategory()))
      .collect(Collectors.toList());
  }

  @Override
  public Article getArticle(@NonNull final FetchArticlesRequest request) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      Locale locale = new Locale(request.getLocale());
      final var articlesCFPath = MessageFormat.format(saudiTourismConfigs.getArticlesCFPath(), locale);
      var articleCFPath = articlesCFPath + "/" + request.getArticleId();
      final var articleCFRoot = resourceResolver.getResource(articleCFPath);
      if (Objects.isNull(articleCFRoot)) {
        LOGGER.error("Article CF Root node not found under %s", articleCFRoot);
        return null;
      }
      var article = Article.fromCFModel(articleCFRoot.adaptTo(ArticleCFModel.class));
      if (Objects.nonNull(article)) {
        TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
        String mappedSplashImage = Optional.ofNullable(article.getSplashImage())
            .filter(StringUtils::isNotEmpty)
            .map(resourceResolver::map)
            .orElse(null);
        article = article.withSplashImage(mappedSplashImage);
        List<ArticleTag> articleTags = mapArticleTags(article.getArticleTags(), tagManager, locale);
        article = article.withTags(articleTags);
      }
      return article;
    }
  }

  /**
   * Fetch All Articles.
   *
   * @param request Current request.
   * @param resourceResolver Resource Resolver.
   * @return All Articles.
   */
  private List<ArticleCFModel> fetchAllArticles(
      @NotNull final FetchArticlesRequest request,
      @NonNull final ResourceResolver resourceResolver) {

    final var articlesCFPath = MessageFormat.format(saudiTourismConfigs.getArticlesCFPath(), request.getLocale());
    final var articlesCFRoot = resourceResolver.getResource(articlesCFPath);
    if (Objects.isNull(articlesCFRoot)) {
      LOGGER.error("Articles CF Root node not found under %s", articlesCFPath);
      return Collections.emptyList();
    }

    final var query = queryBuilder.createQuery(
            PredicateGroup.create(buildArticlesQueryMap(articlesCFPath)),
            resourceResolver.adaptTo(Session.class));
    // Set to 0 : treat it as unlimited
    query.setHitsPerPage(0);
    final var searchResult = query.getResult();
    final var resultResources = searchResult.getResources();
    if (!resultResources.hasNext()) {
      return Collections.emptyList();
    }
    return CommonUtils.iteratorToStream(resultResources)
      .map(resource -> resource.adaptTo(ArticleCFModel.class))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }



  /**
   * Convert to Article Summaries.
   *
   * @param filteredArticlesCF
   * @param resourceResolver
   * @param locale
   * @param tagManager
   * @return  List of Article Summaries.
   */
  private List<ArticleSummary> convertToArticleSummaries(
      List<ArticleCFModel> filteredArticlesCF, ResourceResolver resourceResolver,
      Locale locale, TagManager tagManager) {
    return CollectionUtils.emptyIfNull(filteredArticlesCF).stream()
      .filter(Objects::nonNull)
      .map(articleCFModel -> convertToArticleSummary(articleCFModel, resourceResolver, locale, tagManager))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  /**
   * Convert to Article Summary.
   *
   * @param articleCFModel
   * @param resourceResolver
   * @param locale
   * @param tagManager
   * @return Article Summary.
   */
  private ArticleSummary convertToArticleSummary(
      ArticleCFModel articleCFModel, ResourceResolver resourceResolver,
      Locale locale, TagManager tagManager) {
    ArticleSummary articleSummary = ArticleSummary.fromCFModel(articleCFModel);
    if (articleSummary == null) {
      return null;
    }

    String mappedSplashImage = Optional.ofNullable(articleSummary.getSplashImage())
        .filter(StringUtils::isNotEmpty)
        .map(resourceResolver::map)
        .orElse(null);
    articleSummary = articleSummary.withSplashImage(mappedSplashImage);

    List<ArticleTag> articleTags = mapArticleTags(articleSummary.getArticleTags(), tagManager, locale);
    return articleSummary.withTags(articleTags);
  }

  /**
   * Map Article Tags.
   *
   * @param articleTags
   * @param tagManager
   * @param locale
   * @return List of Article Tags.
   */
  private List<ArticleTag> mapArticleTags(List<String> articleTags, TagManager tagManager, Locale locale) {
    return CollectionUtils.emptyIfNull(articleTags).stream()
      .map(tagId -> resolveTag(tagId, tagManager, locale))
      .filter(Objects::nonNull)
      .collect(Collectors.toList());
  }

  /**
   * Resolve Tag.
   *
   * @param tagId
   * @param tagManager
   * @param locale
   * @return Article Tag.
   */
  private ArticleTag resolveTag(String tagId, TagManager tagManager, Locale locale) {
    Tag tag = tagManager.resolve(tagId);
    if (tag != null) {
      return ArticleTag.builder()
        .id(tag.getTagID())
        .title(tag.getTitle(locale))
        .build();
    }
    return null;
  }

  private Map buildArticlesQueryMap(@NonNull final String articlesCFPath) {
    final var map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, articlesCFPath);

    map.put(Constants.PREDICATE_TYPE, "dam:Asset");
    map.put("orderby", "@jcr:content/jcr:created");
    map.put("orderby.sort", "desc");

    map.put("p.limit", -1);

    return map;
  }
}
