package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Base servlet with some helper methods.
 */
@Slf4j
public class BaseAllMethodsServlet extends SlingAllMethodsServlet {

  /**
   * Error message for exceptions.
   */
  protected static final String MESSAGE_ERROR_IN = "Error in {0}.";

  /**
   * Checks that all obligatory parameters are specified for request.
   *
   * @param request        servlet request
   * @param response       servlet response
   * @param parameterNames parameters list to check
   */
  protected void checkNecessaryParameters(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response, final String... parameterNames) {
    checkNecessaryParameters(request, response,
        Collections.unmodifiableMap(request.getParameterMap()), parameterNames);
  }

  /**
   * Checks that all obligatory parameters are specified for request.
   *
   * @param request        servlet request
   * @param response       servlet response
   * @param parametersMap  map of parameters from convert instance
   * @param parameterNames parameters list to check
   */
  protected void checkNecessaryParameters(final SlingHttpServletRequest request,
      final SlingHttpServletResponse response, Map<String, Object> parametersMap,
      final String... parameterNames) {

    final List<String> missingParams = new LinkedList<>();
    for (String param : parameterNames) {
      // String.valueOf to handle Object[] from request params map
      final Object parameterValue = parametersMap.get(param);
      if (parameterValue == null || (parameterValue.getClass().isArray() && ArrayUtils
          .isEmpty((Object[]) parameterValue))) {
        missingParams.add(param);
      }
    }

    if (!missingParams.isEmpty()) {
      throw new IllegalArgumentException(
          MessageFormat.format(Constants.ERR_MISSING_PARAMETERS, missingParams.toString()));
    }
  }

  /**
   * Outputs error to log & response.
   *
   * @param request       current servlet request
   * @param response      servlet response
   * @param exception     exception
   * @param message       message to output (message format)
   * @param messageParams message format parameters for the message
   * @throws IOException if response stream is already closed
   */
  protected void outError(final SlingHttpServletRequest request,
      final @NotNull SlingHttpServletResponse response, final Exception exception,
      final String message, final String... messageParams) throws IOException {
    this.outError(request, response, StatusEnum.SUCCESS, exception, message, messageParams);
  }

  /**
   * Outputs error to log & response.
   *
   * @param request       current servlet request
   * @param response      servlet response
   * @param status        status
   * @param exception     exception
   * @param message       message to output (message format)
   * @param messageParams message format parameters for the message
   * @throws IOException if response stream is already closed
   */
  protected void outError(final SlingHttpServletRequest request,
      final @NotNull SlingHttpServletResponse response, StatusEnum status,
      final Exception exception, final String message,
      final String... messageParams) throws IOException {

    final String logMessage = MessageFormat.format(message, messageParams);
    LOGGER.error("[" + request.getMethod() + "]" + StringUtils.SPACE + logMessage, exception);

    // Fulfill response output message with some additional data from the Exception
    String responseMessage = logMessage;
    if (exception != null) {
      final String exceptionMessage = exception.getLocalizedMessage();
      if (StringUtils.isNotBlank(exceptionMessage)) {
        responseMessage = logMessage + StringUtils.SPACE + exceptionMessage;
      }

      final Class<?> exceptionType = exception.getClass();
      responseMessage =
          responseMessage + StringUtils.SPACE + "[[" + exceptionType.getSimpleName() + "]]";
    }

    CommonUtils.writeJSON(response, status.getValue(), RestHelper.getObjectMapper()
        .writeValueAsString(new ResponseMessage(MessageType.ERROR.getType(), responseMessage)));

    response.flushBuffer();
  }


  /**
   * Outputs error to log & response.
   *
   * @param request       current servlet request
   * @param response      servlet response
   * @param status        status
   * @param exception     exception
   * @param message       message to output (message format)
   * @param messageParams message format parameters for the message
   * @throws IOException if response stream is already closed
   */
  protected void outError(final SlingHttpServletRequest request,
                          final @NotNull SlingHttpServletResponse response, String status,
                          final Exception exception, final String message,
                          final String... messageParams) throws IOException {

    final String logMessage = MessageFormat.format(message, messageParams);
    LOGGER.error("[" + request.getMethod() + "]" + StringUtils.SPACE + logMessage, exception);

    // Fulfill response output message with some additional data from the Exception
    String responseMessage = logMessage;
    if (exception != null) {
      final String exceptionMessage = exception.getLocalizedMessage();
      if (StringUtils.isNotBlank(exceptionMessage)) {
        responseMessage = logMessage + StringUtils.SPACE + exceptionMessage;
      }

      final Class<?> exceptionType = exception.getClass();
      responseMessage =
        responseMessage + StringUtils.SPACE + "[[" + exceptionType.getSimpleName() + "]]";
    }

    CommonUtils.writeJSON(response, Integer.parseInt(status), RestHelper.getObjectMapper()
        .writeValueAsString(new ResponseMessage(MessageType.ERROR.getType(), responseMessage)));

    response.flushBuffer();
  }

  /**
   * Returns locale from /es/some/page... url suffix, or from full path provided as a suffix.
   *
   * @param suffix suffix provided in the request
   * @return locale if it was found in the suffix
   */
  @Nullable
  public static String getLanguageFromSuffix(@NotNull final String suffix) {
    if (StringUtils.startsWith(suffix, Constants.ROOT_CONTENT_PATH)) {
      return CommonUtils.getLanguageForPath(suffix);
    }

    final boolean firstIsSlash = suffix.charAt(0) == '/';
    final int startIndex;
    final int endIndex;
    if (firstIsSlash) {
      startIndex = Constants.ONE;
      // length of '/en'
      endIndex = Constants.THREE;
    } else {
      startIndex = Constants.ZERO;
      // length of 'en'
      endIndex = Constants.TWO;
    }

    // Check if has language in the path
    if ('/' == suffix.charAt(endIndex)) {
      // es/some/page or /es/some/page -> "es"
      return suffix.substring(startIndex, endIndex);
    }

    return null;
  }
}
