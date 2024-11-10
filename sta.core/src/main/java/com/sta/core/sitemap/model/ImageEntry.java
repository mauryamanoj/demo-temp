package com.sta.core.sitemap.model;


import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Represents an image related to {@link SitemapEntry}.
 * Takes advantage of Google sitemap extension for images.
 */
@Getter
@Setter
public class ImageEntry implements XmlEntry {

  /**
   * The image location.
   */
  private String loc;

  /**
   * The image caption.
   */
  private String caption;

  /**
   * The image title.
   */
  private String title;


  @Override public String toString() {
    return "Image: " + loc;
  }

  @Override public void toXml(StringBuilder builder) {
    if (StringUtils.isNotEmpty(getLoc())) {
      builder.append("\t<image:image>\r\n");
      builder.append("\t\t<image:loc>").append(StringEscapeUtils.escapeXml(getLoc()))
          .append("</image:loc>\r\n");

      if (StringUtils.isNotEmpty(getTitle())) {
        builder.append("\t\t<image:title>").append(StringEscapeUtils.escapeXml(getTitle()))
            .append("</image:title>\r\n");
      }

      if (StringUtils.isNotEmpty(getCaption())) {
        builder.append("\t\t<image:caption>").append(StringEscapeUtils.escapeXml(getCaption()))
            .append("</image:caption>\r\n");
      }

      builder.append("\t</image:image>\r\n");
    }
  }

}
