package com.saudi.tourism.core.servlets.article.v1;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import com.saudi.tourism.core.services.article.v1.ArticlesCFService;
import com.saudi.tourism.core.services.article.v1.FetchArticlesRequest;
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

import static com.saudi.tourism.core.servlets.article.v1.ArticleCFServlet.SERVLET_PATH;

/** Servlet to fetch Article by id. */
@Component(
    service = Servlet.class,
    property = {
      Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Article Servlet",
      ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
      ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + SERVLET_PATH
    })
@Slf4j
public class ArticleCFServlet extends SlingAllMethodsServlet {

  /**
   * Possible paths for this servlet.
   */
  @SuppressWarnings("java:S1075")
  public static final String SERVLET_PATH = "/bin/api/v1/article";

  /**
   * ARTICLE_ID_IS_REQUIRED.
   */
  private static final String ARTICLE_ID_IS_REQUIRED = "Article ID is required";

  /**
   * Article service.
   */
  @Reference
  private ArticlesCFService articlesCFService;

  protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {

    final var fetchArticlesRequest =
        new Convert<>(request, FetchArticlesRequest.class).getRequestData();

    if (StringUtils.isBlank(fetchArticlesRequest.getArticleId())) {
      // Handle error for missing article ID
      LOGGER.error(Constants.DO_GET_MENTHOD, ARTICLE_ID_IS_REQUIRED);
      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.BAD_REQUEST.getValue(),
          ARTICLE_ID_IS_REQUIRED);
      return;
    }

    try {
      if (StringUtils.isBlank(fetchArticlesRequest.getLocale())) {
        LOGGER.error(Constants.DO_GET_MENTHOD, Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.BAD_REQUEST.getValue(),
            Constants.ERROR_MESSAGE_ISNULL_PARAM_LOCALE);
        return;
      }

      final var model = articlesCFService.getArticle(fetchArticlesRequest);
      if (model == null) {
        CommonUtils.writeNewJSONFormat(
            response,
            StatusEnum.NOT_FOUND.getValue(),
            "Article not found");
        return;
      }
      CommonUtils.writeNewJSONFormat(
          response,
          StatusEnum.SUCCESS.getValue(),
          RestHelper.getObjectMapper().writeValueAsString(model));

    } catch (Exception e) {
      LOGGER.error("Error to fetch Articles from CF", e);
      CommonUtils.writeNewJSONFormat(
          response, StatusEnum.INTERNAL_SERVER_ERROR.getValue(), e.getLocalizedMessage());
    }
  }
}