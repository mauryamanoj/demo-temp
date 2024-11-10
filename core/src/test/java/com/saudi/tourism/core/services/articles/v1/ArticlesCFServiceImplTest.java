package com.saudi.tourism.core.services.articles.v1;

import java.util.Arrays;

import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.article.v1.ArticlesCFService;
import com.saudi.tourism.core.services.article.v1.ArticlesCFServiceImpl;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import com.saudi.tourism.core.services.article.v1.filters.ArticleTagsFilter;
import com.saudi.tourism.core.services.article.v1.filters.ArticlesFiltersChain;
import com.saudi.tourism.core.services.article.v1.filters.ArticlesFiltersChainImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ArticlesCFServiceImplTest {

  @Mock
  private UserService userService;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private QueryBuilder queryBuilder;

  @Mock
  private Query query;

  @Mock
  private SearchResult searchResult;

  @Mock
  private ArticlesFiltersChainImpl articlesFiltersChain;

  @Mock
  private RunModeService runModeService;

  private ArticlesCFService service = new ArticlesCFServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) throws InvalidTagFormatException {
    aemContext.load().json("/services/articles-service/article1.json", "/content/dam/sauditourism/cf/en/articles/article1");

    articlesFiltersChain = new ArticlesFiltersChainImpl();
    articlesFiltersChain.setFilters(Arrays.asList(new ArticleTagsFilter()));
    aemContext.registerService(UserService.class, userService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(QueryBuilder.class, queryBuilder);
    aemContext.registerService(ArticlesFiltersChain.class, articlesFiltersChain);
    aemContext.registerService(RunModeService.class, runModeService);
    aemContext.registerInjectActivateService(service);

    aemContext.addModelsForClasses(ArticleCFModel.class);

    final var tagManager = aemContext.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:article/insights", "insights", "insights");
  }

  @Test
  public void getFilteredEventsShouldRaiseIfRequestIsNull(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
        service.getArticles(null);
    });


    //Assert
    assertEquals("request is marked non-null but is null", exception.getMessage());
  }

  @Test
  public void getArticlesShouldRaiseIfLocaleIsNull(){
    // Arrange

    // Act
    final Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              service.getArticles(FetchArticlesRequest.builder().build());
            });

    //Assert
    assertEquals("resourceResolver is marked non-null but is null", exception.getMessage());
  }

  @Test
  public void getArticlesShouldReturnArticles(final AemContext aemContext){
    // Arrange
    final var articleResource = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/articles/article1");

    when(saudiTourismConfigs.getArticlesCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/articles");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {articleResource }).iterator());

    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .limit(10)
      .offset(0)
      .build();

    // Act
    final var response = service.getArticles(request);

    //Assert
    assertNotNull(response);
    assertNotNull(response.getData());

    final var article = response.getData().get(0);

    assertEquals("article1", article.getTitle());
    assertEquals("/content/dam/sauditourism/splashImage.png", article.getSplashImage());
    assertEquals("author", article.getAuthor());
    assertEquals("5 min", article.getTimeToRead());
    assertNotNull(article.getTags());
    assertEquals("insights", article.getTags().get(0).getTitle());

    assertNotNull(response.getPagination());
    assertEquals(1, response.getPagination().getTotal());
    assertEquals(10, response.getPagination().getLimit());
    assertEquals(0, response.getPagination().getOffset());
  }

  @Test
  public void getArticlesHomeShouldReturnArticles(final AemContext aemContext){
    // Arrange
    final var articleResource = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/articles/article1");

    when(saudiTourismConfigs.getArticlesCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/articles");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    when(queryBuilder.createQuery(any(), any())).thenReturn(query);
    when(query.getResult()).thenReturn(searchResult);
    when(searchResult.getResources()).thenReturn(Arrays.asList(new Resource[] {articleResource }).iterator());

    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .build();

    // Act
    final var response = service.getArticlesHome(request);

    //Assert
    assertNotNull(response);
    assertNotNull(response.getNewArticles());

    final var article = response.getNewArticles().get(0);

    assertEquals("article1", article.getTitle());
    assertEquals("/content/dam/sauditourism/splashImage.png", article.getSplashImage());
    assertEquals("author", article.getAuthor());
    assertEquals("5 min", article.getTimeToRead());
    assertNotNull(article.getTags());
    assertEquals("insights", article.getTags().get(0).getTitle());
  }
}