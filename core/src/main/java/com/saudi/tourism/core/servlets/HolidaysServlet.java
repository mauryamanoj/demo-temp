package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.Holidays;
import com.saudi.tourism.core.models.common.ObjectResponse;
import com.saudi.tourism.core.models.components.tripplan.StartEndDatesFilter;
import com.saudi.tourism.core.services.CalendarService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.saudi.tourism.core.servlets.HolidaysServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.HolidaysServlet.SERVLET_PATH;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This servlet returns holidays list according to the request.
 */
@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
               SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
               SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH})
@Slf4j
public class HolidaysServlet extends BaseAllMethodsServlet {

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "Holidays API (Servlet)";

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/holidays";

  /**
   * The Event service.
   */
  @Reference
  private transient CalendarService holidayService;

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request,
      @NotNull SlingHttpServletResponse response) throws IOException {

    try {
      final Convert<StartEndDatesFilter> convert =
          new Convert<>(request, StartEndDatesFilter.class);
      final Map<String, Object> parametersMap = convert.getMapData();
      checkNecessaryParameters(request, response, parametersMap, Constants.PN_LOCALE);

      final StartEndDatesFilter holidaysFilter = convert.getRequestData();
      final Holidays resultHolidaysMap;

      if (Stream.of(holidaysFilter.getStartDate(), holidaysFilter.getEndDate())
          .anyMatch(Objects::nonNull)) {
        // Filter according to start / end dates
        resultHolidaysMap = holidayService.getConfiguredHolidaysMap(holidaysFilter);
      } else {
        // Only locale parameter is there
        resultHolidaysMap = holidayService.getConfiguredHolidaysMap(holidaysFilter.getLocale());
      }

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), RestHelper.getObjectMapper()
          .writeValueAsString(
              new ObjectResponse<>(MessageType.SUCCESS.getType(), resultHolidaysMap)));

    } catch (Exception e) {
      outError(request, response, e, MESSAGE_ERROR_IN, DESCRIPTION);
    }
  }
}
