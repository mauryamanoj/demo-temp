package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.mobile.components.autosections.adapters.items.ItemResponseModelAdapterFactory;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.HeaderResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionsModel;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Item model.
 */
@Exporter(selector = "search", name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = "sauditourism/components/structure/mobile-item-page")
public class ItemsModel {

  /**
   * Header.
   */
  @ChildResource
  private HeaderResponseModel header;

  /**
   * Item.
   */
  @ChildResource
  @JsonIgnore
  private ItemResponseModel item;

  /**
   * Web Path.
   */
  @ValueMapValue
  @JsonIgnore
  private String webpath;


  /**
   * if is web Path.
   */
  @JsonIgnore
  private Boolean isItemWebAuto;

  /**
   * Footer.
   */
  @ChildResource
  private FooterResponseModel footer;

  /**
   * Sections paths.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> sectionPaths;

  /**
   * Sections.
   */
  private List<SectionResponseModel> sections;

  /**
   * Title of the tab.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  @JsonIgnore
  private transient String title;

  /**
   * The constant for mobile section page resourceType.
   */
  private static final String MOBILE_SECTION_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-section-page";

  /**
   * The constant for mobile section page resourceType.
   */
  private static final String MOBILE_SPECIFIC_SECTION_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-specific-section-page";

  /**
   * Resource Resolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * currentResource.
   */
  @SlingObject
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * ItemModelAdapterFactory.
   */
  @Inject
  private ItemResponseModelAdapterFactory itemResponseModelAdapterFactory;

  /**
   * init method.
   */
  @PostConstruct
  public void init() {

    if (Objects.nonNull(header) && Objects.nonNull(header.getTitles()) && StringUtils.isNotBlank(this.title)) {
      header.getTitles().setTitle(this.title);
    }
    handleAutoItem();

    if (CollectionUtils.isNotEmpty(sectionPaths)) {
      sections = sectionPaths.stream()
          .filter(StringUtils::isNotEmpty)
          .map(resourceResolver::getResource)
          .filter(Objects::nonNull)
          .map(r -> r.getChild(JcrConstants.JCR_CONTENT))
          .filter(Objects::nonNull)
          .filter(r -> r.isResourceType(MOBILE_SECTION_PAGE_RESOURCE_TYPE)
              || r.isResourceType(MOBILE_SPECIFIC_SECTION_PAGE_RESOURCE_TYPE))
          .map(r -> r.adaptTo(SectionsModel.class))
          .filter(Objects::nonNull)
          .map(r -> r.getSection())
          .filter(Objects::nonNull)
          .collect(Collectors.toList());

    }
  }

  private void handleAutoItem() {
    isItemWebAuto = StringUtils.isNotBlank(webpath);
    if (!isItemWebAuto) {
      return;
    }

    Resource webPageResource = resourceResolver.getResource(webpath + Constants.SLASH_JCR_CONTENT);
    Optional.ofNullable(webPageResource)
        .map(res -> res.getValueMap().get("referencedFragmentPath", String.class))
        .map(resourceResolver::getResource)
        .ifPresent(this::adaptToItemResponseModel);
  }

  private void adaptToItemResponseModel(Resource cfResource) {
    Optional.ofNullable(itemResponseModelAdapterFactory.getAdapter(cfResource, ItemResponseModel.class))
        .ifPresent(adaptedItem -> this.item = adaptedItem);
  }


}
