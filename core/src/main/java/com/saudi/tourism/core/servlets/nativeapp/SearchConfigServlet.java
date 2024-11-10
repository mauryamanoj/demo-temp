package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.services.SearchConfigService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * The type Get all locale events filters.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Search Config Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/search/config"})
@Slf4j public class SearchConfigServlet extends SlingSafeMethodsServlet {
  /**
   * The Event service.
   */
  @Reference private transient SearchConfigService searchConfigService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String locale = CommonUtils.getLocale(request);
    String source = "web";
    if (null != request.getRequestParameter("source")) {
      source = request.getRequestParameter("source").toString();
    }

    try {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(searchConfigService.getConfig(locale, source)));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", Constants.SEARCH_CONFIG_SERVLET, e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }

  }
}
