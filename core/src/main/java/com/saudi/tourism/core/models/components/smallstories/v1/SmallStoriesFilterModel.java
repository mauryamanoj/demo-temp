package com.saudi.tourism.core.models.components.smallstories.v1;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/** Small Stories Filter Model. */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@With
public class SmallStoriesFilterModel {

  /**
   * Categories Filter: Tag based /content/cq:tags/sauditourism/categories.
   */
  @Expose
  @ValueMapValue
  private List<String> categories;

  /**
   * destinations list, to use in the filter.
   */
  @Expose
  private List<String> destinations = new ArrayList<>();;

  /**
   * Destination Filter: Path of a destination CF.
   */
  @JsonIgnore
  @ValueMapValue
  private String destination;

  /**
   * Init method.
   */
  @PostConstruct
  public void init() {
    if (StringUtils.isNotEmpty(destination)) {
      destinations.add(LinkUtils.getLastPathSegment(destination));
    }
  }
}
