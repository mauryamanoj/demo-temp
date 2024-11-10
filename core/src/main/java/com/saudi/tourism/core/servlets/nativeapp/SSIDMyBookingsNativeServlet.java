package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.login.KeycloakSSIDFunctionnalException;
import com.saudi.tourism.core.login.MiddlewareException;
import com.saudi.tourism.core.login.services.v2.SSIDService;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.BookingService;
import com.saudi.tourism.core.services.HyEncryptionService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.ExceptionUtils;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javax.servlet.http.HttpServletResponse.SC_OK;

/**
 * This is used to get experience booking options from the external site Sample URL :
 * http://localhost:4502/bin/api/v3/bookings . Sample URL :
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "MyBookings Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v3/bookings"})
@Slf4j
public class SSIDMyBookingsNativeServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;
  /**
   * Constants.
   */

  public static final String PROFILE = "profile";
  /**
   * MyBookings service.
   */
  @Reference
  private transient BookingService service;
  /**
   * SSID service.
   */
  @Reference
  private transient SSIDService ssidService;

  /**
   * Hy Encryption Service.
   */
  @Reference
  private transient HyEncryptionService hyEncryptionService;


  @Override
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    if (request.getHeader("userID") == null || request.getHeader("token") == null) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "Missing user id or token");
      return;
    }
    try {
      Object obj = getHalaYallaUserId(request, response);
      if (obj == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "Internal Server Error");
        return;
      }

      String encryptedAndEncodedSSID = hyEncryptionService.encrypt(String.valueOf(obj).replaceAll("\"", ""));

      Map<String, Object> emptyResponse = new HashMap();
      List<String> emptyList = Collections.emptyList();
      emptyResponse.put("data", emptyList);

      if (obj != null && String.valueOf(obj).isEmpty()) {

        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(emptyResponse));
        return;
      }
      Map<String, Object> queryStrings = RestHelper.getParameters(request);
      queryStrings.put("ref", encryptedAndEncodedSSID);
      final Object bookings = service.getAllBookings(queryStrings);

      Map<String, Object> responseBookings = new HashMap();
      final JsonObject bookingsAsJsonObject =
          ((JsonElement) bookings).getAsJsonObject();
      if (bookingsAsJsonObject != null && bookingsAsJsonObject.has("status") && bookingsAsJsonObject.get("status")
          .getAsString().equals("Success")) {
        bookingsAsJsonObject.remove("status");
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(), bookingsAsJsonObject.toString());
        LOGGER.debug("Success :{}, {}", Constants.EXPERIENCEBOOKINGOPTIONS_SERVLET, response);
        return;
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(emptyResponse));
        return;
      }

    } catch (KeycloakSSIDFunctionnalException e) {
      LOGGER.error("Error in SSID Get user details from SSID", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(e.getCode()),
          ExceptionUtils.getMessageUsers(e.getCode(), "Error in MyBookings Servlet V3 from SSID"));
    } catch (MiddlewareException e) {
      LOGGER.error("Error in SSID Get user details from Middleware", e);
      CommonUtils.writeNewJSONFormat(response, Integer.parseInt(ExceptionUtils.getStatusUsers(e)),
          ExceptionUtils.getMessageUsers(ExceptionUtils.getStatusUsers(e), "Error in MyBookings Servlet V3"));

    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}", Constants.EXPERIENCEBOOKINGOPTIONS_SERVLET,
          e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "Internal Server Error");
    }

  }

  /**
   * Het User Id for Hallayalla from user profile.
   *
   * @param request  request for userprofile servlet
   * @param response response for userprofile servlet
   * @return HallayallaID
   * @throws MiddlewareException MiddlewareException
   */
  private Object getHalaYallaUserId(SlingHttpServletRequest request,
                                    SlingHttpServletResponse response)
      throws MiddlewareException, KeycloakSSIDFunctionnalException {

    ResponseMessage respObj = null;
    try {
      respObj = ssidService.getUserInfo(request.getHeader("token"));

      if (respObj.getStatusCode() != SC_OK) {
        LOGGER.error("Bad response fromGET user info from SSID. Code:" + respObj.getStatusCode()
            + ", message: " + respObj.getMessage());
        throw new KeycloakSSIDFunctionnalException(respObj.getMessage(), String.valueOf(respObj.getStatusCode()));
      }



      String userprofile = respObj.getMessage();
      JsonParser parser = new JsonParser();
      JsonObject profileObj = parser.parse(userprofile).getAsJsonObject();
      return profileObj.get("ssid");
    } catch (IOException | ServletException e) {
      LOGGER.warn("Error while getting the ssid from SSID service : ", e);
      return null;
    }
  }
}
