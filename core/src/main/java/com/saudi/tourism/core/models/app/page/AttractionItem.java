package com.saudi.tourism.core.models.app.page;

import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Locale;
import java.util.Optional;

/**
 * Attraction Item model.
 */
@Model(adaptables = Resource.class,
      defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AttractionItem implements Serializable {
  /**
   * Type of Attraction.
   */
  @JsonIgnore
  @ValueMapValue
  private transient String type;

  /**
   * Icon of the attraction.
   */
  @Expose
  @ValueMapValue
  private String icon;

  /**
   * Order variable to give the number.
   */
  @Expose
  @ValueMapValue
  private Integer order;

  /**
   * type key.
   */
  @Expose
  private String typeKey;

  /**
   * type name.
   */
  @Expose
  private String typeName;

  /**
   * ResourceResolver.
   */
  @Self
  private transient Resource currentResource;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    TagManager tagManager = currentResource.getResourceResolver().adaptTo(TagManager.class);
    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);

    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION);

    typeKey = CommonUtils.getActualTagName(type, tagManager);
    typeName =  CommonUtils.getTagName(type, tagManager, new Locale(language));

  }
}
