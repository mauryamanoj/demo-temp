package com.saudi.tourism.core.models.components.attractions.v1;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.attractions.v1.AttractionsCFService;
import com.saudi.tourism.core.services.attractions.v1.FetchAttractionsRequest;
import com.saudi.tourism.core.services.attractions.v1.FetchAttractionsResponse;
import com.saudi.tourism.core.services.destinations.v1.Destination;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.jetbrains.annotations.NotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/** Model for attractions map component. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AttractionsMapModel {

  /**
   * Component Id.
   */
  @ValueMapValue
  @Expose
  private String componentId;

  /** Enable Manual Authoring. */
  @Expose
  @Default(booleanValues = false)
  @ValueMapValue
  private Boolean enableManualAuthoring;

  /** Resource Resolver. */
  @Inject
  private ResourceResolver resourceResolver;

  /** Current resource. */
  @Self
  private transient Resource currentResource;

  /** Reference of Saudi Tourism Configuration. */
  @Inject
  private transient SaudiTourismConfigs saudiTourismConfigs;

  /** The Attractions service. */
  @Inject
  private transient AttractionsCFService attractionsService;

  /**
   * Admin Service.
   */
  @Inject
  private transient AdminSettingsService adminService;

  /** Map Api Key. */
  @Expose
  private String mapApiKey;

  /** Show Map Border. */
  @Expose
  private boolean showMapBorder;

  /** Title. */
  @ValueMapValue
  @Expose
  private String title;

  /** Sub Title. */
  @ValueMapValue
  @Expose
  private String subTitle;

  /** CTA Link. */
  @ChildResource
  @Expose
  private Link link;

  /** Category Filter. */
  @ChildResource
  @Expose
  private ApiFilter categoryFilter;

  /** Destination Filter. */
  @ChildResource
  @Expose
  private ApiFilter destinationFilter;

  /** All Attractions Filter. */
  @Expose
  private ApiFilter allAttractionsFilter;

  /** Destinations. */
  @Inject
  @Named("destinations")
  @JsonIgnore
  private List<DestinationItem> destinationsCFPaths;

  /** Destination drop down values. */
  @Expose
  private List<Destination> destinationList;

  /** categories Icon. */
  @ValueMapValue
  private List<String> categories;

  /** Category Drop Down values. */
  @Expose
  private List<Category> categoryList;

  /**
   * No Results Found Label.
   */
  @ValueMapValue
  @Expose
  private String noResultsFoundLabel = "There are no items under this category";

  @PostConstruct
  void init() {
    mapApiKey = saudiTourismConfigs.getGoogleMapsKey();
    showMapBorder = saudiTourismConfigs.getShowMapBorder();

    if (Objects.nonNull(categoryFilter)) {
      categoryFilter.setApiUrl(saudiTourismConfigs.getCategoriesEndpoint());
    }

    if (Objects.nonNull(destinationFilter)) {
      destinationFilter.setApiUrl(saudiTourismConfigs.getDestinationsEndpoint());
    }

    allAttractionsFilter = new ApiFilter();
    allAttractionsFilter.setApiUrl(saudiTourismConfigs.getAttractionsEndpoint());

    if (!enableManualAuthoring) {
      loadFromContentFragment();
    } else {
      loadFromManualAuthoring();
    }
  }

  private void loadFromManualAuthoring() {

    if (resourceResolver == null) {
      return;
    }
    if (currentResource == null) {
      return;
    }

    destinationList =
      Optional.ofNullable(destinationsCFPaths).orElse(Collections.emptyList()).stream()
            .filter(Objects::nonNull)
            .map(
                destinationItem ->
                    resourceResolver.getResource(destinationItem.getDestinationCFPath()))
            .filter(Objects::nonNull)
            .map(resource -> resource.adaptTo(DestinationCFModel.class))
            .filter(Objects::nonNull)
            .map(
                destinationCF ->
                    Destination.builder()
                        .path(destinationCF.getResource().getPath())
                        .title(destinationCF.getTitle())
                        .build())
            .collect(Collectors.toList());

    TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    final var currentPage = pageManager.getContainingPage(currentResource);
    Locale locale = currentPage.getLanguage(true);

    categoryList =
      Optional.ofNullable(categories).orElse(Collections.emptyList()).stream()
        .map(p -> TagUtils.getTagOrChildren(p, tagManager, resourceResolver, locale.getLanguage()))
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  @NotNull
  private StringBuilder getStringBuilder(String categoriesParam, String destinationsParam) {
    StringBuilder completeUrl = new StringBuilder(saudiTourismConfigs.getAttractionsEndpoint());
    if (!categoriesParam.isEmpty() || !destinationsParam.isEmpty()) {
      completeUrl.append("?");
      boolean appendAnd = false;
      if (!categoriesParam.isEmpty()) {
        completeUrl.append(categoriesParam);
        appendAnd = true;
      }
      if (!destinationsParam.isEmpty()) {
        if (appendAnd) {
          completeUrl.append("&");
        }
        completeUrl.append(destinationsParam);
      }
    }
    return completeUrl;
  }

  private void loadFromContentFragment() {

    final var pageManager = resourceResolver.adaptTo(PageManager.class);
    assert pageManager != null;
    final var currentPage = pageManager.getContainingPage(currentResource);
    if (currentPage == null) {
      return;
    }
    Locale locale = currentPage.getLanguage(true);
    final var cfDestinationPath = Optional.ofNullable(currentPage.getProperties()
          .get(Constants.REFERENCED_FRAGMENT_REFERENCE, String.class)).orElse("");

    final var fetchAttractionsRequest =
        FetchAttractionsRequest.builder().locale(locale.getLanguage()).limit(0).offset(0).build();
    if (StringUtils.isNotEmpty(cfDestinationPath)) {
      fetchAttractionsRequest.setDestination(cfDestinationPath);
    }
    final FetchAttractionsResponse attractionResponse =
        attractionsService.getFilteredAttractions(fetchAttractionsRequest);
    destinationList = attractionResponse.getDestinationList();
    categoryList = filterCategoriesByWhitelist(attractionResponse.getCategoryList(), locale);

    if (StringUtils.isEmpty(cfDestinationPath)) {
      return;
    }

    final var cfResource = resourceResolver.getResource(cfDestinationPath);
    if (cfResource == null) {
      return;
    }

    final var cfModel = cfResource.adaptTo(DestinationCFModel.class);

    if (cfModel == null) {
      return;
    }
    if (Objects.nonNull(destinationFilter)) {
      destinationFilter.setApiUrl(null);
    }
    destinationList =
        Collections.singletonList(
            Destination.builder()
                .title(cfModel.getTitle())
                .path(cfModel.getResource().getPath())
                .build());
  }

  /**
   * Filter the list of categories by the whitelist.
   *
   * @param categoryList
   * @param locale
   * @return List<Category>
   */
  private List<Category> filterCategoriesByWhitelist(List<Category> categoryList, Locale locale) {

    if (CollectionUtils.isEmpty(categoryList)) {
      return Collections.emptyList();
    }

    List<String> whitelistedCategories = adminService.fetchWhitelistedAttractionCategories(locale.getLanguage());

    if (CollectionUtils.isEmpty(whitelistedCategories)) {
      return categoryList;
    }

    Set<String> whitelistedCategorySet = new HashSet<>(whitelistedCategories);

    // Filter the category list to include only those in the whitelist
    return categoryList.stream()
      .filter(category -> whitelistedCategorySet.contains(category.getId()))
      .collect(Collectors.toList());
  }

  /** Model for api filter. */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class ApiFilter {
    /** Filter Label. */
    @Expose
    @ValueMapValue
    private String label;

    /** API url. */
    @Expose
    private String apiUrl;
  }

  /** Questions By ContryType model. */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class DestinationItem {
    /** Destination CF Path. */
    @ValueMapValue
    private String destinationCFPath;
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
