package com.saudi.tourism.core.listeners;

import com.day.cq.commons.Language;
import com.day.cq.wcm.api.LanguageManager;
import com.saudi.tourism.core.services.PlaceholderService;
import com.saudi.tourism.core.utils.Constants;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.saudi.tourism.core.utils.SpecialCharConstants.FORWARD_SLASH;

/**
 * PlaceholderFilter reads placeholders from conf placeholder
 * page(/content/saudi/global-configs/Configs/admin/placeholder) and replaces
 * them in html source from {{key:text}} to text and popup html span.
 */
@Component(service = Filter.class, property = {Constants.SERVICE_DESCRIPTION
    + "=SaudiTourism UpdatedPlaceholersFilter to update placeholder values for incoming requests",
    EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
    EngineConstants.SLING_FILTER_PATTERN + "=" + Constants.ROOT_CONTENT_PATH + "/.*",
    "service.ranking:Integer=10000"

}) public class PlaceholderFilter implements Filter {

  /**
   * The constant PLACEHOLDER_START.
   */
  private static final String PLACEHOLDER_START = "[{";
  /**
   * The constant PLACEHOLDER_END.
   */
  private static final String PLACEHOLDER_END = "}]";
  /**
   * The constant PLACEHOLDER_REGEX.
   */
  private static final String PLACEHOLDER_REGEX = "\\[\\{(.*?)\\}\\]";
  /**
   * The constant DEFAULT_LOCALE_PAGE_PATH.
   */
  private static final String DEFAULT_LOCALE_PAGE_PATH
          = Constants.ROOT_CONTENT_PATH + FORWARD_SLASH + Constants.DEFAULT_LOCALE;

  /**
   * The Filter config.
   */
  @SuppressWarnings("overideproperty")
  private FilterConfig filterConfig;

  /**
   * The Language Manager.
   */
  @Reference
  private LanguageManager languageManager;

  /**
   * The Placeholder Service.
   */
  @Reference
  private PlaceholderService placeholderService;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }

  /**
   * the destroy method.
   */
  @Override
  public void destroy() {
    filterConfig = null;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
          throws IOException, ServletException {

    SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
    Resource requestResource = slingRequest.getResource();
    String path = slingRequest.getResource().getPath();
    String extension = FilenameUtils.getExtension(URLEncoder.encode(path));

    if (request instanceof SlingHttpServletRequest
            && path.startsWith(Constants.ROOT_CONTENT_PATH)
            && ("html".equals(extension) || "".equals(extension))) {

      CharResponseWrapper wrappedResponse = new CharResponseWrapper((HttpServletResponse) response);
      chain.doFilter(request, wrappedResponse);

      String content = wrappedResponse.toString();

      if (isHTMLContent(content) && isPublicPath(path, requestResource.getResourceResolver())) {

        updatePlaceholders(response, content, requestResource);

      } else {
        response.getOutputStream().write(content.getBytes());
      }

    } else {
      chain.doFilter(request, response);
    }

  }

  /**
   * Check if resource content string is of HTML type.
   *
   * @param content the value of content
   * @return the boolean
   */
  private boolean isHTMLContent(String content) {
    return content.contains("<html");
  }

  /**
   * Check if path belongs to public page by checking if valid language is present in path.
   *
   * @param path the value of path
   * @param resourceResolver the value of resourceResolver
   * @return the boolean
   */
  private boolean isPublicPath(String path, ResourceResolver resourceResolver) {
    // check path has atleast 3 levels
    // (/content/sauditourism/{language})

    String pageLanguageCode = path.split("/")[Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION];

    List<Language> siteLanguages = (List<Language>) languageManager.getCqLanguages(
            resourceResolver,
            DEFAULT_LOCALE_PAGE_PATH);

    return siteLanguages.stream()
            .map(Language::getLanguageCode)
            .anyMatch(pageLanguageCode::equals);
  }

  /**
   * Read all placeholders from placeholder node at
   * /content/saudi/{language}/Configs/admin/jcr:content/placeholder/items and create a map from it.
   * Use that map to replace them in source where ever {{key:text}} in source.
   *
   * @param response the response
   * @param htmlContent the html content
   * @param resource the resource
   * @throws IOException the io exception
   */
  private void updatePlaceholders(ServletResponse response, String htmlContent,
          Resource resource) throws IOException {

    String resourceLanguage = languageManager
            .getCqLanguage(resource)
            .getLanguageCode();

    Map<String, String> placeholderMap = placeholderService.getKeyTranslationMapByLanguage(
            resourceLanguage,
            resource.getResourceResolver());

    String htmlContentTemp = updatePlaceholders(htmlContent, placeholderMap);
    if (htmlContentTemp != null) {
      response.getOutputStream().write(htmlContentTemp.getBytes());
    }
  }

  /**
   * CharResponseWrapper is used to warp the HttpServletResponse and get the dom html in Bytes.
   */
  public final class CharResponseWrapper extends HttpServletResponseWrapper {

    /**
     * The Sw.
     */
    private StringWriter sw = new StringWriter();

    /**
     * Instantiates a new Char response wrapper.
     *
     * @param response the response
     */
    private CharResponseWrapper(HttpServletResponse response) {
      super(response);
    }

    /**
     * @return new PrintWriter obj.
     * @throws IOException IOException.
     */
    @Override
    public PrintWriter getWriter() throws IOException {
      return new PrintWriter(sw);
    }

    /**
     * @return toString of sw.
     */
    @Override
    public String toString() {
      return sw.toString();
    }
  }

  /**
   * Replaces are all {{<key>:text}} with value present in placeholder page of that locale.
   *
   * @param text the text
   * @param placeholderMap the placeholder map
   * @return text string
   */
  private String updatePlaceholders(String text, Map<String, String> placeholderMap) {

    if (text != null && text.contains(PLACEHOLDER_START) && null != placeholderMap
            && placeholderMap.size() > 0) {
      Pattern pattern = Pattern.compile(PLACEHOLDER_REGEX);
      Matcher matcher = pattern.matcher(text);
      while (matcher.find()) {
        String matchItem = matcher.group();
        String[] keySet = matchItem.replace(PLACEHOLDER_START, StringUtils.EMPTY)
                .replace(PLACEHOLDER_END, StringUtils.EMPTY)
                .split(Constants.COLON_SLASH_CHARACTER);
        String placeholderKey = keySet[0];
        text = replaceKeyValue(text, placeholderMap, matchItem, placeholderKey);

      }
    }

    return text;
  }

  /**
   * replace the text value from keySet.
   *
   * @param text the text
   * @param placeholderMap the placeholder map
   * @param matchItem the match item
   * @param placeholderKey the placeholder key
   * @return string
   */
  private String replaceKeyValue(String text, Map<String, String> placeholderMap, String matchItem,
          String placeholderKey) {
    if (placeholderMap.containsKey(placeholderKey)) { // when key configured in placeholder page
      text = text.replace(matchItem, placeholderMap.get(placeholderKey));
    }
    return text;
  }
}
