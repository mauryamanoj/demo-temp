package com.saudi.tourism.core.servlets.nativeapp;

import com.day.crx.JcrConstants;
import com.google.gson.Gson;
import com.saudi.tourism.core.models.app.page.AppHomepage;
import com.saudi.tourism.core.models.app.page.NativeAppHomepage;
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
 * Servlet that retrieves all the contact information for a locale.
 * http://localhost:4502/bin/api/v1/app/homepage?locale=en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Native Homepage Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v2/app/homepage"})
@Slf4j
public class NativeAppHomepageServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String resourcePath = null;
    if (!StringUtils.isEmpty(request.getParameter("locale"))) {
      resourcePath =
          ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
              + request.getParameter("locale");
    }

    if (!StringUtils.isEmpty(resourcePath)) {
      NativeAppHomepage model = getNativeAppHomepage(request, resourcePath);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
             "No homepage found for the locale");
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
   * Get the AppHomepage model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  private AppHomepage getAppHomepage(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);

    AppHomepage model = null;
    if (resource != null) {
      model = resource.adaptTo(AppHomepage.class);
    }
    return model;
  }

  /**
   * Get the NativeAppHomepage model.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  private NativeAppHomepage getNativeAppHomepage(SlingHttpServletRequest request, String resourcePath) {

    Resource resource = request.getResourceResolver()
          .getResource(resourcePath + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);

    NativeAppHomepage model = null;
    if (resource != null) {
      model = resource.adaptTo(NativeAppHomepage.class);
    }
    return model;
  }
}
