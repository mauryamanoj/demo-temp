package com.saudi.tourism.core.servlets.mobile.v1;

import com.drew.lang.annotations.NotNull;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.mobile.v1.calendar.CalendarService;
import com.saudi.tourism.core.services.mobile.v1.calendar.FetchCalendarRequest;

import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;

import static com.saudi.tourism.core.servlets.mobile.v1.GetCalendarBottomCardsServlet.SERVLET_PATH;

/** Servlet to fetch events. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Get Calendar Bottom Cards Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class GetCalendarBottomCardsServlet extends SlingSafeMethodsServlet {

  /** Possible paths for this servlet. */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/mobile/v1/calendar/bottom-cards";

  /** The calendar service. */
  @Reference private transient CalendarService calendarService;

  /** The error message no calendar dates found. */
  public static final String ERROR_MESSAGE_NO_CALENDAR_FOUND = "No calendar cards found for these dates";

  @Override
  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {
    final var fetchCalendarRequest =
        new Convert<>(request, FetchCalendarRequest.class).getRequestData();

    if (StringUtils.isBlank(fetchCalendarRequest.getLocale())) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_LOCALE.getCode(),
          new ResponseMessage(
                  MessageType.ERROR.getType(), Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE)
              .toString());
      return;
    }

    if (StringUtils.isBlank(fetchCalendarRequest.getStartDate())) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_START_DATE);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_START_DATE.getCode(),
          new ResponseMessage(
                  MessageType.ERROR.getType(), Constants.ERROR_MESSAGE_ISNULL_PARAM_START_DATE)
              .toString());
      return;
    }

    if (StringUtils.isBlank(fetchCalendarRequest.getEndDate())) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_END_DATE);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_END_DATE.getCode(),
          new ResponseMessage(
                  MessageType.ERROR.getType(), Constants.ERROR_MESSAGE_ISNULL_PARAM_END_DATE)
              .toString());
      return;
    }

    try {
      final var calendar = calendarService.getCalendarBottomCards(fetchCalendarRequest);

      if (Objects.nonNull(calendar)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.SUCCESS.getValue(),
            BusinessErrorCode.SUCCESS.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(calendar));
      } else {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.SUCCESS.getValue(),
            BusinessErrorCode.SUCCESS.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_CALENDAR_FOUND)
            .toString();
      }

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/calendar/bottom-cards", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /** Enum for business error codes used in the GetCalendarBottomCardsServlet. */
  private enum BusinessErrorCode {
    /** Success. */
    SUCCESS(00),

    /** Empty locale. */
    EMPTY_LOCALE(01),

    /** Empty startDate. */
    EMPTY_START_DATE(02),

    /** Empty endDate. */
    EMPTY_END_DATE(03),

    /** technical/unknown error. */
    UNKNOWN_ERROR(99);

    /** code. */
    private final int code;

    BusinessErrorCode(int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }
  }
}
