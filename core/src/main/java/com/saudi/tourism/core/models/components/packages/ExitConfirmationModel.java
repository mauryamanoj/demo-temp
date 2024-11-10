package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

/**
 * The type Lead form success error.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ExitConfirmationModel extends TitleSubtitle {

  /**
   * The Confirm button copy.
   */
  @Expose
  @ChildResource
  private String confirmCopy;
  /**
   * The Deny button copy.
   */
  @Expose
  @ChildResource
  private String denyCopy;
}
