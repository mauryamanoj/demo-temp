package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Fly Deal Model .
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class FlyDeal implements Serializable {

  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * SubTitle.
   */
  @ValueMapValue
  @Expose
  private String image;

  /**
   * description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * ctatitle.
   */
  @ValueMapValue
  @Expose
  private String ctatitle;

  /**
   * ctaUrl.
   */
  @ValueMapValue
  @Expose
  private String ctaurl;
}
