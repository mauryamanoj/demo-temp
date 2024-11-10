package com.saudi.tourism.core.servlets.datasource;

import com.adobe.cq.commerce.common.ValueMapDecorator;
import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.saudi.tourism.core.services.EntertainerCitiesService;
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
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;

import static com.saudi.tourism.core.servlets.datasource.EntertainerLocationDatasourceServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.datasource.EntertainerLocationDatasourceServlet.RESOURCE_TYPE_VALUE;
import static com.saudi.tourism.core.utils.Constants.DEFAULT_LOCALE;
import static com.saudi.tourism.core.utils.Constants.EQUAL;

/**
 * Touch UI dropdown.
 * To use this datasource see below jcr structure
 * - sling:resourceType = "granite/ui/components/coral/foundation/form/select"
 * + datasource
 * - sling:resourceType = sauditourism/datasource/locations"
 */

@Component(service = Servlet.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + EQUAL + DESCRIPTION,
        ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + EQUAL + RESOURCE_TYPE_VALUE,
        ServletResolverConstants.SLING_SERVLET_METHODS + EQUAL + HttpConstants.METHOD_GET})
@Slf4j
public class EntertainerLocationDatasourceServlet extends SlingSafeMethodsServlet {

  /**
   * This data source servlet description.
   */
  static final String DESCRIPTION = "Saudi Tourism - Entertainer Locations Datasource Servlet";

  /**
   * This servlet processing resource type.
   */
  static final String RESOURCE_TYPE_VALUE = "sauditourism/datasource/locations";

  /**
   * entertainerCitiesService.
   */
  @Reference
  private transient EntertainerCitiesService entertainerCitiesService;


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


    JsonArray array = entertainerCitiesService.getEntertainerLocations(DEFAULT_LOCALE);


    final ImmutableList.Builder<Resource> builder = ImmutableList.builder();
    if (array != null) {
      final ResourceResolver resolver = req.getResourceResolver();

      array.forEach(location -> {
        ValueMap vm1 = new ValueMapDecorator(new HashMap<>());
        vm1.put("value", location.getAsJsonObject().get("id").getAsInt());
        vm1.put("text", location.getAsJsonObject().get("name").getAsString());
        builder.add(
            new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
                vm1));
      });
    }

    DataSource ds = new SimpleDataSource(builder.build().iterator());
    req.setAttribute(DataSource.class.getName(), ds);
  }

}
