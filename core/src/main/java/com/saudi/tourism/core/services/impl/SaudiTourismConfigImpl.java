package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
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
 * This class contains Saudi Tourism project configurations.
 */
@Slf4j
@Component(immediate = true, service = SaudiTourismConfigs.class)
@Designate(ocd = SaudiTourismConfigImpl.Configuration.class)
@Getter
public class SaudiTourismConfigImpl implements SaudiTourismConfigs {

  /**
   * Holds map API key.
   */
  private String mapKey;
  /**
   * Holds google map API key.
   */
  private String googleMapsKey;

  /**
   * Show map border.
   */
  private Boolean showMapBorder;

  /**
   * Mailchimp endpoint URL.
   */
  private String mailChimpURL;

  /**
   * Mailchimp email field.
   */
  private String mailChimpField;

  /**
   * Currency external app url - currencyExternalAppURL.
   */
  private String currencyExternalAppURL;

  /**
   * Currency external app url - currencyExternalAppURL.
   */
  private String roadTripScenariosExternalAppURL;
  /**
   * G road trip API Url.
   */
  private String roadTripEndpoint;
  /**
   * package Details Path.
   */
  private String packageDetailsPath;

  /**
   * Weather external app url - weatherExternalAppURL.
   */
  private String simpleWeatherExternalAppUrl;

  /**
   * Daily weather external app url.
   */
  private String oneCallWeatherExternalAppUrl;

  /**
   * Group requests weather external app url.
   */
  private String groupCallWeatherExternalAppUrl;

  /**
   * Dynamic Media profile name.
   */
  private String dynamicMediaProfile;

  /**
   * Dynamic halayallaEndPointUrl Domain.
   */
  private String halayallaEndPointUrl;
  /**
   * Dynamic halayallaEndPointUrlV2 Version Domain.
   */
  private String halayallaEndPointUrlV2;
  /**
   * Dynamic halayallaToken.
   */
  private String halayallaToken;

  /**
   * Dynamic halayallaTokenV2.
   */
  private String halayallaTokenV2;
  /**
   * Dynamic halayallaClientKey.
   */
  private String halayallaClientKey;
  /**
   * Dynamic halayallaClientSecret.
   */
  private String halayallaClientSecret;

  /**
   * Dynamic scene7Domain Domain.
   */
  private String scene7Domain;

  /**
   * Domain Server Url.
   */
  private String domainServerUrlKey;

  /**
   * Summer Packages Api EndPoint.
   */
  private String summerPackagesApiEndpoint;

  /**
   * Winter 2021 cards Api EndPoint.
   */
  private String winter2021CardsApiEndpoint;

  /**
   * Winter Experience Cards Api EndPoint.
   */
  private String winterCardsExperienceApiEndpoint;

  /**
   * List of paths to exclude for app apiEndpoint /bin/api/v1/app/locations.
   */
  private String[] excludingPaths;
  /**
   * Bridge Enable/Disable Sitewide.
   */
  private Boolean bridgeEnable;
  /**
   * Favorite Enable/Disable Sitewide.
   */
  private Boolean enableFavorite;
  /**
   * Contact us Form EndPoint.
   */
  private String contactUsFormEndpoint;
  /**
   * Contact us Form EndPoint.
   */
  private String favBaseUrl;
  /**
   * HY experience domain.
   */
  private String hYExperienceDomain;
  /**
   * HY encryption key.
   */
  private String hyEncryptionKey;

  /**
   * weather Extended API BasePath.
   */
  private String weatherExtendedBasePath;

  /**
   * Countries configuration page.
   */
  private String countriesConfigPath;

  /**
   * Events API endPoint.
   */
  private String eventsApiEndpoint;

  /**
   * Contact US: Retrieve message types API endPoint.
   */
  private String contactUsMessageTypeApiEndpoint;

  /**
   * Contact US: Validate captcha API endPoint.
   */
  private String contactUsValidateCaptchaApiEndpoint;

  /**
   * Dam: Flags path.
   */
  private String flagsPath;

  /**
   * Content: Events filter page path.
   */
  private String eventsFilterPagePath;

  /**
   * middleware: dnsName.
   */
  private String middlewareDNS;

  /**
   * middleware: userfeedback endpoint.
   */
  private String middlewareUserFeedbackEndpoint;

  /**
   * Search endPoint API.
   */
  private String searchApiEndpoint;

  /**
   * GraphQL Endpoint for Category Content Fragment.
   */
  private String graphQLCategoriesEndpoint;

  /**
   * GraphQL Endpoint for Destination Content Fragment.
   */
  private String graphQLDestinationEndpoint;

  /**
   * GraphQL Endpoint for All Attractions Content Fragment.
   */
  private String graphQLAllAttractionsEndpoint;

  /**
   * GraphQL Endpoint for Attractions with filter Content Fragment.
   */
  private String graphQLAttractionsWithFiltersEndpoint;

  /**
   * newsLetter us Form EndPoint.
   */
  private String middlewareNewsLetterEndpoint;

  /**
   * Destinations CF: Destinations CFs path in DAM.
   */
  private String destinationsCFPath;

  /**
   * Season CF: Season CFs path in DAM.
   */
  private String seasonsCFPath;

  /**
   * All Events: All Events Section Path.
   */
  private String allEventsSectionPath;

  /**
   * Holiday CF: Holiday CFs path in DAM.
   */
  private String holidaysCFPath;

  /**
   * Events CF: Events CFs path in DAM.
   */
  private String eventsCFPath;

  /**
   * Attraction CF: Attractions CFs path in DAM.
   *  Attractions CFs path in DAM
   */
  private String attractionsCFPath;

  /**
   * Activity CF: Activities CFs path in DAM.
   * Activities CFs path in DAM
   */
  private String activitiesCFPath;

  /**
   * Tour CF: Tours CFs path in DAM.
   * Tours CFs path in DAM
   */
  private String toursCFPath;

  /**
   * Story CF: Stories CFs path in DAM.
   */
  private String storiesCFPath;

  /**
   * POIs CF: Stories CFs path in DAM.
   */
  private String poisCFPath;

  /**
   * Articles CF: Articles CFs path in DAM.
   */
  private String articlesCFPath;

  /**
   * Articles Tags Path.
   */
  private String articleTagsPath;

  /**
   * Trending: Resource Path.
   */
  private String trendingResourcePath;

  /**
   * Suggestions: Resource Path.
   */
  private String suggestionsResourcePath;

  /**
   * Things ToDo API endPoint.
   */
  private String thingsToDoApiEndpoint;

  /**
   * Things ToDo API endPoint.
   */
  private String categoriesTagsPath;

  /**
   * Categories API endPoint.
   */
  private String categoriesEndpoint;

  /**
    * Destinations API endPoint.
   */
  private String destinationsEndpoint;

  /**
   * Attractions API endPoint.
   */
  private String attractionsEndpoint;

  /**
   * Stories API endPoint.
   */
  private String storiesApiEndpoint;

  /**
   * FAQ: categories endpoint.
   */
  private String faqCategoriesEndpoint;

  /**
   * FAQ: articles endpoint.
   */
  private String faqArticlesEndpoint;

  /**
   * MuleSoft: Client ID.
   * MuleSoft: Client ID
   */
  private String muleSoftClientId;

  /**
   * MuleSoft: Client Secret.
   * MuleSoft: Client Secret
   */
  private String muleSoftClientSecret;
  /**
   * FAQ: MuleSoft categories endpoint.
   */
  private String muleSoftFaqCategoriesEndpoint;
  /**
   * FAQ: MuleSoft articles endpoint.
   */
  private String muleSoftFaqArticlesEndpoint;

  /**
   * Contact US: MuleSoft Form endpoint.
   */
  private String muleSoftContactUsFormEndpoint;

  /**
   * Contact US: MuleSoft MessageType endpoint.
   */
  private String muleSoftContactUsMessageTypeEndpoint;

  /**
   * Mobile Item: Base Path.
   */
  private String mobileItemBasePath;

  /**
   * Mobile Section: Base Path.
   */
  private String mobileSectionBasePath;

  /**
   * This method gets triggered on Activation or modification of configurations.
   * @param sauditourismConfig Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration sauditourismConfig) {
    LOGGER.debug("Saudi Configurations Activate/Modified");

    this.mapKey = sauditourismConfig.mapBoxApiKey();
    this.mailChimpURL = sauditourismConfig.mailChimpURL();
    this.mailChimpField = sauditourismConfig.mailChimpField();
    this.currencyExternalAppURL = sauditourismConfig.currencyExternalAppURL();
    this.roadTripScenariosExternalAppURL = sauditourismConfig.roadTripScenariosExternalAppURL();
    this.simpleWeatherExternalAppUrl = sauditourismConfig.simpleWeatherExternalAppUrl();
    this.oneCallWeatherExternalAppUrl = sauditourismConfig.oneCallWeatherExternalAppUrl();
    this.groupCallWeatherExternalAppUrl = sauditourismConfig.groupCallWeatherExternalAppUrl();
    this.dynamicMediaProfile = sauditourismConfig.dynamicMediaProfile();
    this.scene7Domain = sauditourismConfig.scene7Domain();
    this.halayallaEndPointUrl = sauditourismConfig.halayallaEndPointUrl();
    this.halayallaEndPointUrlV2 = sauditourismConfig.halayallaEndPointUrlV2();
    this.halayallaToken = sauditourismConfig.halayallaToken();
    this.halayallaTokenV2 = sauditourismConfig.halayallaTokenV2();
    this.halayallaClientKey = sauditourismConfig.halayallaClientKey();
    this.halayallaClientSecret = sauditourismConfig.halayallaClientSecret();
    this.domainServerUrlKey = sauditourismConfig.domainServerUrlKey();
    this.summerPackagesApiEndpoint = sauditourismConfig.summerPackagesApiEndpoint();
    this.winter2021CardsApiEndpoint = sauditourismConfig.winter2021CardsApiEndpoint();
    this.winterCardsExperienceApiEndpoint = sauditourismConfig.winterCardsExperienceApiEndpoint();
    this.excludingPaths = sauditourismConfig.excludingPaths();
    this.contactUsFormEndpoint = sauditourismConfig.contactUsFormEndpoint();
    this.roadTripEndpoint = sauditourismConfig.roadTripEndpoint();
    this.packageDetailsPath = sauditourismConfig.packageDetailsPath();
    this.favBaseUrl = sauditourismConfig.favBaseUrl();
    this.enableFavorite = sauditourismConfig.enableFavorite();
    this.hYExperienceDomain = sauditourismConfig.hYExperienceDomain();
    this.hyEncryptionKey = sauditourismConfig.hyEncryptionKey();
    this.weatherExtendedBasePath = sauditourismConfig.weatherExtendedBasePath();
    this.bridgeEnable = sauditourismConfig.bridgeEnable();
    this.googleMapsKey = sauditourismConfig.googleMapsKey();
    this.showMapBorder = sauditourismConfig.showMapBorder();
    this.countriesConfigPath = sauditourismConfig.countriesConfigPath();
    this.eventsApiEndpoint = sauditourismConfig.eventsApiEndpoint();
    this.contactUsMessageTypeApiEndpoint = sauditourismConfig.contactUsMessageTypeApiEndpoint();
    this.contactUsValidateCaptchaApiEndpoint = sauditourismConfig.contactUsValidateCaptchaApiEndpoint();
    this.flagsPath = sauditourismConfig.flagsPath();
    this.eventsFilterPagePath = sauditourismConfig.eventsFilterPagePath();
    this.middlewareDNS = sauditourismConfig.middlewareDNS();
    this.middlewareUserFeedbackEndpoint = sauditourismConfig.middlewareUserFeedbackEndpoint();
    this.searchApiEndpoint = sauditourismConfig.searchApiEndpoint();
    this.graphQLCategoriesEndpoint = sauditourismConfig.graphQLCategoriesEndpoint();
    this.graphQLDestinationEndpoint = sauditourismConfig.graphQLDestinationEndpoint();
    this.graphQLAllAttractionsEndpoint = sauditourismConfig.graphQLAllAttractionsEndpoint();
    this.graphQLAttractionsWithFiltersEndpoint = sauditourismConfig.graphQLAttractionsWithFiltersEndpoint();
    this.middlewareNewsLetterEndpoint = sauditourismConfig.middlewareNewsLetterEndpoint();
    this.destinationsCFPath = sauditourismConfig.destinationsCFPath();
    this.seasonsCFPath = sauditourismConfig.seasonsCFPath();
    this.allEventsSectionPath = sauditourismConfig.getAllEventsSectionPath();
    this.holidaysCFPath = sauditourismConfig.holidaysCFPath();
    this.eventsCFPath = sauditourismConfig.eventsCFPath();
    this.activitiesCFPath = sauditourismConfig.activitiesCFPath();
    this.toursCFPath = sauditourismConfig.toursCFPath();
    this.storiesCFPath = sauditourismConfig.storiesCFPath();
    this.poisCFPath = sauditourismConfig.poisCFPath();
    this.articlesCFPath = sauditourismConfig.articlesCFPath();
    this.articleTagsPath = sauditourismConfig.articleTagsPath();
    this.trendingResourcePath = sauditourismConfig.trendingResourcePath();
    this.suggestionsResourcePath = sauditourismConfig.suggestionsResourcePath();
    this.thingsToDoApiEndpoint = sauditourismConfig.thingsToDoApiEndpoint();
    this.categoriesTagsPath = sauditourismConfig.categoriesTagsPath();
    this.attractionsCFPath = sauditourismConfig.attractionsCFPath();
    this.categoriesEndpoint = sauditourismConfig.categoriesEndpoint();
    this.destinationsEndpoint = sauditourismConfig.destinationsEndpoint();
    this.attractionsEndpoint = sauditourismConfig.attractionsEndpoint();
    this.storiesApiEndpoint = sauditourismConfig.storiesApiEndpoint();
    this.faqCategoriesEndpoint = sauditourismConfig.faqCategoriesEndpoint();
    this.faqArticlesEndpoint = sauditourismConfig.faqArticlesEndpoint();
    this.muleSoftClientId = sauditourismConfig.muleSoftClientId();
    this.muleSoftClientSecret = sauditourismConfig.muleSoftClientSecret();
    this.muleSoftFaqCategoriesEndpoint = sauditourismConfig.muleSoftFaqCategoriesEndpoint();
    this.muleSoftFaqArticlesEndpoint = sauditourismConfig.muleSoftFaqArticlesEndpoint();
    this.muleSoftContactUsFormEndpoint = sauditourismConfig.muleSoftContactUsFormEndpoint();
    this.muleSoftContactUsMessageTypeEndpoint = sauditourismConfig.muleSoftContactUsMessageTypeEndpoint();
    this.mobileItemBasePath = sauditourismConfig.mobileItemBasePath();
    this.mobileSectionBasePath = sauditourismConfig.mobileSectionBasePath();
  }


  /**
   * The interface Configuration.
   */
  @ObjectClassDefinition(name = "Saudi Tourism Configuration")
  @interface Configuration {


    /**
     * Retrieve the check value for enabling favorite.
     * @return Boolean enableFavorite
     */
    @AttributeDefinition(name = "Enable Favorite", type = AttributeType.BOOLEAN)
    boolean enableFavorite() default false;

    /**
     * Retrieve the MapBox API key.
     *
     * @return String MapBox API key
     */
    @AttributeDefinition(name = "Map Box Api Key", type = AttributeType.STRING)
    String mapBoxApiKey() default StringUtils.EMPTY;
    /**
     * Retrieve the Google maps API key.
     * @return String Google Maps API key
     */
    @AttributeDefinition(name = "Google Maps Api Key", type = AttributeType.STRING)
    String googleMapsKey() default StringUtils.EMPTY;

    /**
     * Retrieve the Show Map Border.
     *
     * @return Boolean Show Map Border
     */
    @AttributeDefinition(name = "Show Map Border", type = AttributeType.BOOLEAN)
    boolean showMapBorder() default false;

    /**
     * Retrieve the Mailchimp URL.
     *
     * @return String Mailchimp URL
     */
    @AttributeDefinition(name = "Mail Chimp URL", type = AttributeType.STRING)
    String mailChimpURL() default StringUtils.EMPTY;

    /**
     * Retrieve the Mailchimp Field.
     *
     * @return String Mailchimp Field
     */
    @AttributeDefinition(name = "Mail Chimp Field", type = AttributeType.STRING)
    String mailChimpField() default StringUtils.EMPTY;

    /**
     * Retrieve the Currency Data External App URL .
     *
     * @return String Currency Data External App URL .
     */
    @AttributeDefinition(name = "Currency Data External App URL", type = AttributeType.STRING)
    String currencyExternalAppURL() default StringUtils.EMPTY;

    /**
     * Retrieve the Currency Data External App URL .
     *
     * @return String Currency Data External App URL .
     */
    @AttributeDefinition(name = "Currency Data External App URL", type = AttributeType.STRING)
    String roadTripScenariosExternalAppURL() default StringUtils.EMPTY;

    /**
     * Retrieve the Weather Data External App URL .
     *
     * @return String Weather Data External App URL .
     */
    @AttributeDefinition(name = "Weather Data External App URL", type = AttributeType.STRING)
    String simpleWeatherExternalAppUrl() default StringUtils.EMPTY;

    /**
     * Retrieve one call weather external app url.
     *
     * @return One call weather external app url
     */
    @AttributeDefinition(name = "One Call Weather Forecast Data External App URL", type = AttributeType.STRING)
    String oneCallWeatherExternalAppUrl();

    /**
     * Simple group call OpenWeatherMap API url.
     *
     * @return url for the group calls
     */
    @AttributeDefinition(name = "Group Call Weather Data App URL", type = AttributeType.STRING)
    String groupCallWeatherExternalAppUrl();

    /**
     * Retrieve the Dynamic Media Profile name .
     *
     * @return String Dynamic Media Profile .
     */
    @AttributeDefinition(name = "Dynamic Media Profile", type = AttributeType.STRING)
    String dynamicMediaProfile() default StringUtils.EMPTY;

    /**
     * Retrieve the Scene7 Domain.
     *
     * @return String Scene7 Domain.
     */
    @AttributeDefinition(name = "Scene7 Domain", type = AttributeType.STRING)
    String scene7Domain() default Constants.SCENE7_DOMAIN;

    /**
     * Retrieve the halayalla Domain.
     *
     * @return String halayalla Domain.
     */
    @AttributeDefinition(name = "Halayalla EndPoint", type = AttributeType.STRING)
    String halayallaEndPointUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the halayalla Domain V2 Version.
     * @return String halayalla Domain V2 Version.
     */
    @AttributeDefinition(name = "Halayalla EndPoint V2 Version", type = AttributeType.STRING)
    String halayallaEndPointUrlV2() default StringUtils.EMPTY;

    /**
     * Retrieve the halayalla Token.
     *
     * @return String halayalla Token.
     */
    @AttributeDefinition(name = "Halayalla Token", type = AttributeType.STRING)
    String halayallaToken() default StringUtils.EMPTY;

    /**
     * Retrieve the halayalla Token V2 Version.
     * @return String halayalla Token V2 Version.
     */
    @AttributeDefinition(name = "Halayalla Token V2 Version", type = AttributeType.STRING)
    String halayallaTokenV2() default StringUtils.EMPTY;

    /**
     * Retrieve the halayalla Client Key.
     *
     * @return String halayalla Client Key.
     */
    @AttributeDefinition(name = "Halayalla Client Key", type = AttributeType.STRING)
    String halayallaClientKey() default StringUtils.EMPTY;

    /**
     * Retrieve the halayalla Client Secret.
     *
     * @return String halayalla Client Secret.
     */
    @AttributeDefinition(name = "Halayalla Client Secret", type = AttributeType.STRING)
    String halayallaClientSecret() default StringUtils.EMPTY;

    /**
     * Retrieve the BaseUrl API key.
     *
     * @return String BaseUrl API key
     */
    @AttributeDefinition(name = "Domain Server Api Key", type = AttributeType.STRING)
    String domainServerUrlKey() default StringUtils.EMPTY;

    /**
     * Retrieve Package Details Path.
     *
     * @return String  Package Details Path
     */
    @AttributeDefinition(name = "Package Details Path ", type = AttributeType.STRING)
    String packageDetailsPath() default StringUtils.EMPTY;

    /**
     * Retrieve Package Details Path.
     *
     * @return String  Package Details Path
     */
    @AttributeDefinition(name = "Fav Base URL ", type = AttributeType.STRING)
    String favBaseUrl() default StringUtils.EMPTY;

    /**
     * Retrieve the  Summer Packages API EndPoint.
     *
     * @return String Summer Packages API key
     */
    @AttributeDefinition(name = "Summer Packages api Endpoint ", type = AttributeType.STRING)
    String summerPackagesApiEndpoint() default StringUtils.EMPTY;

    /**
     * Retrieve the  G Road Trip End point.
     *
     * @return String Road Trip API End point
     */
    @AttributeDefinition(name = "Road Trip API Endpoint ", type = AttributeType.STRING)
    String roadTripEndpoint() default StringUtils.EMPTY;

    /**
     * Retrieve the  Winter 2021 cards API EndPoint.
     *
     * @return String Winter 2021 cards API key
     */
    @AttributeDefinition(name = "Winter Cards Experience Categories api Endpoint ", type = AttributeType.STRING)
    String winter2021CardsApiEndpoint() default "https://hy-test.halayalla.rocks/api/vs/experiences/categories";

    /**
     * Retrieve the  Winter 2021 Experience cards API EndPoint.
     *
     * @return String Winter 2021 Experience cards API key
     */
    @AttributeDefinition(name = "Winter Cards Experience api Endpoint ", type = AttributeType.STRING)
    String winterCardsExperienceApiEndpoint() default "https://hy-test.halayalla.rocks/api/vs/experiences";

    /**
     * Exclude paths for app-locations api.
     *
     * @return String [] exludes paths of App-Locations API
     */
    @AttributeDefinition(name = "Excluding paths for app-locations api",
        description = "Add multiple excluded paths for app location api",
        type = AttributeType.STRING)
    String[] excludingPaths() default "/content/sauditourism/app1/en/app-version";

    /**
     * Retrieve the  Contact us Form end point.
     *
     * @return String Contact us Form end point
     */
    @AttributeDefinition(name = "Contact us form api Endpoint ", type = AttributeType.STRING)
    String contactUsFormEndpoint() default "https://visitsaudihelpuat-api.azurewebsites.net/api/"
      + "Conversation/CreateConversation";

    /**
     * Retrieve HY Experience domain.
     *
     * @return HY Experince Domain.
     */
    @AttributeDefinition(name = "HY Experience Domain", type = AttributeType.STRING)
    String hYExperienceDomain() default "https://www.halayalla.com/sa-";

    /**
     * Retrieve HY Encryption key.
     *
     * @return HY Encryption key.
     */
    @AttributeDefinition(name = "HY Encryption key", type = AttributeType.STRING)
    String hyEncryptionKey() default StringUtils.EMPTY;

    /**
     * Retrieve weather extended api base path.
     *
     * @return  weather extended api base path.
     */
    @AttributeDefinition(name = "Weather Extended API Base Path", type = AttributeType.STRING)
    String weatherExtendedBasePath() default "/bin/api/v1/weather-extended";

    /**
     * Retrieve the check value for enabling bridge.
     * @return Boolean Bridge
     */
    @AttributeDefinition(name = "Enable Bridge", type = AttributeType.BOOLEAN)
    boolean bridgeEnable() default false;

    /**
     * Countries configuration page.
     *
     * @return Countries configuration page
     */
    @AttributeDefinition(name = "Countries configuration page", type = AttributeType.STRING)
    String countriesConfigPath() default
        "/content/sauditourism/{0}/country-list/jcr:content/root/responsivegrid/countrylist";

    /**
     * Retrieve events API endPoint.
     *
     * @return events API endpoint
     */
    @AttributeDefinition(name = "Events api Endpoint ", type = AttributeType.STRING)
    String eventsApiEndpoint() default "/bin/api/v3/events";

    /**
     * Contact US: Retrieve message types API endPoint.
     *
     * @return message types API endPoint
     */
    @AttributeDefinition(name = "Contact US messate type api Endpoint", type = AttributeType.STRING)
    String contactUsMessageTypeApiEndpoint() default "/bin/api/contactus/messageType";

    /**
     * Contact US: Validate captcha API endPoint.
     *
     * @return Validate captcha API endPoint
     */
    @AttributeDefinition(name = "Contact US validate captcha api Endpoint", type = AttributeType.STRING)
    String contactUsValidateCaptchaApiEndpoint() default "/bin/api/contactus/validateCaptcha";

    /**
     * Dam: Flags path.
     *
     * @return dam path for flags
     */
    @AttributeDefinition(name = "Dam flags path ", type = AttributeType.STRING)
    String flagsPath() default "/content/dam/saudi-tourism/media/flags";

    /**
     * Content: Events filter page path.
     *
     * @return Events filter page path
     */
    @AttributeDefinition(name = "Events filter page path ", type = AttributeType.STRING)
    String eventsFilterPagePath() default "/content/sauditourism/en/calendar/calendar-events";

    /**
     * Retrieve the  Middleware DNS.
     *
     * @return String Middleware DNS
     */
    @AttributeDefinition(name = "Middleware DNS Host api", type = AttributeType.STRING)
    String middlewareDNS() default StringUtils.EMPTY;

    /**
     * Retrieve User Feedback API endpoint.
     *
     * @return String User Feedback API endpoint
     */
    @AttributeDefinition(name = "Middleware User Feedback API", type = AttributeType.STRING)
    String middlewareUserFeedbackEndpoint() default "/v1/ssid/feedback/feed";

    /**
     * Search endPoint API.
     *
     * @return String Search endPoint API
     */
    @AttributeDefinition(name = "Search endPoint API", type = AttributeType.STRING)
    String searchApiEndpoint() default "/bin/api/solr/search";

    /**
     * GraphQL Endpoint for Category Content Fragment.
     *
     * @return String GraphQL Endpoint for Category Content Fragment
     */
    @AttributeDefinition(name = "GraphQL Endpoint for Category Content Fragment.", type = AttributeType.STRING)
    String graphQLCategoriesEndpoint() default "/graphql/execute.jsons/sauditourism/get-categories";

    /**
     * GraphQL Endpoint for Destination Content Fragment.
     *
     * @return String GraphQL Endpoint for Destination Content Fragment
     */
    @AttributeDefinition(name = "GraphQL Endpoint for Destination Content Fragment.", type = AttributeType.STRING)
    String graphQLDestinationEndpoint() default "/graphql/execute.jsons/sauditourism/get-destinations";

    /**
     * GraphQL Endpoint for Attraction Content Fragment.
     *
     * @return String GraphQL Endpoint for Attraction Content Fragment
     */
    @AttributeDefinition(name = "GraphQL Endpoint for Attraction Content Fragment.", type = AttributeType.STRING)
    String graphQLAllAttractionsEndpoint() default "/graphql/execute.jsons/sauditourism/get-attractions";

    /**
     * GraphQL Endpoint for Attraction with filter Content Fragment.
     *
     * @return String GraphQL Endpoint for Attraction Content Fragment
     */
    @AttributeDefinition(
        name = "GraphQL Endpoint for Attraction with filter Content Fragment.",
        type = AttributeType.STRING)
    String graphQLAttractionsWithFiltersEndpoint() default
        "/graphql/execute.jsons/sauditourism/get-attractions-by-destination-and-category";

    /**
     * News Letter: Submit form API endPoint.
     *
     * @return Submit form API endPoint
     */
    @AttributeDefinition(name = "News Letter submit form api Endpoint ", type = AttributeType.STRING)
    String middlewareNewsLetterEndpoint() default "/v1/ssid/mailchimp/storeMail";

    /**
     *Destinations CF: Destinations CFs path in DAM.
     *
     * @return Destinations CFs path in DAM
     */
    @AttributeDefinition(name = "Destinations CFs path in DAM ", type = AttributeType.STRING)
    String destinationsCFPath() default "/content/dam/sauditourism/cf/{0}/destinations";

    /**
     * Seasons CF: Seasons CFs path in DAM.
     *
     * @return Seasons CFs path in DAM
     */
    @AttributeDefinition(name = "Seasons CFs path in DAM ", type = AttributeType.STRING)
    String seasonsCFPath() default "/content/dam/sauditourism/cf/{0}/seasons";

    /**
     * All Events: All Events Section Path.
     *
     * @return all events section path
     */
    @AttributeDefinition(name = "All Events Section Path", type = AttributeType.STRING)
    String getAllEventsSectionPath() default
        "/content/sauditourism/mobile/{0}/jcr:content/mobileAllEvents";

    /**
     * Holidays CF: Holidays CFs path in DAM.
     *
     * @return Holidays CFs path in DAM
     */
    @AttributeDefinition(name = "Holidays CFs path in DAM ", type = AttributeType.STRING)
    String holidaysCFPath() default "/content/dam/sauditourism/cf/{0}/holidays";

    /**
     * Events CF: Events CFs path in DAM.
     *
     * @return Events CFs path in DAM
     */
    @AttributeDefinition(name = "Events CFs path in DAM ", type = AttributeType.STRING)
    String eventsCFPath() default "/content/dam/sauditourism/cf/{0}/events";

    /**
     * Attractions CF: Attractions CFs path in DAM.
     *
     * @return Attractions CFs path in DAM
     */
    @AttributeDefinition(name = "Attractions CFs path in DAM ", type = AttributeType.STRING)
    String attractionsCFPath() default "/content/dam/sauditourism/cf/{0}/attractions";

    /**
     * Activities CF: Activities CFs path in DAM.
     *
     * @return Activities CFs path in DAM
     */
    @AttributeDefinition(name = "Activities CFs path in DAM ", type = AttributeType.STRING)
    String activitiesCFPath() default "/content/dam/sauditourism/cf/{0}/activities";

    /**
     * Tours CF: Tours CFs path in DAM.
     *
     * @return Tours CFs path in DAM
     */
    @AttributeDefinition(name = "Tours CFs path in DAM ", type = AttributeType.STRING)
    String toursCFPath() default "/content/dam/sauditourism/cf/{0}/tours";

    /**
     * Story CF: Stories CFs path in DAM.
     *
     * @return Stories CFs path in DAM
     */
    @AttributeDefinition(name = "Stories CFs path in DAM ", type = AttributeType.STRING)
    String storiesCFPath() default "/content/dam/sauditourism/cf/{0}/stories";

    /**
     * POIs CF: POIs CFs path in DAM.
     *
     * @return POIs CFs path in DAM
     */
    @AttributeDefinition(name = "POIs CFs path in DAM", type = AttributeType.STRING)
    String poisCFPath() default "/content/dam/sauditourism/cf/{0}/pois";

    /**
     * Articles CF: Articles CFs path in DAM.
     *
     * @return Articles CFs path in DAM
     */
    @AttributeDefinition(name = "Articles CFs path in DAM", type = AttributeType.STRING)
    String articlesCFPath() default "/content/dam/sauditourism/cf/{0}/articles";

    /**
     * Article Tags Path.
     *
     * @return Articles Tags Path
     */
    @AttributeDefinition(name = "Article Tags Path", type = AttributeType.STRING)
    String articleTagsPath() default "/content/cq:tags/sauditourism/article";

    /**
     * Mobile Trending.
     *
     * @return Mobile trending resource path
     */
    @AttributeDefinition(name = "Mobile Trending Resource Path", type = AttributeType.STRING)
    String trendingResourcePath() default "/content/sauditourism/mobile/{0}/jcr:content/mobileTrending";

    /**
     * Mobile Suggestions.
     *
     * @return Mobile suggestions resource path
     */
    @AttributeDefinition(name = "Mobile Suggestions Resource Path", type = AttributeType.STRING)
    String suggestionsResourcePath() default "/content/sauditourism/mobile/{0}/jcr:content/mobileSuggestions";

    /**
     * Retrieve Things ToDo API endPoint.
     *
     * @return Things ToDo API endpoint
     */
    @AttributeDefinition(name = "Things ToDo api Endpoint ", type = AttributeType.STRING)
    String thingsToDoApiEndpoint() default "/bin/api/v1/things-to-do";
    /**
     * Cat Tag path in DAM.
     *
     * @return Cat Tag path in DAM
     */
    @AttributeDefinition(name = "Category path", type = AttributeType.STRING)
    String categoriesTagsPath() default "/content/cq:tags/sauditourism/categories";


    /**
     * Categories endPoint API.
     *
     * @return String Categories endPoint API
     */
    @AttributeDefinition(name = "Categories endPoint API", type = AttributeType.STRING)
    String categoriesEndpoint() default "/bin/api/v1/categories";

    /**
     * Destinations endPoint API.
     *
     * @return String Destinations endPoint API
     */
    @AttributeDefinition(name = "Destinations endPoint API", type = AttributeType.STRING)
    String destinationsEndpoint() default "/bin/api/v1/destinations";

    /**
     * Attractions endPoint API.
     *
     * @return String Attractions endPoint API
     */
    @AttributeDefinition(name = "Attractions endPoint API", type = AttributeType.STRING)
    String attractionsEndpoint() default "/bin/api/v1/attractions";

    /**
     * Stories api Endpoint.
     *
     * @return String Stories api Endpoint
     */
    @AttributeDefinition(name = "Stories api Endpoint ", type = AttributeType.STRING)
    String storiesApiEndpoint() default "/bin/api/v1/stories";

    /**
     * FAQ categories endpoint.
     *
     * @return String FAQ categories endpoint
     */
    @AttributeDefinition(name = "FAQ categories endpoint", type = AttributeType.STRING)
    String faqCategoriesEndpoint();

    /**
     * FAQ articles endpoint.
     *
     * @return String FAQ articles endpoint
     */
    @AttributeDefinition(name = "FAQ articles endpoint", type = AttributeType.STRING)
    String faqArticlesEndpoint();

    /**
     * MuleSoft: Client ID.
     *
     * @return MuleSoft: Client ID
     */
    @AttributeDefinition(name = "MuleSoft Client ID", type = AttributeType.STRING)
    String muleSoftClientId();

    /**
     * MuleSoft: Client Secret.
     *
     * @return MuleSoft: Client Secret
     */
    @AttributeDefinition(name = "MuleSoft Client Secret", type = AttributeType.STRING)
    String muleSoftClientSecret();

    /**
     * FAQ MuleSoft categories endpoint.
     *
     * @return String FAQ categories endpoint
     */
    @AttributeDefinition(name = "FAQ MuleSoft categories endpoint", type = AttributeType.STRING)
    String muleSoftFaqCategoriesEndpoint();

    /**
     * FAQ MuleSoft articles endpoint.
     *
     * @return String FAQ articles endpoint
     */
    @AttributeDefinition(name = "FAQ MuleSoft articles endpoint", type = AttributeType.STRING)
    String muleSoftFaqArticlesEndpoint();

    /**
     * Contact US: MuleSoft Form endpoint.
     *
     * @return MuleSoft Form endpoint
     */
    @AttributeDefinition(name = "Contact US MuleSoft Form endpoint", type = AttributeType.STRING)
    String muleSoftContactUsFormEndpoint();

    /**
     * Contact US: MuleSoft MessageType endpoint.
     *
     * @return MuleSoft MessageType endpoint
     */
    @AttributeDefinition(name = "Contact US MuleSoft MessageType endpoint", type = AttributeType.STRING)
    String muleSoftContactUsMessageTypeEndpoint();

    /**
     * Mobile Item: Base Path.
     *
     * @return Mobile Item Base path
     */
    @AttributeDefinition(name = "Mobile Item Base path", type = AttributeType.STRING)
    String mobileItemBasePath() default "/content/sauditourism/mobile/{0}/items/{1}/jcr:content";

    /**
     * Mobile Section: Base Path.
     *
     * @return Mobile Section Base path
     */
    @AttributeDefinition(name = "Mobile Section Base path", type = AttributeType.STRING)
    String mobileSectionBasePath() default "/content/sauditourism/mobile/{0}/{1}/jcr:content";
  }
}
