package com.saudi.tourism.core.models.app.common;

import com.day.cq.wcm.api.PageManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.saudi.tourism.core.utils.AppUtils;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Some abstract parent class for all app servlets' results.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public abstract class AppPageRequestResultModel implements Serializable {

  /**
   * favId for app to map to web pages.
   */
  @Getter
  @JsonProperty("favId")
  @SerializedName("favId")
  @ValueMapValue
  private String webMappingPath;
  /**
   * Id.
   */
  @Getter
  @Setter
  private String id;

  /**
   * Full path to the corresponding page resource.
   */
  @Getter
  private String path;

  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  public void initAppPageRequestResultModel() {
    final PageManager pageManager =
        currentResource.getResourceResolver().adaptTo(PageManager.class);
    assert pageManager != null;

    setPath(pageManager.getContainingPage(currentResource).getPath());

    webMappingPath = LinkUtils.getFavoritePath(webMappingPath);
    // Cleanup
    this.currentResource = null;
  }

  /**
   * Setter for the path, updates also id field.
   *
   * @param path path to the corresponding page
   */
  public void setPath(final String path) {
    this.path = path;
    if (StringUtils.isNotBlank(path)) {
      this.setId(AppUtils.pathToID(path));
    }
  }
}
