package com.saudi.tourism.core.servlets.nativeapp;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.io.IOUtils;
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

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

/**
 * Get MultipleIDs Deatils By HY  API Servlet.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience with MultipleIds Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_POST,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
    + "/bin/api/v2/experience/multiple-ids"})
public class ExperiencesMultipleIdsServlet extends SlingAllMethodsServlet {

  private static final long serialVersionUID = 1L;

  /**
   * The constant EXPERIENCE_MULTIPLE_IDS_SERVLET.
   */
  public static final String EXPERIENCE_MULTIPLE_IDS_SERVLET = "Experiences Multiple Ids servlet";

  /**
   * The Constant statusCode.
   */
  static final int STATUS_CODE = 404;

  /**
   * The Experiences service.
   */
  @Reference
  private transient ExperienceService service;

  /**
   * for handling json format in response.
   */
  private JsonParser parser = new JsonParser();

  /**
   * @param request  request.
   * @param response response .
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {
      String body = IOUtils.toString(request.getReader());

      if (!checkValidRequestBody(body)) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            "Invalid Request Body");
      } else {
        Object jsonArray = service.getMultipleIds(body);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            jsonArray.toString());
      }
      LOGGER.error("Success :{}, {}", EXPERIENCE_MULTIPLE_IDS_SERVLET, response);
    } catch (IOException e) {
      LOGGER.error("IO Exception :{}", e.getMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), "IO Exception");
    } catch (Exception e) {
      LOGGER.error("Exception :{}", e.getMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "Something went wrong ");
    }
  }

  /**
   * @param body string body .
   * @return boolean.
   */
  private boolean checkValidRequestBody(String body) {
    boolean flag = false;
    JsonElement json = parser.parse(body);
    if (json.getAsJsonObject().has("experience_ids")) {
      if (null != json.getAsJsonObject().getAsJsonArray("experience_ids")
          && json.getAsJsonObject().getAsJsonArray("experience_ids").size() >= 1) {
        flag = true;
        return flag;
      }
    }
    return flag;
  }
}
