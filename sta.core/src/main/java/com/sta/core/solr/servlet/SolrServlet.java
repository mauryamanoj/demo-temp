package com.sta.core.solr.servlet;

import com.saudi.tourism.core.models.mobile.components.MobileSearchResults;
import com.saudi.tourism.core.services.NativeAppHomepageService;
import com.saudi.tourism.core.utils.*;
import com.sta.core.solr.model.*;
import com.sta.core.solr.services.SolrSearchService;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.servlet.Servlet;

import com.sta.core.MmCoreException;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.models.components.search.SearchResultModel;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * Servlet that retrieves the list of solr search results.
 * /bin/api/solr/search?source=web&locale=en&query=searchme&limit=10&sort=date(priority)
 */
@Component(service = Servlet.class,
           property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Solr Search Servlet",
               ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
                   + HttpConstants.METHOD_GET,
               ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
                   + "/bin/api/solr/search"})
@Slf4j
public class SolrServlet extends SlingAllMethodsServlet {

  /**
   * The Solr Search Service.
   */
  @Reference
  private transient SolrSearchService solrSearchService;

  @Override protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws IOException {

    SolrRequest solrRequest = new Convert<>(request, SolrRequest.class).getRequestData();

    if (StringUtils.isEmpty(solrRequest.getSource())) {
      solrRequest.setSource(Constants.SOLR_WEB_SOURCE);
    }

    try {
      if (StringUtils.isBlank(solrRequest.getLocale())) {
        throw new MmCoreException(Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
      }

      if (solrRequest.getLimit() == null || solrRequest.getLimit() == 0) {
        solrRequest.setLimit(NumberConstants.CONST_TWENTY);
      }

      if (!StringUtils.isEmpty(solrRequest.getQuery())) {
        SolrResponse<?> solrResponse = solrSearchService.search(solrRequest);

        if (solrRequest.getSource().equals(Constants.SOLR_APP_SOURCE)) {
          solrResponse = toAppResponse((SolrResponse<SolrResult>) solrResponse);
        }

        if (solrRequest.getSource().equals(Constants.SOLR_MOBILE_SOURCE)) {
          solrResponse = solrSearchService.getSearchAndSuggestions((SolrResponse<SolrResult>) solrResponse, solrRequest.getLocale());
          MobileUtils.writeMobileV1Format(
              response,
              StatusEnum.SUCCESS.getValue(),
              00,
              RestHelper.getObjectMapper().writeValueAsString(solrResponse));
        } else {
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), solrResponse);
        }

      } else if (!StringUtils.isEmpty(solrRequest.getSuggestion())) {
        SolrSuggestionResponse solrSuggestionResponse = solrSearchService.suggest(solrRequest);
        if (solrRequest.getSource().equals(Constants.SOLR_MOBILE_SOURCE)) {
          SolrResponse<?> suggestions = solrSearchService.getSearchAndSuggestions(solrSuggestionResponse, solrRequest.getLocale());
          MobileUtils.writeMobileV1Format(
              response,
              StatusEnum.SUCCESS.getValue(),
              00,
              RestHelper.getObjectMapper().writeValueAsString(suggestions));
        } else {
          CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), solrSuggestionResponse);
        }
      } else {
        throw new MmCoreException("fill in either query or suggestion parameters");
      }
    } catch (Exception e) {
      LOGGER.error("Error in SolrServlet, {}", e.getLocalizedMessage());
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(),
          new ResponseMessage(MessageType.ERROR.getType(), e.getLocalizedMessage()));
    }
  }

  /**
   * This method converts SolrResult to SearchResultModel.
   *
   * @param solrResponse with SolrResult list
   * @return SolrResponse with SearchResultModel list
   */
  private SolrResponse<SearchResultModel> toAppResponse(SolrResponse<SolrResult> solrResponse) {
    SolrResponse<SearchResultModel> appResponse = new SolrResponse<>();
    appResponse.setPagination(solrResponse.getPagination());
    appResponse.setData(new ArrayList<>());

    for (SolrResult solrResult : solrResponse.getData()) {
      SearchResultModel.SearchResultModelBuilder builder = SearchResultModel.builder();

      builder.pagePath(solrResult.getPath());
      builder.id(solrResult.getUrl());
      builder.pageTitle(solrResult.getTitle());
      builder.pageDescription(solrResult.getDescription());
      builder.featureImage(solrResult.getFeatureImage());
      builder.type(solrResult.getType());
      builder.categories((solrResult.getCategories()));
      appResponse.getData().add(builder.build());
    }

    return appResponse;
  }
}
