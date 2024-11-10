package com.saudi.tourism.core.models.components.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.SaudiModeConfig;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * The Class AccountSlingModel.
 */
@Model(adaptables = {Resource.class},
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class UserBoxModel implements Serializable {

  /**
   * yourFavorites.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String yourFavorites;

  /**
   * favoritesPath.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String favoritesPath;

  /**
   * yourTrips.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String yourTrips;

  /**
   * tripsPath.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String tripsPath;

  /**
   * profilePreference.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String profilePreference;

  /**
   * logOut.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String logOut;

  /**
   * logOutUrl.
   */
  @ValueMapValue
  @Setter
  @Expose
  private String logOutUrl;
  /**
   * Image.
   */
  @ChildResource
  @Expose
  private Image avatar;
  /**
   * Inject SaudiModeConfig.
   */
  @Inject
  private transient SaudiModeConfig saudiModeConfig;

  /**
   * Variable Injecting resourceResolver.
   */
  @SlingObject
  @JsonIgnore
  private transient ResourceResolver resolver;

  /**
   * init method.
   */
  @PostConstruct
  public void init() {
    logOutUrl = LinkUtils.getAuthorPublishUrl(resolver, logOutUrl, saudiModeConfig.getPublish());
    tripsPath = LinkUtils.getAuthorPublishUrl(resolver, tripsPath, saudiModeConfig.getPublish());
    favoritesPath = LinkUtils.getAuthorPublishUrl(resolver, favoritesPath, saudiModeConfig.getPublish());
  }

}
