package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.app.entertainer.AppGame;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.GameService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
import java.util.List;

/**
 * The servlet to get  games.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Game  Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/games"})
@Slf4j public class GameServlet extends SlingSafeMethodsServlet {
  /**
   * The Event service.
   */
  @Reference private transient GameService gameService;

  @Override protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {


    String locale = request.getParameter(Constants.LOCALE);
    String filterStatus = request.getParameter(Constants.PARAM_STATUS);
    if (StringUtils.isBlank(locale)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(),
              Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
      return;
    }

    try {
      List<AppGame> listAppGames = gameService.getAllGames(request.getResourceResolver(), locale, filterStatus);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(listAppGames));
    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/v2/games", e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }

  }
}
