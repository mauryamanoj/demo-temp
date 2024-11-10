package com.saudi.tourism.core.models.common;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import java.io.Serializable;

/**
 * Image caption model, originally to be used in image-caption.html template.
 */
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class ImageCaption implements Serializable {

  /**
   * Link type: none, internal, external.
   */
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  @Setter
  @ValueMapValue
  @Expose
  private String linkType;

  /**
   * Caption text.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String copy;

  /**
   * Caption link URL.
   */
  @Setter
  @ValueMapValue
  @Expose
  private String link;

  /**
   * Sling settings service to get sling run modes.
   */
  @JsonIgnore
  @OSGiService
  private transient SlingSettingsService saudiModeConfig;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * This model post construct initialization.
   */
  @PostConstruct
  private void init() {
    // Add .html or remove /content/sauditourism according to sling run mode for internal link
    if (StringUtils.isNotBlank(this.link)) {
      this.link = LinkUtils.getAuthorPublishUrl(resolver, this.link,
          saudiModeConfig.getRunModes().contains(Externalizer.PUBLISH));
    }

    // Cleanup
    this.saudiModeConfig = null;
  }
}
