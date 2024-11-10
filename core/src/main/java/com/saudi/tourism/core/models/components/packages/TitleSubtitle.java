package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The type Title subtitle.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TitleSubtitle implements Serializable {

  /**
   * The title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * The subtitle.
   */
  @Expose
  @ValueMapValue
  private String subtitle;

  /**
   * The copy.
   */
  @Expose
  @ValueMapValue
  private String copy;

  /**
   * The cta.
   */
  @Expose
  @ChildResource
  private PackageFormCta cta;

  /**
   * Package Form CTA.
   */
  private static class PackageFormCta implements Serializable {

    /**
     * The CTA Copy.
     */
    @Expose
    @ValueMapValue
    private String copy;

    /**
     * The CTA Href.
     */
    @Expose
    @ValueMapValue
    private String href;
  }

}
