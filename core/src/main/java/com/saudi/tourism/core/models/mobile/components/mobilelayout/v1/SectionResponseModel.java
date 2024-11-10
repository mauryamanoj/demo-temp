package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.mobile.components.atoms.AutoItemFilter;
import com.saudi.tourism.core.models.mobile.components.atoms.Filter;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemsModel;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import com.saudi.tourism.core.utils.TagUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.Setter;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Sling Model for the Section component in AEM.
 */
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class SectionResponseModel {

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService settingsService;

  /** Tag Manager. */
  @Inject
  @JsonIgnore
  private transient TagManager tagManager;

  /** Resource Resolver. */
  @Inject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /** language. */
  @JsonIgnore
  private String language = Constants.DEFAULT_LOCALE;

  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;

  /**
   * Defines the id of the section.
   */
  private String id;

  /**
   * filter Types List.
   */
  @ValueMapValue(name = "filterType")
  @JsonIgnore
  private List<String> filterTypeList;

  /**
   * filter type.
   */
  private String filterType;

  /**
   * favoriteContentType.
   */
  @ValueMapValue
  private String favoriteContentType;

  /**
   * Represents the style configuration of the item component.
   */
  @ChildResource
  private ItemComponentStyle itemComponentStyle;

  /**
   * Represents the view All style configuration of the item component.
   */
  @ChildResource
  private ItemComponentStyle viewAllItemComponentStyle;

  /**
   * Represents the media gallery configuration.
   */
  @ChildResource
  private List<MediaGallery> mediaGallery;

  /**
   * Represents the header configuration of the section.
   */
  @ChildResource
  private SectionHeader sectionHeader;

  /**
   * auto filter.
   */
  @ChildResource
  @JsonIgnore
  private AutoItemFilter auto;


  /**
   * items.
   */
  private List<ItemResponseModel> items;

  /**
   * Items.
   */
  @JsonIgnore
  @ValueMapValue
  private transient List<String> itemPaths;

  /**
   * totalCount.
   */
  private int totalCount;

  /**
   * Inner class representing the item component style configuration.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class ItemComponentStyle {

    /**
     * Defines the UI type of the component.
     */
    @ValueMapValue
    private String componentUIType;

    /**
     * Defines the scroll direction of the component.
     */
    @ValueMapValue
    private String componentScrollDirection;
  }

  /**
   * Inner class representing the section header configuration.
   */
  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class SectionHeader {

    /**
     * Title of the section.
     */
    @ValueMapValue
    private String title;

    /**
     * SubTitle of the section.
     */
    @ValueMapValue
    private String subtitle;

    /**
     * Title for the icon in the section header.
     */
    @ValueMapValue
    private String iconTitle;

    /**
     * Flag to show or hide the 'View All' option.
     */
    @ValueMapValue
    private Boolean showViewAll;

    /**
     * filter.
     */
    @ChildResource
    private Filter filter;

    /**
     * Configuration for the view history section.
     */
    @ChildResource
    private ViewHistory viewHistory;

    /**
     * Categories Tags.
     */
    @ValueMapValue(name = "categoriesTags")
    @JsonIgnore
    private List<String> categories;

    /**
     * List of category tags associated with the section.
     */
    private List<Category> categoriesTags;
  }

  /**
   * Inner class representing the view history configuration.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class ViewHistory {

    /**
     * Flag to show or hide the view history.
     */
    @ValueMapValue
    private Boolean show;

    /**
     * The CTA.
     */
    @ChildResource
    private CTA cta;
  }


  /**
   * Represents the cta.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class CTA {
    /**
     * Url value.
     */
    @ValueMapValue(name = "value")
    private String url;
    /**
     * type.
     */
    @ValueMapValue
    private String type;
  }

  /**
   * The constant for mobile item page resourceType.
   */
  private static final String MOBILE_ITEM_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-item-page";

  /**
   * init method.
   */
  @PostConstruct
  private void init() {
    final var resourceResolver = resource.getResourceResolver();
    PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
    Page currentPage = pageManager.getContainingPage(resource);
    if (Objects.nonNull(currentPage)) {
      id = MobileUtils.extractSectionId(currentPage.getPath());
    }
    if (CollectionUtils.isNotEmpty(itemPaths)) {
      items = itemPaths.stream()
        .filter(StringUtils::isNotEmpty)
        .map(resourceResolver::getResource)
        .filter(Objects::nonNull)
        .map(r -> r.getChild(JcrConstants.JCR_CONTENT))
        .filter(Objects::nonNull)
        .filter(r -> r.isResourceType(MOBILE_ITEM_PAGE_RESOURCE_TYPE))
        .map(r -> r.adaptTo(ItemsModel.class))
        .filter(Objects::nonNull)
        .map(r -> r.getItem())
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
    }

    language = CommonUtils.getLanguageForPath(currentPage.getPath());
    if (StringUtils.isNotBlank(language)) {
      processCategoriesTags();
    }

    processFilterType();
  }

  private void processCategoriesTags() {
    if (CollectionUtils.isNotEmpty(sectionHeader.categories)) {
      sectionHeader.categoriesTags =
          sectionHeader.categories.stream()
              .flatMap(p -> TagUtils.getTagOrChildren(p, tagManager, resolver, language).stream())
              .peek(tag -> TagUtils.processTagsForMobile(tag, resolver, settingsService))
              .collect(Collectors.toList());
    }
  }

  private void processFilterType() {
    if (isSectionAuto()) {
      filterType = Optional.ofNullable(auto.getFilter().getItemType())
          .map(type -> type.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(Constants.COMMA)))
          .orElse("");
    } else {
      filterType = Optional.ofNullable(filterTypeList)
          .map(type -> type.stream().filter(StringUtils::isNotBlank).collect(Collectors.joining(Constants.COMMA)))
          .orElse("");
    }
  }

  private boolean isSectionAuto() {
    return Objects.nonNull(auto)
      && Objects.nonNull(auto.getFilter())
      && CollectionUtils.isNotEmpty(auto.getFilter().getItemType());
  }
}
