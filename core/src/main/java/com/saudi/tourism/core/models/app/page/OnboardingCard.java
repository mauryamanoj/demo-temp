package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * OnboardingCard model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class OnboardingCard implements Serializable {

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
}
