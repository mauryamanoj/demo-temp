package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.AreaDetailsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * Servlet for get area details.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Area Details Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/app/area-details" })
@Slf4j
public class AppAreaDetailsServlet extends SlingSafeMethodsServlet {

  /**
   * The service.
   */
  @Reference
  private transient AreaDetailsService areaDetailsService;

  /**
   * Get area details.
   * @param request  servlet request
   * @param response servlet response
   * @throws ServletException servlet exception
   * @throws IOException      io exception
   */
  @Override
  protected void doGet(@NotNull final SlingHttpServletRequest request,
                       @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      if (null == request.getRequestParameter("area")) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_AREA);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_AREA));
        return;
      }
      CommonUtils.writeJSONExclude(response, StatusEnum.SUCCESS.getValue(),
          areaDetailsService.getAreaDetails(request));
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", "Area Details",
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }
  }
}
