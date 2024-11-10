package com.sta.core.solr.services.impl;

import lombok.Getter;

import com.sta.core.solr.services.SolrServerConfiguration;

import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.saudi.tourism.core.utils.Constants.PN_TITLE;

/**
 * The Solr server configuration implementation.
 */
@Component(immediate = true,
           service = SolrServerConfiguration.class)
@Designate(ocd = SolrServerConfigurationImpl.Configuration.class)
public class SolrServerConfigurationImpl implements SolrServerConfiguration {

  /**
   * logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(SolrServerConfigurationImpl.class);

  /**
   * solrUrl.
   */
  @Getter
  private String solrUrl;

  /**
   * subtitleScore.
   */
  @Getter
  private String subtitleScore;

  /**
   * titleScore.
   */
  @Getter
  private String titleScore;

  /**
   * exactMatchScore.
   */
  @Getter
  private String exactMatchScore;

  /**
   * textProperties.
   */
  @Getter
  private String[] textProperties;

  /**
   * cfTextProperties.
   */
  @Getter
  private String[] cfTextProperties;

  /**
   * titleProperties.
   */
  @Getter
  private String[] titleProperties;

  /**
   * subtitleProperties.
   */
  @Getter
  private String[] subtitleProperties;

  /**
   * cfProperties.
   */
  @Getter
  private String[] cfProperties;

  /**
   * solrCore.
   */
  @Getter
  private String solrCore;

  /**
   * useAEMSearch.
   */
  @Getter
  private boolean useAEMSearch;

  /**
   * mobilePath.
   */
  @Getter
  private String mobilePath;

  /**
   * appSearchPaths.
   */
  @Getter
  private String[] appSearchPaths;

  /**
   * types.
   */
  @Getter
  private String[] types;

  /**
   * List of components ignored during indexing.
   */
  @Getter
  private String[] ignoredResourceTypes;

  /**
   * List of pages resourceTypes ignored during indexing.
   */
  @Getter
  private String[] appIgnoredResourceTypes;

  /**
   * List of pages resourceTypes included during mobile indexing.
   */
  @Getter
  private String[] mobileIncludedResourceTypes;

  /**
   * Configuration Class.
   */
  @ObjectClassDefinition(name = "Solr Index Configuration")
  @interface Configuration {
    /**
     * Retrieve Solr url.
     *
     * @return String Solr Url
     */
    @AttributeDefinition(name = "Solr domain URL",
                         description = "Solr domain URL",
                         type = AttributeType.STRING) String solrUrl()
        default "http://localhost:8983/solr/";

    /**
     * Retrieve Solr core.
     *
     * @return String Solr core
     */
    @AttributeDefinition(name = "Solr core",
                         description = "Solr Core",
                         type = AttributeType.STRING) String solrCore() default "aem";

    /**
     * Retrieve Title priority score.
     *
     * @return String Title priority score
     */
    @AttributeDefinition(name = "Title priority score",
                         type = AttributeType.STRING) String titleScore() default "40";

    /**
     * Retrieve the Title Properties.
     *
     * @return String Title Properties
     */
    @AttributeDefinition(name = "Title Properties",
                         type = AttributeType.STRING) String[] titleProperties() default {
                             PN_TITLE};

    /**
     * Retrieve Subtitle priority score.
     *
     * @return String Subtitle priority score
     */
    @AttributeDefinition(name = "Subtitle priority score",
                         type = AttributeType.STRING) String subtitleScore() default "30";

    /**
     * Retrieve the Subtitle Properties.
     *
     * @return String Subtitle Properties
     */
    @AttributeDefinition(name = "Subtitle Properties",
                         type = AttributeType.STRING) String[] subtitleProperties() default {
                             "subtitle"};

    /**
     * Retrieve the CF Properties.
     *
     * @return String CF Properties
     */
    @AttributeDefinition(name = "Content Fragment Properties",
      type = AttributeType.STRING) String[] cfProperties() default {
      "title"};

    /**
     * Retrieve the Text Properties.
     *
     * @return String Text Properties
     */
    @AttributeDefinition(name = "Text Properties",
                         type = AttributeType.STRING) String[] textProperties() default {
                             "text"};

    @AttributeDefinition(name = "Content Fragment Text Properties",
      type = AttributeType.STRING) String[] cfTextProperties() default {
      "text"};

    /**
     * Retrieve Exact match priority score.
     *
     * @return String Exact match priority score
     */
    @AttributeDefinition(name = "Exact match priority score",
                         type = AttributeType.STRING) String exactMatchScore() default "70";

    /**
     * Whether to use aem standard search.
     *
     * @return boolean Whether to use aem standard search
     */
    @AttributeDefinition(name = "Use aem standard search",
                         type = AttributeType.BOOLEAN) boolean useAEMSearch() default false;

    /**
     * Retrieve App path.
     *
     * @return String App path
     */
    @AttributeDefinition(name = "App path",
                         type = AttributeType.STRING)
    String mobilePath() default "";

    /**
     * Search Paths.
     * @return search paths
     */
    @AttributeDefinition(name = "Search paths",
                         description = "Paths to be searched in. Use {locale} token",
                         type = AttributeType.STRING)
    String[] appSearchPaths() default {""};

    /**
     * Search Paths.
     * @return search paths
     */
    @AttributeDefinition(name = "Page types",
                         description = "Page types",
                         type = AttributeType.STRING)
    String[] types() default {"highlight", "meet", "see"};

    /**
     * Resource types ignored for indexing.
     *
     * @return resource types
     */
    @AttributeDefinition(name = "Components ignored in search",
                         description = "Resource types ignored in search",
                         type = AttributeType.STRING) String[] ignoredResourceTypes() default {""};
    /**
     * App Resource types ignored for indexing.
     *
     * @return resource types
     */
    @AttributeDefinition(name = "App Pages ignored in search",
                          description = "App Pages Resource types ignored in search",
                          type = AttributeType.STRING) String[] appIgnoredResourceTypes() default {""};

    /**
     * App Resource types included for mobile indexing.
     *
     * @return resource types
     */
    @AttributeDefinition(name = "Web Pages included in Mobile search as auto",
        description = "Web Pages included in Mobile search as auto",
        type = AttributeType.STRING) String[] mobileIncludedResourceTypes() default {""};
  }

  /**
   * activate method.
   *
   * @param configuration Configuration
   */
  @Activate
  @Modified
  protected void activate(Configuration configuration) {
    LOG.debug("Saudi Configurations Activate/Modified");
    solrUrl = PropertiesUtil.toString(configuration.solrUrl(), "http://localhost:8983/solr/");
    textProperties = configuration.textProperties();
    cfTextProperties = configuration.cfTextProperties();
    titleProperties = configuration.titleProperties();
    titleScore = configuration.titleScore();
    subtitleScore = configuration.subtitleScore();
    subtitleProperties = configuration.subtitleProperties();
    cfProperties = configuration.cfProperties();

    exactMatchScore = configuration.exactMatchScore();
    solrCore = configuration.solrCore();
    useAEMSearch = configuration.useAEMSearch();

    mobilePath = configuration.mobilePath();
    appSearchPaths = configuration.appSearchPaths();
    types = configuration.types();
    ignoredResourceTypes = configuration.ignoredResourceTypes();
    appIgnoredResourceTypes = configuration.appIgnoredResourceTypes();
    mobileIncludedResourceTypes = configuration.mobileIncludedResourceTypes();

  }

}
