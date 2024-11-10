package com.saudi.tourism.core.models.app.eventpackage;

import com.saudi.tourism.core.models.common.ImageCaption;
import lombok.Getter;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Package event model.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class PackageItemModel implements Serializable {
  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Part of the day.
   */
  @ValueMapValue
  private String dayPart;

  /**
   * Category tag.
   */
  @ValueMapValue
  private String category;

  /**
   * Featured Image.
   */
  @ValueMapValue
  private String featuredImage;

  /**
   * Featured Image Caption.
   */
  @ChildResource
  private ImageCaption featuredImageCaption;

  /**
   * Preview Image.
   */
  @ValueMapValue
  private String previewImage;

  /**
   * Preview Image Caption.
   */
  @ChildResource
  private ImageCaption previewImageCaption;

  /**
   * Short description.
   */
  @ValueMapValue
  private String shortDescription;

  /**
   * Long description.
   */
  @ValueMapValue
  private String description;
}
