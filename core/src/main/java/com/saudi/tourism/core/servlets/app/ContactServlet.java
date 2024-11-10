package com.saudi.tourism.core.servlets.app;

import com.adobe.xfa.ut.StringUtils;
import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.contact.AppContactPageModel;
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
 * Servlet that retrieves all the contact information for a locale.
 * http://localhost:4502/bin/api/v1/app/contact./en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Contact Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/contact"})
@Slf4j
public class ContactServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String resourcePath = CommonUtils.getGetAppPath(request.getRequestPathInfo().getSuffix());
    if (!StringUtils.isEmpty(resourcePath)) {
      AppContactPageModel model = getContactInformation(request, resourcePath);
      if (model == null) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                "No contact information for the locale"));
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), model);
      }
    } else {
      //no locale
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Undefined locale"));
    }

  }

  /**
   * Get the contact information for a locale.
   *
   * @param request      request
   * @param resourcePath resourcePath code
   * @return model
   */
  private AppContactPageModel getContactInformation(SlingHttpServletRequest request,
      String resourcePath) {

    Resource resource = request.getResourceResolver()
        .getResource(resourcePath + Constants.APP_CONTACT_PAGE + JcrConstants.JCR_CONTENT);

    AppContactPageModel model = null;
    if (resource != null) {
      model = resource.adaptTo(AppContactPageModel.class);
    }
    return model;
  }
}
