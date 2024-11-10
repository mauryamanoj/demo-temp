package com.saudi.tourism.core.models.components.downloadbanner;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Class contains details of applications on Download Banner.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Apps {

  /**
   * Title of application.
   */
  @Expose
  @ValueMapValue
  private String apptitle;

  /**
   * URL of application.
   */
  @Expose
  @ValueMapValue
  private String appurl;

  /**
   * Image of application.
   */
  @Expose
  @ChildResource
  private Image image;

}
