package com.saudi.tourism.core.models.app.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.models.app.page.AppHomepage;
import com.saudi.tourism.core.models.app.page.AppPageInfo;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.ROOT_CONTENT_PATH;
import static com.saudi.tourism.core.utils.Constants.TYPE_EXTERNAL;

/**
 * This model is used to handle one location data.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Setter
public class EmptyPackageBannerModel implements Serializable {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Description.
   */
  @ValueMapValue
  private String description;

  /**
   * Image reference.
   */
  @ValueMapValue
  private String image;
  /**
   * ctaText.
   */
  @ValueMapValue
  private String ctaText;

  /**
   * ctaUrl.
   */
  @ValueMapValue
  private String ctaUrl;

  /**
   * ctaAppNavigation.
   */
  @ChildResource
  private AppPageInfo ctaAppNavigation;

  /**
   * The current component resource.
   */
  @JsonIgnore
  @Self
  private transient Resource currentResource;

  /**
   * Model initialization method.
   */
  @PostConstruct private void init() {
    if (Objects.nonNull(ctaAppNavigation) && StringUtils.isNotBlank(ctaAppNavigation.getType())) {
      ctaAppNavigation.setId(this.ctaUrl);
      ctaAppNavigation.setPath(this.ctaUrl);
    } else if (StringUtils.isNotBlank(this.ctaUrl)) {
      if (!this.ctaUrl.startsWith(ROOT_CONTENT_PATH)) {
        this.ctaAppNavigation = new AppPageInfo(this.ctaUrl, null, TYPE_EXTERNAL, null, null, null);
      } else {
        Resource res = currentResource.getResourceResolver().getResource(this.ctaUrl);
        if (Objects.nonNull(res)) {
          this.ctaAppNavigation = AppHomepage.getAppPageInfo(res);
        }
      }
    }
    this.currentResource = null;
  }

}
