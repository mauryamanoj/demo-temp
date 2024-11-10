package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * NativeAppEVisaModel model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class NativeAppEVisaModel implements Serializable {
  /**
   * title.
   */
  @Expose
  @ValueMapValue
  private String title;
  /**
   * description.
   */
  @ValueMapValue
  @Expose
  private String description;
  /**
   * image.
   */
  @Expose
  @ValueMapValue
  private String image;
  /**
   * ctaTExt.
   */
  @Expose
  @ValueMapValue
  private String ctaText;

  /**
   * linkType .
   */
  @Expose
  @ValueMapValue
  private String linkType;

  /**
   * link.
   */
  @Expose
  @ValueMapValue
  private String link;

  /**
   * Id.
   */
  @Expose
  @ValueMapValue
  private String id;

}
