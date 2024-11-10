package com.saudi.tourism.core.services.articles.v1.filters;

import java.text.ParseException;
import java.util.Arrays;

import com.saudi.tourism.core.models.components.contentfragment.article.ArticleCFModel;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import com.saudi.tourism.core.services.article.v1.filters.ArticleKeywordFilter;
import com.saudi.tourism.core.services.article.v1.filters.ArticleTagsFilter;
import com.saudi.tourism.core.services.article.v1.filters.ArticlesFiltersChainImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ArticlesFiltersChainImplTest {
  private ArticlesFiltersChainImpl filtersChain = new ArticlesFiltersChainImpl();

  @Test
  public void doFilterShouldExcludeOnKeyword(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new ArticleKeywordFilter()));

    final var requestKeyWord = "notfound";
    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .keyword(requestKeyWord)
      .build();

    final var article = ArticleCFModel.builder()
      .title("test DaLIla title")
      .content("Content DaLIla")
      .build();

    //Act
    final var result = filtersChain.doFilter(request, article);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnKeyword(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new ArticleKeywordFilter()));

    final var requestKeyWord = "dalila";
    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .keyword(requestKeyWord)
      .build();

    final var article = ArticleCFModel.builder()
      .title("test DaLIla title")
      .content("Content DaLIla")
      .build();

    //Act
    final var result = filtersChain.doFilter(request, article);

    //Assert
    assertTrue(result);
  }

  @Test
  public void doFilterShouldExcludeOnTags(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new ArticleTagsFilter()));

    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .tagId(Arrays.asList("tag1", "sauditourism:article/desert_safari"))
      .build();

    final var article = ArticleCFModel.builder()
      .title("title")
      .content("Content")
      .tags(new String[]{"sauditourism:article/insights"})
      .build();

    //Act
    final var result = filtersChain.doFilter(request, article);

    //Assert
    assertFalse(result);
  }

  @Test
  public void doFilterShouldIncludeOnTags(final AemContext aemContext) throws ParseException {
    // Arrange
    filtersChain.setFilters(Arrays.asList(new ArticleTagsFilter()));

    final var request = FetchArticlesRequest.builder()
      .locale("en")
      .tagId(Arrays.asList("sauditourism:article/desert_safari", "tag2"))
      .build();

    final var article = ArticleCFModel.builder()
      .title("title")
      .content("Content")
      .tags(new String[]{"sauditourism:article/desert_safari", "tag5"})
      .build();

    //Act
    final var result = filtersChain.doFilter(request, article);

    //Assert
    assertTrue(result);
  }
}