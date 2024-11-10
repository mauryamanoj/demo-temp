package com.saudi.tourism.core.models.common;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Model class of card image related data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class CardImage implements Serializable {

  /**
   * Variable to store image fileReference.
   */
  @ValueMapValue
  @Expose
  private String fileReference;


  /**
   * Variable to store image alternative text (accessibility).
   */
  @ValueMapValue
  @Expose
  private String alt;

  /**
   * Variable to store s7 image fileReference.
   */
  @ValueMapValue(name = "s7fileReference")
  @Expose
  private String s7fileReference;

  /**
   * Copy Image instrance.
   * @return copy of instance
   */
  public Image copy() {
    Image image = new Image();
    image.setFileReference(fileReference);
    image.setAlt(alt);
    image.setS7fileReference(s7fileReference);
    return image;
  }

}
