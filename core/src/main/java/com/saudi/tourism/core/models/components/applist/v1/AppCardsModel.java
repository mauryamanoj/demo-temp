package com.saudi.tourism.core.models.components.applist.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * App Cards Model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class AppCardsModel {

  /**
   * First Line.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Second Line.
   */
  @ValueMapValue
  @Expose
  private String subTitle;

  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image image;
  /**
   * Pill Label.
   */
  @ValueMapValue(name = "pillLabel")
  @Expose
  private String pill;

  /**
   * App Link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * CTA Data.
   */
  @ChildResource
  @Expose
  private CtaData ctaData;

}
