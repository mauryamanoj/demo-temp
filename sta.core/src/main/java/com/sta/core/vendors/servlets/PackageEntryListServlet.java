package com.sta.core.vendors.servlets;

import com.sta.core.vendors.models.PackageListEntry;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.sta.core.vendors.ScriptConstants;
import com.sta.core.vendors.service.PackageEntryService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.sta.core.vendors.PackageEntryUtils;

import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * Package Entry List Servlet.
 */
@Component(service = Servlet.class,
           property = {
              Constants.SERVICE_DESCRIPTION + Constants.EQUAL + PackageEntryListServlet.DESCRIPTION,
              SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
              SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/author/api/v1/entry/package/list"})

@Slf4j
public class PackageEntryListServlet extends SlingSafeMethodsServlet {

  /**
   * Service description.
   */
  static final String DESCRIPTION = "Package Entry List Servlet";

  /**
   * Package entry service.
   */
  @Reference
  private PackageEntryService packageEntryService;

  /**
   * Get list of package entries.
   * @param request request
   * @param response response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {

    try {
      String dmc = PackageEntryUtils.getMandatoryParameter(request, ScriptConstants.VENDOR_KEY);
      List<PackageListEntry> entries = packageEntryService.getEntries(dmc);
      CommonUtils.writeJSON(response, HttpStatus.SC_OK, entries);
    } catch (Exception e) {
      LOGGER.error("Unable to retrieve package list for {}: ",
          request.getParameter(ScriptConstants.VENDOR_KEY), e);
      CommonUtils.writeJSON(response, HttpStatus.SC_INTERNAL_SERVER_ERROR,
          new ResponseMessage(MessageType.ERROR.getType(), e.getMessage()));
    }
  }
}
