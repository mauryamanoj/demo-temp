package com.saudi.tourism.core.models.components.offersanddeals.v1;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CtaData;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * Card Model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Card {


  /**
   * title.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * title2.
   */
  @ValueMapValue
  @Expose
  private String title2;

  /**
   * description2.
   */
  @ValueMapValue
  @Expose
  private String description2;

  /**
   * image.
   */
  @ChildResource
  @Expose
  private Image image;

  /**
   * Link.
   */
  @ChildResource
  @Expose
  private Link link;

  /**
   * Link for Card.
   */
  @ChildResource
  @Expose
  private Link cta;

  /**
   * Card CTA Analytics Data.
   */
  @ChildResource
  @Expose
  private CtaData cardCtaData;

}
