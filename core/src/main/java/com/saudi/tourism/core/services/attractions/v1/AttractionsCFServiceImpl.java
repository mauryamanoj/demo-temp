package com.saudi.tourism.core.services.attractions.v1;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.contentfragment.attraction.AttractionCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.destinations.v1.Destination;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.saudi.tourism.core.services.attractions.v1.AttractionsCFServiceImpl.SERVICE_DESCRIPTION;

/** Destinations CF Service Impl. */
@Component(
    service = AttractionsCFService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class AttractionsCFServiceImpl implements AttractionsCFService, ImageSizesAndCropsAwareService {
  /** Service Description. */
  static final String SERVICE_DESCRIPTION = "Attractions Service";

  /** default limit. */
  private static final long DEFAULT_LIMIT = 6;

  /** Saudi Tourism Config. */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference private UserService userService;

  /** The Query builder. */
  @Reference private QueryBuilder queryBuilder;

  @Override
  public List<Attraction> fetchAllAttractions(
      @NonNull final FetchAttractionsRequest request,
      @NonNull final ResourceResolver resourceResolver) {

    final var query =
        queryBuilder.createQuery(
            PredicateGroup.create(buildAttractionsQueryMap(request)),
            resourceResolver.adaptTo(Session.class));
    // Set to 0 : treat it as unlimited
    query.setHitsPerPage(0);
    LOGGER.info("Starting Query");
    final var searchResult = query.getResult();
    LOGGER.info("Size of returned result: { }",searchResult.getTotalMatches());

    final var resultResources = searchResult.getResources();
    if (!resultResources.hasNext()) {
      return Collections.emptyList();
    }


    return Stream.generate(() -> null)
        .takeWhile(x -> resultResources.hasNext())
        .map(n -> resultResources.next())
        .filter(Objects::nonNull)
        .map(r -> r.adaptTo(AttractionCFModel.class))
        .filter(Objects::nonNull)
        .map(Attraction::fromCFModel)
        .sorted(Comparator.comparing(Attraction::getTitle, Comparator.nullsLast(String::compareTo)))
        .collect(Collectors.toList());
  }

  @Override
  public FetchAttractionsResponse getFilteredAttractions(FetchAttractionsRequest request) {

    List<Attraction> filtredAttractions;
    final var smartCropRenditions = buildSmartCropsRenditions(request);
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      filtredAttractions = fetchAllAttractions(request, resourceResolver);

      LOGGER.info("Filtered Attraction size: {}",filtredAttractions.size());
      TagManager tagManager = userService.getResourceResolver().adaptTo(TagManager.class);
      Locale locale = new Locale(request.getLocale());
      filtredAttractions =
          filtredAttractions.stream()
              .map(
                  attraction -> {


                    // Process image and update the story
                    Image image = attraction.getBannerImage();
                    if (image != null) {
                      DynamicMediaUtils.setAllImgBreakPointsInfo(
                          image,
                          smartCropRenditions,
                          CropEnum.CROP_660x337.getValue(),
                          CropEnum.CROP_760x570.getValue(),
                          BreakPointEnum.DESKTOP.getValue(),
                          BreakPointEnum.MOBILE.getValue());
                    }

                    if (CollectionUtils.isNotEmpty(attraction.getCategories())) {
                      List<Category> categories =
                          attraction.getCategories().stream()
                              .map(Category::getTitle)
                              .map(tag -> tagManager.resolve(tag))
                              .filter(Objects::nonNull)
                              .map(tag -> TagUtils.createCategoryFromTag(tag, locale.getLanguage()))
                              .collect(Collectors.toList());
                      attraction.setCategories(categories);
                    }
                    return attraction;
                  })
              .collect(Collectors.toList());
      List<Attraction> paginatedAttractions;
      if (Objects.equals(request.getLimit(), 0)) {
        paginatedAttractions =
            CollectionUtils.emptyIfNull(filtredAttractions).stream()
                .skip(request.getOffset())
                .limit(DEFAULT_LIMIT)
                .collect(Collectors.toList());
      } else {
        paginatedAttractions =
            CollectionUtils.emptyIfNull(filtredAttractions).stream()
                .skip(request.getOffset())
                .limit(request.getLimit())
                .collect(Collectors.toList());
      }
      final var pagination =
          Pagination.builder()
              .total(CollectionUtils.size(filtredAttractions))
              .limit(request.getLimit())
              .offset(request.getOffset())
              .build();

      List<Destination> destinationList =
          filtredAttractions.stream()
              .map(Attraction::getDestinationCFModel)
              .filter(Objects::nonNull)
              .map(
                  item -> {
                    return Destination.builder()
                        .title(item.getTitle())
                        .path(item.getResource().getPath())
                        .build();
                  })
              .distinct()
              .collect(Collectors.toList());
      List<Category> categoriesList;

      if (StringUtils.isNotEmpty(request.getTag())) {
        // Directly resolve tags from the request
        categoriesList = Arrays.stream(request.getTag().split(","))
          .map(String::trim).map(p -> TagUtils.getTagOrChildren(p, tagManager, resourceResolver, locale.getLanguage()))
            .flatMap(List::stream)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
      } else {
        // Derive tags from filtered attractions
        categoriesList = filtredAttractions.stream()
          .map(Attraction::getCategories)
          .flatMap(List::stream)
          .filter(Objects::nonNull)
          .map(cat -> extractTopLevelCategory(cat, tagManager, locale))
          .filter(Objects::nonNull)
          .distinct()
          .collect(Collectors.toList());
      }

      return FetchAttractionsResponse.builder()
          .data(paginatedAttractions)
          .pagination(pagination)
          .destinationList(destinationList)
          .categoryList(categoriesList)
          .build();
    }
  }

  /**
   *  Extract category from bucket value.
   *
   * @param cat
   * @param tagManager
   * @param locale
   * @return Category
   */
  private Category extractTopLevelCategory(Category cat, TagManager tagManager, Locale locale) {
    // Early exit if category is null
    if (cat == null) {
      return null;
    }

    // Get the category ID and check if it can potentially have a parent
    String catId = cat.getId();
    if (catId.contains("/")) {
      // Split the category ID to extract the possible parent tag
      String[] parts = catId.split("/");
      if (parts.length >= 2) {
        String parentTagPath = parts[0] + "/" + parts[1]; // Construct the parent tag path
        com.day.cq.tagging.Tag parentTag = tagManager.resolve(parentTagPath);
        if (parentTag != null) {
          // Return a new category derived from the parent tag
          return TagUtils.createCategoryFromTag(parentTag, locale.getLanguage());
        }
      }
    }

    // Return the input category by default if no parent tag could be resolved or if structure is insufficient
    return cat;
  }

  private Map<String, Object> buildAttractionsQueryMap(@NonNull FetchAttractionsRequest request) {
    Map<String, Object> map = new HashMap<>();
    String attractionsCFPath = MessageFormat.format(saudiTourismConfigs.getAttractionsCFPath(), request.getLocale());
    map.put("1_group.1_path", attractionsCFPath);

    if (StringUtils.isNotEmpty(request.getDestination())) {
      final AtomicInteger destinationCounter = new AtomicInteger(1);
      Arrays.stream(request.getDestination().split(","))
          .filter(StringUtils::isNotEmpty)
          .forEachOrdered(destination -> {
            map.put("2_group.1_property", "jcr:content/data/master/locationValue");
            map.put("2_group.1_property." + destinationCounter.getAndIncrement() + "_value", destination.trim());
          });
    }

    if (StringUtils.isNotEmpty(request.getTag())) {
      final AtomicInteger tagCounter = new AtomicInteger(1);
      Arrays.stream(request.getTag().split(","))
          .filter(StringUtils::isNotEmpty)
          .forEachOrdered(tag -> {
            map.put("2_group.2_property", "jcr:content/data/master/categories");
            map.put("2_group.2_property.operation", "like");
            map.put("2_group.2_property." + tagCounter.getAndIncrement() + "_value", tag.trim() + "%");
          });
    }

    map.put(Constants.PREDICATE_TYPE, "dam:Asset");
    map.put("orderby", "@jcr:content/jcr:created");
    map.put("orderby.sort", "desc");
    map.put("p.limit", request.getLimit());
    map.put("p.offset", request.getOffset());

    return map;
  }
}
