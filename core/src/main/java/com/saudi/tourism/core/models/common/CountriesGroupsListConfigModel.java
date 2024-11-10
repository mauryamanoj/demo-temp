package com.saudi.tourism.core.models.common;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.inject.Inject;
import java.util.List;

/** Countries Groups List Model. */
@Getter
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class CountriesGroupsListConfigModel {

  /**
   * List of country groups.
   */
  @Inject
  private List<CountryGroupModel> listCountriesGroups;

  /** Country Group model. */
  @Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
  @Getter
  public static class CountryGroupModel {
    /** Name. */
    @ValueMapValue
    private String name;
  }
}
