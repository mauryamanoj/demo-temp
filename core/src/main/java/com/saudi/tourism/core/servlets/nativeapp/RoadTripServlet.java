package com.saudi.tourism.core.servlets.nativeapp;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/**
 * Road Trip Native APP.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "RoadTrip Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
          + HttpConstants.METHOD_POST,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
          + "/bin/api/v2/roadtrip"})
public class RoadTripServlet extends SlingAllMethodsServlet {

  /**
   * roadTripScenariosService.
   */
  @Reference
  private RoadTripScenariosService roadTripScenariosService;
  /**
   * DoGEt method .
   * @param request request .
   * @param response response.
   * @throws ServletException ServletException .
   * @throws IOException IOException .
   */
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String limitParam = request.getParameter("limit");
    String offsetParam = request.getParameter("offset");
    String locale = request.getParameter("locale");
    String body = IOUtils.toString(request.getReader());
    int limit = 0;
    int offset = 0;
    try {
      if (!StringUtils.isBlank(limitParam)) {
        limit = Integer.valueOf(limitParam);
      }
      if (!StringUtils.isBlank(offsetParam)) {
        offset = Integer.valueOf(offsetParam);
      }
      if (StringUtils.isEmpty(locale)) {
        locale = "en";
      }

      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(
            roadTripScenariosService.getRoadTripScenariosNativeApp(limit, offset, locale, body)).toString());
    } catch (IOException e) {
      LOGGER.error(e.getMessage(), e);
    }
  }
}
