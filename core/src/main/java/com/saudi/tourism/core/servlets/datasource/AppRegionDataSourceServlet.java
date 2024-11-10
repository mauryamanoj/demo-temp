package com.saudi.tourism.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableList;
import com.saudi.tourism.core.models.app.location.AppRegion;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.saudi.tourism.core.servlets.datasource.AppRegionDataSourceServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.datasource.AppRegionDataSourceServlet.RESOURCE_TYPE;
import static com.saudi.tourism.core.utils.Constants.EQUAL;
import static com.saudi.tourism.core.utils.Constants.ROOT_APP_CONTENT_PATH;

/**
 * AppRegion Datasource: it can be used to include App regions to a dropdown
 * Touch UI dropdown.
 * To use this datasource see below jcr structure
 * + myselect
 * - sling:resourceType = "granite/ui/components/coral/foundation/form/select"
 * - emptyText = "Select"
 * - name = "./myselect"
 * + datasource
 * - sling:resourceType = sauditourism/datasource/app-regions"
 */

@Component(service = Servlet.class,
           immediate = true,
           property = {Constants.SERVICE_DESCRIPTION + EQUAL + DESCRIPTION,
               ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + EQUAL + RESOURCE_TYPE,
               ServletResolverConstants.SLING_SERVLET_METHODS + EQUAL + HttpConstants.METHOD_GET})
@Slf4j
public class AppRegionDataSourceServlet extends SlingSafeMethodsServlet {

  /**
   *
   * This data source servlet description.
   */
  static final String DESCRIPTION = "Saudi Tourism - App Regions Datasource Servlet";

  /**
   * This servlet processing resource type.
   */
  static final String RESOURCE_TYPE = "sauditourism/datasource/app-regions";

  /**
   * RegionCityService.
   */
  @Reference
  private transient RegionCityService regionCityService;
  /**
   * Path to regions path.
   */
  private static final String REGION_PATH_FORMAT = ROOT_APP_CONTENT_PATH + "/%s/regions";

  /**
   * Override doGet method of SlingSafeMethodsServlet, for more details read
   * class java doc.
   *
   * @param req  SlingHttpServletRequest
   * @param resp SlingHttpServletResponse
   * @throws ServletException error
   * @throws IOException      error
   */
  @Override
  protected void doGet(final SlingHttpServletRequest req,
      final @NotNull SlingHttpServletResponse resp) throws ServletException, IOException {

    final String currentPath = req.getParameter("item");
    final String locale = CommonUtils.getLanguageForPath(currentPath);
    String path = String.format(REGION_PATH_FORMAT, locale);

    List<AppRegion> list = regionCityService.loadAllAppRegionsPages(req, path);

    final ImmutableList.Builder<Resource> builder = ImmutableList.builder();
    if (list != null) {
      final ResourceResolver resolver = req.getResourceResolver();
      list.forEach(appRegion -> {
        String id = appRegion.getId();
        String regionId = id.substring(id.lastIndexOf("/") + 1);
        ValueMap vm1 = new ValueMapDecorator(new HashMap<>());
        vm1.put("value", regionId);
        vm1.put("text", appRegion.getTitle());
        builder.add(
            new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
                vm1));
      });
    }

    DataSource ds = new SimpleDataSource(builder.build().iterator());
    req.setAttribute(DataSource.class.getName(), ds);
  }
}
