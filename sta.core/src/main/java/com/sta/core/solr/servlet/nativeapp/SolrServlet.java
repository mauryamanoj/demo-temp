package com.sta.core.solr.servlet.nativeapp;

import com.sta.core.MmCoreException;
import com.sta.core.exceptions.TechnicalMmCoreException;
import com.sta.core.solr.model.SolrRequest;
import com.sta.core.solr.model.SolrResult;
import com.sta.core.solr.model.SolrSuggestionResponse;
import com.sta.core.solr.model.SolrWithGroupResponse;
import com.sta.core.solr.services.SolrSearchService;
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
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Servlet;
import java.io.IOException;

/**
 * Servlet that retrieves the list of solr search results.
 * /bin/api/v2/solr/search?source=web&locale=en&query=searchme&limit=10&sort=date(priority)
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Solr Search Servlet v2",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
          + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
          + "/bin/api/v2/solr/search"})
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
        SolrWithGroupResponse<?> solrWithGroupResponse = solrSearchService.solrSearchWithGroup(solrRequest);

        if (solrRequest.getSource().equals(Constants.SOLR_APP_SOURCE)) {
          solrWithGroupResponse = SolrSearchService.toAppResponse(
              (SolrWithGroupResponse<SolrResult>) solrWithGroupResponse);
        }
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper()
            .writeValueAsString(solrWithGroupResponse));

      } else if (!StringUtils.isEmpty(solrRequest.getSuggestion())) {
        SolrSuggestionResponse<?> solrSuggestionResponse = solrSearchService.suggest(solrRequest);
        CommonUtils.writeNewJSONFormat(response, StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper()
            .writeValueAsString(solrSuggestionResponse));
      } else {
        throw new MmCoreException("fill in either query or suggestion parameters");
      }
    } catch (TechnicalMmCoreException e) {
      LOGGER.error("Technical Error in SolrServlet v2, {}", e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    } catch (MmCoreException e) {
      LOGGER.error("Functional Error in SolrServlet v2, {}", e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(response, StatusEnum.BAD_REQUEST.getValue(), e.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in SolrServlet v2, {}", e.getLocalizedMessage());
      CommonUtils.writeNewJSONFormat(response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(),
          MessageType.ERROR.getType());
    }
  }
}
