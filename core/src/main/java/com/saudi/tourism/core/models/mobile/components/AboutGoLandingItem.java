package com.saudi.tourism.core.models.mobile.components;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

/**
 * AboutGoLandingItem.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AboutGoLandingItem implements Serializable {

  /**
   * The resource resolver.
   */
  @JsonIgnore
  @Inject
  private ResourceResolver resourceResolver;

  /** Sling settings service. */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * title.
   */
  private String id;
  /**
   * description.
   */
  @ValueMapValue
  @JsonIgnore
  private transient String itemPath;
  /**
   * iconUrl.
   */
  @ValueMapValue
  private String iconUrl;

  @PostConstruct
  void init() {

    if (Objects.nonNull(itemPath)) {
      id = MobileUtils.extractItemId(itemPath);
    }

    if (iconUrl != null) {
      iconUrl =
          LinkUtils.getAuthorPublishAssetUrl(
              resourceResolver,
              iconUrl,
              settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }

  }

}
