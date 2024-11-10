package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * HomeSectionsModel for SEE& DO Order .
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class HomeSections {

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
