package com.saudi.tourism.core.servlets.app;

import com.saudi.tourism.core.models.app.eventpackage.EventPackageModel;
import com.saudi.tourism.core.models.app.eventpackage.PackagesInfo;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.AdminPageOption;
import com.saudi.tourism.core.utils.AdminUtil;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
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

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.day.cq.wcm.api.NameConstants.NN_TEMPLATE;

/**
 * Servlet that retrieves the list of app packages.
 * http://localhost:4502/bin/api/v1/app/packages./en
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Packages Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/app/packages"})
@Slf4j
public class PackagesServlet extends SlingAllMethodsServlet {

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {
    String resourcePath = "";

    String suffix = request.getRequestPathInfo().getSuffix();
    if (suffix != null) {
      resourcePath = CommonUtils.getNewAppPath(suffix);
    }
    try {
      if (!StringUtils.isEmpty(resourcePath) && !resourcePath
          .equals(Constants.FORWARD_SLASH_CHARACTER)) {
        String locale = resourcePath.substring(resourcePath.lastIndexOf(Constants.FORWARD_SLASH_CHARACTER) + 1);
        AdminPageOption adminOptions =
            AdminUtil.getAdminOptions(locale, StringUtils.EMPTY);
        if (adminOptions.isDisableAppPackages()) {
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
              new ResponseMessage(MessageType.ERROR.getType(), "Packages Disabled"));
          return;
        }
        ResourceResolver resourceResolver = request.getResourceResolver();
        Node node = resourceResolver.getResource(resourcePath).adaptTo(Node.class);
        List<EventPackageModel> eventPackages = searchRecursivelyPropPres(request, node,
            new ArrayList<>());

        PackagesInfo packagesInfo = new PackagesInfo();
        packagesInfo.setAvailableCities(findAvailableCities(eventPackages));
        packagesInfo.setPackages(eventPackages);
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), packagesInfo);
      } else {
        CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
            new ResponseMessage(MessageType.ERROR.getType(), "No information for the packages"));
      }
    } catch (Exception e) {
      LOGGER.error("Error in Packages Servlet. " + e.getMessage(), e);
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), "No information for the packages"));
    }

  }

  /**
   * Retrieves the list of package pages.
   *
   * @param request       ServletRequest
   * @param node          root node for the locale
   * @param searchResults recursive list
   * @return list of pages
   */
  private List<EventPackageModel> searchRecursivelyPropPres(SlingHttpServletRequest request,
      Node node, List<EventPackageModel> searchResults) {

    try {
      NodeIterator rootNode = node.getNodes();

      while (rootNode.hasNext()) {
        Node currentSubNode = rootNode.nextNode();
        if (currentSubNode.hasProperty(NN_TEMPLATE) && currentSubNode.getProperty(NN_TEMPLATE)
            .getString().equals(Constants.APP_PACKAGE_TEMPLATE)) {
          searchResults.add(getPageInformation(request, currentSubNode.getPath()));
        }
        searchRecursivelyPropPres(request, currentSubNode, searchResults);
      }
      return searchResults;
    } catch (RepositoryException rpe) {
      LOGGER.error("Error while reading the locations for the app. " + rpe.getMessage());
    }
    return searchResults;
  }

  /**
   * Get the package page information.
   *
   * @param request request
   * @param path    path
   * @return model
   */
  private EventPackageModel getPageInformation(SlingHttpServletRequest request, String path) {

    Resource resource = request.getResourceResolver().getResource(path);
    EventPackageModel model = null;
    if (resource != null
        && Constants.APP_PACKAGE_RESOURCE_TYPE.equals(resource.getResourceType())) {
      model = resource.adaptTo(EventPackageModel.class);
    }
    return model;
  }

  /**
   * Fills cities property.
   *
   * @param listOfPages list of package models
   * @return List of cities
   */
  private List<String> findAvailableCities(List<EventPackageModel> listOfPages) {
    return listOfPages.stream()
        .map(EventPackageModel::getCity)
        .filter(StringUtils::isNotEmpty)
        .distinct()
        .collect(Collectors.toList());
  }
}
