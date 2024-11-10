package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
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
import java.util.stream.Collectors;

/**
 * Contact information model for the App Cruise details.
 */
@Exporter(selector = "search", name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Setter
@Getter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = "sauditourism/components/structure/mobile-tab-page")
public class TabsModel {

  /**
   * Resource Object.
   */
  @JsonIgnore
  @SlingObject
  private transient Resource resource;

  /**
   * Header.
   */
  @ChildResource
  private HeaderResponseModel header;


  /**
   * Sections paths.
   */
  @ValueMapValue
  @JsonIgnore
  private transient List<String> sectionPaths;


  /**
   * Title of the tab.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  @JsonIgnore
  private transient String title;

  /**
   * Boolean to indicate that no destination was found.
   */
  @JsonIgnore
  private transient Boolean noneDestinationFound = false;

  /**
   * Sections.
   */
  private  List<SectionResponseModel> sections;

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
  private ResourceResolver resourceResolver;


  /**
   * init method.
   */
  @PostConstruct
  public void init() {

    if (Objects.nonNull(header) && Objects.nonNull(header.getTitles()) && StringUtils.isNotBlank(this.title)) {
      header.getTitles().setTitle(this.title);
    }

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
}
