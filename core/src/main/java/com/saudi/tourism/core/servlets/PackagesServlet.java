package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.packages.PackageFilterModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.models.components.packages.PackagesRequestParams;
import com.saudi.tourism.core.services.PackageService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
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
import java.io.IOException;
import java.util.ArrayList;

/**
 * The type Get all packages for a locale.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Packages Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/packages"})
@Slf4j
public class PackagesServlet extends SlingAllMethodsServlet {
  /**
   * The Packages service.
   */
  @Reference
  private transient PackageService packageService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    PackagesRequestParams eventsRequestParams =
        new Convert<>(request, PackagesRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(eventsRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE));
        return;
      }

      String source = Constants.WEB_SOURCE;
      if (null != request.getRequestParameter("source")) {
        source = request.getRequestParameter("source").toString();
      }
      AdminPageOption adminOptions =
          AdminUtil.getAdminOptions(eventsRequestParams.getLocale(), StringUtils.EMPTY);
      if ((source.equals(Constants.WEB_SOURCE) && adminOptions.isDisablePackages())
          || (source.equals(Constants.APP_SOURCE)
          && adminOptions.isDisableAppPackages())) { // disable results temporary
        PackagesListModel packages = new PackagesListModel();
        packages.setData(new ArrayList<>());
        PackageFilterModel filters = new PackageFilterModel();
        packages.setFilters(filters);
        packages.setPagination(new Pagination());
        CommonUtils.writeJSONExclude(response, StatusEnum.SUCCESS.getValue(),
            packages);
      } else {
        PackagesListModel packages = packageService.getFilteredPackages(eventsRequestParams);
        CommonUtils.writeJSONExclude(response, StatusEnum.SUCCESS.getValue(),
            packages);
      }



    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.PACKAGE_DETAIL_RES_TYPE,
          e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }
}
