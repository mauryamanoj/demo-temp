package com.saudi.tourism.core.utils;

import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Objects;
import java.util.regex.Pattern;

import static com.day.cq.commons.feed.Feed.SUFFIX_HTML;
import static com.saudi.tourism.core.utils.Constants.FORWARD_SLASH_CHARACTER;
import static com.saudi.tourism.core.utils.Constants.SOLR_APP_SOURCE;

/**
 * This class contains Link/URL utility methods.
 */
public final class LinkUtils {

  /**
   * Regex for checking internal links.
   */
  protected static final Pattern REGEX_SAUDI_PUBLIC_DOMAINS =
      Pattern.compile("^(https?:)?//(.*\\.?)visitsaudi.(com|cn)/.*");

  /**
   * private constructor because it's a utility class.
   */
  private LinkUtils() {

  }

  /**
   * This method takes the page path as parameter and appends the .html extension if required.
   *
   * @param pagePath Page Path.
   * @return String path as URL
   */
  public static String getUrlWithExtension(final String pagePath) {
    if (isInternalLink(pagePath)) {
      return pagePath + Constants.HTML_EXTENSION;
    }
    return pagePath;
  }

  /**
   * This method takes the page path as parameter and appends the .html extension if required.
   *
   * @param pagePath Page Path.
   * @return String path as URL
   */
  public static String getFavoritePath(final String pagePath) {
    if (StringUtils.isNotBlank(pagePath)) {
      if (isInternalLink(pagePath)) {
        return pagePath.substring(SaudiConstants.CONTENT_LOCALE_INDEX);
      } else if (!isExternalLink(pagePath)) {
        // for internal links in publisher  ex: /en/see-do
        return pagePath.replaceFirst("\\/[A-Za-z0-9_-]+\\/", "/");
      }
    }
    return pagePath;
  }

  /**
   * This method takes the page path as parameter and appends the .html extension if required.
   *
   * @param resolver resolver
   * @param pagePath Page Path.
   * @param publish  mode
   * @return String path as URL
   */
  @Generated
  public static String getAuthorPublishUrl(final ResourceResolver resolver,
                                           final String pagePath, final String publish) {
    if (resolver != null) {
      return getAuthorPublishUrl(resolver, pagePath, Boolean.parseBoolean(publish));
    }
    return pagePath;
  }

  /**
   * This method takes the page path as parameter and appends the .html extension or removes
   * /content/sauditourism if required.
   *
   * @param resolver resolver
   * @param pagePath page path to process
   * @param publish  {@code true} for publish mode
   * @return processed path / url
   */
  public static String getAuthorPublishUrl(final ResourceResolver resolver,
                                           final String pagePath, final boolean publish) {
    String filteredPath = pagePath;
    if (publish && isInternalLink(pagePath)) {
      filteredPath = resolver.map(pagePath);
    } else if (isInternalLink(pagePath)) {
      filteredPath = pagePath + Constants.HTML_EXTENSION;
    }
    return filteredPath;
  }

  /**
   * This method takes the image url as parameter and add the prefix to the url.
   *
   * @param resolver resolver
   * @param imagePath image path to process
   * @param publish  mode
   * @return processed path / url
   */
  public static String getAuthorPublishAssetUrl(
      final ResourceResolver resolver, final String imagePath, final boolean publish) {
    String absolutePath = imagePath;
    if (publish && isInternalAssetPath(imagePath)) {
      absolutePath = resolver.map(imagePath);
    }
    return absolutePath;
  }

  /**
   * This will for the url for app with domain..
   *
   * @param pagePath Page Path.
   * @return String path as URL
   */
  public static String getAppFormatUrl(final String pagePath) {
    String filteredPath = pagePath;
    if (Objects.nonNull(pagePath) && pagePath.startsWith(Constants.ROOT_CONTENT_PATH)) {
      filteredPath =
          Constants.VISITSAUDI_DOMAIN_COM + pagePath.replace(Constants.ROOT_CONTENT_PATH, "")
              .replace(".html", "");
    }
    return filteredPath;
  }

  /**
   * This method checks that if the provided path is Internal link or not.
   *
   * @param path link/path that needs to be tested
   * @return true: for Internal URL, and false for External URL
   */
  public static boolean isInternalLink(final String path) {
    return (Objects.nonNull(path) && !path.contains(SpecialCharConstants.DOT) && path
        .contains(SpecialCharConstants.FORWARD_SLASH + Constants.CONTENT));
  }

  /**
   * This method checks that if the provided path is Internal path from the dam or not.
   *
   * @param path link/path that needs to be tested
   * @return true: for Internal URL, and false for External URL
   */
  public static boolean isInternalAssetPath(final String path) {
    return (Objects.nonNull(path)
        && path.contains(SpecialCharConstants.DOT)
        && path.contains(
            SpecialCharConstants.FORWARD_SLASH
                + Constants.CONTENT
                + Constants.DAM));
  }

  /**
   * Get link type for caption link.
   *
   * @param link link to check
   * @return type of link: none, internal or external
   */
  public static String getLinkType(final String link) {
    if (StringUtils.isBlank(link)) {
      return Constants.TYPE_NONE;
    }

    final String linkType;
    if (StringUtils.startsWith(link, Constants.ROOT_CONTENT_PATH) || REGEX_SAUDI_PUBLIC_DOMAINS
        .matcher(link).matches()) {
      linkType = Constants.TYPE_INTERNAL;
    } else {
      linkType = Constants.TYPE_EXTERNAL;
    }
    return linkType;
  }

  /**
   * Transform URL by removing the extension and prefix content.
   *
   * @param url               String path/url to transform
   * @param isRemoveExtension boolean is Remove extension
   * @return transformed URL.
   */
  public static String transformUrl(final String url, final boolean isRemoveExtension) {

    StringBuilder transformedURL = new StringBuilder(url);

    if (isRemoveExtension && url.contains(SUFFIX_HTML)) {
      int indexOf = url.indexOf(SUFFIX_HTML);
      transformedURL.delete(indexOf, indexOf + SUFFIX_HTML.length());
    }

    if (url.contains(Constants.SITE_ROOT)) {
      transformedURL
          .delete(0, transformedURL.indexOf(FORWARD_SLASH_CHARACTER, Constants.SITE_ROOT.length()));
    }
    return transformedURL.toString();
  }

  /**
   * Get WebView URL for App. add suffix .app
   *
   * @param path   path
   * @param isPath true if passed param is path else false if url
   * @return app path for webview with .app suffix
   */
  public static String getWebViewUrlForApp(final String path, final boolean isPath) {
    if (isPath) {
      return getWebViewUrlForApp(getAppFormatUrl(path));
    }
    return getWebViewUrlForApp(path);
  }

  /**
   * Get WebView URL for App. add suffix .app
   *
   * @param url url
   * @return app path for webview with .app suffix
   */
  public static String getWebViewUrlForApp(final String url) {
    return url + Constants.DOT + SOLR_APP_SOURCE;
  }

  /**
   * This method checks that if the provided path is External link or not.
   *
   * @param path link/path that needs to be tested
   * @return true: for external URL
   */
  public static boolean isExternalLink(final String path) {
    return (Objects.nonNull(path) && (path.startsWith("http") || path.startsWith("www")));
  }

  /**
   * Get the last segment of a path.
   * @param path the full path
   * @return the last segment of the path
   */
  public static String getLastPathSegment(String path) {
    int lastIndex = path.lastIndexOf("/") + 1;
    if (lastIndex > 0 && lastIndex < path.length()) {
      return path.substring(lastIndex);
    } else {
      return path;
    }
  }

  /**
   * This method rewrites the links in the HTML content to the publish domain.
   *
   * @param html           HTML content
   * @param resolver       Resource Resolver
   * @param publishRunMode Publish Run Mode
   *
   * @return HTML content with rewritten links
   */
  public static String rewriteLinksInHtml(String html, ResourceResolver resolver, boolean publishRunMode) {
    Document doc = Jsoup.parse(html);
    for (Element link : doc.select("a[href]")) {
      String originalLink = link.attr("href");
      if (originalLink.startsWith(Constants.ROOT_CONTENT_PATH)) {
        originalLink = originalLink.replace(".html", "");
      }
      String rewrittenLink = getAuthorPublishUrl(resolver, originalLink, publishRunMode);
      link.attr("href", rewrittenLink);
    }
    return doc.body().html(); // Return inner HTML content
  }
}

