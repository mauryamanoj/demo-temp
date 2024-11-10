package com.saudi.tourism.core.servlets.app;

import com.adobe.xfa.ut.StringUtils;
import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.content.AppPageModel;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that retrieves the information for a page.
 * http://localhost:4502/bin/api/v1/app/page./content/sauditourism/app/en/content-page
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Page Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/page"})
@Slf4j
public class PageServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String path = request.getRequestPathInfo().getSuffix();
    if (!StringUtils.isEmpty(path)) {
      AppPageModel model = getPageInformation(request, path);
      if (model == null) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), "No information for the page"));
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), model);
      }
    }
  }

  /**
   * Get the content page information.
   *
   * @param request request
   * @param path    path
   * @return model
   */
  private AppPageModel getPageInformation(SlingHttpServletRequest request, String path) {

    Resource resource = request.getResourceResolver()
        .getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
    AppPageModel model = null;
    if (resource != null && resource.getResourceType()
        .equals(Constants.APP_CONTENT_RESOURCE_TYPE)) {
      model = resource.adaptTo(AppPageModel.class);
    }
    return model;
  }

}
