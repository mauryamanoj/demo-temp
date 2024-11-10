package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionsModel;
import com.saudi.tourism.core.services.mobile.v1.MobileLayoutService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.RestHelper;
import lombok.extern.slf4j.Slf4j;
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

/** The servlet to get view all section infos. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "View All  Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/mobile/v1/viewall"
    })
@Slf4j
public class ViewAllServlet extends SlingSafeMethodsServlet {
  /** The mobile Layout service. */
  @Reference private transient MobileLayoutService mobileLayoutService;

  /** The constant ERROR_MESSAGE_ISNULL_PARAM_SECTIONID. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_SECTIONID =
      "Parameters [sectionId] is empty or null";

  /** The constant ERROR_MESSAGE_NO_SECTION_FOUND. */
  public static final String ERROR_MESSAGE_NO_SECTION_FOUND = "No Section found for this id";

  /** The constant SECTION_ID_PARAM. */
  public static final String SECTION_ID_PARAM = "sectionId";

  @Override
  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    MobileRequestParams viewAllRequestParams =
        new Convert<>(request, MobileRequestParams.class).getRequestData();

    var locale = viewAllRequestParams.getLocale();
    var sectionId = viewAllRequestParams.getSectionId();

    if (StringUtils.isBlank(locale)) {
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

    if (StringUtils.isBlank(sectionId)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, ERROR_MESSAGE_ISNULL_PARAM_SECTIONID);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_SECTION_ID.getCode(),
          new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_ISNULL_PARAM_SECTIONID)
              .toString());
      return;
    }

    try {
      SectionsModel section = mobileLayoutService.getSectionById(viewAllRequestParams);

      if ((StringUtils.isBlank(sectionId) || StringUtils.isBlank(locale))) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.SECTION_NOT_FOUND.getCode(),
            new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_SECTION_FOUND)
                .toString());
        return;
      }
      if (Objects.isNull(section)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.SUCCESS.getValue(),
            BusinessErrorCode.SUCCESS.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_SECTION_FOUND).toString();
        return;
      }

      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.SUCCESS.getValue(),
          BusinessErrorCode.SUCCESS.getCode(),
          RestHelper.getObjectMapper().writeValueAsString(section));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/viewall", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /** Enum for business error codes used in the ViewAllServlet. */
  private enum BusinessErrorCode {
    /** Sucess. */
    SUCCESS(00),
    /** Empty locale. */
    EMPTY_LOCALE(01),
    /** Empty sectionId. */
    EMPTY_SECTION_ID(02),
    /** No Section found for this sectionId. */
    SECTION_NOT_FOUND(03),
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
