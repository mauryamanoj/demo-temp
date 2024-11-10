package com.sta.core.solr.services;

/**
 * The interface Solr server configuration.
 */
public interface SolrServerConfiguration {

  /**
   * Gets solr url.
   *
   * @return the solr url
   */
  String getSolrUrl();

  /**
   * Gets solr core.
   *
   * @return the solr url
   */
  String getSolrCore();

  /**
   * Gets Exact match priority score.
   *
   * @return exact match priority score
   */
  String getExactMatchScore();

  /**
   * Gets Title priority score.
   *
   * @return the Title priority score
   */
  String getTitleScore();

  /**
   * Gets subtitle priority score.
   *
   * @return the subtitle priority score
   */
  String getSubtitleScore();

  /**
   * Gets TextProperties.
   *
   * @return the getTextProperties
   */
  String[] getTextProperties();

  /**
   * Gets cfTextProperties.
   *
   * @return the getCfTextProperties
   */
  String[] getCfTextProperties();

  /**
   * Gets TitleProperties.
   *
   * @return the getTitleProperties
   */
  String[] getTitleProperties();

  /**
   * Gets subtitleProperties.
   *
   * @return the getSubtitleProperties
   */
  String[] getSubtitleProperties();

  /**
   * Gets cfProperties.
   *
   * @return the getCfProperties
   */
  String[] getCfProperties();

  /**
   * Whether to use aem standard search.
   *
   * @return boolean Whether to use aem standard search
   */
  boolean isUseAEMSearch();

  /**
   * Gets app path.
   *
   * @return the app path
   */
  String getMobilePath();

  /**
   * Gets includedSearchPaths.
   *
   * @return the includedSearchPaths
   */
  String[] getAppSearchPaths();

  /**
   * Gets types.
   *
   * @return the types
   */
  String[] getTypes();

  /**
   * List of components ignored during indexing.
   *
   * @return the resource types
   */
  String[] getIgnoredResourceTypes();

  /**
   * List of app pages ignored during indexing.
   *
   * @return the resource types
   */
  String[] getAppIgnoredResourceTypes();

  /**
   * List of app pages  included during mobile indexing.
   *
   * @return the resource types
   */
  String[] getMobileIncludedResourceTypes();
}
