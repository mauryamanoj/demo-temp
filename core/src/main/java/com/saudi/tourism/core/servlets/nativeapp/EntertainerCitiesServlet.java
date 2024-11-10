package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.beans.nativeapp.RegionCityEntertainer;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.services.EntertainerCitiesService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
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
import java.util.List;
import java.util.Objects;

/**
 * The servlet to get  Entertainer cities.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Entertainer Cities  Servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/v2/entertainer/cities"})
@Slf4j public class EntertainerCitiesServlet extends SlingSafeMethodsServlet {
  /**
   * The EntertainerCities service.
   */
  @Reference private transient EntertainerCitiesService entertainerCitiesService;

  /**
   *  This parameter activeEntertainer.
   */
  static final String PARAM_ACTIVE_ENTERTAINER = "activeEntertainer";

  /**
   * This parameter cityOfResidency.
   */
  static final String PARAM_CITY_OF_RESIDENCY = "cityOfResidency";

  @Override protected void doGet(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {


    boolean activeEntertainer = false;

    if (StringUtils.isNotBlank(request.getParameter(PARAM_ACTIVE_ENTERTAINER))) {
      activeEntertainer = Boolean.parseBoolean(request.getParameter(PARAM_ACTIVE_ENTERTAINER));
    }

    String cityOfResidency = null;
    if (StringUtils.isNotBlank(request.getParameter(PARAM_CITY_OF_RESIDENCY))) {
      cityOfResidency = request.getParameter(PARAM_CITY_OF_RESIDENCY);
    }




    String locale = request.getParameter(Constants.LOCALE);
    String filterStatus = request.getParameter(Constants.PARAM_STATUS);
    if (StringUtils.isBlank(locale)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(),
              Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
      return;
    }

    try {

      List<RegionCityEntertainer> regionCityEntertainerList =
          entertainerCitiesService.getCityConfig(locale, request.getResourceResolver(), cityOfResidency,
              activeEntertainer);
      if (Objects.isNull(regionCityEntertainerList)) {
        CommonUtils.writeNewJSONFormat(response, StatusEnum.NOT_FOUND.getValue(),
            "No Entertainer Cities config found");
        return;
      }
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(regionCityEntertainerList));
    } catch (Exception e) {
      LOGGER.error("Error in getting module: {}", "/bin/api/v2/entertainer/cities", e);
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }

  }
}
