package com.saudi.tourism.core.models.components.contactus;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * Class contains details of Seasons and Festivals on Events Card Component.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
public class Countries {

  /**
   * nation flag.
   */
  @ValueMapValue
  @Expose
  private String nationFlag;
  /**
   * nation name.
   */
  @ValueMapValue
  @Expose
  private String nationName;
  /**
   * phone number.
   */
  @ValueMapValue
  @Expose
  private String phoneNumber;
  /**
   * phone status.
   */
  @ValueMapValue
  @Expose
  private String phoneStatus;

}
