package com.saudi.tourism.core.models.app.location;

import com.adobe.cq.export.json.ExporterConstants;
import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.ImageCaption;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.ExporterOption;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import static com.saudi.tourism.core.models.app.location.AppCity.convertLinkList;
import static com.saudi.tourism.core.models.app.location.AppCity.convertTagsToNames;

/**
 * App region.
 */
@Model(adaptables = Resource.class,
       resourceType = Constants.RT_APP_REGION_PAGE,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Exporter(
    selector = "search",
    name = ExporterConstants.SLING_MODEL_EXPORTER_NAME,
    extensions = ExporterConstants.SLING_MODEL_EXTENSION,
    options = {@ExporterOption(name = "SerializationFeature.WRITE_DATES_AS_TIMESTAMPS",
                               value = "true")})
@Data
public class AppRegion implements Serializable {
  /**
   * Strapline.
   */
  @ValueMapValue
  private String strapline;
  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Identifer.
   */
  @ValueMapValue
  private String identifier;

  /**
   * Id.
   */
  private String id;
  /**
   * Description.
   */
  @ValueMapValue
  private String description;

  /**
   * Image reference.
   */
  @ValueMapValue
  private String image;

  /**
   * mapIcon.
   */
  @ValueMapValue
  private String mapIcon;

  /**
   * Image caption.
   */
  @ChildResource
  private ImageCaption imageCaption;

  /**
   * previewImage.
   */
  @ValueMapValue
  private String previewImage;
  /**
   * videoLink.
   */
  @ValueMapValue
  private String videoLink;
  /**
   * bestFor.
   */
  @ValueMapValue
  @Named("bestForTags")
  private String[] bestFor;
  /**
   * Region details.
   */
  @ChildResource
  private List<AppRegionDetail> details;
  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;
  /**
   * Cities Object.
   */
  @Setter
  private List<AppCity> cities;
  /**
   * This model post construct initialization.
   */
  @PostConstruct
  public void init() {
    convertTagsToNames(bestFor, currentResource);
    id = AppUtils.pathToID(currentResource.getPath());
    PageManager pageManager = currentResource.getResourceResolver().adaptTo(PageManager.class);
    if (Objects.nonNull(details)) {
      for (AppRegionDetail detail: details) {
        if (Objects.nonNull(detail) && Objects.nonNull(detail.getLinks())) {
          convertLinkList(detail.getLinks(), pageManager);
        }
      }
    }
  }

  /**
   * This model is used to handle one region details data.
   */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Data
  public static class AppRegionDetail extends LocationDetailModel {
    /**
     * All locations for this page.
     */
    @ChildResource
    private List<LocationLink> links;
  }
}
