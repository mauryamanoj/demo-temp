package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

/**
 * About Section Model.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class About {

  /**
   * description.
   */
  @ValueMapValue
  private String description;

  /**
   * categories About.
   */
  @ChildResource
  private List<CategoriesAbout> categoriesAbout;

  /**
   * Inner class representing the about categories.
   */
  @Data
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  public static class CategoriesAbout {

    /**
     * The resource resolver.
     */
    @JsonIgnore
    @Inject
    private transient ResourceResolver resourceResolver;

    /** Sling settings service. */
    @JsonIgnore
    @OSGiService
    private transient SlingSettingsService settingsService;

    /**
     * icon.
     */
    @ValueMapValue
    private String icon;

    /**
     * id.
     */
    @ValueMapValue
    private String id;

    /**
     * title.
     */
    @ValueMapValue
    private String title;

    @PostConstruct
    void init() {
      icon = LinkUtils.getAuthorPublishAssetUrl(resourceResolver, icon,
                settingsService.getRunModes().contains(Externalizer.PUBLISH));
    }

  }

}
