package com.saudi.tourism.core.services.filters.v1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.day.cq.search.facets.Bucket;
import com.day.cq.search.facets.Facet;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.QueryConstants;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import static com.saudi.tourism.core.services.filters.v1.FiltersServiceImpl.SERVICE_DESCRIPTION;

@Component(
    service = FiltersService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class FiltersServiceImpl implements FiltersService {

  /** This Service description for OSGi. */
  static final String SERVICE_DESCRIPTION = "Filters Service";

  /** User Service. */
  @Reference private AdminSettingsService adminService;

  @Override
  public FilterModel getFiltersFromResult(ResourceResolver resourceResolver,
                                    SearchResult result,
                                    List<FiltersSettings> filtersSettings,
                                    String locale,
                                    List<String> requestTypes) {
    FilterModel filters = new FilterModel();
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
    List<FiltersItemFilterModel> distinctFilters = filtersSettings.stream()
        .filter(settings -> filterTypes.contains(settings.getType()))
        .flatMap(settings -> settings.getFilters().stream())
        .distinct()
        .collect(Collectors.toList());

    try {
      Map<String, Facet> facets = result.getFacets();
      if (facets != null) {
        for (FiltersItemFilterModel filterSetting : distinctFilters) {
          switch (filterSetting.getKey()) {
            case "categories":
              List<FiltersIdValueModel> categories = getCategoriesFromResult(
                  facets.get(QueryConstants.CATEGORIES_PREDICATE_KEY),
                  tagManager,
                  locale);
              filterSetting.setData(categories);
              filters.setCategories(filterSetting);
              break;
            case "destinations":
              List<FiltersIdValueModel> locations = getDestinationsFromResult(resourceResolver,
                  facets.get(QueryConstants.DESTINATION_VALUE_PREDICATE_KEY));
              filterSetting.setData(locations);
              filters.setDestinations(filterSetting);
              break;
            case "seasons":
              List<FiltersIdValueModel> seasons = getSeasonsFromResult(resourceResolver,
                  facets.get(QueryConstants.SEASON_PREDICATE_KEY));
              filterSetting.setData(seasons);
              filters.setSeasons(filterSetting);
              break;
            case "date":
              filters.setDate(filterSetting);
            default:  // Do nothing
              break;
          }
        }
      }
    } catch (Exception e) {
      LOGGER.error("Error while getting facets from result", e);
    }
    return filters;
  }

  /**
   * Get locations from result.
   *
   * @param resourceResolver
   * @param facet
   * @return List<AppFilterItem>
   */
  private List<FiltersIdValueModel> getDestinationsFromResult(ResourceResolver resourceResolver, Facet facet) {
    List<FiltersIdValueModel> items = new ArrayList<>();
    if (Objects.nonNull(facet)) {
      for (Bucket bucket : facet.getBuckets()) {
        String locationCFPath = bucket.getValue();
        if (Objects.nonNull(locationCFPath)) {
          FiltersIdValueModel item = new FiltersIdValueModel();
          final var destinationCF = resourceResolver.getResource(locationCFPath);
          if (Objects.nonNull(destinationCF)) {
            var destination = destinationCF.adaptTo(DestinationCFModel.class);
            if (Objects.nonNull(destination)) {
              item.setId(locationCFPath.substring(locationCFPath.lastIndexOf("/") + 1));
              item.setValue(destination.getTitle());
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
   *
   * @return List<IdValueModel>
   */
  private List<FiltersIdValueModel> getSeasonsFromResult(ResourceResolver resourceResolver, Facet facet) {
    List<FiltersIdValueModel> items = new ArrayList<>();
    if (Objects.nonNull(facet)) {
      for (Bucket bucket : facet.getBuckets()) {
        String seasonCFPath = bucket.getValue();
        if (Objects.nonNull(seasonCFPath)) {
          FiltersIdValueModel item = new FiltersIdValueModel();
          final var seasonCF = resourceResolver.getResource(seasonCFPath);
          if (Objects.nonNull(seasonCF)) {
            var season = seasonCF.adaptTo(SeasonCFModel.class);
            if (Objects.nonNull(season)) {
              item.setId(seasonCFPath.substring(seasonCFPath.lastIndexOf("/") + 1));
              item.setValue(season.getTitle());
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
   *
   * @return List<IdValueModel>
   */
  private List<FiltersIdValueModel> getCategoriesFromResult(Facet facet, TagManager tagManager, String locale) {
    Set<FiltersIdValueModel> items = new LinkedHashSet<>();
    if (Objects.nonNull(facet)) {
      Tag tag = null;
      for (Bucket bucket : facet.getBuckets()) {
        String bc = bucket.getValue();
        if (bc.contains("/")) {
          String category = extractCategory(bc);
          tag = tagManager.resolve(category);
          if (Objects.nonNull(tag)) {
            items.add(new FiltersIdValueModel(TagUtils.getTagWithoutNamespace(tag),
                tag.getTitle(Locale.forLanguageTag(locale)),
                tag.getPath().replace(TagUtils.CQ_TAGS, TagUtils.CONTENT_DAM_TAG_ICONS).
                  replace(":", "-").concat(TagUtils.SVG)));
          }
        }
      }
    }
    return new ArrayList<>(items);
  }

  /**
   * Extract category from bucket value.
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
