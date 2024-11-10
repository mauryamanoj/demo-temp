package com.saudi.tourism.core.models.components.contentfragment.menu;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;


@Model(adaptables = Resource.class,
     defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class EvisaCFModel {

  /**
   * Sling settings service.
   */
  @JsonIgnore
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * The Resource.
   */
  @Inject
  private Resource resource;

  /**
   * evisa title.
   */
  @Expose
  @ValueMapValue(name = "evisaTitle")
  private String title;

  /**
   * CTA Link.
   */
  @Expose
  @ValueMapValue(name = "evisaLink")
  private String link;

  @PostConstruct
  public void init() {
    link = LinkUtils.getAuthorPublishUrl(resource.getResourceResolver(), link,
      settingsService.getRunModes().contains(Externalizer.PUBLISH));
  }
}
