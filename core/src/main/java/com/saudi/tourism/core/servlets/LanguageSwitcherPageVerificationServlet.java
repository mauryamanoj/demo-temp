package com.saudi.tourism.core.servlets;

import com.day.cq.commons.jcr.JcrConstants;
import com.saudi.tourism.core.models.LanguageSwitchLocales;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.ResponseMessage;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MessageType;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.saudi.tourism.core.utils.PrimConstants.FORWARD_SLASH_CHARACTER;
import static com.saudi.tourism.core.utils.PrimConstants.ROOT_CONTENT_PATH;
/**
 * Language Switcher Page verification Servlet .
 */
@Component(service = Servlet.class,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Language Switcher",
        ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL
          + HttpConstants.METHOD_GET,
        ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL
          + "/bin/api/v1/langswitcher"})
@Slf4j
public class LanguageSwitcherPageVerificationServlet extends SlingAllMethodsServlet {

  /**
   * MIN_LEVELS.
   */
  public static final int MIN_LEVELS = 4;

  /**
   * LANGUAGE_PART.
   */
  public static final int LANGUAGE_PART = 3;

  protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse response)
      throws ServletException, IOException {

    /**
     * currentLanguage.
     */
    String currentLanguage;
    /**
     * targetLanguage.
     */
    String targetLanguage;
    /**
     * pagePath.
     */
    String pagePath;

    ResourceResolver resolver = request.getResourceResolver();
    if (StringUtils.isEmpty(request.getParameter("pagePath"))
        && StringUtils.isEmpty(request.getParameter("currentLanguage"))) {
      CommonUtils.writeJSON(response, StatusEnum.BAD_REQUEST.getValue(),
          new ResponseMessage(StatusEnum.BAD_REQUEST.getValue(),
          MessageType.ERROR.getType(), "Page Path , Current Language is null"));
      return;
    }
    currentLanguage = request.getParameter("currentLanguage");
    pagePath = request.getParameter("pagePath");
    Resource langRes = resolver.getResource(ROOT_CONTENT_PATH + FORWARD_SLASH_CHARACTER + "languages");
    List<String> targetLanguages = new ArrayList<>();
    if (null != langRes) {
      langRes.getChildren().forEach(lang -> {
        Resource langJcrResource = resolver.getResource(lang.getPath() + "/jcr:content");
        if (null !=  langJcrResource) {
          Link lm = langJcrResource.adaptTo(Link.class);
          if (null != lm  && StringUtils.isNotBlank(lm.getCopy())) {
            targetLanguages.add(lm.getCopy());
          }
        }
      });
    }
    List<String> nonAvailableLocales = new ArrayList<>();
    nonAvailableLocales.addAll(targetLanguages);
    if (!targetLanguages.isEmpty() && pagePath.contains(currentLanguage)) {
      boolean mainLocalePage = pagePath.endsWith(currentLanguage);
      targetLanguages.forEach(tl -> {
        String current = currentLanguage + FORWARD_SLASH_CHARACTER;
        String target = tl + FORWARD_SLASH_CHARACTER;
        if (mainLocalePage) {
          current = FORWARD_SLASH_CHARACTER + currentLanguage;
          target = FORWARD_SLASH_CHARACTER + tl;
        }
        String targetLanguagePagePath = null;
        if (!pagePath.contains(ROOT_CONTENT_PATH)) {
          targetLanguagePagePath = ROOT_CONTENT_PATH + FORWARD_SLASH_CHARACTER
            + pagePath.replace(current, target);
        } else {
          targetLanguagePagePath = pagePath.replace(current, target);
        }
        // Replace multiple slashes with a single slash
        targetLanguagePagePath = targetLanguagePagePath.replaceAll("/{2,}", FORWARD_SLASH_CHARACTER);
        // Check if targetLanguagePagePath contains "jcr:content"
        if (!targetLanguagePagePath.contains(JcrConstants.JCR_CONTENT)) {
          // Append "jcr:content" to the path
          // Check if targetLanguagePagePath ends with FORWARD_SLASH_CHARACTER
          if (targetLanguagePagePath.endsWith(FORWARD_SLASH_CHARACTER)) {
            // If it does, append JCR_CONTENT without adding another FORWARD_SLASH_CHARACTER
            targetLanguagePagePath += JcrConstants.JCR_CONTENT;
          } else {
            // If it doesn't, append JCR_CONTENT with a FORWARD_SLASH_CHARACTER
            targetLanguagePagePath += FORWARD_SLASH_CHARACTER + JcrConstants.JCR_CONTENT;
          }
        }

        if (resolver.getResource(targetLanguagePagePath) != null) {
          nonAvailableLocales.remove(tl);
        } else if (resolver.getResource(addDestinationsAfterLanguage(targetLanguagePagePath)) != null) {
          nonAvailableLocales.remove(tl);
        }
      });
      final LanguageSwitchLocales unAvailableLocalesObj = new LanguageSwitchLocales();
      unAvailableLocalesObj.setPageUnavailableLocales(nonAvailableLocales);
      CommonUtils.writeJSON(response, StatusEnum.SUCCESS.getValue(), unAvailableLocalesObj);
    } else {
      CommonUtils.writeJSON(response, StatusEnum.NOT_FOUND.getValue(),
          "languages are not authored");
    }
  }

  public static String addDestinationsAfterLanguage(String url) {
    return Optional.ofNullable(url)
      .map(path -> {
        String[] parts = path.split(FORWARD_SLASH_CHARACTER);
        if (parts.length >= MIN_LEVELS) {
          String languageCode = parts[LANGUAGE_PART];
          String destination = path.replace(languageCode + "/", languageCode + "/destinations/");
          return destination;
        } else {
          return path; // If URL is not in the expected format, return as it is
        }
      })
      .orElse("");
  }
}
