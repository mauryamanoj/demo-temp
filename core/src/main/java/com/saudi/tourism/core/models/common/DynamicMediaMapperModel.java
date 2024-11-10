package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.DynamicMediaUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Dynamic Media Servlet.
 */
@Slf4j
@Model(adaptables = {Resource.class,
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DynamicMediaMapperModel {

  /**
   * Image Source.
   */
  @RequestAttribute
  private String imageSrc;

  /**
   * Component Profile.
   */
  @RequestAttribute
  private String componentProfile;

  /**
   * Enable Image sharpen.
   */
  @RequestAttribute
  private boolean enableImageSharpen;

  /**
   * Source.
   */
  @Setter
  @Getter
  private String src;

  /**
   * Sling settings service to check if the current environment is author or publish.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;

  /**
   * Model Initializer.
   */
  @PostConstruct protected void init() {
    setSrc(imageSrc);
    if (StringUtils.isNotBlank(imageSrc) && !imageSrc.contains(Constants.DAM)) {
      setSrc(DynamicMediaUtils
          .getScene7ImageWithDefaultImage(imageSrc, componentProfile, enableImageSharpen,
              DynamicMediaUtils.isCnServer(settingsService)));
    }
  }
}
