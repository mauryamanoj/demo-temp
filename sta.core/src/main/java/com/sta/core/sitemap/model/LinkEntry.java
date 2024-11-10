package com.sta.core.sitemap.model;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents an alternate link related to {@link SitemapEntry}.
 */
@Setter
@Getter
public class LinkEntry implements XmlEntry {

  /**
   * The link url.
   */
  private String href;

  /**
   * The link locale.
   */
  private String hreflang;

  @Override public String toString() {
    return "Link: " + href;
  }

  @Override public void toXml(StringBuilder builder) {
    if (StringUtils.isNotEmpty(getHref()) && StringUtils.isNotEmpty(getHreflang())) {
      builder.append("<xhtml:link rel=\"alternate\" ");
      builder.append("hreflang=\"").append(StringEscapeUtils.escapeXml(getHreflang()))
          .append("\" ");
      builder.append("href=\"").append(StringEscapeUtils.escapeXml(getHref())).append("\"/>\n");
    }
  }

}
