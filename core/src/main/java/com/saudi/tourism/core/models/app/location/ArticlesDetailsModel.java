package com.saudi.tourism.core.models.app.location;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class ArticlesDetailsModel {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Image.
   */
  @ValueMapValue
  private String image;

  /**
   * Description.
   */
  @ValueMapValue
  private String description;

  /**
   * Items for activities.
   */
  @ChildResource
  private ArticlesExtraInfoTipsModel extraInfo;

  /**
   * Article Details Links.
   */
  @ChildResource
  private List<ArticlesLinksModel> links;

}
