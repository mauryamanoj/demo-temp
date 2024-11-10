package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.cards.CardsListModel;
import com.saudi.tourism.core.models.components.cards.CardsRequestParams;
import com.saudi.tourism.core.services.CardsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

/**
 * The type Get all cards for a locale.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Cards Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v2/cards"})
@Slf4j
public class CardsServlet extends SlingAllMethodsServlet {
  /**
   * The cards service.
   */
  @Reference
  private transient CardsService cardsService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    CardsRequestParams cardsRequestParams =
        new Convert<>(request, CardsRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(cardsRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
        return;
      }
      if (StringUtils.isBlank(cardsRequestParams.getType())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_TYPE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_TYPE).toString());
        return;
      }
      CardsListModel model = cardsService.getFilteredCards(request, cardsRequestParams, Constants.VERSION_V2);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(model));
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.CARDS_RES_TYPE,
          e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()).toString());
    }
  }
}
