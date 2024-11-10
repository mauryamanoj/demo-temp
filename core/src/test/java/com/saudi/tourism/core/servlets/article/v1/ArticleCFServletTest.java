package com.saudi.tourism.core.servlets.article.v1;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;

import com.saudi.tourism.core.services.article.v1.Article;
import com.saudi.tourism.core.services.article.v1.ArticleSummary;
import com.saudi.tourism.core.services.article.v1.ArticlesCFService;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
import com.saudi.tourism.core.services.article.v1.FetchArticlesResponse;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.saudi.tourism.core.utils.PrimConstants.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ArticleCFServletTest {

  @Mock
  private ArticlesCFService articlesCFService;

  @InjectMocks
  private ArticleCFServlet servlet = new ArticleCFServlet();

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnBadRequestIfArticleIdNotProvided() throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(400, response.getStatus());
    Assertions.assertEquals(
      "{\"code\":400,\"message\":\"Article ID is required\",\"response\":{}}",
      response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnArticle(final AemContext aemContext)
    throws ServletException, IOException {
    // Arrange
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    parameters.put("articleId", "Article1");
    request.setParameterMap(parameters);

    final var article= Article.builder()
            .id("Article1")
            .title("Article1")
            .build();

    when(articlesCFService.getArticle(any(FetchArticlesRequest.class)))
      .thenReturn(article);

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":200,\"message\":\"success\",\"response\":{\"id\":\"Article1\",\"title\":\"Article1\"}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnTechnicalExceptionIfArticleCFServiceRaised(final AemContext aemContext)
    throws ServletException, IOException {
    // Arrange
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    parameters.put("articleId", "Article1");
    request.setParameterMap(parameters);

    when(articlesCFService.getArticle(any(FetchArticlesRequest.class)))
      .thenThrow(new RuntimeException("An exception happened"));

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(500, response.getStatus());
    Assertions.assertEquals(
      "{\"code\":500,\"message\":\"An exception happened\",\"response\":{}}",
      response.getOutputAsString());
  }

}