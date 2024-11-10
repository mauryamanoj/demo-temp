package com.saudi.tourism.core.models.app.page;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Link;
import java.util.List;
import lombok.Data;
import lombok.Setter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * Card Model.
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class Card implements Serializable {
  /**
   * Title.
   */
  @Expose
  @ValueMapValue
  private String title;

  /**
   * Description.
   */
  @Expose
  @ValueMapValue
  private String description;

  /**
   * Image path.
   */
  @Expose
  @ValueMapValue
  private String imagePath;

  /**
   * Link type.
   */
  @Expose
  @ValueMapValue
  private String linkType;

  /**
   * Link.
   */
  @Expose
  @ValueMapValue
  private String link;

  /**
   * CTA text.
   */
  @Expose
  @ValueMapValue
  private String ctaText;

  /**
   * Icon path.
   */
  @Expose
  @ValueMapValue
  private String iconPath;

  /**
   * links.
   */
  @ChildResource
  @Setter
  private List<Link> links;

  /**
   * Icon.
   */
  @Expose
  @ValueMapValue
  private String icon;

  /**
   * URL.
   */
  @Expose
  @ValueMapValue
  private String url;
}
