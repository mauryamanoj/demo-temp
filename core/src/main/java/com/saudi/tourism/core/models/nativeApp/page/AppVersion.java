package com.saudi.tourism.core.models.nativeApp.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

 /**
 * AppVersion Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AppVersion {

  /**
   * label .
   */
  @ValueMapValue
  @Expose
  private String label;

  /**
   * value .
   */
  @ValueMapValue
  @Expose
  private String value;

  /**
   * message . .
   */
  @ValueMapValue
  @Expose
  private String message;

  /**
   * forceUpdateEnabled .
   */
  @ValueMapValue
  @Expose
  private Boolean forceUpdateEnabled;
}
