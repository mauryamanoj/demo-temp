package com.saudi.tourism.core.models.components.tripplan;

import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.MessageFormat;

import static com.saudi.tourism.core.servlets.BaseAllMethodsServlet.getLanguageFromSuffix;

/**
 * Utils class for trip planner.
 */
public final class TripPlannerUtils {
  /**
   * Constructor.
   */
  private TripPlannerUtils() {
  }

  /**
   * Getting trip planner detail page url.
   * @param resolver Resolver
   * @param viewEditPagePath actual trip planner detail page url
   * @param isPublish isPublish
   * @param language language
   * @return trip planner detail page url
   */
  public static String getTripPlannerDetailPageUrl(ResourceResolver resolver, String viewEditPagePath,
                                                   boolean isPublish, String language) {
    if (StringUtils.isBlank(viewEditPagePath)) {
      viewEditPagePath = MessageFormat.format(Constants.DEFAULT_TRIP_PLAN_VIEW_EDIT_PATH, language);
    }
    return LinkUtils.getAuthorPublishUrl(resolver, viewEditPagePath, Boolean.toString(isPublish));
  }

  /**
   * Checks filter if the path property exists there, if no - extracts the parameter value from
   * request and stores it to the filter.
   *
   * @param request the current request
   * @param filter  filter to update
   * @return extracted id value
   * @noinspection DuplicatedCode
   */
  @Nullable
  public static String checkGetByPath(final @NotNull SlingHttpServletRequest request,
      @NotNull final PathLocaleFilter filter) {
    String path = filter.getPath();
    String language = filter.getLocale();
    final boolean localeParamWasBlank = StringUtils.isBlank(language);

    if (StringUtils.isBlank(path)) {
      path = request.getRequestPathInfo().getSuffix();
    }

    // Get locale from path if that wasn't passed as a param
    if (StringUtils.isNotBlank(path) && localeParamWasBlank) {
      language = getLanguageFromSuffix(path);
      filter.setLocale(language);
    }

    if (StringUtils.isNotBlank(path)) {
      // If path is specified for another locale - fix it
      if (!localeParamWasBlank && !StringUtils.startsWith(path,
          Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language
              + Constants.FORWARD_SLASH_CHARACTER)) {
        path = CommonUtils.toAbsolutePath(path, language);
      } else {
        path = CommonUtils.toAbsolutePath(path);
      }

      // Update path in filter to be absolute path
      filter.setPath(path);
    }

    return path;
  }
}
