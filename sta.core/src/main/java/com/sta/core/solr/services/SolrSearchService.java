package com.sta.core.solr.services;

import com.saudi.tourism.core.models.mobile.components.MobileSearchResults;
import com.sta.core.MmCoreException;
import com.sta.core.exceptions.TechnicalMmCoreException;
import com.sta.core.solr.model.SolrArticle;
import com.sta.core.solr.model.SolrGroupSection;
import com.sta.core.solr.model.SolrRequest;
import com.sta.core.solr.model.SolrResponse;
import com.sta.core.solr.model.SolrResult;
import com.sta.core.solr.model.SolrSuggestionResponse;

import com.sta.core.solr.model.SolrWithGroupResponse;
import com.saudi.tourism.core.models.components.search.SearchResultModel;
import org.apache.sling.api.resource.Resource;
import org.apache.solr.client.solrj.impl.HttpSolrClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The interface Solr search service.
 */
public interface SolrSearchService {
  /**
   * This method converts SolrResult to SearchResultModel.
   *
   * @param solrWithGroupResponse with SolrResult list
   * @return SolrResponse with SearchResultModel list
   */
  static SolrWithGroupResponse<SearchResultModel> toAppResponse(
      SolrWithGroupResponse<SolrResult> solrWithGroupResponse) {
    SolrWithGroupResponse<SearchResultModel> appResponse = new SolrWithGroupResponse<>();

    List<SolrGroupSection<SearchResultModel>> sections =
        solrWithGroupResponse.getSections().stream().map(reponseSection -> {
          SolrGroupSection<SearchResultModel> groupSection = new SolrGroupSection<>();
          groupSection.setTitle(reponseSection.getTitle());
          groupSection.setPagination(reponseSection.getPagination());
          groupSection.setType(reponseSection.getType());
          groupSection.setData(toAppListSearchResultModel(reponseSection.getData()));
          return groupSection;
        }).collect(Collectors.toList());

    appResponse.setSections(sections);
    appResponse.setSuggestion(solrWithGroupResponse.getSuggestion());

    return appResponse;
  }

  /**
   * This method converts SolrResult to SearchResultModel.
   *
   * @param solrResponse with SolrResult list
   * @return SolrResponse with SearchResultModel list
   */
  static SolrResponse<SearchResultModel> toAppResponse(SolrResponse<SolrResult> solrResponse) {
    SolrResponse<SearchResultModel> appResponse = new SolrResponse<>();
    appResponse.setPagination(solrResponse.getPagination());
    appResponse.setData(toAppListSearchResultModel(solrResponse.getData()));
    return appResponse;
  }

  /**
   * This method converts SolrResult to SearchResultModel.
   *
   * @param listSolrResult with SolrResult list
   * @return SearchResultModel with SearchResultModel list
   */
  static List<SearchResultModel> toAppListSearchResultModel(List<SolrResult> listSolrResult) {

    List<SearchResultModel> listSearchResultModel =  new ArrayList<>();
    for (SolrResult solrResult : listSolrResult) {
      SearchResultModel.SearchResultModelBuilder builder = SearchResultModel.builder();
      builder.pagePath(solrResult.getPath());
      builder.id(solrResult.getUrl());
      builder.pageTitle(solrResult.getTitle());
      builder.pageDescription(solrResult.getDescription());
      builder.featureImage(solrResult.getFeatureImage());
      builder.type(solrResult.getType());
      builder.categories((solrResult.getCategories()));
      listSearchResultModel.add(builder.build());
    }
    return listSearchResultModel;
  }


  /**
   * Create page metadata object json object.
   *
   * @param pageContent the page content
   * @return the json object
   */
  SolrArticle createPageMetadataObject(Resource pageContent);

  /**
   * Index page to solr boolean.
   *
   * @param solrArticle the index page data
   * @return the boolean
   * @throws MmCoreException exception
   */
  boolean indexPageToSolr(SolrArticle solrArticle)
      throws MmCoreException;

  /**
   * Delete page index from solr.
   *
   * @param path page path
   * @return the boolean
   * @throws MmCoreException exception
   */
  boolean deletePageFromSolr(String path)
      throws MmCoreException;

  /**
   * Delete page index from solr.
   *
   * @param path page path
   * @return the boolean
   * @throws MmCoreException exception
   */
  boolean deleteAllFromSolr(String path)
      throws MmCoreException;

  /**
   * Ping request.
   *
   * @param lang language
   * @return test result
   */
  boolean solrTest(String lang);

  /**
   * Creates HttpSolrClient.
   *
   * @param lang language
   * @return HttpSolrClient instance
   */
  HttpSolrClient getHttpSolrClient(String lang);

  /**
   * Search in solr.
   *
   * @param solrRequest solrRequest
   * @return SolrResponse instance
   */
  SolrResponse<SolrResult> search(SolrRequest solrRequest);

  /**
   * This method is used to search pages via SOLR.
   *
   * @param solrRequest SolrRequest
   * @return SolrResponse list of SolrResult
   */
  SolrWithGroupResponse<SolrResult> solrSearchWithGroup(SolrRequest solrRequest) throws TechnicalMmCoreException;

  /**
   * Suggest in solr.
   *
   * @param solrRequest solrRequest
   * @return SolrSuggestionResponse instance
   */
  SolrSuggestionResponse suggest(SolrRequest solrRequest);

  /**
   * This method converts SolrResult to MobileSolrSearchResults.
   *
   * @param solrResponse with SolrResult list
   * @return SolrResponse with MobileSearchResult list
   */
  SolrResponse<MobileSearchResults> getSearchAndSuggestions(SolrResponse<SolrResult> solrResponse, String locale);

  /**
   * This method converts SolrSuggestionResponse to MobileSolrResults.
   *
   * @param suggestionsResponse with SolrSuggestions list
   * @return SolrResponse with MobileSearchResult list
   */
  SolrResponse<MobileSearchResults> getSearchAndSuggestions(SolrSuggestionResponse suggestionsResponse, String locale);
}
