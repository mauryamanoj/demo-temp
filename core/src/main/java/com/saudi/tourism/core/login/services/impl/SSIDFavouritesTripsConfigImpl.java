package com.saudi.tourism.core.login.services.impl;

import com.saudi.tourism.core.login.services.SSIDFavouritesTripsConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

/**
 * This class contains Saudi Tourism SSID Login configurations.
 */

/** The Constant LOGGER. */
@Slf4j
@Component(immediate = true, service = SSIDFavouritesTripsConfig.class)
@Designate(ocd = SSIDFavouritesTripsConfigImpl.Configuration.class)
public class SSIDFavouritesTripsConfigImpl implements SSIDFavouritesTripsConfig {

  /**
   * Gets the middleware domain.
   *
   * @return the middleware domain
   */
  @Getter
  private String middlewareDomain;

  /**
   * Gets the fetch favorites trips endpoint.
   *
   * @return the fetch favorites trips endpoint
   */
  @Getter
  private String fetchFavoritesTripsEndpoint;

  /**
   * Gets the modify favorites trips endpoint.
   *
   * @return the modify favorites trips endpoint
   */
  @Getter
  private String modifyFavoritesTripsEndpoint;

  /**
   * Gets the nativeApp UserDetails EndPoint.
   *
   * @return the nativeApp UserDetails EndPoint
   */
  @Getter
  private String nativeAppUserDetailsEndpoint;

  /**
   * Gets the get Entertaner locations.
   *
   * @return the entertainer locations
   */
  @Getter
  private String entertainerLocationsEndpoint;



  /**
   * This method gets triggered on Activation or modification of configurations.
   *
   * @param saudiSsidConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(SSIDFavouritesTripsConfigImpl.Configuration saudiSsidConfig) {
    LOGGER.debug("Saudi SSID Favourites Trips Configurations Activate/Modified");

    this.middlewareDomain = saudiSsidConfig.middlewareDomain();
    this.fetchFavoritesTripsEndpoint = saudiSsidConfig.fetchFavoritesTripsEndpoint();
    this.modifyFavoritesTripsEndpoint = saudiSsidConfig.modifyFavoritesTripsEndpoint();
    this.nativeAppUserDetailsEndpoint = saudiSsidConfig.nativeAppUserDetailsEndpoint();
    this.entertainerLocationsEndpoint = saudiSsidConfig.entertainerLocationsEndpoint();
  }

  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Favourites Trips SSID Configuration")
  @interface Configuration {

    /**
     * Middleware domain.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Favourites Trips User Details Domain", type = AttributeType.STRING)
    String middlewareDomain() default StringUtils.EMPTY;

    /**
     * Fetch favorites trips endpoint.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Favourites Trips Update User Details Domain", type = AttributeType.STRING)
    String fetchFavoritesTripsEndpoint() default StringUtils.EMPTY;

    /**
     * Modify favorites trips endpoint.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Favourites Trips User Details Endpoint", type = AttributeType.STRING)
    String modifyFavoritesTripsEndpoint() default StringUtils.EMPTY;

    /**
     * Native App UserDetails endpoint.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Native App V2 User Details Domain",
        type = AttributeType.STRING)
    String nativeAppUserDetailsEndpoint() default StringUtils.EMPTY;

    /**
     * get Entertainer locations endpoint.
     *
     * @return the string
     */
    @AttributeDefinition(name = "Entertainer locations endpoint", type = AttributeType.STRING)
    String entertainerLocationsEndpoint() default StringUtils.EMPTY;

  }
}
