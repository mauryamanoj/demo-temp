package com.saudi.tourism.core.servlets;

import com.google.gson.Gson;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.greenTaxi.GreenTaxiModel;
import com.saudi.tourism.core.services.GreenTaxiService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
 * The type Get all Green Taxi Content for a locale.
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Green taxi Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
      + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
      + "/bin/api/v1/green-taxi/getCards"})
@Slf4j
public class GreenTaxiServlet extends SlingAllMethodsServlet {

  /**
   * The green taxi service.
   */
  @Reference
  private transient GreenTaxiService greenTaxiService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    String locale = StringUtils.EMPTY;
    if (null != request.getRequestParameter("locale")) {
      locale = request.getRequestParameter("locale").toString();
    }
    try {

      if (StringUtils.isBlank(locale)) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
        return;
      }
      GreenTaxiModel model = greenTaxiService.getData(request, locale);
      Gson gson = new Gson();
      CommonUtils.writeExtJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          gson.toJson(model));
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.GREEN_TAXI_RES_TYPE,
          e.getLocalizedMessage());
      CommonUtils.writeExtJSONFormat(response, Constants.EXT_ERROR_CODE,
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()).toString());
    }
  }
}
