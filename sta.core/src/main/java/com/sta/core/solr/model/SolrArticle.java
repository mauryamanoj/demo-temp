package com.sta.core.solr.model;

import lombok.Data;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Model for indexing in Solr.
 */
@Data
public class SolrArticle {

  /**
   * id.
   */
  @Field
  private String id;

  /**
   * Url.
   */
  @Field
  private String url;

  /**
   * publishdate.
   */
  @Field
  private String publishdate;

  /**
   * language.
   */
  @Field
  private String language;

  /**
   * title.
   */
  @Field
  private String title;

  /**
   * List of suggestProperties.
   */
  @Field(value = "title_en")
  private List<String> suggestProperties;

  /**
   * List of suggestProperties.
   */
  @Field(value = "suggestion")
  private List<String> suggestKeys;

  /**
   * List of suggestProperties.
   */
  @Field(value = "source")
  private List<String> source;

  /**
   * description.
   */
  @Field
  private String description;

  /**
   * image.
   */
  @Field
  private String image;

  /**
   * type.
   */
  @Field
  private String type;

  /**
   * List of tags.
   */
  @Field
  private List<String> tags;

  /**
   * List of tags.
   */
  @Field
  private List<String> categories;

  /**
   * List of titles.
   */
  @Field
  private List<String> titles;

  /**
   * List of subtitles.
   */
  @Field
  private List<String> subtitles;

  /**
   * List of texts.
   */
  @Field
  private List<String> text;

  /**
   * priority value.
   */
  @Field
  private int priority;

  /**
   * priority value.
   */
  @Field(value = "suggest_weight")
  private double suggestWeight;

  /**
   * startdate.
   */
  @Field
  private String startdate;

  /**
   * enddate.
   */
  @Field
  private String enddate;

  /**
   * starttime.
   */
  @Field
  private String starttime;

  /**
   * endtime.
   */
  @Field
  private String endtime;

  /**
   * region.
   */
  @Field
  private String region;
}
