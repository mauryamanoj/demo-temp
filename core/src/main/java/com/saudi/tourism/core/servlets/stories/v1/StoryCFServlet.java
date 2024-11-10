package com.saudi.tourism.core.servlets.stories.v1;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.drew.lang.annotations.NotNull;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesRequest;
import com.saudi.tourism.core.services.stories.v1.FetchStoriesResponse;
import com.saudi.tourism.core.services.stories.v1.StoriesCFService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Convert;
import com.saudi.tourism.core.utils.RestHelper;
import com.saudi.tourism.core.utils.StatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/** Servlet to fetch Stories. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Stories Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + StoryCFServlet.SERVLET_PATH
    })
@Slf4j
public class StoryCFServlet extends SlingAllMethodsServlet {

  /** Possible paths for this servlet. */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/stories";

  /** The Stories service. */
  @Reference private transient StoriesCFService storiesCFService;

  protected void doGet(
      @NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var fetchStoriesRequest =
        new Convert<>(request, FetchStoriesRequest.class).getRequestData();

    try {

      if (StringUtils.isBlank(fetchStoriesRequest.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      final FetchStoriesResponse storyResponse =
          storiesCFService.getFilteredStories(fetchStoriesRequest);

      CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.SUCCESS.getValue(),
            RestHelper.getObjectMapper().writeValueAsString(storyResponse));

    } catch (Exception e) {
      LOGGER.error("Error fetching stories from CF", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}
