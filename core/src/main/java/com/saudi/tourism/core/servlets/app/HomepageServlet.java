package com.saudi.tourism.core.servlets.app;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.page.AppHomepage;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Homepage Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/homepage"})
@Slf4j
public class HomepageServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String resourcePath = null;
    if (!StringUtils.isEmpty(request.getParameter("locale"))) {
      resourcePath =
          ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
              + request.getParameter("locale");
    }

    if (!StringUtils.isEmpty(resourcePath)) {
      AppHomepage model = getAppHomepage(request, resourcePath);
      if (model == null) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), "No homepage found for the locale"));
      } else {
        CommonUtils.writeJSONExclude(response, StatusEnum.SUCCESS.getValue(), model);
      }
    } else {
      //no locale
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Undefined locale"));
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
}
