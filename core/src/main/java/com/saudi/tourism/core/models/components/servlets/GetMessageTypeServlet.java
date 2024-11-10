package com.saudi.tourism.core.models.components.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.saudi.tourism.core.models.components.servlets.GetMessageTypeServlet.DESCRIPTION;
import static com.saudi.tourism.core.models.components.servlets.GetMessageTypeServlet.SERVLET_PATH;

/**
 * The Message Type servlet.
 * */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class GetMessageTypeServlet extends SlingSafeMethodsServlet {
  /**
   * Possible paths for this servlet.
   * */
  public static final String SERVLET_PATH = "/bin/api/contactus/messageType";

  /**
   * This servlet description.
   * */
  static final String DESCRIPTION = "Get Message Type Servlet";

  /**
   * MuleSoft: Client ID header.
   * */
  private static final String CLIENT_ID_HEADER = "client_id";

  /**
   * MuleSoft: Client Secret header.
   * */
  private static final String CLIENT_SECRET_HEADER = "client_secret";

  /**
   * Saudi Tourism config.
   * */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  @Override
  protected void doGet(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

      HttpGet muleSoftRequest =
          new HttpGet(saudiTourismConfigs.getMuleSoftContactUsMessageTypeEndpoint());
      muleSoftRequest.addHeader(
          new BasicHeader(CLIENT_ID_HEADER, saudiTourismConfigs.getMuleSoftClientId()));
      muleSoftRequest.addHeader(
          new BasicHeader(CLIENT_SECRET_HEADER, saudiTourismConfigs.getMuleSoftClientSecret()));

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
