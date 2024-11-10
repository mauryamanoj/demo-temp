package com.saudi.tourism.core.models.app.content;

import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;

/**
 * Tab data model.
 */
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class TabModel implements Serializable {

  /**
   * Tab title.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String tabTitle;

  /**
   * Tab description.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String tabDescription;

  /**
   * Tab Category.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String tabCategory;

  /**
   * Tab image.
   */
  @Getter
  @Setter
  @ValueMapValue
  private String tabImage;

  /**
   * List of ctas for the external pages.
   */
  @Getter
  @Setter
  @ChildResource
  private List<Link> externalPages;


  /**
   * ResourceResolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    TagManager tagManager;
    if (resourceResolver != null && StringUtils.isNotBlank(tabCategory)) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
      tabCategory = CommonUtils.getActualTagName(tabCategory, tagManager);
    }
  }
}
