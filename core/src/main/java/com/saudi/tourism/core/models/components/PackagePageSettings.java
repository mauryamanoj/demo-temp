package com.saudi.tourism.core.models.components;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.saudi.tourism.core.services.mobile.v1.filters.FilterCategories;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.util.List;

/**
 * The type Admin page option.
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
@Slf4j
public class PackagePageSettings {

  /**
   * The Use winterCampaignPath.
   */
  @ValueMapValue
  private String winterCampaignPath;

  /**
   * The Use packageDetailsPath.
   */
  @ValueMapValue
  private String packageDetailsPath;


  /**
   * The desktopImage.
   */
  @ValueMapValue
  private String desktopImage;

  /**
   * The Use mobileImage.
   */
  @ValueMapValue
  private String mobileImage;


  /**
   * The filters.
   */
  @ChildResource
  private List<HyPackageFilterModel> filters;

  /**
   * The mobile filters.
   */
  @ChildResource(name = "filters")
  @JsonIgnore
  private List<FilterCategories> mobileFilters;

}

