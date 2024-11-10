package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.nativeApp.page.NativeAppLoyaltyCarouselModel;
import com.saudi.tourism.core.services.v2.NativeAppLoyaltyService;
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
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Native App Loyalty Carousel Servlet .
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION
      + Constants.EQUAL + "Native App Loyalty Carousel Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
    + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
    + "/bin/api/v2/app/loyalty-carousel"})
@Slf4j
public class NativeAppLoyaltyCarouselServlet extends SlingSafeMethodsServlet {


  /**
   * Logger .
   */
  private final Logger logger = LoggerFactory.getLogger(NativeAppLoyaltyCarouselServlet.class);


  /**
   * Root app page node name.
   */
  public static final String NN_APP = "app1";
  /**
   * Root App Content Path.
   */
  public static final String ROOT_APP_CONTENT_PATH = "/content/sauditourism/" + NN_APP;
  /**
   * nativeAppLoyaltyService .
   */
  @Reference
  private NativeAppLoyaltyService nativeAppLoyaltyService;
  @Override
  public void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws IOException {
    try {

      logger.info("Inside DoGet NativeAppLoyaltyCarouselServlet");
      String locale;
      locale = request.getParameter("locale");
      if (StringUtils.isEmpty(locale)) {
        locale = "en";
      }
      String path = ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + locale;

      logger.info("ResourcePath:{}", path);

      if (StringUtils.isNotBlank(path)) {
        NativeAppLoyaltyCarouselModel model = nativeAppLoyaltyService.getLoyaltyCarouselInfo(request, path);
        if (model == null) {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
              "No information for the Loyalty Carousel API");
        } else {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(model));
        }
      }
    }  catch (Exception e) {
      LOGGER.error("Error in Native App Loyalty Carousel Servlet details", e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "InternalServerError While fetching the Loyalty Carousel");
    }
  }
}

