package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
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
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.Iterator;

/**
 * Servlet that retrieves all the AppTitles information for a locale.
 * http://localhost:4502/bin/api/v1/appTranslations?locale=
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "App Translations Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/appTranslations"})
@Slf4j
public class AppTranslationsServlet extends SlingAllMethodsServlet {

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    // Get locale.
    String locale = request.getParameter("locale");

    if (locale != null) {
      // Add the sub directory using locale.
      ResourceResolver resolver = null;
      try {
        resolver = userService.getResourceResolver();
        Resource res = resolver
            .getResource(Constants.APP_SAUDITOURISM_APP_I18N_PATH + "/" + locale);
        if (res == null) {
          res = resolver
            .getResource(Constants.APP_SAUDITOURISM_APP_I18N_PATH + "/" + Constants.DEFAULT_LOCALE);
        }
        if (res != null) {
          JSONObject jsonObject = new JSONObject();

          Iterator<Resource> appTranslationResources = res.listChildren();
          loopAllI18keys(jsonObject, appTranslationResources);
          response.setContentType("application/json; charset=UTF-8");
          response.getWriter().print(jsonObject);

        } else {
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              new ResponseMessage(MessageType.ERROR.getType(),
              "No resource found " + Constants.APP_SAUDITOURISM_APP_I18N_PATH + locale));
        }
      } catch (Exception e) {
        LOGGER.error("Exception occurred", e);
      } finally {
        if (resolver.isLive()) {
          resolver.close();
        }
      }
    } else {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "No locale params given"));
    }
  }

  /**
   * Loop all i 18 keys.
   *
   * @param jsonObject              the json object
   * @param appTranslationResources the app translation resources
   */
  private void loopAllI18keys(final JSONObject jsonObject,
      final Iterator<Resource> appTranslationResources) {
    while (appTranslationResources.hasNext()) {
      Resource appTranslationResource = appTranslationResources.next();
      ValueMap properties = appTranslationResource.adaptTo(ValueMap.class);
      try {
        String value = properties.get(Constants.SLING_MESSAGE, Constants.BLANK);
        if (!value.isEmpty()) {
          jsonObject.put(properties.get(Constants.SLING_KEY, appTranslationResource.getName()),
              StringEscapeUtils.unescapeHtml(value));
        }
      } catch (JSONException e) {
        LOGGER.error("Error reading i18n nodes");
      }
    }
  }



}
