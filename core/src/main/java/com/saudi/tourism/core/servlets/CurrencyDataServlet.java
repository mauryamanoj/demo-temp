package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.CurrencyDataService;
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
 * This is used to get all currency data via an external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v1/currency?currency=SAR . Sample URL :
 * http://localhost:4502/bin/api/v1/currency?currency=EUR,GBP,CAD,PLN,LKR,INR .
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Currency Data Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/currency" })
@Slf4j
public class CurrencyDataServlet extends SlingAllMethodsServlet {

  /**
   * The Currency Data service.
   */
  @Reference
  private transient CurrencyDataService currencyDataService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    String currency = request.getParameter("currency");

    try {
      if (StringUtils.isBlank(currency)) {
        LOGGER.error(Constants.DO_GET_MENTHOD,
            Constants.ERROR_MESSAGE_ISNULL_PARAM_CURRENCY_SOURCE);
        CommonUtils.writeJSON(response, StatusEnum.BAD_REQUEST.getValue(), new ResponseMessage(
            MessageType.ERROR.getType(), Constants.ERROR_MESSAGE_ISNULL_PARAM_CURRENCY_SOURCE));
        return;
      }

      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          currencyDataService.getCurrencyDataFromApi(currency));

    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.CURRENCY_DATA_SERVLET,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(),
              MessageType.ERROR.getType() + ", for more details please see logs"));
    }

  }

}
