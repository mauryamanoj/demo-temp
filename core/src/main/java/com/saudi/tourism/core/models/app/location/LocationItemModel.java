package com.saudi.tourism.core.models.app.location;

import com.saudi.tourism.core.models.app.content.RelatedModel;
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
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class LocationItemModel implements Serializable {

  /**
   * Id.
   */
  private String id;

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Heading.
   */
  @ValueMapValue
  private String heading;

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
   * Image caption.
   */
  @ChildResource
  private ImageCaption imageCaption;

  /**
   * Latitude.
   */
  @ValueMapValue
  private String latitude;

  /**
   * Longitude.
   */
  @ValueMapValue
  private String longitude;

  /**
   * Image caption.
   */
  @ChildResource
  private LocationDetailsModel details;

  /**
   * Image caption.
   */
  @ChildResource
  private LocationRecommendationsModel recommendations;

  /**
   * Related object.
   */
  @ChildResource
  private RelatedModel related;

}
