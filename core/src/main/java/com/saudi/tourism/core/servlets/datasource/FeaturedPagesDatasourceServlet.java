package com.saudi.tourism.core.servlets.datasource;

import static com.saudi.tourism.core.utils.Constants.EQUAL;

import com.adobe.granite.ui.components.ds.DataSource;
import com.adobe.granite.ui.components.ds.SimpleDataSource;
import com.adobe.granite.ui.components.ds.ValueMapResource;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import com.google.common.collect.ImmutableList;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.servlet.Servlet;
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

import static com.saudi.tourism.core.servlets.datasource.FeaturedPagesDatasourceServlet.DESCRIPTION;
import static com.saudi.tourism.core.servlets.datasource.FeaturedPagesDatasourceServlet.RESOURCE_TYPE;

/**
 * Custom Datasource to retrieve pages that have isFeatured as true.
 * To use this datasource see below jcr structure
 * + datasource
 * - sling:resourceType = sauditourism/generic/datasource/featured
 */
@Component(service = Servlet.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + EQUAL + DESCRIPTION,
    ServletResolverConstants.SLING_SERVLET_RESOURCE_TYPES + EQUAL + RESOURCE_TYPE,
    ServletResolverConstants.SLING_SERVLET_METHODS + EQUAL + HttpConstants.METHOD_GET})
@Slf4j
public class FeaturedPagesDatasourceServlet extends SlingSafeMethodsServlet {

  /**
   * This data source servlet description.
   */
  static final String DESCRIPTION = "just a description";

  /**
   * This servlet processing resource type.
   */
  static final String RESOURCE_TYPE = "sauditourism/generic/datasource/featured";

  /**
   * Override doGet method of SlingSafeMethodsServlet, for more details read class java doc.
   *
   * @param req  SlingHttpServletRequest
   * @param resp SlingHttpServletResponse
   */
  @Override
  protected void doGet(final SlingHttpServletRequest req,
      final @NotNull SlingHttpServletResponse resp) {
    final String suffixPath = req.getRequestPathInfo().getSuffix();
    final String locale = CommonUtils.getLanguageForPath(suffixPath);
    ResourceResolver resourceResolver = req.getResourceResolver();
    String sitePath = "/content/sauditourism/" + locale;
    Resource resource = resourceResolver.getResource(sitePath);
    Page rootPage = resource.adaptTo(Page.class);

    List<Page> pages;

    pages = recursiveGetAllPages(rootPage, new ArrayList<>());

    final ImmutableList.Builder<Resource> builder = ImmutableList.builder();
    if (pages != null) {
      pages.forEach(page -> {
        ValueMap vm1 = new ValueMapDecorator(new HashMap<>());
        vm1.put("text", page.getTitle());
        vm1.put("value", page.getPath());
        builder.add(
            new ValueMapResource(resourceResolver, new ResourceMetadata(), JcrConstants.NT_UNSTRUCTURED,
            vm1));
      });
    }

    DataSource ds = new SimpleDataSource(builder.build().iterator());
    req.setAttribute(DataSource.class.getName(), ds);

  }

  private List<Page> recursiveGetAllPages(Page rootPage, List<Page> pages) {
    Page child;
    Iterator<Page> iterator = rootPage.listChildren();
    while (iterator.hasNext()) {
      child = iterator.next();
      ValueMap props = child.getProperties();
      if (props.get("isFeatured") != null) {
        pages.add(child);
      }
      recursiveGetAllPages(child, pages);

    }
    return pages;
  }
}
