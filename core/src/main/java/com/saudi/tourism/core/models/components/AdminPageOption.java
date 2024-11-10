package com.saudi.tourism.core.models.components;

import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * The type Admin page option.
 */
@Model(adaptables = Resource.class,
       cache = true,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Data
public class AdminPageOption {

  /**
   * The Use eventsPath.
   */
  @ValueMapValue
  private String eventsPath;

  /**
   * The Use brandsPath.
   */
  @ValueMapValue
  private String brandsPath;

  /**
   * The Use packagesPath.
   */
  @ValueMapValue
  private String packagesPath;

  /**
   * The Use hotelsPath.
   */
  @ValueMapValue
  private String hotelsPath;

  /**
   * The disablePackages.
   */
  @ValueMapValue
  private boolean disablePackages;

  /**
   * The disableAppPackages.
   */
  @ValueMapValue
  private boolean disableAppPackages;

  /**
   * The disableHotels.
   */
  @ValueMapValue
  private boolean disableHotels;

  /**
   * The disable Hotels filter.
   */
  @ValueMapValue
  private boolean disableArticleHotelsFilter;

  /**
   * The disable Restaurants filter.
   */
  @ValueMapValue
  private boolean disableArticleRestaurantsFilter;

  /**
   * The Header path.
   */
  @ValueMapValue
  private String headerPath;
  /**
   * The Header Logo white.
   */
  @ValueMapValue
  private String headerLogo;

  /**
   * The disableHotels.
   */
  @ValueMapValue
  private boolean enabledHeaderV4;

  /**
   * The Footer path.
   */
  @ValueMapValue
  private String footerPath;

  /**
   * The Sitemap path.
   */
  @ValueMapValue
  private String sitemapPath;

  /**
   * Hide Packages Price.
   */
  @ValueMapValue
  private boolean hidePackagesPrice;

  /**
   * Field contains path to the parent page for all activities.
   */
  private String activitiesPath;

  /**
   * Field contains path to the parent page for all Trip Plans.
   */
  private String tripPlansPath;

  /**
   * Fav Login Model title.
   */
  @ValueMapValue
  private String favLoginTitle;

  /**
   * Fav Login Model copy.
   */
  @ValueMapValue
  private String favLoginCopy;

  /**
   * Fav Login Model button copy.
   */
  @ValueMapValue
  private String favLoginButtonCopy;

  /**
   * The disableWebHalayallaPackages.
   */
  @ValueMapValue
  private boolean disableWebHalayallaPackages;

  /**
   * The disableAppHalayallaPackages.
   */
  @ValueMapValue
  private boolean disableAppHalayallaPackages;

  /**
   * The green Taxi Path.
   */
  @ValueMapValue
  private String greenTaxiPath;

  /**
   * Checkbox for Smart Banner enbale/disable.
   */
  @ValueMapValue
  private String enableSmartBanner;

  /**
   * IOS APp ID.
   */
  @ValueMapValue
  private String iosAppId;

  /**
   * smartBannerTitle.
   */
  @ValueMapValue
  private String smartBannerTitle;

  /**
   * smartBannerAuthorLabel.
   */
  @ValueMapValue
  private String smartBannerAuthor;

  /**
   * smartBannerButtonLabel.
   */
  @ValueMapValue
  private String smartBannerButtonLabel;

  /**
   * iosLabel.
   */
  @ValueMapValue
  private String iosLabel;

  /**
   * androidLabel.
   */
  @ValueMapValue
  private String androidLabel;

  /**
   * iosIcon.
   */
  @ValueMapValue
  private String iosIcon;

  /**
   * androidIcon.
   */
  @ValueMapValue
  private String androidIcon;

  /**
   * iosAppUrl.
   */
  @ValueMapValue
  private String iosAppUrl;
  /**
   * androidAppUrl.
   */
  @ValueMapValue
  private String androidAppUrl;

  /**
   * getLabel.
   */
  @ValueMapValue
  private String getLabel;
  /**
   * closeLabel.
   */
  @ValueMapValue
  private String closeLabel;

}
