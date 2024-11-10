package com.saudi.tourism.core.servlets;

import com.adobe.granite.security.user.UserProperties;
import com.saudi.tourism.core.beans.PackageFormParams;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.MailchimpService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.SaudiConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.text.MessageFormat;

import static com.saudi.tourism.core.servlets.PackageFormServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This Servlet provides web and app API for Trip Plans.
 */
@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
               SLING_SERVLET_PATHS + Constants.EQUAL + PackageFormServlet.POST_URL})

@Slf4j
public class PackageFormServlet extends BaseAllMethodsServlet {

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "Saudi Tourism Package Form API (Servlet)";

  /**
   * App path for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String POST_URL = "/bin/api/v1/package-form";

  /**
   * Mailchimp service to manage subscriptions.
   */
  @Reference
  private transient MailchimpService mailchimpService;

  /**
   * Servlet's method post to subscribe user in Mailchimp.
   *
   * @param request  the request
   * @param response the response
   * @throws IOException if response is already committed or another servlet exception
   * @see org.apache.sling.api.servlets.SlingAllMethodsServlet#doPost(SlingHttpServletRequest,
   * SlingHttpServletResponse)
   */
  @Override protected void doPost(@NotNull SlingHttpServletRequest request,
      @NotNull SlingHttpServletResponse response) throws IOException {
    try {
      final PackageFormParams formParams =
          new Convert<>(request, PackageFormParams.class).getRequestData();

      // "email" param should be present
      if (StringUtils.isBlank(formParams.getEmail())) {
        throw new IllegalArgumentException(
            MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, UserProperties.EMAIL));
      }
      String url = formParams.getUrl();
      if (!url.contains(Constants.ROOT_CONTENT_PATH)) {
        url = Constants.ROOT_CONTENT_PATH + url;
        formParams.setUrl(url);
      }
      LOGGER.debug("url ---> {}", url);
      @Nullable Resource packageResource =
          request.getResourceResolver().getResource(url + "/jcr:content");

      String vendor;
      if (packageResource != null && packageResource.getValueMap()
          .containsKey(SaudiConstants.PACKAGE_DMC)) {

        vendor = packageResource.getValueMap().get(SaudiConstants.PACKAGE_DMC).toString();

      } else {
        vendor = CommonUtils.readDmcFromPath(null, url);
      }

      final ResponseMessage serviceResponse = mailchimpService.addSubscription(formParams, vendor);
      CommonUtils.writeJSON(response, serviceResponse.getStatusCode(), serviceResponse);
    } catch (Exception e) {
      outError(request, response, e, MESSAGE_ERROR_IN, DESCRIPTION);
    }
  }
}
