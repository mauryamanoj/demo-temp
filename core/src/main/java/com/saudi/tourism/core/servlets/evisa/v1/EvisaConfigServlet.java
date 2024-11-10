package com.saudi.tourism.core.servlets.evisa.v1;

import com.saudi.tourism.core.services.evisa.v1.EvisaService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
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

import static com.saudi.tourism.core.utils.Constants.PN_LOCALE;

/**
 * Servlet exposing eVisa config.
 */
@Component(
    service = Servlet.class,
    property = {
        Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "eVisa Config Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/evisa/config"
    })
@Slf4j
public class EvisaConfigServlet extends SlingAllMethodsServlet {

  /**
   * eVisa OSGI service.
   */
  @Reference
  private transient EvisaService evisaService;

  /**
   * question only .
   */
  public static final String QUESTIONS_ONLY = "questionsOnly";
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
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {


    boolean questionsOnly = false;
    if (null != request.getParameter(QUESTIONS_ONLY)) {
      questionsOnly = Boolean.parseBoolean(request.getParameter(QUESTIONS_ONLY));
    }
    boolean visaDetailsOnly = false;
    if (null != request.getParameter(VISA_DETAILS_ONLY)) {
      visaDetailsOnly = Boolean.parseBoolean(request.getParameter(VISA_DETAILS_ONLY));
    }
    final String visaGroup = request.getParameter(VISA_GROUP);
    final String responseCode = request.getParameter(RESPONSE_CODE);
    final String platform = request.getParameter(PLATFORM);


    try {
      if (request.getRequestParameter(PN_LOCALE) == null) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      if (questionsOnly && visaDetailsOnly) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_QUESTIONS_ONLY_VISA_DETAILS_ONLY);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_QUESTIONS_ONLY_VISA_DETAILS_ONLY);
        return;
      }
      final String locale = request.getRequestParameter(PN_LOCALE).getString();

      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper()
              .writeValueAsString(
                  evisaService.fetchFilteredEvisaConfig(locale, visaGroup, questionsOnly, visaDetailsOnly,
                      responseCode, platform)));
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", "eVisa Config", e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
