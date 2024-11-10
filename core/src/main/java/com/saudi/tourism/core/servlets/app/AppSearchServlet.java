package com.saudi.tourism.core.servlets.app;

import com.day.cq.wcm.api.NameConstants;
import com.saudi.tourism.core.beans.SearchParams;
import com.saudi.tourism.core.exceptions.MissingRequestParameterException;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.search.SearchListResultModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingSafeMethodsServlet;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Servlet for app search.
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "App Search Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/v1/appSearch"},
           configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = SearchServletConfig.class)
@Slf4j
public class AppSearchServlet extends SlingSafeMethodsServlet {

  /**
   * Locale template fragment we expect to be included in configuration paths.
   */
  protected static final String LOCALE_TEMPLATE = "{" + Constants.LOCALE + "}";

  /**
   * Search service.
   */
  @Reference
  private transient SearchResultsService searchResultsService;

  /**
   * Paths to be included in search.
   */
  private List<String> includeSearchPaths;

  /**
   * List of node types to search.
   */
  private List<String> nodeTypes = Arrays.asList(NameConstants.NT_PAGE);

  /**
   * Activation.
   * @param config configuration
   */
  @Activate
  public void activate(SearchServletConfig config) {
    this.includeSearchPaths = Arrays.stream(config.includeSearchPaths())
        .collect(Collectors.toList());
  }

  /**
   * Fulltext search.
   * @param request servlet request
   * @param response servlet response
   * @throws ServletException servlet exception
   * @throws IOException io exception
   */
  @Override protected void doGet(@NotNull final SlingHttpServletRequest request,
      @NotNull final SlingHttpServletResponse response) throws ServletException, IOException {
    try {
      String locale = CommonUtils.getMandatoryParameter(request, Constants.LOCALE);
      String searchText = CommonUtils.getMandatoryParameter(request, Constants.SEARCH_QUERY);
      Long offset =
          Optional.ofNullable(request.getParameter("offset")).map(Long::valueOf).orElse(0L);
      int rowsPerPage =
          Optional.ofNullable(request.getParameter("limit")).map(Integer::parseInt).orElse(
              NumberConstants.CONST_TEN);

      List<String> paths =
          this.includeSearchPaths.stream().map(path -> path.replace(LOCALE_TEMPLATE, locale))
              .collect(Collectors.toList());

      if (CollectionUtils.isEmpty(paths)) {
        CommonUtils.writeJSON(response, HttpStatus.SC_INTERNAL_SERVER_ERROR,
            new ResponseMessage(MessageType.ERROR.getType(), "Search paths not defined"));
        return;
      }

      SearchParams searchParams = SearchParams.builder()
          .searchPaths(paths)
          .nodeTypes(this.nodeTypes)
          .searchString(searchText + Constants.QUERY_PARTIAL_TEXT_CHARACTER)
          .offset(offset)
          .limit(rowsPerPage)
          .fullTextSearch(true)
          .build();

      SearchListResultModel result =
          searchResultsService.appSearch(request.getResourceResolver(), searchParams);

      CommonUtils.writeJSON(response, HttpStatus.SC_OK, result);
    } catch (MissingRequestParameterException e) {
      CommonUtils.writeJSON(response, HttpStatus.SC_BAD_REQUEST,
          new ResponseMessage(MessageType.ERROR.getType(), e.getMessage()));
    } catch (Exception e) {
      CommonUtils.writeJSON(response, HttpStatus.SC_INTERNAL_SERVER_ERROR,
          new ResponseMessage(MessageType.ERROR.getType(), e.getMessage()));
    }
  }

}
