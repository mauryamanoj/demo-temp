package com.saudi.tourism.core.servlets.nativeapp.v3;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.packages.PackageFilterModel;
import com.saudi.tourism.core.models.components.packages.PackagesListModel;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * This is used to get all experiences from the external site (
 * http://apilayer.net/api/live ) Sample URL :
 * http://localhost:4502/bin/api/v3/experience . Sample URL :
 */
@Component(service = Servlet.class, property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Experience Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v3/app/experience"})
@Slf4j
public class ExperiencesV3Servlet extends SlingSafeMethodsServlet {

  private static final long serialVersionUID = 1L;
  /**
   * The ExperienceService service.
   */
  @Reference
  private transient ExperienceService service;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
        throws ServletException, IOException {

    try {
      String locale = "en";
      if (null != request.getRequestParameter("locale")) {
        locale = request.getRequestParameter("locale").toString();
      }
      String source = "web";
      if (null != request.getRequestParameter("source")) {
        source = request.getRequestParameter("source").toString();
      }
      AdminPageOption adminOptions =
            AdminUtil.getAdminOptions(locale, StringUtils.EMPTY);
      if ((source.equals("web") && adminOptions.isDisableWebHalayallaPackages())
            || (source.equals("app") && adminOptions.isDisableAppHalayallaPackages())) { // disable results temporary
        PackagesListModel packages = new PackagesListModel();
        packages.setData(new ArrayList<>());
        PackageFilterModel filters = new PackageFilterModel();
        packages.setFilters(filters);
        packages.setPagination(new Pagination());
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              RestHelper.getObjectMapper().writeValueAsString(packages).toString());
      } else {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
              service.getAllExperiencesV2(RestHelper.getParameters(request)).toString());
        LOGGER.error("Success :{}, {}", Constants.EXPERIENCES_SERVLET, response);
      }
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", Constants.EXPERIENCES_SERVLET,
            e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }

  }

}
