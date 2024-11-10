package com.saudi.tourism.core.models.components.servlets;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.io.IOUtils;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

import java.io.IOException;
import java.nio.charset.Charset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class PostFormDataServletTest {

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private CloseableHttpClient closeableHttpClient;

  @Mock
  private CloseableHttpResponse closeableHttpResponse;

  @InjectMocks
  private PostFormDataServlet servlet = new PostFormDataServlet();

  @Captor
  private ArgumentCaptor<HttpPost> httpRequestParamsCaptor;

  @BeforeEach
  void setUp(final AemContext aemContext) throws IOException {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    when(saudiTourismConfigs.getMuleSoftContactUsFormEndpoint())
        .thenReturn("https://acc-api.sta.gov.sa/vswebapi/v1/contactus/formData");
    when(saudiTourismConfigs.getMuleSoftClientId())
        .thenReturn("b7c64b54-9327-4553-a3ab-6c71d21b9625");
    when(saudiTourismConfigs.getMuleSoftClientSecret())
        .thenReturn("98b4d5d7-cf6a-4a60-9cbe-202c19562676");
    when(closeableHttpClient.execute(any())).thenReturn(closeableHttpResponse);
  }

  @Test
  public void shouldCallConfiguredCRMPath(final AemContext aemContext) throws IOException {
    // Arrange
    when(closeableHttpResponse.getStatusLine())
        .thenReturn(
            new StatusLine() {
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

      final var body =
          "{\n"
              + "    \"firstName\": \"Abdullah\",\n"
              + "    \"lastName\": \"Abdulrahman\",\n"
              + "    \"emailAddress\": \"Abdullah.Abdulrahman@gmail.com\",\n"
              + "    \"mobileNumber\": \"+9666664364634\",\n"
              + "    \"caseType\": \"30a01b3e-fb48-eb11-a812-00224899a6a7\",\n"
              + "    \"incomingMessage\": \"A message\"\n"
              + "}";
      aemContext.request().setContent(body.getBytes(Charset.defaultCharset()));
      aemContext.request().setContentType(ContentType.APPLICATION_JSON.getMimeType());

      // Act
      servlet.doPost(aemContext.request(), aemContext.response());

      // Assert
      verify(closeableHttpClient).execute(httpRequestParamsCaptor.capture());
      assertEquals("POST", httpRequestParamsCaptor.getValue().getMethod());
      assertEquals(
          "https://acc-api.sta.gov.sa/vswebapi/v1/contactus/formData",
          httpRequestParamsCaptor.getValue().getURI().toString());
      assertEquals(
          "b7c64b54-9327-4553-a3ab-6c71d21b9625",
          httpRequestParamsCaptor.getValue().getHeaders("client_id")[0].getValue());
      assertEquals(
          "98b4d5d7-cf6a-4a60-9cbe-202c19562676",
          httpRequestParamsCaptor.getValue().getHeaders("client_secret")[0].getValue());
      assertEquals(
          "{\n"
              + "    \"firstName\": \"Abdullah\",\n"
              + "    \"lastName\": \"Abdulrahman\",\n"
              + "    \"emailAddress\": \"Abdullah.Abdulrahman@gmail.com\",\n"
              + "    \"mobileNumber\": \"+9666664364634\",\n"
              + "    \"caseType\": \"30a01b3e-fb48-eb11-a812-00224899a6a7\",\n"
              + "    \"incomingMessage\": \"A message\"\n"
              + "}",
          IOUtils.toString(
              httpRequestParamsCaptor.getValue().getEntity().getContent(),
              Charset.defaultCharset()));
    }
  }
}
