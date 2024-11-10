package com.saudi.tourism.core.services.stories.v1;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.jcr.Session;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.day.cq.tagging.TagManager;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.FiltersSettings;
import com.saudi.tourism.core.models.components.contentfragment.story.StoryCFModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.filters.v1.FilterModel;
import com.saudi.tourism.core.services.filters.v1.FiltersService;
import com.saudi.tourism.core.services.stories.v1.filters.StoriesFiltersChain;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.QueryConstants;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;


@Component(
    service = StoriesCFService.class,
    immediate = true)
@Slf4j
public class StoriesCFServiceImpl implements StoriesCFService, ImageSizesAndCropsAwareService {

  /**
   * Saudi Tourism Config.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /** The Query builder. */
  @Reference private QueryBuilder queryBuilder;

  /**
   * Stories filters chain.
   */
  @Reference
  private StoriesFiltersChain storiesFiltersChain;

  /**
   * adminService.
   */
  @Reference
  private AdminSettingsService adminService;

  /**
   * Filters Service.
   */
  @Reference
  private FiltersService filtersService;

  @Override
  public FetchStoriesResponse getFilteredStories(FetchStoriesRequest request) {

    List<Story> filteredStories = Collections.emptyList();
    final var smartCropRenditions = buildSmartCropsRenditions(request);
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(request.getLocale());
      var allStories = fetchAllStories(request, resourceResolver);
      if (Objects.nonNull(allStories)) {
        filteredStories =  CollectionUtils.emptyIfNull(allStories.getData()).stream()
          .filter(e -> StringUtils.isNotEmpty(e.getTitle()) && storiesFiltersChain.doFilter(request, e))
          .collect(Collectors.toList());
      }
      var filters = allStories.getFilters();

      filteredStories =
        CollectionUtils.emptyIfNull(filteredStories).stream()
          .filter(Objects::nonNull)
          .map(story -> {
            // Process tags and update the story
            if (tagManager != null && story.getTags() != null && !story.getTags().isEmpty()) {
              String firstTag = story.getTags().get(0);
              if (StringUtils.isNotBlank(firstTag)) {
                String tagName = CommonUtils.getTagName(firstTag, tagManager, locale);
                story = story.withTag(tagName);
              }
            }
            // Process image and update the story
            Image image = story.getImage();
            if (image != null) {
              DynamicMediaUtils.setAllImgBreakPointsInfo(
                  image,
                  smartCropRenditions,
                  CropEnum.CROP_375x667.getValue(),
                  CropEnum.CROP_375x667.getValue(),
                  BreakPointEnum.DESKTOP.getValue(),
                  BreakPointEnum.MOBILE.getValue());
              story = story.withImage(image);

              story =
                story.withCategories(
                  CollectionUtils.emptyIfNull(story.getCategoriesTags()).stream()
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
              if (CollectionUtils.isNotEmpty(story.getCategories())) {
                // Set the first category as tag
                story = story.withTag(story.getCategories().get(0).getTitle());
              }

              CollectionUtils.emptyIfNull(story.getBannerImages()).stream()
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

            // Update hideFavorite based on configuration
            boolean hideFavoriteValue = !saudiTourismConfigs.getEnableFavorite()
                || Boolean.TRUE.equals(story.getHideFavorite());
            story = story.withHideFavorite(hideFavoriteValue);

            return story;
          })
          .collect(Collectors.toList());
      int limit = request.getLimit();
      if (limit <= 0) {
        limit = filteredStories.size();
      }
      List<Story> paginatedStories;
      if (Objects.equals(request.getLimit(), 0)) {
        paginatedStories =
          CollectionUtils.emptyIfNull(filteredStories).stream()
            .skip(request.getOffset())
            .collect(Collectors.toList());
      } else {
        paginatedStories =
          CollectionUtils.emptyIfNull(filteredStories).stream()
            .skip(request.getOffset())
            .limit(limit)
            .collect(Collectors.toList());
      }
      final var pagination =
          Pagination.builder()
          .total(CollectionUtils.size(filteredStories))
          .limit(limit)
          .offset(request.getOffset())
          .build();

      return FetchStoriesResponse.builder()
        .data(paginatedStories)
        .filters(filters)
        .pagination(pagination)
        .build();
    }
  }

  /**
   * Fetch all stories.
   *
   * @param request Fetch Stories Request
   * @param resourceResolver Resource Resolver
   * @return
   */
  @Override
  public FetchStoriesResponse fetchAllStories(
      @NonNull final FetchStoriesRequest request,
      @NonNull final ResourceResolver resourceResolver) {
    final var query =
        queryBuilder.createQuery(
          PredicateGroup.create(buildStoriesQueryMap(request)),
          resourceResolver.adaptTo(Session.class));
    query.setHitsPerPage(0);
    final var searchResult = query.getResult();

    final var resultResources = searchResult.getResources();

    if (!resultResources.hasNext()) {
      return FetchStoriesResponse.builder()
        .data(Collections.emptyList())
        .filters(new FilterModel())
        .build();
    }

    var stories = CommonUtils.iteratorToStream(resultResources)
        .map(r -> r.adaptTo(StoryCFModel.class))
        .filter(Objects::nonNull)
        .map(Story::fromCFModel)
        .collect(Collectors.toList());
    FiltersSettings filtersSettings = adminService.getStoriesFilters(request.getLocale());
    var  filters = filtersService.getFiltersFromResult(resourceResolver, searchResult,
        Collections.singletonList(filtersSettings), request.getLocale(),
        Collections.singletonList("story"));

    return FetchStoriesResponse.builder()
      .data(stories)
      .filters(filters)
      .build();
  }

  /**
   * Fetch latest stories.
   * @param locale Current locale
   * @param limit limit
   * @return list of Season CF Model.
   */
  @Override
  public List<StoryCFModel> fetchLatestStories(@NonNull String locale, String limit) {
    final String defaultLimit = "10";
    limit = Optional.ofNullable(limit).orElse(defaultLimit);
    FetchStoriesRequest request = FetchStoriesRequest.builder()
        .locale(locale)
        .limit(Integer.valueOf(limit))
        .build();
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var query =
          queryBuilder.createQuery(
          PredicateGroup.create(buildStoriesQueryMap(request)),
          resourceResolver.adaptTo(Session.class));
      final var searchResult = query.getResult();

      final var resultResources = searchResult.getResources();
      if (!resultResources.hasNext()) {
        return Collections.emptyList();
      }

      return Stream.generate(() -> null)
        .takeWhile(x -> resultResources.hasNext())
        .map(n -> resultResources.next())
        .map(r -> r.adaptTo(StoryCFModel.class))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    } catch (Exception e) {
      LOGGER.error("Error fetching latest stories: ", e);
      return Collections.emptyList();
    }
  }

  /**
   * Build the query map for fetching stories.
   *
   * @param request
   * @return query map.
   */
  private Map buildStoriesQueryMap(@NonNull FetchStoriesRequest request) {
    Map<String, Object> map = new HashMap<>();
    String storiesCFPath = MessageFormat.format(saudiTourismConfigs.getStoriesCFPath(), request.getLocale());
    map.put("1_group.1_path", storiesCFPath);

    map.put(QueryConstants.CATEGORIES_PREDICATE_KEY, "jcr:content/data/master/categories");
    map.put(QueryConstants.DESTINATION_VALUE_PREDICATE_KEY, "jcr:content/data/master/destination");

    map.put(Constants.PREDICATE_TYPE, "dam:Asset");
    map.put("orderby", "@jcr:content/jcr:lastModified");
    map.put("orderby.sort", "desc");
    map.put("p.limit", request.getLimit());
    return map;
  }
}
