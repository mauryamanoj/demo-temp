package com.saudi.tourism.core.models.components.fetchExperiences.v1;

import java.util.Collections;
import java.util.List;
import javax.annotation.PostConstruct;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.With;
import org.apache.commons.lang3.StringUtils;
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
public class FetchExperienceFilterModel {

  /**
   * Type Filter.
   */
  @Expose
  @ValueMapValue
  private List<String> type;

  /**
   * Destination Filter.
   */
  @Expose
  @ValueMapValue
  private String destination;

  /**
   * Start Date.
   */
  @Expose
  @ValueMapValue
  private String startDate;

  /**
   * End Date.
   */
  @Expose
  @ValueMapValue
  private String endDate;

  /**
   * freeOnly.
   */
  @Expose
  @ValueMapValue
  private Boolean freeOnly;

  /**
   * Init.
   */
  @PostConstruct
  void init() {
    if (StringUtils.isNotEmpty(destination)) {
      destination = destination.substring(destination.lastIndexOf("/") + 1);
    }
    type = Collections.singletonList(Constants.EXPERIENCE);
  }
}
