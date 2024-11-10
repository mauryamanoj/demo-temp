package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Section.
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Section implements Serializable {
  /**
   * sectionTitle to give title of the component.
   */
  @Expose
  @ValueMapValue
  private String sectionTitle;

  /**
   * Order variable to give the number.
   */
  @Expose
  @ValueMapValue
  private Integer order;
  /**
   * enabled button to true/false.
   */

  @Expose
  @ValueMapValue
  private Boolean enabled;

  /**
   * apiUrl endpoint.
   */
  @Expose
  @ValueMapValue
  private String apiUrl;

  /**
   * sectionKey defines the Key of the section.
   */
  @Expose
  @ValueMapValue
  private String sectionKey;

}
