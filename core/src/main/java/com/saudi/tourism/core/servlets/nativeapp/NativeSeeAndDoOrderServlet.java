package com.saudi.tourism.core.servlets.nativeapp;

import com.google.gson.Gson;
import com.saudi.tourism.core.models.app.page.NativeAppSeeAndDoOrder;
import com.saudi.tourism.core.services.v2.SeeAndDoService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;

import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * NativeSeeAndDoOrderServlet .
 */
@Component(service = Servlet.class, immediate = true, property = {Constants.SERVICE_DESCRIPTION
    + Constants.EQUAL + "Native App See & Do Order Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
    + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
    + "/bin/api/v2/app/see-do-order"})
public class NativeSeeAndDoOrderServlet extends SlingSafeMethodsServlet {
  /**
   * SeeDoOrder .
   */
  private static final String  SEE_DO_ORDER = "seeDoOrder";

  /**
   * SeeAndDOService Reference.
   */
  @Reference
  private SeeAndDoService seeAndDoService;

  @Override
  protected void doGet(@NotNull SlingHttpServletRequest request,
                       @NotNull SlingHttpServletResponse response) throws ServletException, IOException {
    String resourcePath = null;
    if (!StringUtils.isEmpty(request.getParameter("locale"))) {
      resourcePath =
        ROOT_APP_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER
          + request.getParameter("locale");
    }

    if (!StringUtils.isEmpty(resourcePath)) {
      NativeAppSeeAndDoOrder model = seeAndDoService.getNativeSeeAndDoOrder(request, resourcePath);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            "No See and Do section is found for this locale");
      } else {
        Gson gson = new Gson();
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(), gson.toJson(model));
      }
    } else {
      //no locale
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          "Undefined locale");
    }
  }
}
