package com.saudi.tourism.core.models.components.loyalty.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.login.servlets.SSIDLoginUrlServlet;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * LoginModal Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class LoginModalButtonModel {
  /**
   * Copy text.
   */
  @ValueMapValue
  @Expose
  private String copy;

  /**
   * button Link.
   */
  @Expose
  private String link = SSIDLoginUrlServlet.SERVLET_PATH;
}
