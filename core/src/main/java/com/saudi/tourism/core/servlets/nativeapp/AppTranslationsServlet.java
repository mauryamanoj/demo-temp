package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.Gson;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;

/**
 * Servlet that retrieves all the AppTitles information for a locale.
 * http://localhost:4502/bin/api/v2/appTranslations?locale=
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "App Translations Servlet",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + "/bin/api/v2/appTranslations"})
@Slf4j
public class AppTranslationsServlet extends SlingSafeMethodsServlet {

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;
  /**
    * Target Android.
   */
  static final String TARGET_ANDROID = "android";
  /**
   * Target IOS.
   */
  static final String TARGET_IOS = "ios";
  /**
   * Target Parameter.
   */
  static final String PARAM_TARGET = "target";

  /**
   * Original variable.
   */
  static final String ORIGNAL_VARIABLE = "{{x}}";

  /**
   * Original pattern for variables.
   */
  static final String ORIGNAL_PATTERN = "\\{\\{([0-9])\\}\\}";

  /**
   * IOS variable.
   */
  static final String IOS_PATTERN = "%@";

  /**
   * ANDROID PATTERN.
   */
  static final String ANDROID_PATTERN = "%$1\\$s";

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    // Get locale.
    String locale = request.getParameter("locale");

    // Get target.
    String target = request.getParameter(PARAM_TARGET);

    if (locale != null) {
      try (ResourceResolver resolver = userService.getResourceResolver()) {
        Resource res = resolver.getResource(Constants.APP_SAUDITOURISM_APP_I18N_PATH_NATIVE_APP + "/" + locale);
        if (res == null) {
          res = resolver
            .getResource(Constants.APP_SAUDITOURISM_APP_I18N_PATH_NATIVE_APP + "/" + Constants.DEFAULT_LOCALE);
        }
        if (res != null) {
          JSONObject jsonObject = new JSONObject();

          Iterator<Resource> appTranslationResources = res.listChildren();
          loopAllI18keys(jsonObject, appTranslationResources, target);

          Gson gson = new Gson();
          gson.toJson(jsonObject);
          CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              gson.toJson(jsonObject));

        } else {
          CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
              "No resource found " + Constants.APP_SAUDITOURISM_APP_I18N_PATH + " " + locale);
        }
      }
    } else {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "No locale params given");
    }
  }

  /**
   * Loop all i 18 keys.
   *
   * @param jsonObject the json object
   * @param target target
   * @param appTranslationResources the app translation resources
   */
  private void loopAllI18keys(final JSONObject jsonObject,
                              final Iterator<Resource> appTranslationResources, final String target) {
    while (appTranslationResources.hasNext()) {
      Resource appTranslationResource = appTranslationResources.next();
      ValueMap properties = appTranslationResource.adaptTo(ValueMap.class);
      try {
        String value = properties.get(Constants.SLING_MESSAGE, Constants.BLANK);
        if (!value.isEmpty()) {
          value = StringEscapeUtils.unescapeHtml(value);
          if (TARGET_ANDROID.equals(target)) {
            value = value.replaceAll(ORIGNAL_PATTERN, ANDROID_PATTERN);
          }
          if (TARGET_IOS.equals(target)) {
            value = value.replaceAll(ORIGNAL_PATTERN, IOS_PATTERN);
          }
          jsonObject.put(properties.get(Constants.SLING_KEY, appTranslationResource.getName()),
              value);
        }
      } catch (JSONException e) {
        LOGGER.error("Error reading i18n nodes");
      }
    }
  }

}
