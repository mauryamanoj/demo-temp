package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.hotels.HotelsFilterModel;
import com.saudi.tourism.core.models.components.hotels.HotelsListModel;
import com.saudi.tourism.core.models.components.hotels.HotelsRequestParams;
import com.saudi.tourism.core.services.v2.HotelService;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.RestHelper;
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
import java.util.ArrayList;

/**
 * The type Get all hotels for a locale.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Hotels Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v2/hotels"})
@Slf4j
public class HotelsServlet extends SlingAllMethodsServlet {
  /**
   * The Hotels service.
   */
  @Reference
  private transient HotelService hotelService;

  @Override
  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    HotelsRequestParams hotelsRequestParams =
        new Convert<>(request, HotelsRequestParams.class).getRequestData();

    try {

      if (StringUtils.isBlank(hotelsRequestParams.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(),
                Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE).toString());
        return;
      }
      AdminPageOption adminOptions =
          AdminUtil.getAdminOptions(hotelsRequestParams.getLocale(), StringUtils.EMPTY);
      if (adminOptions.isDisableHotels()) { // disable results temporary
        HotelsListModel hotelsListModel = new HotelsListModel();
        hotelsListModel.setData(new ArrayList<>());
        HotelsFilterModel filters = new HotelsFilterModel();
        hotelsListModel.setFilters(filters);
        hotelsListModel.setPagination(new Pagination());
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(hotelsListModel));
      } else {
        HotelsListModel model = hotelService.getFilteredPackages(request, hotelsRequestParams);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(model));
      }
    } catch (Exception e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.HOTELS_RES_TYPE,
          e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()).toString());
    }

  }
}
