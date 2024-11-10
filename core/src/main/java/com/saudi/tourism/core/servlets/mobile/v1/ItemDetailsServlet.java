package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsDetailsResponseModel;
import com.saudi.tourism.core.services.mobile.v1.MobileLayoutService;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.MessageType;
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

/**
 * The servlet to get  Item details.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Item details  Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/mobile/v1/itemdetails"})
@Slf4j
public class ItemDetailsServlet extends SlingSafeMethodsServlet {
  /**
   * The mobile Layout service.
   */
  @Reference
  private transient MobileLayoutService mobileLayoutService;

  /**
   * The constant ERROR_MESSAGE_ISNULL_PARAM_STATES.
   */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_ITEMID = "Parameters [itemId] is empty or null";

  /**
   * The constant TabId not found.
   */
  public static final String ERROR_MESSAGE_NO_ITEM_FOUND = "No Item found for this id";

  /**
   * The constant TAB_ID_PARAM.
   */
  public static final String ITEM_ID_PARAM = "itemId";


  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    MobileRequestParams itemDetailsRequestParams =
        new Convert<>(request, MobileRequestParams.class).getRequestData();

    String locale = itemDetailsRequestParams.getLocale();
    String itemId = itemDetailsRequestParams.getItemId();
    if (StringUtils.isBlank(locale)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      MobileUtils.writeMobileV1Format(response, StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_LOCALE.getCode(),
          new ResponseMessage(MessageType.ERROR.getType(),
              Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
      return;
    }

    if (StringUtils.isBlank(itemId)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, ERROR_MESSAGE_ISNULL_PARAM_ITEMID);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_ITEM_ID.getCode(),
          new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_ISNULL_PARAM_ITEMID)
              .toString());
      return;
    }

    try {
      ItemsDetailsResponseModel item = mobileLayoutService.getItemById(itemDetailsRequestParams);

      if (Objects.isNull(item)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.ITEM_NOT_FOUND.getCode(),
            new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_NO_ITEM_FOUND)
                .toString());
        return;
      }

      MobileUtils.writeMobileV1Format(response, StatusEnum.SUCCESS.getValue(), BusinessErrorCode.SUCCESS.getCode(),
          RestHelper.getObjectMapper().writeValueAsString(item));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/itemdetails", e);
      MobileUtils.writeMobileV1Format(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }

  }

  /**
   * Enum for business error codes used in the ItemDetailsServlet.
   */
  private enum BusinessErrorCode {
    /**
     * Sucess.
     */
    SUCCESS(00),
    /**
     * Empty locale.
     */
    EMPTY_LOCALE(01),
    /**
     * Empty itemId.
     */
    EMPTY_ITEM_ID(02),
    /**
     * No item found for this itemId.
     */
    ITEM_NOT_FOUND(03),
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
