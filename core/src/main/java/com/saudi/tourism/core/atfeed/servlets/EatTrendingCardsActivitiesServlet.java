/**
 *
 */
package com.saudi.tourism.core.atfeed.servlets;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.xml.ws.http.HTTPException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.ServletResolverConstants;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.Externalizer;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.utils.AIRCsvExporter;
import com.saudi.tourism.core.models.components.bigslider.v1.BigSlide;
import com.saudi.tourism.core.atfeed.services.GeneralQueryService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

/**
 * This is used to get trending cards from the node. Sample URL :
 * http://localhost:4502/bin/api/air/v1/trending
 */
@Component(service = Servlet.class, property = {
    Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "EatTrending feed servlet",
    ServletResolverConstants.SLING_SERVLET_METHODS + Constants.EQUAL + HttpConstants.METHOD_GET,
    ServletResolverConstants.SLING_SERVLET_PATHS + Constants.EQUAL + "/bin/api/air/v1/eattrending",
    ServletResolverConstants.SLING_SERVLET_EXTENSIONS + Constants.EQUAL + "csv" })

@Slf4j
public class EatTrendingCardsActivitiesServlet extends AbstractATFeedServlet {

  private static final long serialVersionUID = 20L;
  /** Nested folder path. */
  private static final String NESTED_FOLDER_PATH = "eat";
  /**Discover regions category. */
  private static final String TRENDING_CATEGORY = "foodanddrink";
  /**CATEGORY. */
  private static final String CATEGORY = "category";
  /**EN_EAT. */
  private static final String EN_EAT = "/en/eat/";

  /**
   * The service for obtaining resource resolver.
   */
  @Reference
  private UserService resolverService;
  /**
   * The Query builder.
   */
  @Reference
  private QueryBuilder queryBuilder;

  /**
   * The Activities Service.
   */
  @Reference
  private GeneralQueryService generalQueryService;

  /**
   * The Settings Service.
   */
  @Reference
  private SlingSettingsService settingsService;

  /**
   * The Region City Service.
   */
  @Reference
  private RegionCityService regionCityService;

  /**
   * The Configurations.
   */
  @Reference
  private SaudiTourismConfigs saudiTourismConfigs;

  /**
   * Localization bundle provider.
   */
  @Reference(target = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  @Override
protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response)
      throws ServletException, IOException {
    try {
      String locale = generalQueryService.getLocale(request);
      String searchLocation = getSearchLocation(locale, NESTED_FOLDER_PATH);
      SearchResult searchResult = generalQueryService.findRegionModel(request, searchLocation);
      String csv = getEatTrendingCSV(request, searchResult);
      OutputStream outputStream = response.getOutputStream();
      response.setContentType(AIRConstants.CSV_CONTENT_TYPE);
      response.setHeader(AIRConstants.DOWNLOAD_RESPONSE_HEADER, AIRConstants.FEEDS_RESPONSE_HEADER);
      outputStream.write(csv.getBytes("UTF-8"));
      outputStream.flush();
      outputStream.close();
      LOGGER.debug("Success :{}, {}", "TrendingFeedServlet", response);

    } catch (HTTPException h) {
      LOGGER.error("HTTP Exception Error", h.getLocalizedMessage());
    } catch (Exception e) {
      LOGGER.error("Error in servlet :{}, {}", "TrendingFeedServlet", e.getLocalizedMessage());
    }
  }

  /**
   * Save Entities data in csv format.
   *
   * @param request      SlingHTTPServletRequest
   * @param searchResult Nodes
   * @return Entities in CSV
   */
  private String getEatTrendingCSV(SlingHttpServletRequest request, SearchResult searchResult) {
    String csv = StringUtils.EMPTY;
    Iterator<Resource> regionResources = searchResult.getResources();
    List<String> paths = new ArrayList<String>();
    String lang = CommonUtils.getLanguageForPath(request.getRequestURI());
    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(lang));
    boolean isPublish = settingsService.getRunModes().contains(Externalizer.PUBLISH);
    while (regionResources.hasNext()) {
      paths.add(regionResources.next().getPath());
    }
    String[] pathsArr = paths.toArray(new String[0]);
    Stream<String> stream = Optional.ofNullable(pathsArr).map(Arrays::stream).orElse(Stream.empty());
    List<BigSlide> slides = stream.map(path -> BigSlide
        .ofCategory(path, regionCityService, request.getResourceResolver(), isPublish, i18n, null))
        .filter(Objects::nonNull).collect(Collectors.toList());
    csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region");
    csv = StringUtils.join(csv, System.lineSeparator());
    csv = StringUtils.join(csv, exportSlidesToCsv(slides, request));
    return csv;
  }

  /**
   * Add Bigslider model into csv file..
   * @param slides List<bigSlide>
   * @param request        Host of requestTt
   * @return CSV File with value
   */
  private String exportSlidesToCsv(@NotNull List<BigSlide> slides, SlingHttpServletRequest request) {
    String csvLine = StringUtils.EMPTY;
    List<Entity> entityList = new ArrayList<Entity>();
    Iterator<BigSlide> slideIterator = slides.iterator();
    while (slideIterator.hasNext()) {
      BigSlide slideModel = slideIterator.next();
      Entity entity = new Entity();
      if (null != slideModel.getLink().getUrl()) {
        String cityURL = slideModel.getLink().getUrl();
        cityURL = cityURL.replaceAll(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(EN_EAT, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(Constants.HTML_EXTENSION, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(Constants.FORWARD_SLASH_CHARACTER, AIRConstants.DASH);
        entity.setId(cityURL);
      } else {
        entity.setId(ComponentUtils.generateId(TRENDING_CATEGORY, slideModel.getTitle()
            .replace(Constants.HTML_EXTENSION, StringUtils.EMPTY).replace(EN_EAT, StringUtils.EMPTY)
             .replaceAll(Constants.FORWARD_SLASH_CHARACTER, AIRConstants.DASH)));
      }
      entity.setName(slideModel.getTitle());
      entity.setCategoryId(TRENDING_CATEGORY);
      String message = slideModel.getDescription();
      if (StringUtil.isEmpty(message)) {
        entity.setMessage(message);
      } else {
        entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
      }
      if (null != slideModel.getImage()) {
        if (null != slideModel.getImage().getFileReference()) {
          entity.setThumbnailUrl(setHost(slideModel.getImage().getFileReference(), getHost(request)));
        }
        if (null != slideModel.getImage().getS7fileReference()) {
          entity.setThumbnailS7Url(setHost(slideModel.getImage().getS7fileReference(), getHost(request)));
        }
      }
      if (null != slideModel.getLink()) {
        entity.setCtaURL(slideModel.getLink().getUrl().replace(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY)
             .replaceAll(Constants.HTML_EXTENSION, StringUtils.EMPTY));
      }
      if (slideModel.getParameters().containsKey(CATEGORY)) {
        String[] tags = slideModel.getParameters().get(CATEGORY).toString().split(AIRConstants.DEFAULT_SEPARATOR);
        List<String> tagsFromCat = Arrays.asList(tags);
        entity.setInterestTags(tagsFromCat);
      }
      if (slideModel.getParameters().containsKey(AIRConstants.REGION)
          && null != slideModel.getParameters().get(AIRConstants.REGION)) {
        entity.setArea(slideModel.getParameters().get(AIRConstants.REGION).toString());
      }
      entityList.add(entity);
    }

    LOGGER.info(String.valueOf(entityList));
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (String tag : Optional.ofNullable(en.getInterestTags()).orElse(Collections.emptyList())) {
        for (Entry<String, List<String>> entry : data.entrySet()) {
          if (entry.getValue().contains(tag.trim())) {
            interests.add(entry.getKey());
          }
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getName()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getThumbnailUrl()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaURL());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(en.getInterestTags()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(interestTags));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getArea());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return csvLine;
  }

}

