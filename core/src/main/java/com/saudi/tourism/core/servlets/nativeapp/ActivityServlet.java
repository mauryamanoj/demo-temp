package com.saudi.tourism.core.servlets.nativeapp;

import com.saudi.tourism.core.beans.activities.ActivitiesFilterModel;
import com.saudi.tourism.core.beans.activities.ActivitiesListModel;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.events.Pagination;
import com.saudi.tourism.core.models.components.tripplan.v1.Activity;
import com.saudi.tourism.core.models.components.tripplan.v1.SearchActivityFilter;
import com.saudi.tourism.core.services.ActivityService;
import com.saudi.tourism.core.servlets.BaseAllMethodsServlet;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.models.components.tripplan.TripPlannerUtils.checkGetByPath;
import static com.saudi.tourism.core.servlets.nativeapp.ActivityServlet.DESCRIPTION;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_METHODS;
import static org.apache.sling.api.servlets.ServletResolverConstants.SLING_SERVLET_PATHS;

/**
 * This servlet provides an API for searching activities.
 */
@Component(service = Servlet.class,
        immediate = true,
        property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + DESCRIPTION,
                SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
            SLING_SERVLET_PATHS + Constants.EQUAL + ActivityServlet.SERVLET_V3_PATH})
@Slf4j
public class ActivityServlet extends BaseAllMethodsServlet {

  /**
   * This servlet's description.
   */
  static final String DESCRIPTION = "Activities API (Servlet)";

  /**
   * URL path for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_V3_PATH = "/bin/api/v3/activities";

  /**
   * The Activities Service.
   */
  @Reference
  private transient ActivityService activitiesService;

  /**
   * ResourceBundleProvider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Override
  protected void doGet(@NotNull final SlingHttpServletRequest request,
                       @NotNull final SlingHttpServletResponse response) throws IOException {

    try {
      final Convert<SearchActivityFilter> parameters =
              new Convert<>(request, SearchActivityFilter.class);
      final Map<String, Object> parametersMap = parameters.getMapData();
      final SearchActivityFilter filter = parameters.getRequestData();

      // Check if the path parameter was passed in the request (or as a suffix)
      //  (.../activities?path=/path/to/activities/page
      //  or .../activities.json/path/to/activities/page)
      String path = checkGetByPath(request, filter);

      if (StringUtils.isBlank(path)) {
        // If the path parameter wasn't provided, language must be set
        checkNecessaryParameters(request, response, parametersMap, Constants.PN_LOCALE);
      }
        // Sample Request
        // http://localhost:4502/bin/api/v3/activities?locale=en&city=jeddah&limit=10&offset=1

      ActivitiesListModel model = getActivitiesWithFiltersAndPagination(request, filter);

        // Out successful result

      CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(model));


    } catch (IllegalArgumentException e) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(),
          e.getLocalizedMessage());
    } catch (Exception e) {
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }
  }

  /**
   * This Method will get All Activities with Filters for Cities & with Pagination Support.
   *
   * @param request SlingHttpServletRequest SlingHttpServlet Request.
   * @param filter  SearchActivityFilter Search Activity Filter.
   * @return ActivitiesListModel Activities List Model.
   */
  @NotNull
  private ActivitiesListModel getActivitiesWithFiltersAndPagination(@NotNull SlingHttpServletRequest request,
                                                                    SearchActivityFilter filter) {
    ActivitiesListModel model = new ActivitiesListModel();
    List<Activity> allActivities = activitiesService.getActivityList(filter.getLocale());
    Locale locale = new Locale(filter.getLocale());
    ResourceBundle i18n = i18nProvider.getResourceBundle(locale);


    final List<Activity> resultList = new LinkedList<>();
    allActivities.stream().filter(activity -> activitiesService.matchFilters(activity, filter))
            .map(activity -> activity.setLinkText(i18n.getString(Constants.LEARNMORE)))
            .collect(Collectors.toCollection(() -> resultList));

    ActivitiesFilterModel filters = new ActivitiesFilterModel();
    List<AppFilterItem> items = new ArrayList<>();
    Map<String, String> cityMap = new HashMap<>();
    for (Activity activity : allActivities) {
      RegionCity city = activity.getCity();
      if (!cityMap.containsKey(city.getId())) {
        cityMap.put(city.getId(), city.getName());
      }
    }

    cityMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(city -> {
      AppFilterItem item = new AppFilterItem(city.getKey(), city.getValue());
      items.add(item);
    });

    Pagination pagination = new Pagination();
    pagination.setTotal(resultList.size());
    String limitParam = request.getParameter("limit");

    int limit;
    int offset = 0;

    if (StringUtils.isNotBlank(limitParam)) {
      limit = Integer.parseInt(limitParam);
    } else {
      limit = NumberConstants.CONST_TEN;
    }
    pagination.setLimit(limit);

    String offsetParam = request.getParameter("offset");

    if (StringUtils.isNotBlank(offsetParam)) {
      offset = Integer.parseInt(offsetParam);
    }

    pagination.setOffset(offset);

    model.setPagination(pagination);

    List<Activity> filteredActivities = resultList.stream().skip(offset).limit(limit).collect(Collectors.toList());

    model.setData(filteredActivities);
    filters.setCity(items);
    model.setFilters(filters);
    model.setPagination(pagination);
    return model;
  }

  /**
   * Post servlet method.
   *
   * @param request  servlet request
   * @param response servlet response
   * @throws IOException if response is already committed or another servlet exception
   */
  @Override
  protected void doPost(@NotNull final SlingHttpServletRequest request,
                        @NotNull final SlingHttpServletResponse response) throws IOException {
    this.doGet(request, response);
  }
}
