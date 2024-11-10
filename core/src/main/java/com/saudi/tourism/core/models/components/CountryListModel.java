package com.saudi.tourism.core.models.components;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Exporter;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;

import java.util.List;

/**
 * Class for CountryListModel.
 */
@Model(adaptables = Resource.class, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = "sauditourism/components/content/countryList")
@Getter
@Slf4j
@Exporter(name = "jackson", extensions = "json")
public class CountryListModel {

  /**
   * Injecting multi field .
   */
  @ChildResource
  private List<Resource> country;

  /**
   * Injecting multi field .
   */
  @ChildResource
  private List<Resource> data;

}
