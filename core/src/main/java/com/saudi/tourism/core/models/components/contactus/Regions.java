package com.saudi.tourism.core.models.components.contactus;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

import java.util.List;

/**
 * Class contains details of Seasons and Festivals on Events Card Component.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Regions {
  /**
   * Region Name.
   */
  @ValueMapValue
  @Expose
  private String regionName;

  /**
   * Countries.
   */
  @ChildResource
  @Getter
  @Expose
  private List<Countries> countries;

}
