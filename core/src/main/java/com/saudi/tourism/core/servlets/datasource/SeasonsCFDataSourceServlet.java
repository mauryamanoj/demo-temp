package com.saudi.tourism.core.servlets.datasource;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.google.common.collect.ImmutableList;
import com.saudi.tourism.core.services.seasons.v1.SeasonsCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceMetadata;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;

import static com.saudi.tourism.core.servlets.datasource.SeasonsCFDataSourceServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.datasource.SeasonsCFDataSourceServlet.RESOURCE_TYPE_VALUE;
import static com.saudi.tourism.core.utils.PrimConstants.EQUAL;

/**
 * Touch UI dropdown. To use this datasource see below jcr structure - sling:resourceType =
 * "granite/ui/components/coral/foundation/form/select" + datasource - sling:resourceType =
 * sauditourism/datasource/seasons"
 */
@Component(
    service = Servlet.class,
    immediate = true,
    property = {
      Constants.SERVICE_DESCRIPTION + EQUAL + DESCRIPTION,
      ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + EQUAL + RESOURCE_TYPE_VALUE,
      ServletResolverConstants.SLING_SERVLET_METHODS + EQUAL + HttpConstants.METHOD_GET
    })
@Slf4j
public class SeasonsCFDataSourceServlet extends SlingSafeMethodsServlet {
  /** This data source servlet description. */
  static final String DESCRIPTION = "Saudi Tourism - Seasons CF Datasource Servlet";

  /** This servlet processing resource type. */
  static final String RESOURCE_TYPE_VALUE = "sauditourism/datasource/seasons";

  /**
   * Seasons CF service.
   */
  @Reference
  private transient SeasonsCFService seasonsCFService;

  /**
   * Override doGet method of SlingSafeMethodsServlet, for more details read class java doc.
   *
   * @param req SlingHttpServletRequest
   * @param resp SlingHttpServletResponse
   * @throws ServletException error
   * @throws IOException error
   */
  @Override
  protected void doGet(
      final SlingHttpServletRequest req, @NonNull final SlingHttpServletResponse resp)
      throws ServletException, IOException {

    final String suffixPath = req.getRequestPathInfo().getSuffix();
    final String locale = CommonUtils.getLanguageForPath(suffixPath);

    if (StringUtils.isEmpty(locale)) {
      LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      CommonUtils.writeNewJSONFormat(
          resp, StatusEnum.BAD_REQUEST.getValue(), Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      return;
    }

    final var list = seasonsCFService.fetchAllSeasons(locale);

    final ImmutableList.Builder<Resource> builder = ImmutableList.builder();
    final var resolver = req.getResourceResolver();

    // Add empty option
    final var vm = new ValueMapDecorator(new HashMap<>());
    vm.put("value", "");
    vm.put("text", "");
    builder.add(
        new ValueMapResource(resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm));

    if (CollectionUtils.isNotEmpty(list)) {

      list.forEach(
          d -> {
            ValueMap vm1 = new ValueMapDecorator(new HashMap<>());
            vm1.put("value", d.getResource().getPath());
            vm1.put("text", d.getTitle());
            builder.add(
                new ValueMapResource(
                    resolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED, vm1));
          });
    }

    DataSource ds = new SimpleDataSource(builder.build().iterator());
    req.setAttribute(DataSource.class.getName(), ds);
  }
}
