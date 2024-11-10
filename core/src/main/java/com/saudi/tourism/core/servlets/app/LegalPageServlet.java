package com.saudi.tourism.core.servlets.app;

import com.adobe.xfa.ut.StringUtils;
import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.app.legal.LegalPageModel;
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
 * Servlet that retrieves all the legal information for a locale.
 * http://localhost:4502/bin/api/v1/app/legal./content/sauditourism/app/en/legal-page
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Legal Page Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/legal"})
@Slf4j
public class LegalPageServlet extends SlingAllMethodsServlet {

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String locale = request.getRequestPathInfo().getSuffix();
    if (!StringUtils.isEmpty(locale)) {
      LegalPageModel model = getLegalPageInformation(request, locale);
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
   * Get the legal page information.
   *
   * @param request request
   * @param path    path
   * @return model
   */
  private LegalPageModel getLegalPageInformation(SlingHttpServletRequest request, String path) {

    Resource resource = request.getResourceResolver()
        .getResource(path + Constants.FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT);
    LegalPageModel model = null;
    if (resource != null && resource.getResourceType()
        .equals(Constants.APP_LEGAL_PAGE_RESOURCE_TYPE)) {
      model = resource.adaptTo(LegalPageModel.class);
    }
    return model;
  }
}
