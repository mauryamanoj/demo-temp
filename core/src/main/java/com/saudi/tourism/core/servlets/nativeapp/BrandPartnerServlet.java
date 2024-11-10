package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.components.brands.BrandListModel;
import com.saudi.tourism.core.models.components.brands.BrandRequestParams;
import com.saudi.tourism.core.services.BrandPartnerService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Map;

/**
 * The type Get all brands for a locale.
 * Example servlet calls
 * /bin/api/v2/app/earning/partners?locale=en&new=true&limit=10
 * /bin/api/v2/app/earning/partners?locale=en&close=true&limit=10&latitude=12&longitude=40
 * /bin/api/v2/app/earning/partners?locale=en&homePage=true&latitude=12&longitude=40
 * /bin/api/v2/app/earning/partners?locale=en&popular=true&limit=7
 *
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Brand Partner Servlet Native App",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
            + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
            + BrandPartnerServlet.SERVLET_PATH
    })
@Slf4j
public class BrandPartnerServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1070")
  public static final String SERVLET_PATH = "/bin/api/v2/app/earning/partners";
  /**
   * The Brand Partner service.
   */
  @Reference
  private transient BrandPartnerService brandPartnerService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    BrandRequestParams brandRequestParams =
        new Convert<>(request, BrandRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(brandRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
            "Undefined locale");
        return;
      } else if (brandRequestParams.isHomePage()) {
        Map<String, BrandListModel> homePageListMap = brandPartnerService.getHomePageItems(request, brandRequestParams);
        CommonUtils.writeNewJSONFormatExclude(response, StatusEnum.SUCCESS.getValue(), homePageListMap);
      } else {
        BrandListModel model = brandPartnerService.getFilteredBrands(request, brandRequestParams);
        CommonUtils.writeNewJSONFormatExclude(response, StatusEnum.SUCCESS.getValue(), model);
      }
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.BRANDS_RES_TYPE,
          e.getLocalizedMessage(), e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          "Internal Server Error");
    }
  }
}
