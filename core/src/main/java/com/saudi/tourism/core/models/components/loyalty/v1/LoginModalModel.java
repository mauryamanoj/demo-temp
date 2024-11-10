package com.saudi.tourism.core.models.components.loyalty.v1;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * LoginModal Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class LoginModalModel {
  /**
   * The Title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * The Paragraph.
   */
  @ValueMapValue
  @Expose
  private String paragraph;

  /**
   * Login modal button.
   */
  @ChildResource
  @Expose
  private LoginModalButtonModel button;
}
