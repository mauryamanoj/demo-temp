package com.saudi.tourism.core.models.mobile.components.atoms;

import com.day.cq.commons.Externalizer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.stream.Collectors;

/**
 * Item General Information Widget Model.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class GeneralInformationWidget {

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
   * Rows General Information Widget.
   */
  @ChildResource(name = "rowsGeneralInformationWidget")
  private List<RowsGeneralInformationWidget> rows;

  /**
   * title.
   */
  @ValueMapValue
  private String title;

  /**
   * icon Url.
   */
  @ValueMapValue
  private String iconUrl;

  /**
   * expandable.
   */
  @ValueMapValue
  private Boolean expandable;

  @PostConstruct
  void init() {
    iconUrl =
        LinkUtils.getAuthorPublishAssetUrl(
            resourceResolver,
            iconUrl,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));
    if (CollectionUtils.isNotEmpty(rows)) {
      rows.stream()
          .map(
              r -> {
                r.setIconUrl(
                    LinkUtils.getAuthorPublishAssetUrl(
                        resourceResolver,
                        r.getIconUrl(),
                        settingsService.getRunModes().contains(Externalizer.PUBLISH)));
                return r.getIconUrl();
              })
          .collect(Collectors.toList());
    }
  }
}
