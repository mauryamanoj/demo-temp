package com.saudi.tourism.core.services.mobile.v1.impl;

import com.day.crx.JcrConstants;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.saudi.tourism.core.beans.bestexperience.Experience;
import com.saudi.tourism.core.beans.bestexperience.ExperienceDetails;
import com.saudi.tourism.core.models.mobile.components.atoms.Period;
import com.saudi.tourism.core.models.mobile.components.atoms.SubTab;
import com.saudi.tourism.core.models.mobile.components.atoms.AutoItemFilter;
import com.saudi.tourism.core.models.mobile.components.autosections.adapters.pages.ItemsDetailsResponseModelAdapterFactory;
import com.saudi.tourism.core.models.mobile.components.autosections.utils.ExperienceUtils;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionsModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.TabsModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsDetailsResponseModel;
import com.saudi.tourism.core.services.destinations.v1.DestinationsCFService;
import com.saudi.tourism.core.services.events.v1.EventsCFService;
import com.saudi.tourism.core.services.mobile.v1.MobileLayoutService;
import com.saudi.tourism.core.services.mobile.v1.items.adapters.AutoItemsAdapterFactory;
import com.saudi.tourism.core.services.mobile.v1.items.comparators.ItemComparatorsChain;
import com.saudi.tourism.core.services.mobile.v1.items.filters.ItemFiltersChain;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Objects;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * The MobileLayout Service implementation.
 */
@Component(service = MobileLayoutService.class, immediate = true)
@Slf4j
public class MobileLayoutServiceImpl implements MobileLayoutService {

  /**
   * User Service.
   */
  @Reference
  private UserService userService;

  /**
   * Item FiltersChain.
   */
  @Reference
  private ItemFiltersChain filtersChain;

  /**
   * Saudi Tourism Config.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Destinations CF service.
   */
  @Reference
  private transient DestinationsCFService destinationsCFService;

  /**
   * Events CF service.
   */
  @Reference
  private transient EventsCFService eventsCFService;

  /**
   * Experience service.
   */
  @Reference
  private transient ExperienceService experienceService;

  /**
   * Sling Settings service.
   */
  @Reference
  private transient SlingSettingsService settingsService;

  /**
   * Item Adapter Factory.
   */
  @Reference
  private transient AutoItemsAdapterFactory itemAdapterFactory;

  /**
   * Item comparators chain.
   */
  @Reference
  private transient ItemComparatorsChain itemComparatorsChain;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * Default offset.
   */
  public static final Integer DEFAULT_OFFSET = 0;

  /**
   * ITEMS LIMIT.
   */
  private static final int ITEMS_LIMIT = 5;

  /**
   * The constant for tabs path.
   */
  private static final String TABS_PATH = "tabs";

  /**
   * The constant for tabs path.
   */
  private static final String ITEMS_PATH = "items";

  /**
   * The constant for mobile tab page resourceType.
   */
  private static final String MOBILE_TAB_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-tab-page";

  /**
   * The constant for mobile section page resourceType.
   */
  private static final String MOBILE_SECTION_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-section-page";

  /**
   * The constant for mobile specific section page resourceType.
   */
  private static final String MOBILE_SPECIFIC_SECTION_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-specific-section-page";

  /**
   * The constant for mobile section page resourceType.
   */
  private static final String MOBILE_ITEM_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-item-page";

  /**
   * ItemModelAdapterFactory.
   */
  @Reference
  private ItemsDetailsResponseModelAdapterFactory itemsDetailsResponseModelAdapterFactory;

  @Override
  public TabsModel getTabById(@NonNull MobileRequestParams request) {
    final ItemDestinationComparator byDestinationMatch;
    if (CollectionUtils.isNotEmpty(request.getDestinations())) {
      byDestinationMatch = new ItemDestinationComparator(request.getDestinations());
    } else {
      byDestinationMatch = null;
    }
    // Checking first if Destination exists
    final var listDestinations = destinationsCFService.fetchAllDestination(request.getLocale());
    final var listDestinationsIds =
        listDestinations.stream()
        .filter(Objects::nonNull)
        .filter(destination -> Objects.nonNull(destination.getResource()))
        .map(
          destination ->
            StringUtils.substringAfterLast(destination.getResource().getPath(), "/"))
        .collect(Collectors.toList());

    var noneContained = false;

    if (CollectionUtils.isNotEmpty(request.getDestinations())) {

      noneContained =
        request.getDestinations().stream()
          .filter(Objects::nonNull)
          .noneMatch(listDestinationsIds::contains);

      if (noneContained) {
        request.setDestinations(Collections.emptyList());
      }
    }

    String tabSearchPath =
        Constants.MOBILE_APP_ROOT
        + Constants.FORWARD_SLASH_CHARACTER
        + request.getLocale()
        + Constants.FORWARD_SLASH_CHARACTER
        + TABS_PATH;

    try (ResourceResolver resolver = userService.getResourceResolver()) {
      Resource resource =
          resolver.getResource(
          tabSearchPath
            + Constants.FORWARD_SLASH_CHARACTER
            + request.getTabId()
            + Constants.FORWARD_SLASH_CHARACTER
            + JcrConstants.JCR_CONTENT);

      TabsModel model = null;
      if (resource != null && resource.isResourceType(MOBILE_TAB_PAGE_RESOURCE_TYPE)) {
        model = resource.adaptTo(TabsModel.class);
      }

      if (model != null) {
        List<SectionResponseModel> sections = model.getSections();
        if (CollectionUtils.isNotEmpty(sections)) {
          sections.forEach(
              section -> {
              boolean isSectionExcluded =
                  request.getExcludeSectionsId() != null
                  && request.getExcludeSectionsId().contains(section.getId());

              if (!isSectionExcluded) {
                List<ItemResponseModel> items;
                if (isSectionAuto(section.getAuto())) {
                  items = getAutomaticItems(section, resolver, request);
                  section.setItems(items);
                } else {
                  items = section.getItems();
                }
                if (CollectionUtils.isNotEmpty(items)) {
                  if (StringUtils.isNotBlank(request.getFilters())) {
                    var filteredItems =
                        items.stream()
                            .filter(Objects::nonNull)
                            .filter(item -> filtersChain.doFilter(request, item))
                            .collect(Collectors.toList());
                    section.setItems(filteredItems);
                  }
                } else {
                  section.setItems(Collections.emptyList());
                }

              }

              Stream<ItemResponseModel> itemsStream = Optional.ofNullable(section.getItems())
                  .map(Collection::stream)
                  .orElseGet(Stream::empty);

              // Apply sorting only if request.getDestinations() is not null and not empty
              if (byDestinationMatch != null) {
                itemsStream = itemsStream.sorted(byDestinationMatch);
              }

              List<ItemResponseModel> limitedItems = itemsStream.limit(ITEMS_LIMIT).collect(Collectors.toList());
              section.setTotalCount(Optional.ofNullable(section.getItems()).map(List::size).orElse(0));
              section.setItems(limitedItems);
            });
        }

        List<SectionResponseModel> filteredSections =
            sections.stream()
            .filter(section -> CollectionUtils.isNotEmpty(section.getItems()))
            .collect(Collectors.toList());

        model.setSections(filteredSections);
      }

      if (noneContained && model != null) {
        model.setNoneDestinationFound(noneContained);
      }
      return model;
    }
  }


  @Override
  public SectionsModel getSectionById(@NonNull final MobileRequestParams request) {
    try (ResourceResolver resolver = userService.getResourceResolver()) {
      final var sectionPath =
          MessageFormat.format(
          saudiTourismConfigs.getMobileSectionBasePath(),
          request.getLocale(),
          request.getSectionId());
      Resource resource = resolver.getResource(sectionPath);

      SectionsModel model = null;
      if (resource != null
          && (resource.isResourceType(MOBILE_SECTION_PAGE_RESOURCE_TYPE)
          || resource.isResourceType(MOBILE_SPECIFIC_SECTION_PAGE_RESOURCE_TYPE))) {
        model = resource.adaptTo(SectionsModel.class);
      }

      if (Objects.nonNull(model)) {
        List<ItemResponseModel> items = model.getSection().getItems();
        var autoItemFilter = model.getSection().getAuto();

        if (isSectionAuto(autoItemFilter)) {
          items = getAutomaticItems(model, resolver, request);
          model.getSection().setItems(items);
        }

        if (CollectionUtils.isNotEmpty(items)) {
          if (StringUtils.isNotBlank(request.getFilters())) {
            var filteredItems =
                items.stream()
                  .filter(Objects::nonNull)
                  .filter(item -> filtersChain.doFilter(request, item))
                  .collect(Collectors.toList());

            model.getSection().setItems(filteredItems);
          }
        }
      }

      var limit = request.getLimit();
      var offset = request.getOffset();

      if (model != null) {
        var items = model.getSection().getItems();

        if (CollectionUtils.isNotEmpty(items)) {
          List<ItemResponseModel> itemsPaginated =
              items.stream().skip(offset).limit(limit).collect(Collectors.toList());

          model.getSection().setItems(itemsPaginated);
          model.getSection().setTotalCount(items.size());
        }
      }

      return model;
    }
  }

  private boolean isSectionAuto(AutoItemFilter autoItemFilter) {
    return Objects.nonNull(autoItemFilter)
        && Objects.nonNull(autoItemFilter.getFilter())
        && CollectionUtils.isNotEmpty(autoItemFilter.getFilter().getItemType());
  }

  /**
   * This method gets all the items from CF and then filter, sort and return them.
   *
   * @param model    section model
   * @param resolver resource resolver
   * @param request  request params
   * @return list of ItemResponseModel
   */
  private List<ItemResponseModel> getAutomaticItems(
      SectionsModel model, ResourceResolver resolver, MobileRequestParams request) {
    return getItemModels(model.getSection(), request, resolver);
  }

  private List<ItemResponseModel> getAutomaticItems(
      SectionResponseModel section, ResourceResolver resolver, MobileRequestParams request) {
    return getItemModels(section, request, resolver);
  }

  @NotNull
  private List<ItemResponseModel> getItemModels(
      SectionResponseModel section, MobileRequestParams request, ResourceResolver resolver) {
    var autoItemFilter = section.getAuto();
    MobileRequestParams requestFilter = fillMobileRequest(autoItemFilter, request);
    List<ItemResponseModel> items =
        itemAdapterFactory.getItems(
            resolver,
            settingsService,
            requestFilter,
            autoItemFilter.getFilter().getItemType());

    return items.stream()
        .filter(Objects::nonNull)
        .filter(item -> filtersChain.doFilter(requestFilter, item))
        .sorted(itemComparatorsChain.buildComparator(requestFilter))
        .collect(Collectors.toList());
  }

  private MobileRequestParams fillMobileRequest(AutoItemFilter autoFilter, MobileRequestParams request) {
    List<String> requestDestinations = null;
    List<String> requestSetSeasons = null;
    String requestStartDate = null;
    String requestEndDate = null;
    List<String> requestSortBy = null;
    List<String> requestCategories = null;
    List<String> requestPoiType = null;
    String experienceDestination = null;
    Boolean freeOnly = null;
    String experienceStartDate = null;
    String experienceEndDate = null;

    if (Objects.nonNull(autoFilter.getFilter())) {
      requestDestinations = autoFilter.getFilter().getDestinationsIds();
      requestSetSeasons = autoFilter.getFilter().getSeasonsIds();
      requestCategories = autoFilter.getFilter().getCategoriesIds();
      requestPoiType = autoFilter.getFilter().getPoiTypes();

      if (Objects.nonNull(autoFilter.getFilter().getDate())) {
        requestStartDate =
            CommonUtils.dateToString(
                autoFilter.getFilter().getDate().getStartDate(), Constants.FORMAT_DATE);
        requestEndDate =
            CommonUtils.dateToString(
                autoFilter.getFilter().getDate().getEndDate(), Constants.FORMAT_DATE);
      }
      if (Objects.nonNull(autoFilter.getSortBy())) {
        requestSortBy = autoFilter.getSortBy().getSortBy();
      }

      Period period = autoFilter.getPeriod();
      if (Objects.nonNull(period) && StringUtils.isNotBlank(period.getPeriod())) {
        requestStartDate = period.getStartDate();
        requestEndDate = period.getEndDate();
      }

      if (Objects.nonNull(autoFilter.getExperience())) {
        if (Objects.nonNull(autoFilter.getExperience().getFilter())
            && Objects.nonNull(autoFilter.getExperience().getFilter().getDate())) {
          experienceStartDate =
              CommonUtils.dateToString(
                  autoFilter.getExperience().getFilter().getDate().getStartDate(),
                  Constants.FORMAT_DATE);
          experienceEndDate =
              CommonUtils.dateToString(
                  autoFilter.getExperience().getFilter().getDate().getEndDate(),
                  Constants.FORMAT_DATE);
        }
        experienceDestination = autoFilter.getExperience().getDestination();
        freeOnly = autoFilter.getExperience().getFreeOnly();
      }

      if (CollectionUtils.isNotEmpty(request.getDestinations())) {
        experienceDestination = Optional.ofNullable(request.getDestinations())
            .map(destinations -> destinations.stream()
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(",")))
            .orElse("");
      }
    }

    FetchThingsToDoRequest thingsToDoParams =
        FetchThingsToDoRequest.builder()
            .locale(request.getLocale())
            .startDate(experienceStartDate)
            .endDate(experienceEndDate)
            .freeOnly(freeOnly)
            .destination(experienceDestination)
            .offset(DEFAULT_OFFSET)
            .limit(-1)
            .build();

    return MobileRequestParams.builder()
      .locale(request.getLocale())
      .poiTypes(requestPoiType)
      .destinations(requestDestinations).seasons(requestSetSeasons).categories(requestCategories)
      .startDate(requestStartDate).endDate(requestEndDate).sortBy(requestSortBy)
      .thingsToDoRequest(thingsToDoParams)
      .discounted(request.getDiscounted())
      .minPrice(request.getMinPrice())
      .maxPrice(request.getMaxPrice())
      .build();
  }

  @Override
  public ItemsDetailsResponseModel getItemById(@NonNull MobileRequestParams request) {
    if (StringUtils.isBlank(request.getLocale()) || StringUtils.isBlank(request.getItemId())) {
      return null;
    }
    ItemsDetailsResponseModel model = null;

    if (StringUtils.isNotBlank(request.getType()) && request.getType().equals("auto")) {
      try (ResourceResolver resolver = userService.getResourceResolver()) {
        Resource resource = resolver.getResource(request.getItemId() + Constants.SLASH_JCR_CONTENT);
        if (Objects.nonNull(resource)) {
          model = itemsDetailsResponseModelAdapterFactory.getAdapter(resource, ItemsDetailsResponseModel.class);
          model.setId(request.getItemId());
          return model;
        } else {
          return null;
        }
      }
    } else if (StringUtils.isNotBlank(request.getType()) && request.getType().equals("experience")) {
      try (ResourceResolver resolver = userService.getResourceResolver()) {
        return getItemByExperienceId(request, resolver);
      }
    } else {
      final var itemPath =
          MessageFormat.format(
          saudiTourismConfigs.getMobileItemBasePath(),
          request.getLocale(),
          request.getItemId());
      try (ResourceResolver resolver = userService.getResourceResolver()) {
        Resource resource = resolver.getResource(itemPath);
        if (resource != null && resource.isResourceType(MOBILE_ITEM_PAGE_RESOURCE_TYPE)) {
          model = resource.adaptTo(ItemsDetailsResponseModel.class);
        }
        if (Objects.isNull(model)) {
          return null;
        }
        // Handling auto case
        if (model.getIsItemWebAuto()) {
          try (ResourceResolver userServiceResourceResolver = userService.getResourceResolver()) {
            Resource webPageResource =
                userServiceResourceResolver.getResource(model.getWebpath() + Constants.SLASH_JCR_CONTENT);
            var webPageModel =
                itemsDetailsResponseModelAdapterFactory.getAdapter(webPageResource, ItemsDetailsResponseModel.class);
            if (Objects.nonNull(webPageModel)) {
              return webPageModel;
            }
          }
        }

        fillModelWithSectionBanner(model, request);
        getSectionsFromSubTab(model, request);

        if (model.getSubtabs() == null || model.getSubtabs().size() <= 1) {
          model.setSubtabs(null);
        }
        if (CollectionUtils.isNotEmpty(model.getSections())) {
          model
              .getSections()
              .forEach(
                section -> {
                  List<ItemResponseModel> items = section.getItems();
                  section.setTotalCount(Optional.ofNullable(items).map(List::size).orElse(0));
                });
        }
        return model;
      }
    }
  }

  private ItemsDetailsResponseModel getItemByExperienceId(
      @NonNull MobileRequestParams request, ResourceResolver resolver) {
    ItemsDetailsResponseModel item = null;
    List<ItemResponseModel> items = new ArrayList<>();
    Map<String, Object> queryStrings = new HashMap<>();
    Optional.ofNullable(request.getLocale()).ifPresent(locale -> queryStrings.put("lang", locale));
    Experience allExperiences;
    ExperienceDetails experienceDetails;
    try {
      JsonElement halaYJsonResponse =
          (JsonElement) experienceService.getExperienceDetails(queryStrings, request.getItemId());
      if (halaYJsonResponse != null) {
        experienceDetails =
            new Gson().fromJson(halaYJsonResponse.toString(), ExperienceDetails.class);
        if (Objects.nonNull(experienceDetails.getData())) {
          if (StringUtils.isNotBlank(experienceDetails.getData().getCity())) {
            queryStrings.put("city", AppUtils.stringToID(experienceDetails.getData().getCity()));
          }

          JsonElement halaYAllExperiencesJsonResponse =
              (JsonElement) experienceService.getAllExperiences(queryStrings);
          if (halaYAllExperiencesJsonResponse != null) {
            allExperiences = new Gson().fromJson(halaYAllExperiencesJsonResponse.toString(), Experience.class);
            if (Objects.nonNull(allExperiences)
                && CollectionUtils.isNotEmpty(allExperiences.getData())) {
              items =
                  allExperiences.getData().stream()
                      .filter(Objects::nonNull)
                      .filter(e -> !experienceDetails.getData().getId().equals(e.getId()))
                      .map(
                          experience -> {
                            try {
                              return MobileUtils.buildItemFromExperience(
                                  experience,
                                  resolver,
                                  settingsService,
                                  request.getLocale(),
                                  i18nProvider);
                            } catch (ParseException e) {
                              LOGGER.warn("Couldn't parse date into calendar date");
                              return null;
                            }
                          })
                      .limit(ITEMS_LIMIT)
                      .collect(Collectors.toList());
            }
          }
          item =
              ExperienceUtils.convertHalaYExperienceDataToItemDetails(
                  experienceDetails.getData(), i18nProvider, request.getLocale(), items);
        }
      }
    } catch (IOException e) {
      LOGGER.error("Error while getting Experience Details", e);
      return null;
    }

    return item;
  }

  /**
   * This method handle the logic to fill item with section banner.
   *
   * @param model
   * @param request
   */
  private void fillModelWithSectionBanner(
      @NonNull ItemsDetailsResponseModel model, @NonNull MobileRequestParams request) {

    if (StringUtils.isNotBlank((model.getSectionBannerPath()))) {
      var sectionBannerId = MobileUtils.extractSectionId(model.getSectionBannerPath());
      MobileRequestParams mobileRequestParams = new MobileRequestParams();
      mobileRequestParams.setLocale(request.getLocale());
      mobileRequestParams.setSectionId(sectionBannerId);
      SectionsModel sectionModel = this.getSectionById(mobileRequestParams);
      if (sectionModel != null && sectionModel.getSection() != null) {
        model.setSectionBanner(sectionModel.getSection());
      }
    }
  }

  /**
   * This method handle the logic to get sections from sub tabs.
   *
   * @param model
   * @param request
   */
  private void getSectionsFromSubTab(
      @NonNull ItemsDetailsResponseModel model, @NonNull MobileRequestParams request) {

    List<SectionResponseModel> sections;

    if (CollectionUtils.isNotEmpty(model.getSubtabs())) {

      SubTab subTab;

      if (StringUtils.isBlank(request.getSubTabId())) {
        subTab = model.getSubtabs().stream().min(Comparator.comparing(SubTab::getId)).orElse(null);
      } else {
        try {
          int subTabId = Integer.parseInt(request.getSubTabId());
          subTab =
              model.getSubtabs().stream()
                  .filter(t -> t.getId() == subTabId)
                  .findFirst()
                  .orElse(null);
        } catch (NumberFormatException e) {
          subTab = null;
        }
      }

      if (subTab != null && CollectionUtils.isNotEmpty(subTab.getSectionPaths())) {
        sections =
            subTab.getSectionPaths().stream()
                .map(
                    path -> {
                      request.setSectionId(path);
                      SectionsModel sectionModel = this.getSectionById(request);
                      if (sectionModel != null) {
                        return sectionModel.getSection();
                      }
                      return null;
                    })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        model.setSections(sections);
      }
    } else {
      model.setSections(Collections.emptyList());
    }
  }
}
