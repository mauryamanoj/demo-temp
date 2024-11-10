package com.saudi.tourism.core.servlets.faq.v1;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FaqArticlesServletTest {
  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private CloseableHttpClient closeableHttpClient;

  @Mock
  private CloseableHttpResponse closeableHttpResponse;

  @InjectMocks
  private FaqArticlesServlet servlet = new FaqArticlesServlet();

  @Captor
  private ArgumentCaptor<HttpGet> httpGetRequestParamsCaptor;

  @BeforeEach
  void setUp(final AemContext aemContext) throws IOException {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    lenient().when(saudiTourismConfigs.getMuleSoftFaqArticlesEndpoint()).thenReturn("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveKnowledgeArticles");
    lenient().when(saudiTourismConfigs.getMuleSoftClientId()).thenReturn("b7c64b54-9327-4553-a3ab-6c71d21b9625");
    lenient().when(saudiTourismConfigs.getMuleSoftClientSecret()).thenReturn("98b4d5d7-cf6a-4a60-9cbe-202c19562676");
    lenient().when(closeableHttpClient.execute(any())).thenReturn(closeableHttpResponse);
  }

  @Test
  void doGetShouldReturnBadRequestIfLanguageNotProvided(final AemContext aemContext) throws ServletException, IOException {
    // Arrange

    // Act
    servlet.doGet(aemContext.request(), aemContext.response());

    // Assert
    Assertions.assertEquals(400, aemContext.response().getStatus());
    Assertions.assertEquals(
      "{\"code\":400,\"message\":\"Missing required parameters: language\",\"response\":{}}",
      aemContext.response().getOutputAsString());
  }

  @Test
  void doGetShouldReturnBadRequestIfCategoryCodeNotProvided(final AemContext aemContext) throws ServletException, IOException {
    // Arrange
    aemContext.request().addRequestParameter("language", "en");

    // Act
    servlet.doGet(aemContext.request(), aemContext.response());

    // Assert
    Assertions.assertEquals(400, aemContext.response().getStatus());
    Assertions.assertEquals(
      "{\"code\":400,\"message\":\"Missing required parameters: CategoryCode\",\"response\":{}}",
      aemContext.response().getOutputAsString());
  }

  @Test
  public void doGetShouldCallConfiguredMuleSoftPath(final AemContext aemContext) throws IOException {
    //Arrange
    aemContext.request().addRequestParameter("language", "en");
    aemContext.request().addRequestParameter("CategoryCode", "1");

    when(closeableHttpResponse.getStatusLine()).thenReturn(new StatusLine() {
      @Override
      public ProtocolVersion getProtocolVersion() {
        return null;
      }

      @Override
      public int getStatusCode() {
        return 404;
      }

      @Override
      public String getReasonPhrase() {
        return null;
      }
    });

    try (MockedStatic<HttpClients> mockedStatic = Mockito.mockStatic(HttpClients.class)) {
      mockedStatic.when(HttpClients::createDefault).thenReturn(closeableHttpClient);

      //Act
      servlet.doGet(aemContext.request(), aemContext.response());

      //Assert
      verify(closeableHttpClient).execute(httpGetRequestParamsCaptor.capture());
      assertEquals("GET", httpGetRequestParamsCaptor.getValue().getMethod());
      assertEquals("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveKnowledgeArticles?language=en&CategoryCode=1", httpGetRequestParamsCaptor.getValue().getURI().toString());
      assertEquals("b7c64b54-9327-4553-a3ab-6c71d21b9625", httpGetRequestParamsCaptor.getValue().getHeaders("client_id")[0].getValue());
      assertEquals("98b4d5d7-cf6a-4a60-9cbe-202c19562676", httpGetRequestParamsCaptor.getValue().getHeaders("client_secret")[0].getValue());
    }
  }
}
