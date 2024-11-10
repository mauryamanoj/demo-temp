package com.saudi.tourism.core.models.components.thingstodo.v1;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Cards Filter Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class ThingsToDoCardsFilterModel {
  /**
   * CFM type: attraction, activity, tour.
   */
  @Expose
  @ValueMapValue
  private List<String> type;

  /**
   * Categories Filter: Tag based /content/cq:tags/sauditourism/categories.
   */
  @Expose
  @ValueMapValue
  private List<String> categories;

  /**
   * POI Types Filter: Tag Based /content/cq:tags/sauditourism/type.
   */
  @Expose
  @ValueMapValue
  private List<String> poiTypes;

  /**
   * destinations list, to use in the filter.
   */
  @Expose
  @ValueMapValue
  private List<String> destinations;

  /**
   * Init method.
   */
  @PostConstruct
  public void init() {
    if (CollectionUtils.isNotEmpty(destinations)) {
      this.destinations = destinations.stream()
        .filter(Objects::nonNull)
        .map(LinkUtils::getLastPathSegment)
        .collect(Collectors.toList());
    }
  }
}
