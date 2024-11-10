package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppLocaleModel;
import com.saudi.tourism.core.services.v2.NativeAppLocaleService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * NativeApp Locale Servlet .
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION
      + Constants.EQUAL + "Native App Locale Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
    + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
    + "/bin/api/v2/app/locales"})
@Slf4j
public class NativeAppLocalesServlet extends SlingSafeMethodsServlet {


  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(NativeAppLocalesServlet.class);


  /**
   * Root app page node name.
   */
  public static final String NN_APP = "app1";
  /**
   * Root App Content Path.
   */
  public static final String ROOT_APP_CONTENT_PATH = "/content/sauditourism/" + NN_APP;
  /**
   * nativeAppVersionService .
   */
  @Reference
  private NativeAppLocaleService nativeAppLocaleService;

  /**
   *
   * @param request request.
   * @param response response .
   * @throws IOException IOException.
   */
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
    try {

      logger.info("Inside DoGet NativeAppLocaleServlet");
      String locale = "en";
      String path = ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale;
      logger.info("ResourcePath:{}", path);
      NativeAppLocaleModel model = nativeAppLocaleService.getAllLocaleInfo(request, path);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            "No information for the App Locale API");
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(model.getAppLocale()));
      }

    }  catch (Exception e) {
      LOGGER.error("Error in NativeApp Locale details", e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "InternalServerError While fetching the App Locales");
    }
  }
}

