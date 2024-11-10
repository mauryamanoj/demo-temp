package com.saudi.tourism.core.servlets.nativeapp.v3;

import com.saudi.tourism.core.services.AppTranslationService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Map;

/**
 * Servlet that retrieves all the AppTitles information for a locale.
 * http://localhost:4502/bin/api/v3/appTranslations?locale=
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "App Translations Servlet v3",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/api/v3/appTranslations"})
@Slf4j
public class AppTranslationsServlet extends SlingSafeMethodsServlet {

  /**
   * The appTranslationService.
   */
  @Reference
  private transient AppTranslationService appTranslationService;
  /**
    * Target Android.
   */
  static final String TARGET_ANDROID = "android";
  /**
   * Target Parameter.
   */
  static final String PARAM_TARGET = "target";
  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    // Get locale.
    String locale = request.getParameter("locale");
    if (locale == null) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "No locale params given");
      return;
    }

    // Get target.
    String target = request.getParameter(PARAM_TARGET);
    if (target == null) {
      target = TARGET_ANDROID; // Default to android
    }
    Map<String, String> allKeyValues =
        appTranslationService.loopAllI18keys(request.getResourceResolver(), target, locale);

    if (allKeyValues == null) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "No resource found " + Constants.APP_SAUDITOURISM_APP_I18N_PATH + " " + locale);
      return;

    }
    CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
        RestHelper.getObjectMapper().writeValueAsString(allKeyValues));

  }
}
