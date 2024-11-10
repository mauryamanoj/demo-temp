package com.saudi.tourism.core.models.components.searchresult.v1;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.SearchUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Filter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class SearchResultModel {

  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * The Current resource.
   */
  @Self
  private transient Resource currentResource;

  /**
   * ResourceBundleProvider.
   */
  @Inject
  @Filter("(component.name=org.apache.sling.i18n.impl.JcrResourceBundleProvider)")
  private ResourceBundleProvider i18nProvider;

  /**
   * RunMode Service.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * contentTypeFilter.
   */
  @Expose
  private List<DictItem> contentTypeFilter = new LinkedList<>();

  /**
   * Regions.
   */
  @Expose
  private List<DictItem> regions = new LinkedList<>();

  /**
   * searchPagePath.
   */
  @Getter
  @Expose
  private String searchPagePath;

  /**
   * Api call limit.
   */
  @ValueMapValue
  @Expose
  private int searchLimit;

  /**
   * resultsCountLabel.
   */
  @ValueMapValue
  @Expose
  private String resultsCountLabel;

  /**
   * clearAllFiltersLabel.
   */
  @ValueMapValue
  @Expose
  private String clearAllFiltersLabel;

  /**
   * clearAllResultsLabel.
   */
  @ValueMapValue
  @Expose
  private String clearAllResultsLabel;

  /**
   * locationLabel.
   */
  @ValueMapValue
  @Expose
  private String locationLabel;

  /**
   * contentLabel.
   */
  @ValueMapValue
  @Expose
  private String contentLabel;

  /**
   * readMoreLabel.
   */
  @ValueMapValue
  @Expose
  private String readMoreLabel;

  /**
   * Most recent search.
   */
  @ValueMapValue
  @Expose
  private String dateSort;

  /**
   * variable to hold searchPlaceholderLabel.
   */
  @ValueMapValue
  @Expose
  private String searchPlaceholderLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @ValueMapValue
  @Expose
  private String noResultsFoundLabel;

  /**
   * variable to hold noResultsFoundLabel.
   */
  @ValueMapValue
  @Expose
  private String sortFilterLabel;

  /**
   * variable to hold noResultFoundDescription.
   */
  @ValueMapValue
  @Expose
  private String noResultFoundDescription;

  /**
   * variable to hold View As.
   */
  @ValueMapValue
  @Expose
  private String viewAs;

  /**
   * variable to hold filters.
   */
  @ValueMapValue
  @Expose
  private String filters;

  /**
   * variable to hold View All results in the search modal.
   */
  @ValueMapValue
  @Expose
  private String viewAllResultsLabel;

  /**
   * variable to hold Recently searched in the search modal.
   */
  @ValueMapValue
  @Expose
  private String recentlySearchedLabel;

  /**
   * variable to hold mobile filters modal title.
   */
  @ValueMapValue
  @Expose
  private String modalFiltersTitle;

  /**
   * Reset all filters label.
   */
  @ValueMapValue
  @Expose
  private String resetAllFiltersLabel;

  /**
   * Load more label.
   */
  @ValueMapValue
  @Expose
  private String loadMoreLabel;

  /**
   * Select Location Label.
   */
  @ValueMapValue
  @Expose
  private String selectLocationLabel;

  /**
   * Button Label.
   */
  @ValueMapValue
  @Expose
  private String buttonLabel;

  /**
   * Select Label.
   */
  @ValueMapValue
  @Expose
  private String selectLabel;

  /**
   * Clear All Button.
   */
  @ValueMapValue
  @Expose
  private String clearAllButton;

  /**
   * search cancel Label.
   */
  @Expose
  private String cancelLabel;

  /**
   * clearLabel.
   */
  @Expose
  private String clearLabel;

  /**
   * applyLabel.
   */
  @Expose
  private String applyLabel;

  /**
   * Search Modal Title.
   */
  @Expose
  @ValueMapValue
  private String searchModalTitle;

  @PostConstruct public void init() {
    initRegions();
  }

  /**
   * initRegions method.
   */
  private void initRegions() {
    final var language = CommonUtils.getLanguageForPath(currentResource.getPath());
    if (Objects.nonNull(i18nProvider)) {
      final var i18n = i18nProvider.getResourceBundle(new Locale(language));
      cancelLabel = CommonUtils.getI18nString(i18n, I18nConstants.CANCEL);
      clearLabel = CommonUtils.getI18nString(i18n, I18nConstants.SEARCH_CLEAR);
      applyLabel = CommonUtils.getI18nString(i18n, I18nConstants.I18_KEY_APPLY);
    }
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (Objects.nonNull(currentPage)) {
      searchPagePath = LinkUtils.getAuthorPublishUrl(resourceResolver, currentPage.getPath(),
        runModeService.isPublishRunMode());
    }
    contentTypeFilter = SearchUtils.getPageTypes(resourceResolver, language);
    regions = SearchUtils.getCites(resourceResolver, language, "region");
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

  @Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class RegionItem {

    /**
     * Region.
     */
    @ValueMapValue
    private String region;
  }
}
