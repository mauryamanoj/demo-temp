package com.sta.core.sitemap;

import com.sta.core.sitemap.model.XmlEntry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * The class responsible for saving sitemap files into repository.
 */
@Slf4j
public class SitemapWriter {

  /**
   * The limit of entries per single sitemap file.
   */
  private static final int MAX_ENTRIES_PER_FILE = 50_000;

  /**
   * The sitemap file root element name.
   */
  private String rootElementName;

  /**
   * The sitemap file output path.
   */
  private String outputPath;

  /**
   * The resource resolver.
   */
  private ResourceResolver resolver;

  /**
   * The list of sitemap entries.
   */
  private final List<XmlEntry> entries = new ArrayList<>();

  /**
   * The list of created sitemap files.
   */
  private final List<String> files = new ArrayList<>();

  /**
   * The total number of sitemap entries (across all files).
   */
  private int totalNumberOfEntries;

  /**
   * The string used to store the sitemap file content before saving it to
   * repository.
   */
  private final StringBuilder sitemapFileContent = new StringBuilder();

  /**
   * The public constructor.
   *
   * @param outputPath the output path
   * @param resolver   the resource resolver
   */
  public SitemapWriter(String outputPath, ResourceResolver resolver) {
    this(null, outputPath, resolver);
  }

  /**
   * The protected constructor.
   *
   * @param rootElementName the XML root element name
   * @param outputPath      the output path
   * @param resolver        the resource resolver
   */
  protected SitemapWriter(String rootElementName, String outputPath, ResourceResolver resolver) {
    this.rootElementName = StringUtils.defaultIfEmpty(rootElementName, "urlset");
    this.outputPath = outputPath;
    this.resolver = resolver;
  }

  /**
   * Adds entries to the sitemap.
   *
   * @param entry the entries to add
   */
  public void addEntries(XmlEntry entry) {
    if (entry != null) {
      addEntry(entry);
    }
  }

  /**
   * Add new entry to the sitemap.
   *
   * @param entry the sitemap entry
   */
  public void addEntry(XmlEntry entry) {
    entries.add(entry);
    totalNumberOfEntries++;
    if (entries.size() >= MAX_ENTRIES_PER_FILE) {
      flushSitemap(false);
    }
  }

  /**
   * Getter for entries.
   *
   * @return xml entries for the sitemap
   */
  public List<XmlEntry> getEntries() {
    return entries;
  }

  /**
   * Saves the entries (not yet flushed) to file.
   *
   * @param lastFlush whether it is a last flush or internal flush due to exceeding
   *          sitemap entry limit
   */
  public void flushSitemap(boolean lastFlush) {
    if (!entries.isEmpty()) {
      final String filePath = outputPath + "/sitemap" + ".xml";

      sitemapFileContent.setLength(0);
      sitemapFileContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
      sitemapFileContent.append("<").append(rootElementName)
          .append(" xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"");
      sitemapFileContent.append(" xmlns:image=\"http://www.google.com/schemas/sitemap-image/1.1\"");
      sitemapFileContent.append(" xmlns:video=\"http://www.google.com/schemas/sitemap-video/1.1\"");
      sitemapFileContent.append(" xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"");
      sitemapFileContent.append(">\r\n");

      for (XmlEntry entry : entries) {
        entry.toXml(sitemapFileContent);
      }

      sitemapFileContent.append("</").append(rootElementName).append(">\r\n");

      try {
        if (Objects.nonNull(resolver)) {
          final Session session = resolver.adaptTo(Session.class);

          @SuppressWarnings("squid:S2259")
          Node folderNode = session.getNode(StringUtils.substringBeforeLast(filePath, "/"));
          writeFile(filePath, folderNode);
          session.save();
          files.add(filePath);
        } else {
          LOGGER.debug("ResourceResolver is NULL");
        }

      } catch (RepositoryException e) {
        LOGGER.error("Unable to save sitemap to the file {}", filePath, e);
      } finally {
        entries.clear();
      }
    }
  }

  /**
   * Write file.
   *
   * @param filePath   the file path
   * @param folderNode the folder node
   * @throws RepositoryException the repository exception
   */
  private void writeFile(String filePath, Node folderNode) throws RepositoryException {
    try {
      JcrUtils.putFile(folderNode, StringUtils.substringAfterLast(filePath, "/"), "application/xml",
          IOUtils.toInputStream(sitemapFileContent.toString(), StandardCharsets.UTF_8));
    } catch (Exception e) {
      LOGGER.error("Error {}", e);
    }
  }

  /**
   * Gets the paths to created sitemap files.
   *
   * @return the path to sitemap files
   */
  public List<String> getFiles() {
    return files;
  }

  /**
   * Gets the total number of outputted sitemap entries.
   *
   * @return the number of appended entries
   */
  public int getTotalNumberOfEntries() {
    return totalNumberOfEntries;
  }

}
