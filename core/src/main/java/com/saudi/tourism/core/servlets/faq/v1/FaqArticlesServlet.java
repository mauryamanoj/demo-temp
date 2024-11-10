package com.saudi.tourism.core.servlets.faq.v1;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
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
import java.net.URISyntaxException;
import java.text.MessageFormat;

import static com.saudi.tourism.core.servlets.faq.v1.FaqArticlesServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.faq.v1.FaqArticlesServlet.SERVLET_PATH;

/** FAQ Articles servlet. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class FaqArticlesServlet extends SlingSafeMethodsServlet {
  /** Possible paths for this servlet. */
  static final String SERVLET_PATH = "/bin/api/v1/faq/articles";

  /** This servlet description. */
  static final String DESCRIPTION = "FAQ Articles Servlet";

  /** MuleSoft: Client ID header. */
  private static final String CLIENT_ID_HEADER = "client_id";

  /** MuleSoft: Client Secret header. */
  private static final String CLIENT_SECRET_HEADER = "client_secret";

  /** CategoryCode query param. */
  private static final String PN_CATEGORY_CODE = "CategoryCode";

  /** Language query param. */
  private static final String PN_LANGUAGE = "language";

  /** Saudi Tourism config. */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  @Override
  protected void doGet(SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {
    final var language = request.getParameter(PN_LANGUAGE);
    final var categoryCode = request.getParameter(PN_CATEGORY_CODE);

    if (StringUtils.isBlank(language)) {
      LOGGER.error(
          Constants.DO_GET_MENTHOD,
          MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, PN_LANGUAGE));
      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, PN_LANGUAGE));
      return;
    }

    if (StringUtils.isBlank(categoryCode)) {
      LOGGER.error(
          Constants.DO_GET_MENTHOD,
          MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, PN_CATEGORY_CODE));
      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, PN_CATEGORY_CODE));
      return;
    }

    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {

      final var uriBuilder = new URIBuilder(saudiTourismConfigs.getMuleSoftFaqArticlesEndpoint());
      uriBuilder.addParameter(PN_LANGUAGE, language);
      uriBuilder.addParameter(PN_CATEGORY_CODE, categoryCode);

      HttpGet muleSoftRequest = new HttpGet(uriBuilder.build());
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
    } catch (URISyntaxException e) {
      LOGGER.error("Exception is {}", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "Error when calling CRM.");
    }
  }
}
