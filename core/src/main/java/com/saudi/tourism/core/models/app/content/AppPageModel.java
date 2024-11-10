package com.saudi.tourism.core.models.app.content;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.common.AppBaseModel;
import com.saudi.tourism.core.models.app.page.ComponentInfo;
import com.saudi.tourism.core.models.app.page.Search;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * App page model.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.APP_CONTENT_RESOURCE_TYPE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})
@ToString
public class AppPageModel extends AppBaseModel {

  /**
   * Main title.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Named(com.day.cq.commons.jcr.JcrConstants.JCR_TITLE)
  private String title;


  /**
   * Main page description.
   */
  @Getter
  @Setter
  @ValueMapValue
  @Named(JcrConstants.JCR_DESCRIPTION)
  private String description;

  /**
   * Tags.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String[] tags;

  /**
   * Featured Image.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String featuredImage;

  /**
   * Featured.
   */
  @Getter
  @Setter
  @ValueMapValue
  private boolean featured;

  /**
   * Preview Image.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String previewImage;

  /**
   * Tabs data.
   */
  @Getter
  @Setter
  @ChildResource
  private List<TabModel> tabs;

  /**
   * Publish Date.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String publishDate;

  /**
   * Related object.
   */
  @Getter
  @Setter
  @ChildResource
  private RelatedModel related;

  /**
   * Components in parsys.
   */
  @Getter
  @Setter
  private List<ComponentInfo> components;

  /**
   * Search Tags.
   */
  @Getter
  @Setter
  @ChildResource
  private Search search;
  /**
   * Type.
   */
  @ValueMapValue
  @Getter
  @Setter
  private String type;

  /**
   * Constructor.
   * @param currentResource current resource
   */
  @Inject
  public AppPageModel(
      @SlingObject @Named("currentResource") Resource currentResource
  ) {
    this.components = Optional.ofNullable(currentResource.getParent())
        .map(Resource::getPath)
        .map(path -> AppUtils.getComponentInfo(currentResource.getResourceResolver(), path))
        .orElse(Collections.emptyList());
  }

  /**
   * ResourceResolver.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    setWebMappingPath(LinkUtils.getFavoritePath(getWebMappingPath()));
  }

}
