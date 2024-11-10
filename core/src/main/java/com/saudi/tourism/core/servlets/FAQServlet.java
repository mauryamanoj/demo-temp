package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.faq.FAQModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * FAQ Servlet .
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "FAQ Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v1/faq"})
@Slf4j
public class FAQServlet extends SlingAllMethodsServlet {

  @Override
  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws ServletException, IOException {

    ResourceResolver resolver = request.getResourceResolver();
    if (StringUtils.isEmpty(request.getParameter("path"))) {
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType(), "FAQs path is null"));
      return;
    }

    String faqResourcePath = request.getParameter("path");

    Resource faqResource = resolver.getResource(faqResourcePath);
    if (null == faqResource) {
      if (LOGGER.isDebugEnabled()) {
        LOGGER.debug("FAQs resource is null");
      }

      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType(), "FAQs path is null"));

      return;
    }

    FAQModel faqModel = faqResource.adaptTo(FAQModel.class);
    CommonUtils.writeJSONExclude(response, StatusEnum.SUCCESS.getValue(), faqModel);

  }
}
