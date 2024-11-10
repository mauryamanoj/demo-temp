package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Objects;

/**
 * Section model.
 */
@Exporter(selector = "search", name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION)
@Getter
@Setter
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
    cache = true,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
    resourceType = {"sauditourism/components/structure/mobile-section-page",
                    "sauditourism/components/structure/mobile-specific-section-page"})
public class SectionsModel {

    /**
   * Header.
   */
  @ChildResource
  private HeaderResponseModel header;


  /**
   * Section.
   */
  @ChildResource
  private SectionResponseModel section;


  /**
   * Title of the tab.
   */
  @ValueMapValue(name = JcrConstants.JCR_TITLE)
  @JsonIgnore
  private transient String title;

  /**
   * The constant for mobile section page resourceType.
   */
  private static final String MOBILE_ITEM_PAGE_RESOURCE_TYPE =
      "sauditourism/components/structure/mobile-item-page";

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

  }
}
