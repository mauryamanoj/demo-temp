package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.CardImage;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The Article detail model.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class ArticleDetail {

  /**
   * The title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * The description.
   */
  @Expose
  @ValueMapValue
  private String description;

  /**
   * The image.
   */
  @Expose
  @ChildResource
  private CardImage image;

}
