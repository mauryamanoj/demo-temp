package com.saudi.tourism.core.models.app.location;

import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Objects;

/**
 * This model is used to handle one location data.
 */
@Data
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class SponsorsModel implements Serializable {

  /**
   * Title.
   */
  @ValueMapValue
  private String title;

  /**
   * Path.
   */
  @ValueMapValue
  private String path;

  /**
   * Images path.
   */
  @ValueMapValue
  private String[] images;

  /**
   * Model initializer.
   */
  @PostConstruct
  protected void init() {
    if (Objects.isNull(images)) {
      images = new String[] {};
    }
    if (StringUtils.isNotBlank(path)) {
      setPath(LinkUtils.getWebViewUrlForApp(path, true));
    }
  }
}
