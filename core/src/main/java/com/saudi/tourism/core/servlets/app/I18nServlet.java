package com.saudi.tourism.core.servlets.app;

import com.adobe.xfa.ut.StringUtils;
import com.saudi.tourism.core.models.app.i18n.I18nDetail;
import com.saudi.tourism.core.models.app.i18n.I18nObject;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceUtil;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Servlet that retrieves all the contact information for a locale.
 * http://localhost:4502/bin/api/v1/i18n./en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "i18n Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v1/i18n"})
@Slf4j
public class I18nServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    String locale = request.getRequestPathInfo().getSuffix();
    if (!StringUtils.isEmpty(locale)) {
      I18nObject output = getI18nInformation(request, locale);
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), output);
    } else {
      //no locale
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "Undefined locale"));
    }

  }

  /**
   * Retrieve the i18n nodes for a locale.
   *
   * @param request request
   * @param locale  locale
   * @return Object with the i18n info for a locale
   */
  private I18nObject getI18nInformation(SlingHttpServletRequest request, String locale) {

    Resource rootResource =
        request.getResourceResolver().getResource(Constants.APP_SAUDITOURISM_I18N_PATH + locale);

    List<I18nDetail> resultList = new ArrayList<>();
    I18nObject result = new I18nObject(
        locale.replace(Constants.FORWARD_SLASH_CHARACTER, Constants.BLANK), resultList);

    if (rootResource != null) {
      Iterator<Resource> iteratorResources = rootResource.listChildren();

      while (iteratorResources.hasNext()) {
        Resource resource = iteratorResources.next();
        ValueMap properties = ResourceUtil.getValueMap(resource);
        resultList.add(new I18nDetail(properties.get(Constants.SLING_KEY, Constants.BLANK),
            properties.get(Constants.SLING_MESSAGE, Constants.BLANK)));
      }
      result.setI18n(resultList);
    }
    return result;
  }
}
