package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.evisa.v1.EvisaService;
import com.saudi.tourism.core.utils.Constants;
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

/**
 * SSP Onboarding Servlet .
 */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
        Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Onboarding Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS
            + Constants.EQUAL
            + "/bin/api/mobile/v1/evisa"
    })
public class EVisaConfigServlet extends SlingSafeMethodsServlet {

  /**
   * error message.
   */
  public static final String NO_EVISA_CONFIG_IS_FOUND_FOR_THIS_LOCALE =
      "No eVisa  Config is found for this locale";

  /**
   * eVisa OSGI service.
   */
  @Reference
  private transient EvisaService evisaService;


  /** The constant ERROR_MESSAGE_ISNULL_PARAM_VISAGROUP. */
  public static final String ERROR_MESSAGE_ISNULL_PARAM_VISAGROUP =
      "Parameters [visaGroup] is empty or null";
  /**
   * visaGroup.
   */
  public static final String VISA_GROUP = "visaGroup";

  /**
   * responseCode.
   */
  public static final String RESPONSE_CODE = "responseCode";
  /**
   * visa details only.
   */
  public static final String VISA_DETAILS_ONLY = "visaDetailsOnly";
  /**
   * visa details only.
   */
  public static final String PLATFORM = "platform";

  @Override
  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {


    final String visaGroup = request.getParameter(VISA_GROUP);
    final String responseCode = request.getParameter(RESPONSE_CODE);
    final var locale = request.getParameter(Constants.LOCALE);

    if (StringUtils.isBlank(visaGroup)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, ERROR_MESSAGE_ISNULL_PARAM_VISAGROUP);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          BusinessErrorCode.EMPTY_VISAGROUP.getCode(),
          new ResponseMessage(MessageType.ERROR.getType(), ERROR_MESSAGE_ISNULL_PARAM_VISAGROUP)
              .toString());
      return;
    }


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
    try {
      final var model = evisaService.fetchMobileFilteredEvisaConfig(locale, visaGroup,
          responseCode);

      if (Objects.isNull(model)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.NO_EVISA_CONFIG_FOUND.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(
            MessageType.ERROR.getType(), NO_EVISA_CONFIG_IS_FOUND_FOR_THIS_LOCALE)
            .toString();
        return;
      }

      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.SUCCESS.getValue(),
          BusinessErrorCode.SUCCESS.getCode(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/evisa", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /**
   * Enum for business error codes used in the ViewAllServlet.
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
     * No evisa config found.
     */
    NO_EVISA_CONFIG_FOUND(02),
    /**
     * No VisaGroup found.
     */
    EMPTY_VISAGROUP(03),
    /**
     * Unknown Error.
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
