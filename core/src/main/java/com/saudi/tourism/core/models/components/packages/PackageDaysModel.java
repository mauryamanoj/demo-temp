package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * The Class PackageDetail.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PackageDaysModel implements Serializable {

  /**
   * The title.
   */
  @ValueMapValue
  @Getter
  @Expose
  private String title;

  /**
   * The Image Sharpen for all images.
   */
  @ValueMapValue
  @Getter
  @Expose
  private boolean enableImageSharpenAll;

  /**
   * List of links.
   */
  @ChildResource
  @Getter
  @Expose
  private List<PackageDayModel> days;

}
