package com.saudi.tourism.core.servlets;

import java.io.IOException;
import java.util.Iterator;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.contentfragment.ContentFragmentRequestParams;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

/**
 * The content fragment servlet.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Content fragment Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + ContentFragmentServlet.SERVLET_PATH
})
@Slf4j
public class ContentFragmentServlet extends SlingAllMethodsServlet {
  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/contentfragment";

  /**
   * This method is used to get content fragment data.
   *
   * @param request  the request
   * @param response the response
   * @throws ServletException the servlet exception
   * @throws IOException       the io exception
   */
  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    ContentFragmentRequestParams contentFragmentRequestParams =
        new Convert<>(request, ContentFragmentRequestParams.class).getRequestData();
    final ResourceResolver resourceResolver = request.getResourceResolver();
    try {
      Resource fragmentResource;
      if (StringUtils.isNotEmpty(contentFragmentRequestParams.getPath())) {
        fragmentResource = resourceResolver.getResource(contentFragmentRequestParams.getPath());
        ContentFragment currentContentFragment = fragmentResource.adaptTo(ContentFragment.class);
        if (currentContentFragment != null) {
          JsonObjectBuilder jsonObjectBuilder = getJson(resourceResolver, currentContentFragment, "master");
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), jsonObjectBuilder.build().toString());
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error in getting content fragment : {}", e);
      CommonUtils.writeJSON(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          new ResponseMessage(StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
              MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }
  }

  private static JsonObjectBuilder getJson(ResourceResolver resourceResolver, ContentFragment contentFragment,
                                           String variationName) {
    JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
    jsonObjectBuilder.add("contentFragment_title", contentFragment.getTitle());
    Resource contentFragmentResource = contentFragment.adaptTo(Resource.class);
    if (contentFragmentResource != null) {
      jsonObjectBuilder.add("contentFragment_path", contentFragmentResource.getPath());
    }
    if (variationName != null) {
      jsonObjectBuilder.add("contentFragment_variation", variationName);
    }
    if (contentFragmentResource != null) {
      Iterator<ContentElement> contentElementIterator = contentFragment.getElements();
      if (contentElementIterator != null) {
        while (contentElementIterator.hasNext()) {
          final ContentElement contentElement = contentElementIterator.next();
          if ("content-fragment".equals(contentElement.getValue().getDataType().getSemanticType())) {
            if (contentElement.getValue().getDataType().isMultiValue()) {
              JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
              for (String reference : contentElement.getValue().getValue(String[].class)) {
                Resource fragmentResource = resourceResolver.getResource(reference);
                ContentFragment referenceContentFragment = fragmentResource.adaptTo(ContentFragment.class);
                arrayBuilder.add(getJson(resourceResolver, referenceContentFragment, null));
              }
              jsonObjectBuilder.add(contentElement.getName(), arrayBuilder);
            } else {
              Resource fragmentResource = resourceResolver.getResource(contentElement.getContent());
              ContentFragment referenceContentFragment = fragmentResource.adaptTo(ContentFragment.class);
              jsonObjectBuilder.add(contentElement.getName(),
                  getJson(resourceResolver, referenceContentFragment, null));
            }
          } else {
            jsonObjectBuilder.add(contentElement.getName(), contentElement.getContent());
          }
        }
      }
    }
    return jsonObjectBuilder;
  }
}
