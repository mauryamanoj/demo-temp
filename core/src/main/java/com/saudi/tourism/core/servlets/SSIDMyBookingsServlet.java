package com.saudi.tourism.core.servlets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.services.SSIDLoginUserService;
import com.saudi.tourism.core.login.services.impl.SSIDLoginUserServiceImpl;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.BookingService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Map;

/**
 * This is used to get experience booking options from the external site Sample URL :
 * http://localhost:4502/bin/api/v1/bookings . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "MyBookings Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/bookings" })
@Slf4j
public class SSIDMyBookingsServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;
  /**
   * MyBookings service.
   */
  @Reference
  private transient BookingService service;
  /**
   * Login service.
   */
  @Reference
  private transient SSIDLoginUserService loginservice;

  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    if (request.getHeader("userID") == null) {
      CommonUtils.writeJSON(response, StatusEnum.BAD_REQUEST.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Missing user id"));
    }
    try {
      Object obj = getHalaYallaUserId(request, response);
      if (obj == null || (obj != null && String.valueOf(obj).isEmpty())) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), "No Booking Found");
        return;
      }
      Map<String, Object> queryStrings = RestHelper.getParameters(request);
      queryStrings.put("ref", String.valueOf(obj).replaceAll("\"", ""));
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          service.getAllBookings(queryStrings));

      LOGGER.error("Success :{}, {}", Constants.EXPERIENCEBOOKINGOPTIONS_SERVLET, response);

    } catch (MiddlewareException e) {
      LOGGER.error("Error in SSID Get user details from Middleware", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in MyBookings Servlet V3"));

    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.EXPERIENCEBOOKINGOPTIONS_SERVLET,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.BAD_REQUEST.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

  /**
   * Het User Id for Hallayalla from user profile.
   * @param request  request for userprofile servlet
   * @param response response for userprofile servlet
   * @throws MiddlewareException Middleware exception .
   * @return HallayallaID
   */
  private Object getHalaYallaUserId(SlingHttpServletRequest request,
      SlingHttpServletResponse response) throws MiddlewareException {

    try {
      String userprofile = loginservice.getUserDetails(SSIDGetUserDetailsServlet.getUserID(request),
          SSIDLoginUserServiceImpl.PROFILE);

      JsonParser typeParser = new JsonParser();
      JsonObject profileObj = typeParser.parse(userprofile).getAsJsonObject();
      return profileObj.get(Constants.HALAYALLA_USER_ID);
    } catch (IOException e) {
      LOGGER.error("Error parsing the HALAYALLA_USER_ID : ", e);
      return "";
    }
  }

}
