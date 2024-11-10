package com.sta.core.solr.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Object for search result.
 */
@Data
public class SolrResult implements Serializable {

  /**
   * url.
   */
  private String url;

  /**
   * title.
   */
  private String title;

  /**
   * description.
   */
  private String description;

  /**
   * featureImage.
   */
  private String featureImage;

  /**
   * region.
   */
  private String region;

  /**
   * startdate.
   */
  private String startdate;

  /**
   * enddate.
   */
  private String enddate;

  /**
   * startdate.
   */
  private String starttime;
  /**
   * enddate.
   */
  private String endtime;

  /**
   * tags.
   */
  private List<String> tags;

  /**
   * categories.
   */
  private List<String> categories;

  /**
   * type.
   */
  private transient String type;

  /**
   * type.
   */
  private transient String path;

  /**
   * language.
   */
  private transient String language;
  /**
   * Content Type Title.
   */
  private transient String contentTypeTitle;
}
