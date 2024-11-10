package com.saudi.tourism.core.services.events.v1;

import com.day.cq.search.PredicateGroup;
import com.day.cq.search.QueryBuilder;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.events.v1.comparators.EventComparatorsChain;
import com.saudi.tourism.core.services.events.v1.filters.EventFiltersChain;
import com.saudi.tourism.core.utils.BreakPointEnum;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.CropEnum;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.jcr.Session;
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

/** Events Service. */
@Component(service = EventsCFService.class, immediate = true)
@Slf4j
public class EventsCFServiceImpl implements EventsCFService, ImageSizesAndCropsAwareService {
  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /**
   * Saudi Tourism Configs.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * Events filters chain.
   */
  @Reference
  private EventFiltersChain eventFiltersChain;

  /**
   * Events comparators chain.
   */
  @Reference
  private EventComparatorsChain eventComparatorsChain;

  /**
   * i18n.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Override
  public FetchEventsResponse getFilteredEvents(@NonNull final FetchEventsRequest request) {
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var eventsCFs = getAllEvents(resourceResolver, request.getLocale());

      final var filteredEventsCFs =
          CollectionUtils.emptyIfNull(eventsCFs).stream()
              .filter(e -> eventFiltersChain.doFilter(request, e))
              .collect(Collectors.toList());

      final var smartCropRenditions = buildSmartCropsRenditions(request);

      final var resourceBundle = i18nProvider.getResourceBundle(new Locale(request.getLocale()));

      final var filteredEvents =
          CollectionUtils.emptyIfNull(filteredEventsCFs).stream()
              .filter(Objects::nonNull)
              .map(
                  e -> {
                    var event = Event.fromCFModel(e);
                    if (Objects.nonNull(event)) {
                      event = event.withType(Optional.ofNullable(resourceBundle.getString(event.getType()))
                          .orElse("").toUpperCase());

                      event =
                          event.withCategories(
                              CollectionUtils.emptyIfNull(event.getCategoriesTags()).stream()
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

                      event =
                          event.withHideFavorite(
                              !saudiTourismConfigs.getEnableFavorite()
                                  || ObjectUtils.defaultIfNull(
                                      event.getHideFavorite(), Boolean.FALSE));

                      CollectionUtils.emptyIfNull(event.getBannerImages()).stream()
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
                    return event;
                  })
              .filter(Objects::nonNull)
              .sorted(eventComparatorsChain.buildComparator(request))
              .collect(Collectors.toList());

      int limit = request.getLimit();
      if (limit <= 0) {
        limit = filteredEvents.size();
      }

      List<Event> paginatedEvents = null;
      if (Objects.equals(limit, 0)) {
        paginatedEvents =
            CollectionUtils.emptyIfNull(filteredEvents).stream()
                .skip(request.getOffset())
                .collect(Collectors.toList());
      } else {
        paginatedEvents =
            CollectionUtils.emptyIfNull(filteredEvents).stream()
                .skip(request.getOffset())
                .limit(limit)
                .collect(Collectors.toList());
      }

      final var pagination =
          Pagination.builder()
              .total(CollectionUtils.size(filteredEvents))
              .limit(limit)
              .offset(request.getOffset())
              .build();

      return FetchEventsResponse.builder().data(paginatedEvents).pagination(pagination).build();
    }

  }

  /**
   * Method to fetch all the events.
   *
   * @param locale current locale
   * @param resourceResolver resource resolver
   * @return List of events
   */
  @Override
  public List<EventCFModel> getAllEvents(
      @NonNull final ResourceResolver resourceResolver, @NonNull final String locale) {

    final var eventsCFPath = MessageFormat.format(saudiTourismConfigs.getEventsCFPath(), locale);

    final var eventsCFRoot = resourceResolver.getResource(eventsCFPath);
    if (Objects.isNull(eventsCFRoot)) {
      LOGGER.error("EventsCF Root node not found under %s", eventsCFPath);
      return Collections.emptyList();
    }

    final var query = queryBuilder.createQuery(
            PredicateGroup.create(buildAllEventsQueryMap(eventsCFPath)),
            resourceResolver.adaptTo(Session.class));

    final var searchResult = query.getResult();

    final var resultResources = searchResult.getResources();
    if (!resultResources.hasNext()) {
      return Collections.emptyList();
    }

    return Stream.generate(() -> null)
        .takeWhile(x -> resultResources.hasNext())
        .map(r -> resultResources.next())
        .filter(Objects::nonNull)
        .map(r -> r.adaptTo(EventCFModel.class))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  private Map buildAllEventsQueryMap(@NonNull final String eventsCFPath) {
    final var map = new HashMap<>();
    map.put(Constants.PATH_PROPERTY, eventsCFPath);

    map.put(Constants.PREDICATE_TYPE, "dam:Asset");
    map.put("orderby", "@jcr:content/data/master/startDate");
    map.put("orderby.sort", "asc");

    map.put("1_group.1_group.p.or", "true");
    map.put("1_group.1_group.1_group.1_relativedaterange.property", "jcr:content/data/master/endDate");
    map.put("1_group.1_group.1_group.1_relativedaterange.lowerBound", "-1d");
    map.put("1_group.1_group.1_group.2_relativedaterange.property", "jcr:content/data/master/startDate");
    map.put("1_group.1_group.1_group.2_relativedaterange.upperBound", "90d");
    map.put("1_group.1_group.2_group.1_property", "jcr:content/data/master/endDate");
    map.put("1_group.1_group.2_group.1_property.operation", "not");
    map.put("1_group.1_group.2_group.2_relativedaterange.property", "jcr:content/data/master/startDate");
    map.put("1_group.1_group.2_group.2_relativedaterange.lowerBound", "-1d");

    map.put("p.limit", "-1");
    return map;
  }
}
