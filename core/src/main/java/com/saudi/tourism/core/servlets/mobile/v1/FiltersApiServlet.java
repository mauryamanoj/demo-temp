package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.mobile.v1.filters.FetchMobileFiltersRequest;
import com.saudi.tourism.core.services.mobile.v1.filters.MobileFiltersService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Objects;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/** Filters API Servlet. */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Filters API Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/mobile/v1/viewall/filters"
    })
public class FiltersApiServlet extends SlingSafeMethodsServlet {

  /** error message. */
  public static final String NO_FILTER_IS_FOUND_FOR_THIS_LOCALE =
      "No filter is found for this locale";

  /** Mobile Filters service. */
  @Reference private transient MobileFiltersService mobileFiltersService;

  @Override
  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var fetchMobileFiltersRequest =
        new Convert<>(request, FetchMobileFiltersRequest.class).getRequestData();

    if (StringUtils.isBlank(fetchMobileFiltersRequest.getLocale())) {
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

    try {
      final var model = mobileFiltersService.getFilters(fetchMobileFiltersRequest);

      if (Objects.isNull(model)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.NO_FILTER_FOUND.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(MessageType.ERROR.getType(), NO_FILTER_IS_FOUND_FOR_THIS_LOCALE)
            .toString();
        return;
      }

      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.SUCCESS.getValue(),
          BusinessErrorCode.SUCCESS.getCode(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/viewall/filters", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /** Enum for business error codes used in the ViewAllServlet. */
  private enum BusinessErrorCode {
    /** Success. */
    SUCCESS(00),
    /** Empty locale. */
    EMPTY_LOCALE(01),
    /** No filter found. */
    NO_FILTER_FOUND(02),
    /** Unknown Error. */
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
