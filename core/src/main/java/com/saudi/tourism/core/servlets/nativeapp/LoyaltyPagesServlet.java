package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.app.page.PageInfo;
import com.saudi.tourism.core.services.v2.LoyaltyPagesService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Node;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.List;

/**
 * Servlet that retrieves the list of content pages.
 * http://localhost:4502/bin/api/v2/app/loyalty/pages./en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Loyalty Pages Servlet Native APP",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v2/app/loyalty/pages"})
@Slf4j
public class LoyaltyPagesServlet extends SlingAllMethodsServlet {
  /**
   * LoyaltyPages Service.
   */
  @Reference
  private transient LoyaltyPagesService loyaltyPagesService;
  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    String resourcePath = CommonUtils.getGetAppPath(request.getRequestPathInfo().getSuffix());

    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
          .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);
        List<PageInfo> listOfPages = loyaltyPagesService.searchRecursivelyPropPres(node, null);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(listOfPages));
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            "No information for the content pages");
      }
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.APP_LOYALTY_CONTENT_TEMPLATE,
          e.getLocalizedMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
           "Internal Server Error while fetching the loyalty pages");
    }

  }
}
