package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.mobile.components.atoms.SubTab;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.HeaderResponseModel;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionResponseModel;
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
import java.util.List;
import java.util.Objects;

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
public class ItemsDetailsResponseModel {
  /**
   * Id.
   */
  @Setter
  private String id;

  /**
   * is_dmc.
   */
  @Setter
  private String isDmc;

  /**
   * package_url.
   */
  @Setter
  private String packageUrl;

  /**
   * item type [manual, auto].
   */
  @Setter
  private String type;

  /**
   * Header.
   */
  @ChildResource
  @Setter
  private HeaderResponseModel header;

  /**
   * Footer.
   */
  @ChildResource
  @Setter
  private FooterResponseModel footer;

  /**
   * Subtabs.
   */
  @ChildResource
  @Setter
  private List<SubTab> subtabs;

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
   * Sectionner Banner.
   */
  @Setter
  private SectionResponseModel sectionBanner;

  /**
   * Sections Banner Paths.
   */
  @ValueMapValue
  @JsonIgnore
  private String sectionBannerPath;

  /**
   * Sections.
   */
  @Setter
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
  private ResourceResolver resourceResolver;


  /**
   * init method.
   */
  @PostConstruct
  public void init() {

    if (Objects.nonNull(header) && Objects.nonNull(header.getTitles()) && StringUtils.isNotBlank(this.title)) {
      header.getTitles().setTitle(this.title);
    }
    isItemWebAuto = StringUtils.isNotBlank(webpath);
  }


}
