package com.saudi.tourism.core.models.components.citydetails;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

/**
 * Details.
 */
@Model(adaptables = {
    Resource.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Details {

  /**
   * The Sling settings service.
   */
  @OSGiService
  private transient SlingSettingsService settingsService;

  /**
   * Label.
   */
  @ValueMapValue
  @Expose
  private String label;

  /**
   * Description.
   */
  @ValueMapValue
  @Expose
  private String description;

  /**
   * Learn more label.
   */
  @ValueMapValue
  @Expose
  private String learnMoreLabel;

  /**
   * Learn more label link.
   */
  @ValueMapValue
  @Expose
  private String learnMoreLabelLink;

  /**
   * Learn more label link target.
   */
  @ValueMapValue
  @Expose
  private String learnMoreLabelLinkTarget;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    learnMoreLabelLink = LinkUtils
            .getAuthorPublishUrl(getResolver(), learnMoreLabelLink,
              settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }

}
