package com.saudi.tourism.core.servlets.app;

import com.adobe.xfa.ut.StringUtils;
import com.saudi.tourism.core.models.app.i18n.I18nDetail;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Servlet that retrieves the countries for a page.
 * http://localhost:4502/bin/api/v1/app/countries
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Countries Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/countries"})
@Slf4j
public class CountriesServlet extends SlingAllMethodsServlet {

  /**
   * The User service.
   */
  @Reference
  private transient UserService userService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String locale = request.getParameter(Constants.LOCALE);
    if (!StringUtils.isEmpty(locale)) {
      List<I18nDetail> countries = new ArrayList<>();
      final ResourceBundle i18n = request.getResourceBundle(new Locale(locale));

      try (ResourceResolver resolver = userService.getResourceResolver()) {
        final String datasourcePath = "sauditourism/components/content/utils/country";
        final Resource datasourceResource = resolver.getResource(datasourcePath);

        Iterator<Resource> appTranslationResources = datasourceResource.listChildren();
        while (appTranslationResources.hasNext()) {
          Resource appTranslationResource = appTranslationResources.next();
          ValueMap properties = appTranslationResource.adaptTo(ValueMap.class);
          try {

            I18nDetail country =
                new I18nDetail(properties.get("text", Constants.BLANK),
                    i18n.getString(properties.get("value", Constants.BLANK)));
            countries.add(country);
          } catch (Exception e) {
            LOGGER.error("error in country");
          }
        }


      }
      if (countries.isEmpty()) {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), "No Countries found for the page"));
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), countries);
      }
    }
  }

}
