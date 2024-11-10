package com.saudi.tourism.core.utils;

import com.adobe.aemds.guide.utils.JcrResourceConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.DictItem;
import com.saudi.tourism.core.models.components.search.SearchPage;
import com.saudi.tourism.core.models.components.search.SearchPill;
import com.saudi.tourism.core.models.components.search.SearchPillModel;
import com.saudi.tourism.core.models.components.search.SearchPillsModel;
import com.saudi.tourism.core.models.components.search.SearchTrendingsModel;
import com.saudi.tourism.core.models.components.search.TrendingCard;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The Class SearchUtils.
 */
@Slf4j
public final class SearchUtils {

  /**
   * Instantiates a new search utils.
   */
  private SearchUtils() {
  }

  /**
   * Gets SearchTrendingsModel object.
   *
   * @param title            title
   * @param resourceResolver resource resolver
   * @param language         language
   * @param searchedPage
   * @return SearchTrendingsModel
   */
  public static SearchTrendingsModel getSearchTrendings(String title,
      ResourceResolver resourceResolver, String language, String searchedPage) {
    SearchTrendingsModel trendings = new SearchTrendingsModel();
    trendings.setTitle(title);
    trendings.setCards(getTrendingPages(resourceResolver, language, searchedPage));
    return trendings;
  }


  /**
   * Gets SearchTrendingsModel object.
   *
   * @param title            title
   * @param resourceResolver resource resolver
   * @param language         language
   * @param source
   * @return SearchTrendingsModel
   */
  public static SearchTrendingsModel getSearchTrendingsWithSource(String title,
                                                                  ResourceResolver resourceResolver, String language,
                                                                  String source) {
    SearchTrendingsModel trendings = new SearchTrendingsModel();
    trendings.setTitle(title);
    trendings.setCards(getTrendingPagesWithSource(resourceResolver, language, source));
    return trendings;
  }

  /**
   * This method is used to get list of trending pages path.
   *
   * @param resourceResolver ResourceResolver
   * @param language         locale
   * @param searchedPage
   * @return list of paths
   */
  public static List<String> getSearchedPaths(ResourceResolver resourceResolver, String language,
      String searchedPage) {
    List<String> trendingPaths = new ArrayList<>();
    Resource trendingResource =
        getAdminSearchResource(resourceResolver, language).getChild(searchedPage);
    if (trendingResource != null) {
      for (Resource trendingPage : trendingResource.getChildren()) {
        String path = trendingPage.getValueMap().get(searchedPage, Constants.BLANK);
        if (!path.equals(Constants.BLANK)) {
          trendingPaths.add(path);
        }
      }
    }
    return trendingPaths;
  }

  /**
   * This method is used to get list of cities filter.
   *
   * @param resourceResolver ResourceResolver
   * @param language         locale
   * @param searchedPage
   * @return list of paths
   */
  public static List<DictItem> getCites(ResourceResolver resourceResolver, String language,
                                              String searchedPage) {
    List cites = new ArrayList<>();
    Locale locale = new Locale(language);
    List<String> trendingPaths = new ArrayList<>();
    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    Resource trendingResource =
        getAdminSearchResource(resourceResolver, language).getChild(searchedPage);
    if (trendingResource != null) {
      for (Resource trendingPage : trendingResource.getChildren()) {
        String path = trendingPage.getValueMap().get(searchedPage, Constants.BLANK);
        if (!path.equals(Constants.BLANK)) {
          trendingPaths.add(path);
        }
      }
    }

    if (!trendingPaths.isEmpty()) {
      cites = trendingPaths.stream().map(cityTag -> Constants.TAGS_URL + cityTag
              .replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER))
          .map(resourceResolver::resolve)
          .map(resource -> tagManager.resolve(resource.getPath()))
          .map(tag -> new DictItem(tag.getName().toLowerCase(), tag.getTitle(locale)))
          .collect(Collectors.toList());
    }

    return cites;
  }

  /**
   *
   * @param resourceResolver
   * @param language
   * @param searchedPage
   * @param isPublish
   * @return SearchPage
   */
  public static SearchPage getSearchPage(ResourceResolver resourceResolver, String language,
      String searchedPage, Boolean isPublish) {
    SearchPage searchPage = new SearchPage();
    Resource searchPageResource =
        getAdminSearchResource(resourceResolver, language).getChild(searchedPage);
    List<DictItem> types = getPageTypes(resourceResolver, language);

    if (searchPageResource != null) {
      searchPage.setSearchPagePath(LinkUtils.getAuthorPublishUrl(
          resourceResolver, String.valueOf(searchPageResource.getValueMap().get("searchPagePath")), isPublish));
      searchPage.setContentTypeFilter(types);
      searchPage.setRegions(SearchUtils.getCites(resourceResolver, language, "region"));
      searchPage.setSearchLimit(Integer.parseInt(
          String.valueOf(searchPageResource.getValueMap().get("searchLimit"))));
      searchPage.setLocationLabel(String.valueOf(searchPageResource.getValueMap().get("locationLabel")));
      searchPage.setContentLabel(String.valueOf(searchPageResource.getValueMap().get("contentLabel")));
      searchPage.setReadMoreLabel(String.valueOf(searchPageResource.getValueMap().get("loadMoreLabel")));
      searchPage.setResultsCountLabel(String.valueOf(searchPageResource.getValueMap().get("resultsCountLabel")));
      searchPage.setClearAllFiltersLabel(String.valueOf(searchPageResource.getValueMap().get("clearAllFiltersLabel")));
      searchPage.setApplyAllFiltersLabel(String.valueOf(searchPageResource.getValueMap().get("applyAllFiltersLabel")));
      searchPage.setListViewLabel(String.valueOf(searchPageResource.getValueMap().get("listViewLabel")));
      searchPage.setGridViewLabel(String.valueOf(searchPageResource.getValueMap().get("gridViewLabel")));
      searchPage.setDateSort(String.valueOf(searchPageResource.getValueMap().get("dateSort")));
      searchPage.setSearchPlaceholderLabel(String.valueOf(searchPageResource.getValueMap()
          .get("searchPlaceholderLabel")));
      searchPage.setNoResultsFoundLabel(String.valueOf(searchPageResource.getValueMap().get("noResultsFoundLabel")));
      searchPage.setSortFilterLabel(String.valueOf(searchPageResource.getValueMap().get("sortFilterLabel")));
      searchPage.setNoResultFoundDescription(
          String.valueOf(searchPageResource.getValueMap().get("noResultFoundDescription")));
      searchPage.setViewAs(String.valueOf(searchPageResource.getValueMap().get("viewAs")));
      searchPage.setFilters(String.valueOf(searchPageResource.getValueMap().get("filters")));
      searchPage.setViewAllResultsLabel(String.valueOf(searchPageResource.getValueMap().get("viewAllResultsLabel")));
      searchPage.setRecentlySearchedLabel(
          String.valueOf(searchPageResource.getValueMap().get("recentlySearchedLabel")));
      searchPage.setShowAllFilters(String.valueOf(searchPageResource.getValueMap().get("showAllFilters")));
      searchPage.setModalFiltersTitle(String.valueOf(searchPageResource.getValueMap().get("modalFiltersTitle")));
      searchPage.setModalSortByFilter(String.valueOf(searchPageResource.getValueMap().get("modalSortByFilter")));
      searchPage.setModalViewAsLabel(String.valueOf(searchPageResource.getValueMap().get("modalViewAsLabel")));
    }

    return searchPage;
  }

  /**
   * This method is used to get list of trending pages path.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @param source source
   * @return list of paths
   */
  private static List<String> getTrendingPaths(ResourceResolver resourceResolver, String language, String source) {
    List<String> trendingPaths = new ArrayList<>();

    String trendingPageField = Constants.TRENDING_PAGE;
    if (source.equals("app")) {
      trendingPageField = Constants.TRENDING_PAGE_APP;
    }
    Resource trendingResource =
        getAdminSearchResource(resourceResolver, language).getChild(Constants.TRENDING_PAGE);
    if (trendingResource != null) {
      for (Resource trendingPage : trendingResource.getChildren()) {
        String path = trendingPage.getValueMap().get(trendingPageField, Constants.BLANK);
        if (!path.equals(Constants.BLANK)) {
          trendingPaths.add(path);
        }
      }
    }
    return trendingPaths;
  }

  /**
   * This method is used to get list of search categories.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @return list of paths
   */
  public static List<String> getSearchCategories(ResourceResolver resourceResolver,
      String language) {
    List<String> categories = new ArrayList<>();

    Resource categoriesResource =
        getAdminSearchResource(resourceResolver, language).getChild(Constants.SEARCH_CATEGORY);
    if (categoriesResource != null) {
      for (Resource categoryResource : categoriesResource.getChildren()) {
        String category =
            categoryResource.getValueMap().get(Constants.SEARCH_CATEGORY, Constants.BLANK);
        if (!category.equals(Constants.BLANK)) {
          categories.add(category);
        }
      }
    }
    return categories;
  }

  /**
   * This method is used to get list of trending pages.
   *
   * @param resourceResolver ResourceResolver
   * @param language         locale
   * @param searchedPage
   * @return list of trending pages
   */
  public static List<TrendingCard> getTrendingPages(ResourceResolver resourceResolver,
      String language, String searchedPage) {
    List<String> trendingPaths = getSearchedPaths(resourceResolver, language, searchedPage);
    return trendingPaths.stream().map(s -> s + "/" + JcrConstants.JCR_CONTENT)
        .map(resourceResolver::resolve).map(resource -> resource.adaptTo(TrendingCard.class))
        .filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * This method is used to get list of trending pages.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @param source source
   * @return list of trending pages
   */
  private static List<TrendingCard> getTrendingPagesWithSource(ResourceResolver resourceResolver,
                                                     String language, String source) {
    List<String> trendingPaths = getTrendingPaths(resourceResolver, language, source);
    return trendingPaths.stream().map(s -> s + "/" + JcrConstants.JCR_CONTENT)
      .map(resourceResolver::resolve).map(resource -> resource.adaptTo(TrendingCard.class))
      .filter(Objects::nonNull).collect(Collectors.toList());
  }

  /**
   * This method is used to get admin search config.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @return resource
   */
  public static Resource getAdminSearchResource(ResourceResolver resourceResolver,
                                                String language) {
    String adminSuggestionsPath = StringUtils
        .replaceEach(Constants.ADMIN_SOLR_PATH, new String[] {Constants.LANGUAGE_PLACEHOLDER},
            new String[] {language});

    return resourceResolver.resolve(adminSuggestionsPath);
  }

  /**
   * Gets SearchPillsModel object.
   *
   * @param title            title
   * @param resourceResolver resource resolver
   * @param language         language
   * @return SearchPillsModel
   */
  public static SearchPillsModel getSearchPills(String title,
      ResourceResolver resourceResolver, String language) {
    SearchPillsModel searchPillsModel = new SearchPillsModel();
    searchPillsModel.setTitle(title);
    searchPillsModel.setPills(getSearchSuggestions(resourceResolver, language));
    return searchPillsModel;
  }

  /**
   * This method is used to get list of SearchPill.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @return list of SearchPill
   */
  private static List<SearchPill> getSearchSuggestions(ResourceResolver resourceResolver,
      String language) {
    Resource solrKeyResource =
        getAdminSearchResource(resourceResolver, language).getChild(Constants.SOLR_KEY);
    if (solrKeyResource != null) {
      return CommonUtils.iteratorToStream(solrKeyResource.getChildren().iterator())
          .limit(NumberConstants.SIX).map(resource -> {

            SearchPillModel model = resource.adaptTo(SearchPillModel.class);
            SearchPill searchPill = new SearchPill();
            searchPill.setTitle(model.getSolrKey());
            searchPill.setId(model.getSolrKey());
            searchPill.setType(model.getType());
            searchPill.setUrlApp(model.getUrlApp());
            searchPill.setUrlWeb(model.getUrlWeb());
            searchPill.setAppType(model.getAppType());
            return searchPill;
          }).collect(Collectors.toList());
    }
    return new ArrayList<>();
  }

  /**
   * This method is used to get list of search Categories from cq:tags.
   *
   * @param lang             search string
   * @param resourceResolver resource resolver
   * @return list of types
   */
  public static List<DictItem> getSearchCategoriesFromTags(ResourceResolver resourceResolver, String lang) {
    Locale locale = new Locale(lang);
    try (ResourceResolver resolver = resourceResolver) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      Stream<Resource> catResources;
      List<String> categories = getSearchCategories(resolver, lang);
      Resource searchCats =
          resolver.resolve(Constants.TAGS_URL + "/sauditourism/searchCategories");
      Iterator<Resource> resources = searchCats.listChildren();
      catResources = CommonUtils.iteratorToStream(resources);

      return catResources.map(resource -> tagManager.resolve(resource.getPath()))
          .map(tag -> new DictItem(tag.getName(), tag.getTitle(locale)))
          .collect(Collectors.toList());
    }
  }

  /**
   * This method is used to get title of the tag excluding Highligh searchCategory.
   *
   * @param language         locale
   * @param resourceResolver ResourceResolver
   * @param tags             tags array
   * @return tag title
   */
  public static DictItem getSearchCategoryTagTitleExcludeHighlight(ResourceResolver resourceResolver, String language,
                                           String[] tags) {
    if (tags != null) {
      Locale locale = new Locale(language);
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);

      for (String tag : tags) {
        if (!tag.contains("Highlight")) {

          return new DictItem(CommonUtils.getActualTagName(tag, tagManager),
              CommonUtils.getTagName(tag, tagManager, locale));
        }
      }
    }
    return null;
  }


  /**
   * This method is used to return favorite category.
   *
   * @param currentResource         current resource
   * @return favCategory
   */
  public static String getFavCategory(Resource currentResource) {
    String favCategory = "";
    String path =
        Optional.ofNullable(currentResource.getParent()).map(Resource::getPath).orElse(StringUtils.EMPTY);
    ValueMap properties = currentResource.adaptTo(ValueMap.class);
    String resourceType =
        properties.get(JcrResourceConstants.SLING_RESOURCE_TYPE_PROPERTY, Constants.BLANK);
    String language = CommonUtils.getLanguageForPath(path);

    DictItem tagCategory =
        getSearchCategoryTagTitleExcludeHighlight(currentResource.getResourceResolver(), language,
            properties.get("searchCategories", String[].class));

    if (tagCategory != null) {
      favCategory = tagCategory.getCode();
    } else {
      favCategory = AppUtils.getDefaultSearchCategory(resourceType);
    }

    return favCategory;
  }

  /**
   * This method is used to get list of types.
   *
   * @param resolver resolver
   * @param lang search string
   * @return list of types
   */
  public static List<DictItem> getPageTypes(ResourceResolver resolver, String lang) {
    Locale locale = new Locale(lang);
    List<DictItem> searchTypes = new ArrayList<>();
    if (Objects.nonNull(resolver)) {
      TagManager tagManager = resolver.adaptTo(TagManager.class);
      searchTypes = Optional.ofNullable(getAdminSearchResource(resolver, lang)
          .getChild(Constants.SEARCH_TYPE))
        .map(searchTypesresource ->
          StreamSupport.stream(searchTypesresource.getChildren().spliterator(), false)
            .map(typeResource ->
              typeResource.getValueMap().get(Constants.SEARCH_TYPE, Constants.BLANK))
            .filter(type -> !type.equals(Constants.BLANK))
            .map(tagId -> Constants.TAGS_URL
              + tagId.replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER))
            .map(resolver::resolve)
            .map(resource -> tagManager.resolve(resource.getPath()))
            .map(tag -> new DictItem(tag.getName(), tag.getTitle(locale)))
            .collect(Collectors.toList())
        )
        .orElse(Collections.emptyList());
    }
    return searchTypes;
  }
}
