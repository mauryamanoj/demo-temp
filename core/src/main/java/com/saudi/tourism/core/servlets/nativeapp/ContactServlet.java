package com.saudi.tourism.core.servlets.nativeapp;

import com.adobe.xfa.ut.StringUtils;
import com.saudi.tourism.core.beans.ContactResponse;
import com.saudi.tourism.core.services.v2.ContactService;
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
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that retrieves all the contact information for a locale.
 * http://localhost:4502/bin/api/v2/app/contact./en
 */
@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Contact Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/app/contact"})
@Slf4j
public class ContactServlet extends SlingSafeMethodsServlet {

  /**
   * The Contact service.
   */
  @Reference
  private transient ContactService contactService;

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws IOException {

    String suffixPath = request.getRequestPathInfo().getSuffix();

    if (!StringUtils.isEmpty(suffixPath)) {
      String localePath = CommonUtils.getGetAppPath(suffixPath);
      ContactResponse model = contactService.getContactInfos(request, localePath);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            "No contact information for the locale");
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(model));
      }
    } else {
      //no locale
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(), "Undefined locale");
    }

  }
}
