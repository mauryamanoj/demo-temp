package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppFeatureModel;
import com.saudi.tourism.core.services.v2.ShowingFeatureService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Showing Features Servlets .
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION
      + Constants.EQUAL + "Enable & Disable Feature Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v2/app/features"})
@Slf4j
public class ShowingFeaturesServlet extends SlingAllMethodsServlet {


  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(ShowingFeaturesServlet.class);

  /**
   * Root app page node name.
   */
  public static final String NN_APP = "app1";
  /**
   * Root App Content Path.
   */
  public static final String ROOT_APP_CONTENT_PATH = "/content/sauditourism/" + NN_APP;
  /**
   * ShowingFeatureService showingFeatureService.
   */
  @Reference
  private ShowingFeatureService showingFeatureService;

  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
    try {
      logger.info("Inside DoGet ShowingFeaturesServlet");
      String locale;
      locale = request.getParameter("locale");
      if (StringUtils.isEmpty(locale)) {
        locale = "en";
      }
      String path = ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale;

      logger.info("ResourcePath:{}", path);

      if (StringUtils.isNotBlank(path)) {
        NativeAppFeatureModel model = showingFeatureService.getAllFeaturesInfo(request, path);
        if (model == null) {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
              "No information for the Feature API");
        } else {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(model.getFeaturesList()));
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in getting module: ", e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "Internal Server Exception");
    }
  }

}

