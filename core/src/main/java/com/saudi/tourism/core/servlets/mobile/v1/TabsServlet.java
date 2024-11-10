package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.TabsModel;
import com.saudi.tourism.core.services.mobile.v1.MobileLayoutService;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
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

/**
 * The servlet to get tabs.
 */
@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Tabs  Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/mobile/v1/tabs"
    })
@Slf4j
public class TabsServlet extends SlingSafeMethodsServlet {
  /**
   * The mobile Layout service.
   */
  @Reference
  private transient MobileLayoutService mobileLayoutService;

  /**
   * The constant ERROR_MESSAGE_ISNULL_PARAM_STATES.
   */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_TABID =
      "Parameters [tabId] is empty or null";

  /**
   * The constant TabId not found.
   */
  public static final String ERROR_MESSAGE_NO_TAB_FOUND = "No Tab found for this id";

  /**
   * The constant TAB_ID_PARAM.
   */
  public static final String TAB_ID_PARAM = "tabId";

  @Override
  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    var tabsRequestParams = new Convert<>(request, MobileRequestParams.class).getRequestData();

    var locale = tabsRequestParams.getLocale();
    var tabId = tabsRequestParams.getTabId();

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

    if (StringUtils.isBlank(tabId)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, ERROR_MESSAGE_ISNULL_PARAM_TABID);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_TAB_ID.getCode(),
          new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_ISNULL_PARAM_TABID)
              .toString());
      return;
    }

    try {
      TabsModel tab = mobileLayoutService.getTabById(tabsRequestParams);

      if ((StringUtils.isBlank(tabId) || StringUtils.isBlank(locale))) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.TAB_NOT_FOUND.getCode(),
            new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_TAB_FOUND)
                .toString());
        return;
      }

      if (Objects.isNull(tab)) {


        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.SUCCESS.getValue(),
            BusinessErrorCode.SUCCESS.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_TAB_FOUND).toString();
        return;
      }

      var successCode = BusinessErrorCode.SUCCESS.getCode();
      if (tab.getNoneDestinationFound()) {
        successCode = BusinessErrorCode.SUCCESS_NO_DESTINATION_FOUND.getCode();
      }

      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.SUCCESS.getValue(),
          successCode,
          RestHelper.getObjectMapper().writeValueAsString(tab));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/tabs", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /**
   * Enum for business error codes used in the TabsServlet.
   */
  private enum BusinessErrorCode {
    /**
     * Sucess.
     */
    SUCCESS(00),
    /**
     * Sucess but no destination found.
     */
    SUCCESS_NO_DESTINATION_FOUND(10),
    /**
     * Empty locale.
     */
    EMPTY_LOCALE(01),
    /**
     * Empty tabId.
     */
    EMPTY_TAB_ID(02),
    /**
     * No tab found for this tabId.
     */
    TAB_NOT_FOUND(03),
    /**
     * technical/unknown error.
     */
    UNKNOWN_ERROR(99);

    /**
     * code.
     */
    private final int code;

    BusinessErrorCode(int code) {
      this.code = code;
    }

    public int getCode() {
      return code;
    }
  }
}
