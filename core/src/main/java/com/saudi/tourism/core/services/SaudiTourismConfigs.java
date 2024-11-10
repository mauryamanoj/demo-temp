package com.saudi.tourism.core.services;

/**
 * Interface for Saudi Tourism Configurations Service to expose only required
 * methods.
 */
public interface SaudiTourismConfigs {

  /**
   * this method should return the map API key.
   *
   * @return String map API key.
   */
  String getMapKey();
  /**
   * this method should return the google map API key.
   *
   * @return String google map API key.
   */
  String getGoogleMapsKey();
  /**
   * this method should return the show map border.
   * @return Boolean show map border.
   */
  Boolean getShowMapBorder();
  /**
   * Retrieve the Mailchimp URL.
   *
   * @return String Mailchimp URL
   */
  String getMailChimpURL();

  /**
   * Retrieve the Mailchimp Field.
   *
   * @return String Mailchimp Field
   */
  String getMailChimpField();

  /**
   * Retrieve the Currency Data external app URL .
   *
   * @return String Currency Data external app URL .
   */
  String getCurrencyExternalAppURL();

  /**
   * Retrieve the Road Trip Scenarios external app URL .
   *
   * @return String RoadTripScenarios external app URL .
   */
  String getRoadTripScenariosExternalAppURL();

  /**
   * Retrieve the Weather Data external app URL .
   *
   * @return String Weather Data external app URL .
   */
  String getSimpleWeatherExternalAppUrl();

  /**
   * One Call Weather API Url.
   *
   * @return one call weather forecast API url
   */
  String getOneCallWeatherExternalAppUrl();

  /**
   * Group Call Weather API Url.
   *
   * @return one call weather forecast API url
   */
  String getGroupCallWeatherExternalAppUrl();

  /**
   * Retrieve Dynamic Media Profile name.
   *
   * @return dynamic media profile name.
   */
  String getDynamicMediaProfile();

  /**
   * Retrieve Scene7 Domain.
   *
   * @return Scene 7 domain.
   */
  String getScene7Domain();

  /**
   * Retrieve halayallaEndPointUrl Domain.
   *
   * @return halayallaEndPointUrl domain.
   */
  String getHalayallaEndPointUrl();

  /**
   * Retrieve getHalayallaEndPointUrlV2 Version Domain.
   *
   * @return getHalayallaEndPointUrlV2 Version domain.
   */
  String getHalayallaEndPointUrlV2();


  /**
   * Retrieve HalayallaToken Domain.
   *
   * @return HalayallaToken domain.
   */
  String getHalayallaToken();

  /**
   * Retrieve HalayallaTokenV2 Domain.
   *
   * @return HalayallaTokenV2 domain.
   */
  String getHalayallaTokenV2();

  /**
   * Retrieve HalayallaClientKey Domain.
   *
   * @return HalayallaClientKey domain.
   */
  String getHalayallaClientKey();
  /**
   * Retrieve HalayallaClientSecret Domain.
   *
   * @return HalayallaClientSecret domain.
   */
  String getHalayallaClientSecret();


  /**
   * Retrieve Domain Server Url Key.
   *
   * @return Domain Server URL  Key.
   */
  String getDomainServerUrlKey();

  /**
   * Retrieve Summer Package API Endpoint.
   *
   * @return Summer Package API Endpoint .
   */
  String getSummerPackagesApiEndpoint();

  /**
   * Retrieve Winter 2021 Cards API Endpoint.
   *
   * @return Winter 2021 Cards API Endpoint .
   */
  String getWinter2021CardsApiEndpoint();

  /**
   * Retrieve Winter Experience Cards API Endpoint.
   *
   * @return Winter Experience Cards API Endpoint .
   */
  String getWinterCardsExperienceApiEndpoint();

  /**
   * Reterive Excluding paths of APP-Locations api.
   * @return Excluded Path List
   */
  String[] getExcludingPaths();

  /**
   * Retrieve Winter Experience Cards API Endpoint.
   *
   * @return Winter Experience Cards API Endpoint .
   */
  String getContactUsFormEndpoint();
  /**
   * Retrieve Road Trip API Endpoint.
   *
   * @return Road Trip API Endpoint .
   */
  String getRoadTripEndpoint();
  /**
   * Retrieve Package Details Path.
   *
   * @return  Package Details Path.
   */
  String getPackageDetailsPath();
  /**
   * Retrieve Package Details Path.
   *
   * @return  Package Details Path.
   */
  String getFavBaseUrl();
    /**
   * Retrieve HY Experience domain.
   *
   * @return  HY Experince Domain.
   */
  String getHYExperienceDomain();

  /**
   * Retrieve HY Encryption key.
   *
   * @return  HY Encryption key.
   */
  String getHyEncryptionKey();

  /**
   * Retrieve weather extended api base path.
   *
   * @return  weather extended api base path.
   */
  String getWeatherExtendedBasePath();

  /**
   *
   * @return check value of bridge .
   */
  Boolean getBridgeEnable();


  /**
   *
   * @return check value of enable favorite .
   */
  Boolean getEnableFavorite();

  /**
   * Countries configuration page.
   *
   * @return Countries configuration page
   */
  String getCountriesConfigPath();

  /**
   * Retrieve events API endPoint.
   *
   * @return events API endpoint
   */
  String getEventsApiEndpoint();

  /**
   * Contact US: Retrieve message types API endPoint.
   *
   * @return message types API endPoint
   */
  String getContactUsMessageTypeApiEndpoint();

  /**
   * Contact US: Validate captcha API endPoint.
   *
   * @return Validate captcha API endPoint
   */
  String getContactUsValidateCaptchaApiEndpoint();

  /**
   * Dam: Flags path.
   *
   * @return dam path for flags
   */
  String getFlagsPath();

  /**
   * Content: Events filter page path.
   *
   * @return Events filter page path
   */
  String getEventsFilterPagePath();

  /**
   * Middleware: middleware dns.
   *
   * @return middleware dns
   */
  String getMiddlewareDNS();

  /**
   * Middleware: middleware user feedback endpoint.
   *
   * @return middleware middleware user feedback endpoint
   */
  String getMiddlewareUserFeedbackEndpoint();

  /**
   * Search API endPoint.
   *
   * @return Search API endPoint
   */
  String getSearchApiEndpoint();

  /**
   * GraphQL Endpoint for Category Content Fragment.
   *
   * @return String GraphQL Endpoint for Category Content Fragment
   */
  String getGraphQLCategoriesEndpoint();

  /**
   * GraphQL Endpoint for Destination Content Fragment.
   *
   * @return String GraphQL Endpoint for Destination Content Fragment
   */
  String getGraphQLDestinationEndpoint();

  /**
   * GraphQL Endpoint for All Attraction Content Fragment.
   *
   * @return String GraphQL Endpoint for All Attraction Content Fragment
   */
  String getGraphQLAllAttractionsEndpoint();

  /**
   * GraphQL Endpoint for Attraction with filter Content Fragment.
   *
   * @return String GraphQL Endpoint for All Attraction Content Fragment
   */
  String getGraphQLAttractionsWithFiltersEndpoint();

  /**
   * News Letter: Submit form API endPoint.
   *
   * @return Submit form API endPoint
   */
  String getMiddlewareNewsLetterEndpoint();

  /**
   * Destinations CF: Destinations CFs path in DAM.
   *
   * @return Destinations CFs path in DAM
   */
  String getDestinationsCFPath();

  /**
   * Seasons CF: Seasons CFs path in DAM.
   *
   * @return Seasons CFs path in DAM
   */
  String getSeasonsCFPath();

  /**
   * All Events: All Events Section Path.
   *
   * @return all events section path
   */
  String getAllEventsSectionPath();

  /**
   * Holidays CF: Holidays CFs path in DAM.
   *
   * @return Holidays CFs path in DAM
   */
  String getHolidaysCFPath();

  /**
   * Events CF: Events CFs path in DAM.
   *
   * @return Events CFs path in DAM
   */
  String getEventsCFPath();

  /**
   * Attraction CF: Attractions CFs path in DAM.
   *
   * @return Attractions CFs path in DAM
   */
  String getAttractionsCFPath();

  /**
   * Activity CF: Activities CFs path in DAM.
   *
   * @return Activities CFs path in DAM
   */
  String getActivitiesCFPath();

  /**
   * Tour CF: Tours CFs path in DAM.
   *
   * @return Tours CFs path in DAM
   */
  String getToursCFPath();

  /**
   * Story CF : Stories CFs path in DAM.
   * @return Stories CFs path in DAM
   */
  String getStoriesCFPath();

  /**
   * POI CF : Stories CFs path in DAM.
   * @return POIs CFs path in DAM
   */
  String getPoisCFPath();

  /**
   * Articles CF: Articles CFs path in DAM.
   *
   * @return Articles CFs path in DAM
   */
  String getArticlesCFPath();

  /**
   * Article Tags Path.
   *
   * @return article tags path
   */
  String getArticleTagsPath();

   /** Trending: Resource Path.
   *
   * @return mobile trending resource path
   */
  String getTrendingResourcePath();

  /**
   * Suggestions: Resource Path.
   *
   * @return mobile suggestions resource path
   */
  String getSuggestionsResourcePath();

  /**
   * Retrieve Things ToDo API endPoint.
   *
   * @return Things ToDo API endpoint
   */
  String getThingsToDoApiEndpoint();

  /**
   * Retrieve Categories Tags.
   * @return Categories Tags
   */
  String getCategoriesTagsPath();

  /**
   * Endpoint for Category.
   *
   * @return String Endpoint for Category
   */
  String getCategoriesEndpoint();

  /**
   * Endpoint for Destination.
   *
   * @return String Endpoint for Destination
   */
  String getDestinationsEndpoint();

  /**
   * Endpoint for Attraction.
   *
   * @return String Endpoint for Attraction
   */
  String getAttractionsEndpoint();


  /**
   * Endpoint for Story.
   *
   * @return String Endpoint for Story
   */
  String getStoriesApiEndpoint();

  /*
   * FAQ: categories endpoint.
   *
   * @return Mulesoft FAQ categories endpoint
   */
  String getMuleSoftFaqCategoriesEndpoint();

  /**
   * FAQ: articles endpoint.
   *
   * @return Mulesoft FAQ articles endpoint
   */
  String getMuleSoftFaqArticlesEndpoint();

  /**
   * FAQ: categories endpoint.
   *
   * @return FAQ categories endpoint
   */
  String getFaqCategoriesEndpoint();

  /**
   * FAQ: articles endpoint.
   *
   * @return FAQ articles endpoint
   */
  String getFaqArticlesEndpoint();

  /**
   * MuleSoft: Client ID.
   *
   * @return MuleSoft: Client ID
   */
  String getMuleSoftClientId();

  /**
   * MuleSoft: Client Secret.
   *
   * @return MuleSoft: Client Secret
   */
  String getMuleSoftClientSecret();

  /**
   * Contact US: MuleSoft Form endpoint.
   *
   * @return MuleSoft Form endpoint
   */
  String getMuleSoftContactUsFormEndpoint();

  /**
   * Contact US: MuleSoft MessageType endpoint.
   *
   * @return MuleSoft MessageType endpoint
   */
  String getMuleSoftContactUsMessageTypeEndpoint();

  /**
   * Mobile Item: Base Path.
   *
   * @return Mobile Item Base path
   */
  String getMobileItemBasePath();

  /**
   * Mobile Section: Base Path.
   *
   * @return Mobile Section Base path
   */
  String getMobileSectionBasePath();
}
