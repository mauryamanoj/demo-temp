package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.services.destinations.v1.DestinationsCFService;
import com.saudi.tourism.core.services.seasons.v1.SeasonsCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Filter {

  /**
   * type.
   */
  @ValueMapValue
  private String typeId;

  /**
   * itemType.
   */
  @ValueMapValue
  @JsonIgnore
  private List<String> itemType;

  /**
   * date.
   */
  @ChildResource
  private Date date;

  /**
   * url.
   */
  @ValueMapValue
  private String url;

  /**
   * Category tags.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] categoryTags;

  /**
   * Categories Ids.
   */
  @JsonIgnore
  private transient List<String> categoriesIds;

  /**
   * destinationCFPath.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] destinationCFPath;

  /**
   * Destinations Ids.
   */
  @JsonIgnore
  private transient List<String> destinationsIds;

  /**
   * seasonCFPath.
   */
  @ValueMapValue
  @JsonIgnore
  private String[] seasonCFPath;

  /**
   * Seasons Ids.
   */
  @JsonIgnore
  private transient List<String> seasonsIds;

  /**
   * POI Tags.
   */
  private transient List<String> poiTypes;

  /**
   * POI Tags.
   */
  @JsonIgnore
  @ValueMapValue
  private transient String[] poiTypesTags;

  /**
   * categories.
   */
  private List<CategoryFilterItem> categories = new ArrayList<>();

  /**
   * Tag Manager.
   */
  @Inject
  @JsonIgnore
  private transient TagManager tagManager;

  /**
   * Resource Resolver.
   */
  @Inject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;
  /**
   * language.
   */
  @JsonIgnore
  private String language = Constants.DEFAULT_LOCALE;


  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * Destination Service.
   */
  @JsonIgnore
  @OSGiService
  private transient DestinationsCFService destinationsCFService;

  /**
   * Seasons Service.
   */
  @JsonIgnore
  @OSGiService
  private transient SeasonsCFService seasonsCFService;


  /**
   * init method.
   */
  @PostConstruct
  private void init() {

    final ResourceResolver resourceResolver = resource.getResourceResolver();
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    if (pageManager == null) {
      return;
    }
    Page currentPage = pageManager.getContainingPage(resource);


    language = CommonUtils.getLanguageForPath(currentPage.getPath());

    processCategoriesTags();
    processItemDestination();
    processItemSeasons();
    processPoiTags();
  }

  /**
   * This method aims to prepare item categories tags.
   */
  private void processCategoriesTags() {

    List<Category> categoriesItems = null;
    if (ArrayUtils.isNotEmpty(categoryTags)) {
      categoriesItems =
          Arrays.stream(categoryTags)
              .flatMap(p -> TagUtils.getTagOrChildren(p, tagManager, resolver, language).stream())
              .peek(tag -> TagUtils.processTagsForMobile(tag, resolver, settingsService))
              .collect(Collectors.toList());
    }
    if (CollectionUtils.isNotEmpty(categoriesItems)) {
      CategoryFilterItem categoriesItem = CategoryFilterItem.builder().id("categories").items(categoriesItems).build();
      categoriesIds = categoriesItems.stream().map(Category::getId).collect(Collectors.toList());
      categories.add(categoriesItem);
    }

  }

  /**
   * This method aims to prepare item poi tags.
   */
  private void processPoiTags() {
    Locale locale = new Locale(language);
    if (ArrayUtils.isNotEmpty(poiTypesTags)) {
      if (tagManager == null) {
        tagManager = resolver.adaptTo(TagManager.class);
      }
      poiTypes = Arrays.stream(poiTypesTags)
        .map(tag -> CommonUtils.getTagName(tag, tagManager, locale))
        .collect(Collectors.toList());
    }

  }
  /**
   * This method aims to prepare destination object of item location.
   */
  private void processItemDestination() {

    List<Category> destinationsItems = null;

    if (ArrayUtils.isNotEmpty(destinationCFPath)) {
      destinationsItems = Arrays.stream(destinationCFPath).filter(StringUtils::isNotBlank).map(
              LinkUtils::getLastPathSegment)
          .map(destinationId -> destinationsCFService.getDestinationById(language, destinationId)).
          filter(Objects::nonNull)
          .map(destinationCFModel -> new Category(destinationCFModel.getDestinationId(), destinationCFModel.getTitle(),
              ""))
          .collect(Collectors.toList());
    }
    if (CollectionUtils.isNotEmpty(destinationsItems)) {
      CategoryFilterItem destinationsItem =
          CategoryFilterItem.builder().id("destinations").items(destinationsItems).build();
      destinationsIds = destinationsItems.stream().map(Category::getId).collect(Collectors.toList());
      categories.add(destinationsItem);
    }


  }


  /**
   * This method aims to prepare seasons object of item location.
   */
  private void processItemSeasons() {

    List<Category> seasonsItems = null;

    if (ArrayUtils.isNotEmpty(seasonCFPath)) {
      seasonsItems = Arrays.stream(seasonCFPath)
          .filter(StringUtils::isNotBlank)
          .map(LinkUtils::getLastPathSegment)  // Extract last path segment as seasonId
          .map(seasonId -> {
            // Fetch season by ID and pass seasonId to Category constructor
            var seasonCFModel = seasonsCFService.getSeasonById(language, seasonId);
            if (seasonCFModel != null) {
              return new Category(seasonId, seasonCFModel.getTitle(), "");
            }
            return null;
          })
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }

    if (CollectionUtils.isNotEmpty(seasonsItems)) {
      CategoryFilterItem seasonsItem =
          CategoryFilterItem.builder().id("seasons").items(seasonsItems).build();
      seasonsIds = seasonsItems.stream().map(Category::getId).collect(Collectors.toList());
      categories.add(seasonsItem);
    }


  }

}
