package com.saudi.tourism.core.models.components.contentfragment.footer;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.InjectionStrategy;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;

@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class SubFragmentBranding {

  /**
   * visitSaudiLogo of the fragment.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String visitSaudiLogo;

  /**
   * visitSaudiLogoLink of the fragment.
   */
  @ValueMapValue
  @Expose
  private String visitSaudiLogoLink;

  /**
   * poweredBy of the fragment.
   */
  @ValueMapValue
  @Expose
  private String poweredBy;

  /**
   * staLogo of the fragment.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String staLogo;

  /**
   * staLogoLink of the fragment.
   */
  @ValueMapValue
  @Expose
  private String staLogoLink;

  /**
   * language.
   */
  @JsonIgnore
  @Setter
  private String language;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * setting service.
   */
  @OSGiService(injectionStrategy = InjectionStrategy.REQUIRED)
  private SlingSettingsService settingsService;
  /**
   * init method.
   */
  @PostConstruct
  public void init() {
    staLogoLink = LinkUtils.getAuthorPublishUrl(resourceResolver, staLogoLink,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
    visitSaudiLogoLink = LinkUtils.getAuthorPublishUrl(resourceResolver, visitSaudiLogoLink,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
  public String buildVisitSaudiLogo() {
    if (StringUtils.isNotEmpty(this.visitSaudiLogo)) {
      // Replace {language} with the value of the language.
      if (!visitSaudiLogo.endsWith("/")) {
        visitSaudiLogo += "/";
      }
      visitSaudiLogo = new StringBuilder(visitSaudiLogo).append(language).append(".svg").toString();
    }
    return visitSaudiLogo;
  }
}
