package com.saudi.tourism.core.models.components.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.saudi.tourism.core.login.services.SaudiLoginConfig;
import com.saudi.tourism.core.login.servlets.SSIDGetUserDetailsServlet;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;
import java.util.Objects;

import static com.saudi.tourism.core.utils.Constants.TRIPS_EXTENSION;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
       resourceType = "sauditourism/components/content/login/account")
@Slf4j
public class AccountSlingModel implements Serializable {

  /**
   * The Request.
   */
  @SlingObject
  @JsonIgnore
  private transient SlingHttpServletRequest request;

  /**
   * heroDataModel.
   */
  @ChildResource
  private HeroDataModel heroData;

  /**
   * userBoxModel.
   */
  @ChildResource
  private UserBoxModel userBox;

  /**
   * userApiModel.
   */
  @ChildResource
  @Setter
  private UserApiModel userApi;

  /**
   * preferenceBoxModel.
   */
  @ChildResource
  private PreferenceBoxModel preferenceBox;

  /**
   * The Saudi login config.
   */
  @Inject
  private transient SaudiLoginConfig saudiLoginConfig;

  /**
   * getJson method for account component.
   *
   * @return json representation.
   */
  @JsonIgnore
  public String getJson() {
    return new Gson().toJson(this);
  }

  /**
   * init method.
   */
  @PostConstruct
  public void init() {
    if (Objects.isNull(userApi)) {
      UserApiModel userApiModel = new UserApiModel();
      userApiModel.setPath("/bin/api/v1/user/");
      userApiModel.setGetProfile("get.profile");
      userApiModel.setUpdateProfile("update.profile");
      userApiModel.setDomain(saudiLoginConfig.getAuth0Domain());
      userApiModel.setClientId(saudiLoginConfig.getClientId());
      userApiModel.setGetTripPlansEndpoint(SSIDGetUserDetailsServlet.SERVLET_PATH + TRIPS_EXTENSION);
      setUserApi(userApiModel);
    }

  }

}
