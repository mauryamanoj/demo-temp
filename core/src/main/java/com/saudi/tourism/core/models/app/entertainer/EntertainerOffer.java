package com.saudi.tourism.core.models.app.entertainer;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * This model is used to get Entertainer offer.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class EntertainerOffer implements Serializable {
  /**
   * The title of the entertainer offer.
   */
  @ValueMapValue
  private String title;
  /**
   * The description of the entertainer offer.
   */
  @ValueMapValue
  private String description;

  /**
   * The image of the entertainer offer.
   */
  @ValueMapValue
  private String image;

}
