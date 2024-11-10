package com.saudi.tourism.core.models.components.account;

import com.google.gson.annotations.Expose;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class UserApiModel implements Serializable {

  /**
   * path.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String path;

  /**
   * getProfile.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String getProfile;

  /**
   * updateProfile.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String updateProfile;

  /**
   * domain.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String domain;

  /**
   * clientId.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String clientId;

  /**
   * getTripPlansEndpoint.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String getTripPlansEndpoint;
}
