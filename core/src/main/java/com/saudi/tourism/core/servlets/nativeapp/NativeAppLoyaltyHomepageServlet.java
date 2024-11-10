package com.saudi.tourism.core.servlets.nativeapp;

import com.day.crx.JcrConstants;
import com.google.gson.Gson;
import com.saudi.tourism.core.models.app.page.NativeAppLoyaltyHomepage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * Servlet that retrieves all the loyalty page component details for a locale.
 * http://localhost:4502/bin/api/v2/app/loyalty-homepage?locale=en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Native Loyalty Homepage Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v2/app/loyalty-homepage"})
@Slf4j
public class NativeAppLoyaltyHomepageServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String resourcePath = null;
    if (!StringUtils.isEmpty(request.getParameter("locale"))) {
      resourcePath =
          ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
              + request.getParameter("locale");
    }

    if (!StringUtils.isEmpty(resourcePath)) {
      NativeAppLoyaltyHomepage model = getNativeAppHomepage(request, resourcePath);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
             "No Loyalty homepage component found for the locale");
      } else {
        Gson gson = new Gson();
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(), gson.toJson(model));
      }
    } else {
      //no locale
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
           "Undefined locale");
    }

  }

  /**
   * Get the NativeAppLoyaltyHomepage model.
   *
   * @param request request.
   * @param resourcePath resourcePath code .
   * @return model .
   */
  private NativeAppLoyaltyHomepage getNativeAppHomepage(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
          .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);

    NativeAppLoyaltyHomepage model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppLoyaltyHomepage.class);
    }
    return model;
  }
}
