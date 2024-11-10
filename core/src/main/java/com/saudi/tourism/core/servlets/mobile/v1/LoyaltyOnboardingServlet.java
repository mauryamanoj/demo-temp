package com.saudi.tourism.core.servlets.mobile.v1;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.NativeAppHomepageService;
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
import static com.saudi.tourism.core.utils.PrimConstants.MOBILE_APP_ROOT;

/** Loyalty Onboarding Servlet . */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Loyalty Onboarding Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS
          + Constants.EQUAL
          + "/bin/api/mobile/v1/loyalty-onboarding"
    })
public class LoyaltyOnboardingServlet extends SlingSafeMethodsServlet {

  /** error message. */
  public static final String NO_LOYALTY_ONBOARDING_CONFIG_IS_FOUND_FOR_THIS_LOCALE =
      "No Loyalty Onboarding Config is found for this locale";

  /** NativeAppHomepageService Reference. */
  @Reference
  private NativeAppHomepageService nativeAppHomepageService;

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var locale = request.getParameter(Constants.LOCALE);

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

    final var resourcePath = MOBILE_APP_ROOT + Constants.FORWARD_SLASH_CHARACTER + locale;

    try {
      final var model = nativeAppHomepageService.getLoyaltyOnboarding(request, resourcePath);

      if (Objects.isNull(model)) {
        MobileUtils.writeMobileV1Format(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            BusinessErrorCode.NO_LOYALTY_ONBOARDING_CONFIG_FOUND.getCode(),
            RestHelper.getObjectMapper().writeValueAsString(null));
        new ResponseMessage(
                MessageType.ERROR.getType(), NO_LOYALTY_ONBOARDING_CONFIG_IS_FOUND_FOR_THIS_LOCALE)
            .toString();
        return;
      }

      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.SUCCESS.getValue(),
          BusinessErrorCode.SUCCESS.getCode(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/mobile/v1/loyalty-onboarding", e);
      MobileUtils.writeMobileV1Format(
          response,
          StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          BusinessErrorCode.UNKNOWN_ERROR.getCode(),
          MessageType.ERROR.getType());
    }
  }

  /** Enum for business error codes used in the LoyaltyOnboardingServlet. */
  private enum BusinessErrorCode {
    /** Sucess. */
    SUCCESS(00),
    /** Empty locale. */
    EMPTY_LOCALE(01),
    /** Unable to find resource. */
    UNABLE_TO_FIND_RESOURCE(02),
    /** No loyalty onboarding config found. */
    NO_LOYALTY_ONBOARDING_CONFIG_FOUND(03),
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
