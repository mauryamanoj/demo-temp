package com.saudi.tourism.core.services.mobile.v1.filters;

import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.models.components.PackagePageSettings;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.filters.v1.FilterModel;
import com.saudi.tourism.core.services.filters.v1.FiltersIdValueModel;
import com.saudi.tourism.core.services.filters.v1.FiltersItemFilterModel;
import com.saudi.tourism.core.services.filters.v1.FiltersService;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesResponse;
import com.saudi.tourism.core.services.stories.v1.StoriesCFService;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.QueryConstants;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.day.cq.personalization.offerlibrary.usebean.OffercardPropertiesProvider.LOGGER;

@Component(service = MobileFiltersService.class, immediate = true)
public class MobileFiltersServiceImpl implements MobileFiltersService {

  /** adminService. */
  @Reference private AdminSettingsService adminService;

  /** userService. */
  @Reference private UserService userService;

  /** filtersService. */
  @Reference private FiltersService filtersService;

  /** thingsToDoCFService. */
  @Reference private ThingsToDoCFService thingsToDoCFService;

  /** storiesCFService. */
  @Reference private StoriesCFService storiesCFService;

  @Override
  public MobileFilter getFilters(@NonNull FetchMobileFiltersRequest request) {
    MobileFilter filters = new MobileFilter();
    List<FilterCategories> categories = new ArrayList<>();
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      final var filtersSettings = getFiltersSettings(request.getLocale(), resolver);
      FetchThingsToDoRequest fetchThingsToDoRequest = new FetchThingsToDoRequest();
      fetchThingsToDoRequest.setLocale(request.getLocale());
      fetchThingsToDoRequest.setType(request.getTypes());


      List<String> ttdFilterTypes = Arrays.asList("attraction", "tour", "activity", "poi", "event");
      // Case noType is considered like all things to do.
      if (CollectionUtils.isEmpty(request.getTypes()) || CollectionUtils.isNotEmpty(request.getTypes()) && request
          .getTypes().stream()
          .anyMatch(ttdFilterTypes::contains)) {

        SearchResult result = thingsToDoCFService.fetchAllThingsToDo(fetchThingsToDoRequest, resolver);

        final var thingsToDoFilterCategories =
            getFilterCategoriesFromResult(
                resolver, result, filtersSettings, fetchThingsToDoRequest.getLocale(),
                fetchThingsToDoRequest.getType());
        categories.addAll(thingsToDoFilterCategories);

      } else if (CollectionUtils.isNotEmpty(request.getTypes())
          && request.getTypes().contains("story")) {

        FetchStoriesRequest fetchStoriesRequest = new FetchStoriesRequest();
        fetchStoriesRequest.setLocale(request.getLocale());

        FetchStoriesResponse storiesResponse = storiesCFService.fetchAllStories(fetchStoriesRequest, resolver);
        var storiesFilters = adminService.getStoriesFilters(request.getLocale());
        fillFilterCategoriesFromStoriesResult(storiesResponse.getFilters(), storiesFilters);
        if (storiesFilters != null && storiesFilters.getMobileFilters() != null) {
          categories.addAll(storiesFilters.getMobileFilters());
        }
      } else if (CollectionUtils.isNotEmpty(request.getTypes())
          && request.getTypes().contains(Constants.EXPERIENCE)) {
        PackagePageSettings hySettings = adminService.getPackagePageSettings(request.getLocale());
        if (hySettings != null) {
          categories.addAll(hySettings.getMobileFilters());
        }
      }
      filters.setCategories(categories);

      final var header = getHeaderTypes(request, resolver);
      filters.setHeader(header);
    }

    return filters;
  }

  private TypeFilterModel getHeaderTypes(FetchMobileFiltersRequest request, ResourceResolver resolver) {
    String mobileFiltersConfigPath =
        StringUtils.replaceEach(
            Constants.MOBILE_FILTERS_CONFIG_RS_PATH,
            new String[] {Constants.LANGUAGE_PLACEHOLDER},
            new String[] {request.getLocale()});

    final var resource = resolver.getResource(mobileFiltersConfigPath);
    if (resource == null) {
      return null;
    }

    TypeFilterModel model = resource.adaptTo(TypeFilterModel.class);

    if (CollectionUtils.isNotEmpty(request.getFilterTypes())) {
      model.setItems(
          model.getItems().stream()
              .filter(Objects::nonNull)
              .filter(type -> Objects.isNull(type.getId()) || request.getFilterTypes().contains(type.getId()))
              .collect(Collectors.toList()));
    }

    return model;
  }

  private List<MobileFiltersSettings> getFiltersSettings(
      final String language, ResourceResolver resolver) {
    List<MobileFiltersSettings> filtersSettingsList = new ArrayList<>();

    String thingsToDoFiltersPath =
        StringUtils.replaceEach(
            Constants.THINGS_TO_DO_TYPES_FILTERS_PATH,
            new String[] {Constants.LANGUAGE_PLACEHOLDER},
            new String[] {language});

    return Optional.ofNullable(resolver.getResource(thingsToDoFiltersPath))
        .map(Resource::getChildren)
        .map(
            children ->
                StreamSupport.stream(children.spliterator(), false)
                    .map(child -> Optional.ofNullable(child.adaptTo(MobileFiltersSettings.class)))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList()))
        .orElseGet(
            () -> {
              LOGGER.warn("Resource not found by path {}", thingsToDoFiltersPath);
              return Collections.emptyList();
            });
  }

  private void fillFilterCategoriesFromStoriesResult(FilterModel storiesFilterModel,
                                                                         FiltersSettings filterSettings) {

    var mobileFilters = filterSettings.getMobileFilters();
    if (mobileFilters == null) {
      return;
    }
    for (FilterCategories filterSetting : mobileFilters) {

      if (filterSetting == null || filterSetting.getId() == null) {
        continue; // Skip if filterSetting or its ID is null
      }

      List<FiltersIdTitleModel> filterItems = null;

      switch (filterSetting.getId()) {
        case "categories":
          filterItems = mapFilters(storiesFilterModel.getCategories());
          break;
        case "destinations":
          filterItems = mapFilters(storiesFilterModel.getDestinations());
          break;
        case "seasons":
          filterItems = mapFilters(storiesFilterModel.getSeasons());
          break;
        default: // Do nothing
          continue;
      }
      filterSetting.setItems(filterItems); // Safely set items, as null is handled inside mapFilters

    }
  }


  private List<FiltersIdTitleModel> mapFilters(FiltersItemFilterModel dataHolder) {
    if (dataHolder == null || dataHolder.getData() == null) {
      return Collections.emptyList();
    }

    return dataHolder.getData().stream()
        .map(this::createFiltersIdTitleModel)
        .collect(Collectors.toList());
  }

  private FiltersIdTitleModel createFiltersIdTitleModel(FiltersIdValueModel filtersIdValueModel) {
    if (filtersIdValueModel == null) {
      return null;
    }

    var filtersValues = new FiltersIdTitleModel();
    filtersValues.setId(filtersIdValueModel.getId());
    filtersValues.setTitle(filtersIdValueModel.getValue());
    return filtersValues;
  }

  private List<FilterCategories> getFilterCategoriesFromResult(
      ResourceResolver resourceResolver,
      SearchResult result,
      List<MobileFiltersSettings> filtersSettings,
      String locale,
      List<String> requestTypes) {
    List<FilterCategories> filterCategories = new ArrayList<>();
    var tagManager = resourceResolver.adaptTo(TagManager.class);

    List<String> filterTypes;
    if (CollectionUtils.isEmpty(requestTypes)) {
      // If no specific types are requested, consider all types
      filterTypes = Collections.singletonList("all");
    } else {
      // Otherwise, use the provided list of types
      filterTypes = requestTypes;
    }

    // Find all settings that match any of the types in the list
    List<FilterCategories> distinctFilters =
        filtersSettings.stream()
            .filter(settings -> filterTypes.contains(settings.getType()))
            .flatMap(settings -> settings.getFilters().stream())
            .distinct()
            .collect(Collectors.toList());

    try {
      Map<String, Facet> facets = result.getFacets();
      if (facets != null) {
        for (FilterCategories filterSetting : distinctFilters) {
          switch (filterSetting.getId()) {
            case "categories":
              List<FiltersIdTitleModel> categories =
                  getCategoriesFromResult(
                      facets.get(QueryConstants.CATEGORIES_PREDICATE_KEY), tagManager, locale);
              filterSetting.setItems(categories);
              filterCategories.add(filterSetting);
              break;
            case "destinations":
              List<FiltersIdTitleModel> locations =
                  getDestinationsFromResult(
                      resourceResolver, facets.get(QueryConstants.DESTINATION_VALUE_PREDICATE_KEY));
              filterSetting.setItems(locations);
              filterCategories.add(filterSetting);
              break;
            case "seasons":
              List<FiltersIdTitleModel> seasons =
                  getSeasonsFromResult(
                      resourceResolver, facets.get(QueryConstants.SEASON_PREDICATE_KEY));
              filterSetting.setItems(seasons);
              filterCategories.add(filterSetting);
              break;
            case "date":
              filterCategories.add(filterSetting);
            default: // Do nothing
              break;
          }

        }
      }
    } catch (Exception e) {
      LOGGER.error("Error while getting facets from result", e);
    }
    return filterCategories;
  }

  /**
   * Get locations from result.
   *
   * @param resourceResolver
   * @param facet
   * @return List<AppFilterItem>
   */
  private List<FiltersIdTitleModel> getDestinationsFromResult(
      ResourceResolver resourceResolver, Facet facet) {
    List<FiltersIdTitleModel> items = new ArrayList<>();
    if (Objects.nonNull(facet)) {
      for (Bucket bucket : facet.getBuckets()) {
        String locationCFPath = bucket.getValue();
        if (Objects.nonNull(locationCFPath)) {
          FiltersIdTitleModel item = new FiltersIdTitleModel();
          final var destinationCF = resourceResolver.getResource(locationCFPath);
          if (Objects.nonNull(destinationCF)) {
            var destination = destinationCF.adaptTo(DestinationCFModel.class);
            if (Objects.nonNull(destination)) {
              item.setId(locationCFPath.substring(locationCFPath.lastIndexOf("/") + 1));
              item.setTitle(destination.getTitle());
              items.add(item);
            }
          }
        }
      }
    }
    return items;
  }

  /**
   * Get seasons from result.
   *
   * @param resourceResolver
   * @param facet
   * @return List<IdValueModel>
   */
  private List<FiltersIdTitleModel> getSeasonsFromResult(
      ResourceResolver resourceResolver, Facet facet) {
    List<FiltersIdTitleModel> items = new ArrayList<>();
    if (Objects.nonNull(facet)) {
      for (Bucket bucket : facet.getBuckets()) {
        String seasonCFPath = bucket.getValue();
        if (Objects.nonNull(seasonCFPath)) {
          FiltersIdTitleModel item = new FiltersIdTitleModel();
          final var seasonCF = resourceResolver.getResource(seasonCFPath);
          if (Objects.nonNull(seasonCF)) {
            var season = seasonCF.adaptTo(SeasonCFModel.class);
            if (Objects.nonNull(season)) {
              item.setId(seasonCFPath.substring(seasonCFPath.lastIndexOf("/") + 1));
              item.setTitle(season.getTitle());
              items.add(item);
            }
          }
        }
      }
    }
    return items;
  }

  /**
   * Get categories from result.
   *
   * @param facet
   * @param tagManager
   * @param locale
   * @return List<IdValueModel>
   */
  private List<FiltersIdTitleModel> getCategoriesFromResult(
      Facet facet, TagManager tagManager, String locale) {
    Set<FiltersIdTitleModel> items = new LinkedHashSet<>();
    if (Objects.nonNull(facet)) {
      Tag tag = null;
      for (Bucket bucket : facet.getBuckets()) {
        String bc = bucket.getValue();
        if (bc.contains("/")) {
          String category = extractCategory(bc);
          tag = tagManager.resolve(category);
          if (Objects.nonNull(tag)) {
            items.add(
                new FiltersIdTitleModel(
                    TagUtils.getTagWithoutNamespace(tag),
                    tag.getTitle(Locale.forLanguageTag(locale)),
                    tag.getPath()
                        .replace(TagUtils.CQ_TAGS, TagUtils.CONTENT_DAM_TAG_ICONS)
                        .replace(":", "-")
                        .concat(TagUtils.SVG)));
          }
        }
      }
    }
    return new ArrayList<>(items);
  }

  /**
   * Extract category from bucket value.
   *
   * @param bc
   * @return String
   */
  public String extractCategory(String bc) {
    if (bc == null) {
      return null;
    }

    String[] parts = bc.split("/");
    if (parts.length >= 2) {
      return parts[0] + "/" + parts[1];
    }

    return bc;
  }
}
