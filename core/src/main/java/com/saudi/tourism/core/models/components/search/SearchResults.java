package com.saudi.tourism.core.models.components.search;

import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.saudi.tourism.core.models.common.TextModel;
import com.saudi.tourism.core.services.SearchResultsService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.NumberConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * SearchResults.
 * This model contains all the valuable data of search model
 */
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class SearchResults {

  /**
   * Inject ResourceResolver.
   */
  @SlingObject
  @Getter
  private transient ResourceResolver resourceResolver;
  /**
   * The searchResultsJsonModel.
   */
  @Self
  @Getter
  private SearchResultsJsonModel searchResultsJsonModel;
  /**
   * The data.
   */
  @Getter
  private String data;

  /**
   * Inject SearchResultsService.
   */
  @Inject
  @Getter
  private SearchResultsService searchResultsService;

  /**
   * Variable to hold currentPage of Page.
   */
  @Inject private Page currentPage;

  /**
   * Variable to hold list of SearchResults.
   */
  private List<SearchResultModel> listOfSearchResults;

  /**
   * Variable to hold list of mainResults.
   */
  @Getter
  private List<SearchResultModel> mainResults;

  /**
   * Variable to hold list of secodaryResults.
   */
  @Getter
  private List<SearchResultModel> secondaryResults;

  /**
   * Variable to hold totalResults.
   */
  @Getter
  private Long totalResults;

  /**
   * Variable to hold noOfPages.
   */
  @Getter
  private Long noOfPages;

  /**
   * Variable to hold curResultsPage.
   */
  @Getter
  private Long curResultsPage;

  /**
   * Variable to hold resultsOffSet.
   */
  @Getter
  private Long resultsOffSet;

  /**
   * Variable to hold text related data for noResults.
   */
  @Getter
  @ChildResource
  private TextModel noResults;

  /**
   * Variable to hold text related data for learnMore.
   */
  @Getter
  @ChildResource
  private TextModel learnMore;

  /**
   * Variable to hold text related data for linkPrevious.
   */
  @Getter
  @ChildResource
  private TextModel linkPrevious;

  /**
   * Variable to hold text related data for linkNext.
   */
  @Getter
  @ChildResource
  private TextModel linkNext;

  /**
   * Variable to hold search text.
   */
  @Inject private SlingHttpServletRequest httpServletRequest;

  /**
   * Model Initializer.
   */
  @PostConstruct protected void init() {
    Gson gson = new Gson();
    data = gson.toJson(searchResultsJsonModel);
    Map<String, String[]> requestParams = httpServletRequest.getParameterMap();
    String fullSearchedText = StringUtils.EMPTY;
    fullSearchedText = constructSearchRequest(requestParams, fullSearchedText);
    if (StringUtils.isNotEmpty(fullSearchedText)) {
      findSearchResults(fullSearchedText, Boolean.TRUE);
      if (listOfSearchResults.size() == NumberConstants.CONST_ZERO) {
        findSearchResults(fullSearchedText, Boolean.FALSE);
      }
      if (listOfSearchResults.size() != NumberConstants.CONST_ZERO) {
        populateSearchSections(listOfSearchResults);
      }
    }
  }

  /**
   * This method is used to find search results.
   *
   * @param fullSearchedText String
   * @param isFullTextSearch Boolean
   */
  private void findSearchResults(String fullSearchedText, Boolean isFullTextSearch) {
    listOfSearchResults = searchResultsService
        .webSearch(resourceResolver,
            currentPage,
            fullSearchedText,
            resultsOffSet,
            isFullTextSearch);
  }


  /**
   * This method is used to map request params.
   *
   * @param requestParams    Map<String, String[]>
   * @param fullSearchedText String
   * @return String .
   */
  public String constructSearchRequest(final Map<String, String[]> requestParams,
      String fullSearchedText) {

    String strSearchText = getRequestParam(requestParams, Constants.SEARCH);
    String strState = getRequestParam(requestParams, Constants.STATE);
    String strResultsPage = getRequestParam(requestParams, Constants.RESULTS);

    if (strSearchText != null) {
      fullSearchedText = StringUtils
          .join(strSearchText + Constants.QUERY_PARTIAL_TEXT_CHARACTER);
    }
    if (StringUtils.isNotEmpty(fullSearchedText) && Objects.nonNull(strResultsPage) && Objects
        .nonNull(strState)) {
      Long resultsPage = Long.parseLong(strResultsPage);
      if (resultsPage > NumberConstants.CONST_ZERO_L) {
        if (strState.equalsIgnoreCase(Constants.NEXT)) {
          resultsOffSet = resultsPage * NumberConstants.CONST_TEN;
          curResultsPage = resultsPage + NumberConstants.CONST_ONE_L;
        } else if (strState.equalsIgnoreCase(Constants.PREVIOUS)
            && !resultsPage.equals(NumberConstants.CONST_ONE_L)) {
          resultsOffSet = -(resultsPage * NumberConstants.CONST_TEN);
          curResultsPage = resultsPage - NumberConstants.CONST_ONE_L;
        } else {
          resultsOffSet = NumberConstants.CONST_ZERO_L;
          curResultsPage = NumberConstants.CONST_ONE_L;
        }
      }
    } else {
      curResultsPage = NumberConstants.CONST_ONE_L;
      resultsOffSet = NumberConstants.CONST_ZERO_L;
    }

    return fullSearchedText;
  }

  /**
   * This method is used to get String value by name from requestParams.
   *
   * @param requestParams Map<String, String[]>
   * @param name of parameter
   * @return String .
   */
  private String getRequestParam(Map<String, String[]> requestParams, String name) {
    String[] params = requestParams.get(name);
    if (params != null && params.length > 0) {
      return params[NumberConstants.CONST_ZERO];
    }
    return null;
  }

  /**
   * This method is used to populate search sections.
   *
   * @param searchResults List<SearchResultModel>
   */
  public void populateSearchSections(final List<SearchResultModel> searchResults) {
    List<SearchResultModel> mainResultsList = new ArrayList<>();
    List<SearchResultModel> secondaryResultsList = new ArrayList<>();
    Long totalSearchResults = NumberConstants.CONST_ZERO_L;
    double noOfSearchPage;
    Long roundValueNoOfPages = NumberConstants.CONST_ZERO_L;

    int mainArraySize = NumberConstants.CONST_FOUR;
    int secondaryArrayStartPos = NumberConstants.CONST_FOUR;
    if (mainArraySize > searchResults.size()) {
      mainArraySize = searchResults.size();
      secondaryArrayStartPos = searchResults.size();
    }
    for (int position = NumberConstants.CONST_ZERO; position < mainArraySize; position++) {
      mainResultsList.add(searchResults.get(position));
    }
    for (int position = secondaryArrayStartPos; position < searchResults.size(); position++) {
      secondaryResultsList.add(searchResults.get(position));
    }
    if (searchResults.get(NumberConstants.CONST_ZERO) != null) {
      SearchResultModel searchResultModel = searchResults.get(NumberConstants.CONST_ZERO);
      totalSearchResults = searchResultModel.getTotalResults();
      if (!(totalSearchResults.equals(NumberConstants.CONST_ZERO_L))) {
        Double valueBeforeRoundUp = totalSearchResults / NumberConstants.CONST_TEN_D;
        noOfSearchPage = Math.round(totalSearchResults / NumberConstants.CONST_TEN_D);
        roundValueNoOfPages = Math.round(noOfSearchPage);
        if (valueBeforeRoundUp > roundValueNoOfPages) {
          roundValueNoOfPages = roundValueNoOfPages + NumberConstants.CONST_ONE;
        }
      }
    }
    mainResults = mainResultsList;
    secondaryResults = secondaryResultsList;
    totalResults = totalSearchResults;
    noOfPages = roundValueNoOfPages;
  }

}
