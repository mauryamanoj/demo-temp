package com.saudi.tourism.core.models.components.packages;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.io.Serializable;

/**
 * The type Lead form success error.
 */
@Data
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class LeadFormSuccessError implements Serializable {

  /**
   * The Success message.
   */
  @Expose
  @ChildResource
  private TitleSubtitle success;
  /**
   * The Error message.
   */
  @Expose
  @ChildResource
  private TitleSubtitle error;

  /**
   * The Exit Confirmation message.
   */
  @Expose
  @ChildResource
  private ExitConfirmationModel exit;
}
