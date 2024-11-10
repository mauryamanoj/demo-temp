package com.sta.core.sitemap.model;


import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.time.FastDateFormat;

/**
 * Represents a single sitemap entry.
 */
@Setter
@Getter
public class SitemapEntry implements XmlEntry {

  /**
   * The sitemap datetime format.
   */
  private static final FastDateFormat DATETIME_FORMAT =
      FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mmZZ");

  /**
   * The sitemap date format.
   */
  private static final FastDateFormat DATE_FORMAT =
      FastDateFormat.getInstance("yyyy-MM-dd'T00:00'ZZ");

  /**
   * The location.
   */
  private String loc;

  /**
   * The creation date.
   */
  private Calendar created;

  /**
   * The last modification date.
   */
  private Calendar lastModified;

  /**
   * The change frequency.
   */
  private String changefreq;

  /**
   * The priority.
   */
  private Double priority;

  /**
   * The related images.
   */
  private final List<ImageEntry> images = new ArrayList<>();

  /**
   * The alternate links.
   */
  private final List<LinkEntry> links = new ArrayList<>();

  @Override public String toString() {
    return "Location: " + loc;
  }

  @Override public void toXml(StringBuilder builder) {
    if (StringUtils.isNotEmpty(getLoc())) {
      builder.append("<url>\r\n");
      builder.append("\t<loc>").append(StringEscapeUtils.escapeXml(getLoc())).append("</loc>\r\n");

      if (getLastModified() != null) {
        builder.append("\t<lastmod>").append(DATETIME_FORMAT.format(getLastModified()))
            .append("</lastmod>\r\n");
      }

      if (getCreated() != null) {
        builder.append("\t<created>").append(DATE_FORMAT.format(getCreated()))
            .append("</created>\r\n");
      }

      if (StringUtils.isNotEmpty(getChangefreq())) {
        builder.append("\t<changefreq>").append(getChangefreq()).append("</changefreq>\r\n");
      }

      if (getPriority() != null) {
        builder.append("\t<priority>").append(getPriority()).append("</priority>\r\n");
      }

      for (ImageEntry image : getImages()) {
        image.toXml(builder);
      }

      for (LinkEntry link : getLinks()) {
        link.toXml(builder);
      }

      builder.append("</url>\r\n");
    }
  }

}
