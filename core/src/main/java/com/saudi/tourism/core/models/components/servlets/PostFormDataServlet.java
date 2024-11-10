package com.saudi.tourism.core.models.components.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.saudi.tourism.core.models.components.servlets.PostFormDataServlet.DESCRIPTION;
import static com.saudi.tourism.core.models.components.servlets.PostFormDataServlet.SERVLET_PATH;

/** The Message Type servlet. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class PostFormDataServlet extends SlingAllMethodsServlet {

  /** MuleSoft: Client ID header. */
  private static final String CLIENT_ID_HEADER = "client_id";

  /** MuleSoft: Client Secret header. */
  private static final String CLIENT_SECRET_HEADER = "client_secret";

  /** The saudi tourism config. */
  @Reference
  private SaudiTourismConfigs saudiTourismConfig;

  /** Possible paths for this servlet. */
  public static final String SERVLET_PATH = "/bin/api/contactus/formData";

  /** This servlet description. */
  static final String DESCRIPTION = "Post Form Data Servlet";

  @Override
  protected void doPost(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

      final var muleSoftRequest =
          new HttpPost(saudiTourismConfig.getMuleSoftContactUsFormEndpoint());
      muleSoftRequest.addHeader(
          new BasicHeader(CLIENT_ID_HEADER, saudiTourismConfig.getMuleSoftClientId()));
      muleSoftRequest.addHeader(
          new BasicHeader(CLIENT_SECRET_HEADER, saudiTourismConfig.getMuleSoftClientSecret()));

      final var jsonRequestText = IOUtils.toString(request.getReader());
      final var entity = new StringEntity(jsonRequestText, ContentType.APPLICATION_JSON);
      muleSoftRequest.setEntity(entity);

      final var httpResponse = httpClient.execute(muleSoftRequest);

      int statusCode = httpResponse.getStatusLine().getStatusCode();
      if (statusCode == HttpStatus.SC_OK) {
        final var responseJson = EntityUtils.toString(httpResponse.getEntity());
        final var responseMsg = new Gson().fromJson(responseJson, JsonObject.class);

        CommonUtils.writeNewJSONFormat(
            response, StatusEnum.SUCCESS.getValue(), responseMsg.get("apidataObj").toString());
      } else {
        CommonUtils.writeNewJSONFormat(
            response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "Error when calling CRM.");
      }
    } catch (IOException e) {
      LOGGER.error("Exception is {}", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "Error when calling CRM.");
    }
  }
}
