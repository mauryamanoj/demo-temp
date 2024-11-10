package com.saudi.tourism.core.models.components.categories;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SubFragment {

  /**
   * Card Title.
   */
  @ValueMapValue
  @Expose
  private String title;


  /**
   * Image.
   */
  @ValueMapValue
  @Expose
  private String image;

  /**
   *  Alt.
   */
  @ValueMapValue
  @Expose
  private String alt;

  /**
   * s7image.
   */
  @ValueMapValue
  @Expose
  private String s7image;

  /**
   *  Icon.
   */
  @ValueMapValue
  @Expose
  private String icon;
}
