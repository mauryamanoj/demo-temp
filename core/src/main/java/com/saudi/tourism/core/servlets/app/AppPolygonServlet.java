package com.saudi.tourism.core.servlets.app;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.saudi.tourism.core.models.app.location.AppPolygonModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Objects;
import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * Servlet to get polygon coordinates.
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + AppPolygonServlet.SERVLET_DESC,
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + AppPolygonServlet.API_END_POINT })
@Slf4j
public class AppPolygonServlet extends SlingSafeMethodsServlet {

  /**
   * Resource type of app's polygon component.
   */
  public static final String RT_APP_POLYGON_PAGE = "sauditourism/components/structure/app-polygon-page";

  /**
   * Path to polygon components.
   */
  public static final String POLYGON_PATH_FORMAT = ROOT_APP_CONTENT_PATH + "/%s/polygon";

  /**
   * Polygon Description.
   */
  public static final String SERVLET_DESC = "Polygon Servlet";
  /**
   * Polygon API end point.
   */
  public static final String API_END_POINT = "/bin/api/v1/app/region-polygon";

  /**
   * app polygon details.
   * @param request
   * @param response
   * @throws ServletException ServletException.
   * @throws IOException IOException.
   */
  @Override
  protected void doGet(@NotNull final SlingHttpServletRequest request,
                       @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    String locale = CommonUtils.getLocale(request);
    String path = String.format(POLYGON_PATH_FORMAT, locale);
    AppPolygonModel appPolygonData = loadCoordinates(request.getResourceResolver(), path);
    if (null != appPolygonData) {
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(appPolygonData));
    } else {
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          RestHelper.getObjectMapper().writeValueAsString("No Data Found"));
    }
  }

  /**
   * load coordinates.
   * @param resolver resolver.
   * @param path page path.
   * @return polygon coordinates.
   */
  private AppPolygonModel loadCoordinates(ResourceResolver resolver, String path) {
    PageManager pageManager = resolver.adaptTo(PageManager.class);
    Page polygonPage = pageManager.getPage(path);
    if (Objects.isNull(polygonPage)) {
      throw new IllegalArgumentException("The path is not accessible");
    }
    if (polygonPage.getContentResource().isResourceType(RT_APP_POLYGON_PAGE)) {
      AppPolygonModel appPolygon = polygonPage.getContentResource().adaptTo(AppPolygonModel.class);
      if (Objects.nonNull(appPolygon)) {
        return appPolygon;
      }
    }
    return null;
  }
}
