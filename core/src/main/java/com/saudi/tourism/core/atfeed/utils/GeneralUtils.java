package com.saudi.tourism.core.atfeed.utils;


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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.eclipse.jetty.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import com.adobe.cq.wcm.core.components.util.ComponentUtils;
import com.day.cq.commons.Externalizer;
import com.day.cq.search.result.SearchResult;
import com.saudi.tourism.core.atfeed.models.Entity;
import com.saudi.tourism.core.atfeed.servlets.AbstractATFeedServlet;
import com.saudi.tourism.core.models.components.TextCardsModel;
import com.saudi.tourism.core.models.components.bigslider.v1.BigSlide;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.models.components.hotels.HotelDetail;
import com.saudi.tourism.core.models.components.hotels.HotelsSpecialOffers;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.utils.AIRConstants;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;

import lombok.extern.slf4j.Slf4j;
/**
 * The Class CommonUtils.
 */
@Slf4j

public final class GeneralUtils extends AbstractATFeedServlet {

  /** Static category of hotel feed. */
  public static final String PLAN_YOUR_TRIP = "PlanYourTrip";

  /**Discover regions category. */
  private static final String TRENDING_CATEGORY = "trending";
  /**EN_DO. */
  private static final String EN_DO = "/en/do/";
  /**CATEGORY.*/
  private static final String CATEGORY = "category";

  /** Name of car-rentals folder. */
  public static final String CAR_RENTALS = "car-rentals";
  /** Name of Partners_Promotions folder. */
  public static final String PARTNERS_PROMOTIONS = "Partners_Promotions";
  /** Category of car deals. */
  public static final String CAR_RENTALS_CATEGORY = "CarRentals";
  /** Category of partners promotions deals. */
  public static final String PARTNER_PROMOTIONS_CATEGORY  = "PartnersPromotions";
  /** SPACES_TO_CHECK to check. */
  public static final String SPACES_TO_CHECK = "(?m)^\\s*\\r?\\n|\\r?\\n\\s*(?!.*\\r?\\n)";
/**
    * Instantiates a new CsvExporter.
    */
  private GeneralUtils() {
  }

  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getAttractionFeedsCSV(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      String description = (StringEscapeUtils.escapeCsv(en.getMessage().replace(AIRConstants.OPEN_P_TAG,
           StringUtils.EMPTY).replace(AIRConstants.CLOSE_P_TAG, StringUtils.EMPTY)));
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(description);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(interestTags));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getRegion());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }

    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getAviationFeedsCSV(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    if (StringUtils.isNotBlank(csv)) {
      csv = StringUtils.join(csv, ",entity.copy,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url,entity.language");
      String csvLine = StringUtils.EMPTY;
      LOGGER.info(String.valueOf(entityList));
      csv = StringUtils.join(csv, System.lineSeparator());
      Map<String, List<String>> data = getTagsInterestMapping();
      for (Entity en : entityList) {
        Set<String> interests = new HashSet<String>();
        for (Entry<String, List<String>> entry : data.entrySet()) {
          if (entry.getValue().contains(en.getTags())) {
            interests.add(entry.getKey());
          }
        }
        List<String> interestTags = new ArrayList<String>();
        if (interests.size() > 0) {
          interestTags.addAll(interests);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(en.getId());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getName());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCategoryId());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getMessage());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getValue());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getPageUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getInventory());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getMargin());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailS7Url());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getTags());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getInterests());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCustom1());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailS7Url());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCustom2());
        String line = sb.toString();
        csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
      }
      csv = StringUtils.join(csv, csvLine);
    }
    return  csv;
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getCarFeedsCSV(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    if (StringUtils.isNotBlank(csv)) {
      csv = StringUtils.join(csv, ",entity.copy,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url,entity.language");
      String csvLine = StringUtils.EMPTY;
      LOGGER.info(String.valueOf(entityList));
      csv = StringUtils.join(csv, System.lineSeparator());
      Map<String, List<String>> data = getTagsInterestMapping();
      for (Entity en : entityList) {
        Set<String> interests = new HashSet<String>();
        for (Entry<String, List<String>> entry : data.entrySet()) {
          if (entry.getValue().contains(en.getTags())) {
            interests.add(entry.getKey());
          }
        }
        List<String> interestTags = new ArrayList<String>();
        if (interests.size() > 0) {
          interestTags.addAll(interests);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(en.getId());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getName());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCategoryId());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getMessage());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getValue());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getPageUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getInventory());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getMargin());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailS7Url());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getTags());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getInterests());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCustom1());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailUrl());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getThumbnailS7Url());
        sb.append(AIRConstants.DEFAULT_SEPARATOR);
        sb.append(en.getCustom2());
        String line = sb.toString();
        csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
      }
      csv = StringUtils.join(csv, csvLine);
    }
    return  csv;
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getCreateEventFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.path,entity.city");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getName()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getBrand());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getCreateItinerariesFeeds(SlingHttpServletRequest request, List<Entity> entityList,
        String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.path,entity.city,entity.pathId,entity.language");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getName()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getBrand());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom1());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom2());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getFoodAndDrinkFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region,entity.favoritePageURL");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getRegion());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getGeneralFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region,entity.city");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getName()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getBrand());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getGuidesFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(interestTags));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getRegion());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getHotelFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.copy,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url,entity.language");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom1());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom2());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }


  /**
   * Save Entities data in csv format.
   * @param request SlingHTTPServletRequest
   * @param searchResult Nodes
   * @return Entities in CSV
   */
  public static String getHotelsFeedsCSV(SlingHttpServletRequest request, SearchResult searchResult) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.ctaText,entity.brand,entity.city");
    csv = StringUtils.join(csv, System.lineSeparator());
    Iterator<Resource> hotelResources = searchResult.getResources();
    String csvLine = StringUtils.EMPTY;
    List<Entity> entityList = getEntities(hotelResources, getHost(request));
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getName()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getMessage()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getThumbnailUrl()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getPageUrl()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(en.getThumbnailS7Url()));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getBrand());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getArea());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return StringUtils.join(csv, csvLine);
  }

  /**
   * Get List of entities.
   * @param resource Hotel Resources
   * @param host Host of request
   * @return List
   */
  @NotNull
  public static List<Entity> getEntities(Iterator<Resource> resource, String host) {
    List<Entity> entityList = new ArrayList<Entity>();

    while (resource.hasNext()) {
      HotelDetail hotelDetail = resource.next().adaptTo(HotelDetail.class);
      if (hotelDetail != null) {
        HotelsSpecialOffers specialOffers = hotelDetail.getSpecialOffers();
        Entity entity = new Entity();
        entity.setId(hotelDetail.getId().replaceAll(AIRConstants.SLASH, AIRConstants.DASH));
        entity.setName(hotelDetail.getHotelName());
        entity.setCategoryId(PLAN_YOUR_TRIP);
        entity.setBrand(getBrand(hotelDetail));
        entity.setPageUrl(hotelDetail.getCtaUrl());
        if (null != hotelDetail.getHotelImage()) {
          entity.setThumbnailUrl(setHost(hotelDetail.getHotelImage(), host));
        } else {
          entity.setThumbnailUrl(AIRConstants.NULL);
        }
        entity.setArea(getCity(hotelDetail));
        entity.setCtaText(hotelDetail.getCtaText());
        entity.setCtaURL(hotelDetail.getCtaUrl());
        if (null != hotelDetail.getHotelImageAltText()) {
          entity.setThumbnailUrlText(hotelDetail.getHotelImageAltText());
        } else {
          entity.setThumbnailUrlText(hotelDetail.getHotelName());
        }
        if (null != specialOffers) {
          if (specialOffers.getDetails() != null) {
            String[] description =  hotelDetail.getSpecialOffers().getDetails();
            String formatedDescription = AIRConstants.LEFT_SQUARE_BRACKET;
            for (String str : description) {
              formatedDescription  += AIRConstants.DOUBLE_QUOTES.concat(str).concat(AIRConstants.DOUBLE_QUOTES)
                  .concat(AIRConstants.DEFAULT_SEPARATOR);
            }
            formatedDescription = formatedDescription.replaceAll(",$", StringUtils.EMPTY);
            formatedDescription += AIRConstants.RIGHT_SQUARE_BRACKET;
            entity.setMessage((formatedDescription));
          } else {
            entity.setMessage(AIRConstants.NULL);
          }
        } else {
          entity.setMessage(AIRConstants.NULL);
        }
        entityList.add(entity);
      }
    }
    return entityList;
  }

  /**
   * Get brand.
   * @param hotelDetail HotelDetail
   * @return String
   */
  public static String getBrand(HotelDetail hotelDetail) {
    if (hotelDetail.getChain() != null) {
      return  hotelDetail.getChain()[0].split(AIRConstants.SLASH)[1];
    }
    return StringUtils.EMPTY;
  }

  /**
   * Get City.
   * @param hotelDetail HotelDetail
   * @return String
   */
  public static String getCity(HotelDetail hotelDetail) {
    if (hotelDetail.getHotelCity() != null) {
      return  hotelDetail.getHotelCity()[0].split("/")[1];
    }
    return StringUtils.EMPTY;
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getItinerariesFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    Map<String, List<String>> data = getTagsInterestMapping();
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      String description = (en.getMessage().replace(AIRConstants.OPEN_P_TAG, StringUtils.EMPTY)
          .replace(AIRConstants.CLOSE_P_TAG, StringUtils.EMPTY));
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(description);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(StringEscapeUtils.escapeCsv(Entity.getStringFromArray(en.getTags())));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(interestTags));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getRegion());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }

    return StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getPackageFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.region,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url,"
              + "entity.language");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getRegion());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom1());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return  StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getPartnerPromotionFeeds(SlingHttpServletRequest request, List<Entity> entityList,
       String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.copy,entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url,entity.language");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    Map<String, List<String>> data = getTagsInterestMapping();
    for (Entity en : entityList) {
      Set<String> interests = new HashSet<String>();
      for (Entry<String, List<String>> entry : data.entrySet()) {
        if (entry.getValue().contains(en.getTags())) {
          interests.add(entry.getKey());
        }
      }
      List<String> interestTags = new ArrayList<String>();
      if (interests.size() > 0) {
        interestTags.addAll(interests);
      }
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom1());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCustom2());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return  StringUtils.join(csv, csvLine);
  }


  /**
   * Get feeds information saved in csv format.
   * @param request SlingHttpRequest
   * @param entityList List of entities
   * @param locale locale
   * @return CSVString
   */
  public static String getTransportFeeds(SlingHttpServletRequest request, List<Entity> entityList, String locale) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.mobileThumbnailUrl,entity.mobileThumbnailS7Url");
    String csvLine = StringUtils.EMPTY;
    LOGGER.info(String.valueOf(entityList));
    csv = StringUtils.join(csv, System.lineSeparator());
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getValue());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInventory());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMargin());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getTags());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getInterests());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return  StringUtils.join(csv, csvLine);
  }


  /**
   * Save Entities data in csv format.
   * @param i18nProvider to get local
   * @param settingsService
   * @param regionCityService
   * @param request      SlingHTTPServletRequest
   * @param searchResult Nodes
   * @return Entities in CSV
   */
  public static String getFeedsCSV(SlingHttpServletRequest request, SearchResult searchResult,
        ResourceBundleProvider i18nProvider, SlingSettingsService settingsService,
        RegionCityService regionCityService) {
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
  private static String exportSlidesToCsv(@NotNull List<BigSlide> slides, SlingHttpServletRequest request) {
    String csvLine = StringUtils.EMPTY;
    List<Entity> entityList = new ArrayList<Entity>();
    Iterator<BigSlide> slideIterator = slides.iterator();
    while (slideIterator.hasNext()) {
      BigSlide slideModel = slideIterator.next();
      Entity entity = new Entity();
      if (null != slideModel.getLink().getUrl()) {
        String cityURL = slideModel.getLink().getUrl();
        cityURL = cityURL.replaceAll(AIRConstants.CONTENT_SAUDITOURISM, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(EN_DO, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(Constants.HTML_EXTENSION, StringUtils.EMPTY);
        cityURL = cityURL.replaceAll(Constants.FORWARD_SLASH_CHARACTER, AIRConstants.DASH);
        entity.setId(cityURL);
      } else {
        entity.setId(ComponentUtils.generateId(TRENDING_CATEGORY, slideModel.getTitle()
              .replace(Constants.HTML_EXTENSION, StringUtils.EMPTY).replace(EN_DO, StringUtils.EMPTY)
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
             .replace(Constants.HTML_EXTENSION, StringUtils.EMPTY));
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
      sb.append(StringEscapeUtils.escapeCsv(Entity.getStringFromListOfArray(en.getInterestTags())));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(Entity.interestToString(interestTags));
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getArea());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }
    return csvLine;
  }


  /**
   * Save Entities data in csv format.
   * @param request SlingHTTPServletRequest
   * @param searchResult Nodes
   * @return Entities in CSV
   */
  public static String getPartnerCSV(SlingHttpServletRequest request, SearchResult searchResult) {
    String csv = AIRCsvExporter.getCSVHead();
    csv = StringUtils.join(csv, ",entity.copy");
    csv = StringUtils.join(csv, System.lineSeparator());
    Iterator<Resource> cardResources = searchResult.getResources();
    while (cardResources.hasNext()) {
      Resource cardResource = cardResources.next();
      TextCardsModel textCardsModel = cardResource.adaptTo(TextCardsModel.class);
      csv = StringUtils.join(csv, writeCardModelToCSV(textCardsModel, getHost(request), getCategory(cardResource)));
    }
    return csv;
  }

  /**
   * Category of feeds.
   * @param resource Resource
   * @return Category
   */
  @NotNull
  private static String getCategory(Resource resource) {
    if (resource.getPath().contains(CAR_RENTALS)) {
      return CAR_RENTALS_CATEGORY;
    }
    return PARTNER_PROMOTIONS_CATEGORY;
  }

  /**
   * Add card model into csv file.
   *
   * @param textCardsModel TextCardModel
   * @param host Host of request
   * @param category Category of feed
   * @return CSV File with value
   */
  private static String writeCardModelToCSV(@NotNull TextCardsModel textCardsModel, String host, String category) {
    String pattern = SPACES_TO_CHECK;
    String csvLine = StringUtils.EMPTY;
    List<Entity> entityList = new ArrayList<Entity>();
    Iterator<CardModel> textCardIterator = textCardsModel.getCards().iterator();
    while (textCardIterator.hasNext()) {
      CardModel cardModel = textCardIterator.next();
      Entity entity = new Entity();
      entity.setId(ComponentUtils.generateId("card-items", cardModel.getTitle()));
      entity.setName(cardModel.getTitle());
      entity.setCategoryId(category);
      String message = cardModel.getDescription().replaceAll(pattern, Constants.SPACE)
           .replaceAll(AIRConstants.NEW_LINE, Constants.SPACE);
      if (StringUtil.isEmpty(message)) {
        entity.setMessage(message);
      } else {
        entity.setMessage(StringEscapeUtils.escapeCsv(message.strip()));
      }
      entity.setThumbnailUrl(setHost(cardModel.getImage().getFileReference(), host));
      entity.setThumbnailS7Url(setHost(cardModel.getImage().getS7fileReference(), host));
      entity.setCtaText(cardModel.getLink().getCopy());
      entity.setPageUrl(cardModel.getLink().getUrl());
      entityList.add(entity);
    }

    LOGGER.info(String.valueOf(entityList));
    for (Entity en : entityList) {
      StringBuilder sb = new StringBuilder();
      sb.append(en.getId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getName());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCategoryId());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getMessage());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getPageUrl());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getThumbnailS7Url());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaURL());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaURL());
      sb.append(AIRConstants.DEFAULT_SEPARATOR);
      sb.append(en.getCtaText());
      String line = sb.toString();
      csvLine = StringUtils.join(csvLine, StringUtils.join(line, System.lineSeparator()));
    }

    return csvLine;
  }

}
