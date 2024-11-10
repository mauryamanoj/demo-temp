package com.saudi.tourism.core.services.thingstodo.v1;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.jcr.Session;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.saudi.tourism.core.beans.bestexperience.Experience;
import com.saudi.tourism.core.beans.bestexperience.ExperienceData;
import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.events.v1.Pagination;
import com.saudi.tourism.core.services.filters.v1.FiltersService;
import com.saudi.tourism.core.services.filters.v1.FilterModel;
import com.saudi.tourism.core.services.thingstodo.v1.comparators.ThingToDoComparatorsChain;
import com.saudi.tourism.core.services.thingstodo.v1.filters.ThingsToDoFiltersChain;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.NumberConstants;
import com.saudi.tourism.core.utils.QueryConstants;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** ThingsToDo Service. */
@Component(service = ThingsToDoCFService.class, immediate = true)
@Slf4j
public class ThingsToDoCFServiceImpl implements ThingsToDoCFService, ImageSizesAndCropsAwareService {

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /**
   * adminService.
   */
  @Reference
  private AdminSettingsService adminService;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;


  /**
   * Saudi Tourism Configs.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Filters Service.
   */
  @Reference
  private FiltersService filtersService;

  /**
   * MemCache.
   */
  @Reference
  private Cache memCache;

  /**
   * i18n.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * Comparators chain.
   */
  @Reference
  private ThingToDoComparatorsChain comparatorsChain;

  /**
   * ThingsToDo filters chain.
   */
  @Reference
  private ThingsToDoFiltersChain thingsToDoFiltersChain;

  /**
   * Experience Service.
   */
  @Reference
  private transient ExperienceService experienceService;

  @Override
  public FetchThingsToDoResponse getFilteredThingsToDo(@NonNull final FetchThingsToDoRequest request) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var smartCropRenditions = buildSmartCropsRenditions(request);
      final var resourceBundle = i18nProvider.getResourceBundle(new Locale(request.getLocale()));
      FilterModel filters = new FilterModel();

      List<ThingToDoModel> filtredThingsToDo = Collections.emptyList();
      int total = 0;
      List<ThingToDoModel> paginatedThingsToDo = null;

      // Case 1 : Get handpicked ThingsToDo
      if (CollectionUtils.isNotEmpty(request.getPaths())) {
        filtredThingsToDo = fetchHandPickedThingsToDo(request, resourceResolver);
        total = CollectionUtils.size(filtredThingsToDo);
      }

      boolean isExperience = request.getType() != null
          && request.getType().size() == 1
          && Constants.EXPERIENCE.equals(request.getType().get(0));
      // Case 2 : if type is EXPERIENCE, get filtredThingsToDo from getAllExperiences (halaYalla)
      if (CollectionUtils.isEmpty(request.getPaths()) && isExperience) {
        Map<String, Object> queryStrings = buildExperienceQueryParameters(request);
        JsonElement halaYJsonResponse = fetchAllExperiences(queryStrings);
        if (halaYJsonResponse != null) {
          Experience experience = new Gson().fromJson(halaYJsonResponse.toString(), Experience.class);
          filtredThingsToDo = convertExperienceDataToThingToDoModels(experience.getData(),
              new Locale(request.getLocale()));
          total = CollectionUtils.size(filtredThingsToDo);
        }
      }

      // Case 3 : Get ThingsToDo from content fragment
      if (CollectionUtils.isEmpty(request.getPaths()) && !isExperience) {
        var thingsToDoResult = fetchFilteredThingsToDo(request, resourceResolver);
        filtredThingsToDo = thingsToDoResult.getData();
        filters = thingsToDoResult.getFilters();
        total = thingsToDoResult.getData().size();
        if (Objects.nonNull(request.getSortBy()) && request.getSortBy().contains("randomized")) {
          Collections.shuffle(filtredThingsToDo); // Shuffle for randomization
        } else {
          filtredThingsToDo = CollectionUtils.emptyIfNull(filtredThingsToDo).stream()
            .filter(Objects::nonNull)
            .sorted(comparatorsChain.buildComparator(request))
            .collect(Collectors.toList());
        }
      }

      paginatedThingsToDo = getPaginatedThingsToDo(filtredThingsToDo, request);

      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      paginatedThingsToDo =
          CollectionUtils.emptyIfNull(paginatedThingsToDo).stream()
              .filter(Objects::nonNull)
              .filter(t -> StringUtils.isNotEmpty(t.getType()))
              .map(
                  t -> {
                    if (Objects.nonNull(t)) {
                      t = t.withType(Optional.ofNullable(
                          getCardType(t, resourceBundle, resourceResolver, request.getLocale()))
                          .orElse("")
                          .toUpperCase());

                      t =
                          t.withCategories(
                              CollectionUtils.emptyIfNull(t.getCategoriesTags()).stream()
                                  .map(
                                      p -> {
                                        final var tags =
                                            TagUtils.getTagOrChildren(
                                                p, null, resourceResolver, request.getLocale());

                                        if (CollectionUtils.isNotEmpty(tags)) {
                                          return tags.get(0);
                                        }
                                        return null;
                                      })
                                  .filter(Objects::nonNull)
                                  .collect(Collectors.toList()));

                      CollectionUtils.emptyIfNull(t.getBannerImages()).stream()
                          .forEach(
                              i ->
                                  DynamicMediaUtils.setAllImgBreakPointsInfo(
                                      i,
                                      smartCropRenditions,
                                      CropEnum.CROP_600x600.getValue(),
                                      CropEnum.CROP_600x600.getValue(),
                                      BreakPointEnum.DESKTOP.getValue(),
                                      BreakPointEnum.MOBILE.getValue()));
                    }

                    t = t.withHideFavorite(
                        !saudiTourismConfigs.getEnableFavorite() || t.getHideFavorite());

                    if (tagManager != null && StringUtils.isNotBlank(t.getAgeTag())) {
                      t = t.withAge(
                        Optional.ofNullable(CommonUtils.getTagName(t.getAgeTag(), tagManager,
                            new Locale(request.getLocale())))
                          .orElse(null));
                    }

                    t = t.withIdealFor(t.getCategories());

                    return t;
                  })
              .filter(Objects::nonNull)
              .collect(Collectors.toList());

      final var pagination =
          Pagination.builder()
              .total(total)
              .limit(request.getLimit())
              .offset(request.getOffset())
              .build();

      return FetchThingsToDoResponse.builder()
          .data(paginatedThingsToDo)
          .filters(filters)
          .pagination(pagination)
          .build();
    }
  }

  /**
   * Get Paginated ThingsToDo.
   * @param filteredThingsToDo
   * @param request
   * @return List<ThingToDoModel>
   */
  private List<ThingToDoModel> getPaginatedThingsToDo(List<ThingToDoModel> filteredThingsToDo,
                                                      FetchThingsToDoRequest request) {
    if (request.getLimit() > 0) {
      return CollectionUtils.emptyIfNull(filteredThingsToDo).stream()
        .skip(request.getOffset())
        .limit(request.getLimit())
        .collect(Collectors.toList());
    } else {
      return CollectionUtils.emptyIfNull(filteredThingsToDo).stream()
        .skip(request.getOffset())
        .collect(Collectors.toList());
    }
  }
  /**
   * Get card type.
   *
   * @param thingToDoModel
   * @param resourceBundle
   * @param resourceResolver
   * @param language
   * @return String
   */
  private String getCardType(ThingToDoModel thingToDoModel, ResourceBundle resourceBundle,
                                ResourceResolver resourceResolver, String language) {
    if ("poi".equals(thingToDoModel.getType())) {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      // Make sure tagManager is available and typeValue is not empty
      if (Objects.isNull(tagManager) || StringUtils.isEmpty(thingToDoModel.getTypeValue())) {
        return "";
      }
      Locale locale = new Locale(language);
      return CommonUtils.getTagName(thingToDoModel.getTypeValue(), tagManager, locale);
    } else {
      if (Objects.nonNull(resourceBundle)) {
        return resourceBundle.getString(thingToDoModel.getType());
      }
      return thingToDoModel.getType();
    }
  }

  /**
   * Fetch handpicked ThingsToDo.
   *
   * @param request Current request.
   * @param resourceResolver Resource Resolver.
   * @return Handpicked ThingsToDo.
   */
  private List<ThingToDoModel> fetchHandPickedThingsToDo(
      @NotNull FetchThingsToDoRequest request, @NonNull ResourceResolver resourceResolver) {
    return request.getPaths().stream()
        .map(p -> resourceResolver.getResource(p))
        .filter(Objects::nonNull)
        .map(
            r -> {
              var tingToDo = r.adaptTo(ThingToDoModel.class);
              return tingToDo;
            })
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }


  /**
   * Fetch Filtered ThingsToDo.
   *
   * @param request Current request.
   * @param resourceResolver Resource Resolver.
   * @return ThingsToDo with filters
   */
  private FetchThingsToDoResponse fetchFilteredThingsToDo(
      @NotNull final FetchThingsToDoRequest request,
      @NonNull final ResourceResolver resourceResolver) {

    final String memCacheKey = generateThingsToDoCacheKey(request);
    FetchThingsToDoResponse holder = (FetchThingsToDoResponse) memCache.get(memCacheKey);

    if (holder == null) {
      var allResultResources = fetchAllThingsToDo(request, resourceResolver);
      if (Objects.isNull(allResultResources)
          || Objects.isNull(allResultResources.getResources()) || !allResultResources.getResources().hasNext()) {
        return FetchThingsToDoResponse.builder()
          .data(Collections.emptyList())
          .filters(new FilterModel())
          .pagination(Pagination.builder()
            .total(0)
            .build())
          .build();
      }
      var filteredResultResources = CommonUtils.iteratorToStream(allResultResources.getResources())
          .filter(r -> {
            Resource subResource = resourceResolver.getResource(r.getPath() + Constants.JCR_CONTENT_DATA_MASTER);
            if (subResource == null) {
              return false;
            }
            ValueMap valueMap = subResource.getValueMap();
            String title = valueMap.get("title", String.class);
            return StringUtils.isNotEmpty(title) && thingsToDoFiltersChain.doFilter(request, subResource);
          })
          .collect(Collectors.toList());
      var filteredThingsToDo = filteredResultResources.stream()
          .map(r -> r.adaptTo(ThingToDoModel.class))
          .collect(Collectors.toList());
      List<FiltersSettings> filtersSettings = adminService.getThingsToDoFilters(request.getLocale());
      var  filters = filtersService.getFiltersFromResult(resourceResolver, allResultResources, filtersSettings,
          request.getLocale(),
          request.getType());

      holder = FetchThingsToDoResponse.builder()
        .data(filteredThingsToDo)
        .filters(filters)
        .build();

      if (CollectionUtils.isNotEmpty(holder.getData())) {
        memCache.add(memCacheKey, holder, NumberConstants.CACHE_PERIOD_15_MINUTES);
      }
    }
    return holder;
  }

  /**
   * Generate cache key for the request.
   * @param request
   * @return String
   */
  public static String generateThingsToDoCacheKey(FetchThingsToDoRequest request) {
    // Use Stream.concat to combine multiple streams for various fields
    String keyPart = Stream.of(
          request.getLocale(),
          Objects.toString(request.getLimit()),
          Objects.toString(request.getOffset()),
          listToString(request.getType()),
          listToString(request.getCategories()),
          listToString(request.getPoiTypes()),
          listToString(request.getDestinations()),
          listToString(request.getSeasons()),
          Objects.toString(request.getStartDate(), ""),
          Objects.toString(request.getEndDate(), ""),
          Objects.toString(request.getKeyword())
          )
        .filter(Objects::nonNull) // Filter out any null values
        .collect(Collectors.joining("_")); // Concatenate with an underscore as a delimiter
    // Hash the key part to ensure a manageable key size
    return Constants.KEY_PREFIX_ALL_THINGS_TO_DO + keyPart.hashCode();
  }

  private static String listToString(List<String> list) {
    if (list == null) {
      return "";
    }
    return String.join(",", list);
  }

  /**
   * Fetch All ThingsToDo with filters.
   *
   * @param request          Current request.
   * @param resourceResolver Resource Resolver.
   *
   * @return All ThingsToDo with filters, with active events only (see applyEventDateFilters).
   */
  @Override
  public SearchResult fetchAllThingsToDo(
      @NotNull final FetchThingsToDoRequest request,
      @NonNull final ResourceResolver resourceResolver) {
    final var query = queryBuilder.createQuery(
        PredicateGroup.create(buildThingsToDoQueryMap(request)),
        resourceResolver.adaptTo(Session.class));
    // Set to 0 : treat it as unlimited
    query.setHitsPerPage(0);
    return query.getResult();
  }

  private Map buildThingsToDoQueryMap(@NonNull FetchThingsToDoRequest request) {
    final Map<String, Object> map = new HashMap<>();
    // To keep track of the index in the map
    // it's important to start from 20 to avoid conflicts with the others groups 1_group 2_group etc.
    final AtomicInteger index = new AtomicInteger(20);

    // Map each type to its corresponding configuration path
    Map<String, String> typeToPathMap = Map.of(
        ThingToDoType.ATTRACTION.getCode(), saudiTourismConfigs.getAttractionsCFPath(),
        ThingToDoType.ACTIVITY.getCode(), saudiTourismConfigs.getActivitiesCFPath(),
        ThingToDoType.TOUR.getCode(), saudiTourismConfigs.getToursCFPath(),
        ThingToDoType.EVENT.getCode(), saudiTourismConfigs.getEventsCFPath(),
        ThingToDoType.POI.getCode(), saudiTourismConfigs.getPoisCFPath()
    );

    if (CollectionUtils.isEmpty(request.getType())) {
      map.put("group.p.or", "true");
      typeToPathMap.forEach((type, path) -> {
        String groupKey = "group." + index.getAndIncrement();
        map.put(groupKey + "_path",
            MessageFormat.format(path, request.getLocale()));
        if (type.equals(ThingToDoType.EVENT.getCode())) {
          applyEventDateFilters(map, groupKey + "_group");
        }
      });
    } else {
      map.put("group.p.or", "true");
      List<String> distinctTypes = request.getType().stream()
          .distinct()
          .collect(Collectors.toList());
      distinctTypes.forEach(type -> {
        if (typeToPathMap.containsKey(type)) {
          String groupKey = "group." + index.getAndIncrement();
          map.put(groupKey + "_path",
              MessageFormat.format(typeToPathMap.get(type), request.getLocale()));
          if (type.equals(ThingToDoType.EVENT.getCode())) {
            applyEventDateFilters(map, groupKey + "_group");
          }
        }
      });
    }

    map.put(QueryConstants.CATEGORIES_PREDICATE_KEY, "jcr:content/data/master/categories");

    if (CollectionUtils.isNotEmpty(request.getPoiTypes())) {
      map.put("3_group.p.or", "true");
      // On attraction POI type is stored under the property 'typeValue'
      map.put("3_group.1_property", "jcr:content/data/master/typeValue");
      IntStream.range(0, CollectionUtils.size(request.getPoiTypes()))
          .forEach(
              idx ->
                  map.put(
                      String.format("3_group.1_property.%d_value", idx),
                      request.getPoiTypes().get(idx)));

      // On activity POI type is stored under the property 'type'
      map.put("3_group.2_property", "jcr:content/data/master/type");
      IntStream.range(0, CollectionUtils.size(request.getPoiTypes()))
          .forEach(
              idx ->
                  map.put(
                      String.format("3_group.2_property.%d_value", idx),
                      request.getPoiTypes().get(idx)));
    }

    map.put(QueryConstants.DESTINATION_VALUE_PREDICATE_KEY, "jcr:content/data/master/locationValue");

    map.put(QueryConstants.SEASON_PREDICATE_KEY, "jcr:content/data/master/season");

    map.put(Constants.PREDICATE_TYPE, "dam:Asset");
    map.put("orderby", "@jcr:content/jcr:created");
    map.put("orderby.sort", "desc");

    map.put("p.limit", -1);

    return map;
  }

  /**
   * Apply Event Date Filters.
   * @param map
   * @param groupKey
   */
  private void applyEventDateFilters(Map<String, Object> map, String groupKey) {
    String baseGroupKey = groupKey + ".1_group";
    map.put(baseGroupKey + ".p.or", "true");

    // Condition 1: endDate is in the future (not expired)
    String futureDateKey = baseGroupKey + ".1_group.1_relativedaterange";
    map.put(futureDateKey + ".property", "jcr:content/data/master/endDate");
    map.put(futureDateKey + ".lowerBound", "0d");  // includes today and future dates

    // Condition 2: endDate is not set
    String noEndDateKey = baseGroupKey + ".2_group";
    map.put(noEndDateKey + ".1_property", "jcr:content/data/master/endDate");
    map.put(noEndDateKey + ".1_property.operation", "not");
    map.put(noEndDateKey + ".2_relativedaterange.property", "jcr:content/data/master/startDate");
    map.put(noEndDateKey + ".2_relativedaterange.lowerBound", "0d");
  }

  /**
   * Build Experience Query Parameters.
   * @param request FetchThingsToDoRequest
   * @return Map<String, Object>
   */
  private Map<String, Object> buildExperienceQueryParameters(FetchThingsToDoRequest request) {
    Map<String, Object> queryStrings = new HashMap<>();
    Optional.ofNullable(request.getLocale())
        .ifPresent(locale -> queryStrings.put("lang", locale));
    Optional.ofNullable(request.getDestination())
        .ifPresent(destination -> queryStrings.put("city", destination));
    Optional.ofNullable(request.getFreeOnly())
        .ifPresent(freeOnly -> queryStrings.put("free_only", freeOnly));
    Optional.ofNullable(request.getStartDate())
        .ifPresent(startDate -> queryStrings.put("start_date", startDate));
    Optional.ofNullable(request.getEndDate())
        .ifPresent(endDate -> queryStrings.put("end_date", endDate));
    return queryStrings;
  }

  /**
   * Fetch All Experiences.
   * Call HalaYalla API to get all experiences.
   *
   * @param queryStrings HalaYalla queryStrings
   * @return All Experiences JSON Format
   */
  private JsonElement fetchAllExperiences(Map<String, Object> queryStrings) {
    try {
      return (JsonElement) experienceService.getAllExperiences(queryStrings);
    } catch (IOException e) {
      LOGGER.error("Error while getting All Experiences", e);
      return null; // Or handle it differently based on your application's needs
    }
  }

  /**
   * Convert ExperienceData to ThingToDoModel.
   *
   * @param experienceDataList List<ExperienceData>
   * @param locale Locale
   *
   * @return List<ThingToDoModel>
   */
  private List<ThingToDoModel> convertExperienceDataToThingToDoModels(List<ExperienceData> experienceDataList,
                                                                      Locale locale) {
    final var bookNow = i18nProvider.getResourceBundle(locale).getString("Book Now");
    final var sar = i18nProvider.getResourceBundle(locale).getString("Saudi Riyal");

    return experienceDataList.stream()
      .map(experienceData -> ThingToDoModel.builder()
        .type(Constants.EXPERIENCE)
        .title(experienceData.getName())
        .description(experienceData.getDescription())
        .bannerImages(Collections.singletonList(Image.builder()
          .defaultImage(experienceData.getBackgroundImage())
          .desktopImage(experienceData.getBackgroundImage())
          .mobileImage(experienceData.getBackgroundImage())
          .build()))
        .lng(experienceData.getLongitude())
        .lat(experienceData.getLatitude())
        .ticketPrice(String.valueOf(experienceData.getListPrice()))
        .ticketPriceSuffix(sar)
        .destination(ThingToDoModel.Destination.builder()
          .title(experienceData.getCity())
          .build())
        .ticketsCtaLink(Link.builder()
          .url(experienceData.getPackageUrl())
          .text(bookNow)
          .targetInNewWindow(true)
          .build())
        .pageLink(Link.builder()
          .url(experienceData.getPackageUrl())
          .targetInNewWindow(true)
          .build())
        .build())
      .collect(Collectors.toList());
  }
}
