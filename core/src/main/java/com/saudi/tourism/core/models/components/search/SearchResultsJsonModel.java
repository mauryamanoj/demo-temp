package com.saudi.tourism.core.models.components.search;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.models.components.packages.NoResultSlingModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SearchUtils;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.FORWARD_SLASH_CHARACTER;

/**
 * The Class SearchResultsJsonModel.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class SearchResultsJsonModel implements Serializable {

  /**
   * The constant PACKAGE_COUNT.
   */
  private static final int FALLBACK_RESULT_COUNT = 7;
  /**
   * The searchResultsTitle.
   */
  @Setter
  @Getter
  private String searchResultsTitle;
  /**
   * The noResults.
   */
  @ChildResource
  @Getter
  private NoResultSlingModel noResultsMessage;
  /**
   * The pagination total.
   */
  @Getter
  @Setter
  private String paginationTotal;

  /**
   * The pagination current.
   */
  @Getter
  @Setter
  private String paginationCurrent;

  /**
   * The suggestionsEndpoint.
   */
  @Getter
  @Setter
  private String suggestionsEndpoint;
  /**
   * The searchEndpoint.
   */
  @Getter
  @Setter
  private String searchEndpoint;
  /**
   * The searchPagePath.
   */
  @ValueMapValue
  @Setter
  private String searchPagePath;
  /**
   * The previousButtonLabel.
   */
  @Getter
  @Setter
  private String previousButtonLabel;
  /**
   * The nextButtonLabel.
   */
  @Getter
  @Setter
  private String nextButtonLabel;
  /**
   * The searchResultLinkLabel.
   */
  @Getter
  @Setter
  private String searchResultLinkLabel;

  /**
   * The loadMoreButtonLabel.
   */
  @Getter
  @Setter
  private String loadMoreButtonLabel;

  /**
   * The searchPlaceholder.
   */
  @Getter
  @Setter
  private String searchPlaceholder;

  /**
   * The hoursAbbreviationLabel.
   */
  @Getter
  @Setter
  private String hoursAbbreviationLabel;

  /**
   * The didYouMeanLabel.
   */
  @Getter
  @Setter
  private String didYouMeanLabel;

  /**
   * The clearLabel.
   */
  @Getter
  @Setter
  private String clearLabel;

  /**
   * The resultsPerPage.
   */
  @ValueMapValue
  @Getter
  private int resultsPerPage;

  /**
   * Variable to store pillBlock.
   */
  @SuppressWarnings("java:S1068")
  private SearchPillsModel pillBlock;

  /**
   * Variable to store trendingBLock.
   */
  @SuppressWarnings("java:S1068")
  private SearchTrendingsModel trendingBlock;

  /**
   * Variable to store featured.
   */
  private SearchTrendingsModel featured;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private transient Page currentPage;
  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private transient ResourceBundleProvider i18nProvider;

  /**
   * The init method.
   */
  @PostConstruct protected void init() {
    try {
      String path = Optional.ofNullable(currentPage.getPath()).orElse(StringUtils.EMPTY);
      // language needed for tag translation title
      String language =
          CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);
      if (resultsPerPage == 0) {
        resultsPerPage = FALLBACK_RESULT_COUNT;
      }
      suggestionsEndpoint = "/bin/api/solr/search";
      searchEndpoint = suggestionsEndpoint;
      String pillBlockTitle = "";
      String trendingBlockTitle = "";
      String featuredBlockTitle = "";
      if (Objects.nonNull(i18nProvider)) { // update city to i18n value
        ResourceBundle i18nBundle = i18nProvider.getResourceBundle(new Locale(language));

        setPreviousButtonLabel(CommonUtils.getI18nString(i18nBundle, "previous"));
        setNextButtonLabel(CommonUtils.getI18nString(i18nBundle, "next"));
        setSearchResultsTitle(CommonUtils.getI18nString(i18nBundle, "resultsfound"));
        setSearchResultLinkLabel(CommonUtils.getI18nString(i18nBundle, "learnmore"));
        setPaginationTotal(CommonUtils.getI18nString(i18nBundle, "searchPaginationTotal"));
        setPaginationCurrent(CommonUtils.getI18nString(i18nBundle, "searchPaginationCurrent"));
        setLoadMoreButtonLabel(CommonUtils.getI18nString(i18nBundle, "searchLoadMore"));
        setSearchPlaceholder(CommonUtils.getI18nString(i18nBundle, "searchPlaceholder"));
        setHoursAbbreviationLabel(CommonUtils.getI18nString(i18nBundle, "hoursAbbreviationLabel"));
        setDidYouMeanLabel(CommonUtils.getI18nString(i18nBundle, "didYouMeanLabel"));
        setClearLabel(CommonUtils.getI18nString(i18nBundle, "searchClear"));

        pillBlockTitle = CommonUtils.getI18nString(i18nBundle, "pillBlockTitle");
        trendingBlockTitle = CommonUtils.getI18nString(i18nBundle, "trendingBlockTitle");
        featuredBlockTitle = CommonUtils.getI18nString(i18nBundle, "featuredBlockTitle");
      }
      if (searchPagePath == null) {
        searchPagePath = FORWARD_SLASH_CHARACTER + language + "/search#search=";
      }
      if (noResultsMessage == null) {
        noResultsMessage = new NoResultSlingModel();
        noResultsMessage.setFirstLine("Sorry!");
      }
      ResourceResolver resolver = currentPage.getContentResource().getResourceResolver();
      trendingBlock =
          SearchUtils.getSearchTrendings(trendingBlockTitle, resolver, language, Constants.TRENDING_PAGE);
      featured = SearchUtils.getSearchTrendings(featuredBlockTitle, resolver, language, Constants.FEATURED_PAGE);
      pillBlock = SearchUtils.getSearchPills(pillBlockTitle, resolver, language);
    } catch (Exception e) {
      LOGGER.error("Error in PackageFilterSlingModel");
    }

  }
}
