package com.saudi.tourism.core.models.errrorhandler;

import com.adobe.acs.commons.errorpagehandler.ErrorPageHandlerService;
import com.adobe.acs.commons.wcm.vanity.VanityURLService;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.Self;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.apache.commons.lang3.StringUtils.substringBeforeLast;
import static org.apache.commons.mail.EmailConstants.TEXT_SUBTYPE_HTML;

/**
 * Handler for 404 Error for packages pages.
 */
@Model(adaptables = SlingHttpServletRequest.class)
@Slf4j
public class Error404PageHandler {

  /**
   * Packages fragment within url path.
   */
  private static final String PACKAGES_FRAGMENT = Constants.FORWARD_SLASH_CHARACTER
      + Constants.PACKAGES + Constants.FORWARD_SLASH_CHARACTER;

  /**
   * Current request.
   */
  @Self
  private SlingHttpServletRequest request;

  /**
   * Current response.
   */
  @ScriptVariable
  private SlingHttpServletResponse response;

  /**
   * ACS Commons error page handler service.
   */
  @OSGiService
  private ErrorPageHandlerService errorPageHandlerService;

  /**
   * ACS Commons Vanity url handler service.
   */
  @OSGiService
  private VanityURLService vanityURLService;

  /**
   * Determine if page that causes 404 Error is located under /packages path. This is needed to
   * identify correct error page handler.
   * @return if project error page handler is to be used.
   */
  public boolean isCustomHandler() {
    boolean packagesPage =
        TEXT_SUBTYPE_HTML.equalsIgnoreCase(request.getRequestPathInfo().getExtension())
        && request.getRequestPathInfo().getResourcePath().contains(PACKAGES_FRAGMENT);
    if (packagesPage && LOGGER.isDebugEnabled()) {
      LOGGER.debug("Page with {} path fragment not found. 404 will be handled manually.",
          PACKAGES_FRAGMENT);
    }
    return packagesPage;
  }

  /**
   * Handling 404 error response for pages under /packages path.
   * This method will return an empty string or throw an exception. String is returned to avoid
   * htl errors as method is invoked via htl-command.
   * @throws ServletException exception that vanityURLService.dispatch may throw
   * @throws RepositoryException exception that vanityURLService.dispatch may throw
   * @throws IOException exception that vanityURLService.dispatch may throw
   * @return temp value
   */
  public String handle404() throws ServletException, RepositoryException, IOException {
    if (errorPageHandlerService != null && errorPageHandlerService.isEnabled()) {

      // Handle ACS AEM Commons vanity logic
      if (errorPageHandlerService.isVanityDispatchCheckEnabled()
          && vanityURLService != null && vanityURLService.dispatch(request, response)) {
        return null;
      }

      // Check for and handle 404 Requests properly according on Author/Publish
      if (errorPageHandlerService.doHandle404(request, response)) {
        final String path =
            substringBeforeLast(request.getRequestPathInfo().getResourcePath(), PACKAGES_FRAGMENT)
                + PACKAGES_FRAGMENT;
        response.setStatus(HttpStatus.SC_NOT_FOUND);
        errorPageHandlerService.resetRequestAndResponse(request, response, HttpStatus.SC_NOT_FOUND);
        errorPageHandlerService.includeUsingGET(request, response, path);
      }
    }
    return null;
  }

}
