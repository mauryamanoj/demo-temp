package com.sta.core.sitemap.model;

/**
 * Represents entries that can be stored in XML.
 */
public interface XmlEntry {

  /**
   * Prints the entry information in XML format.
   *
   * @param builder the {@link StringBuilder} to print the XML to
   */
  void toXml(StringBuilder builder);

}
