package com.saudi.tourism.core.utils;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.policies.ContentPolicy;
import com.day.cq.wcm.api.policies.ContentPolicyManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.exceptions.MissingRequestParameterException;
import com.saudi.tourism.core.login.models.UserIDToken;
import com.saudi.tourism.core.models.common.AnalyticsLinkModel;
import com.saudi.tourism.core.models.common.CategoryTag;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.SmartCropRenditions;
import com.saudi.tourism.core.models.components.events.AppFilterItem;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPlan;
import com.saudi.tourism.core.models.components.tripplan.v1.TripPoint;
import com.saudi.tourism.core.utils.gson.GsonAnnotationsSerializer;
import lombok.Generated;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.jackrabbit.util.Base64;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.jcr.api.SlingRepository;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import javax.jcr.Credentials;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Spliterators;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.saudi.tourism.core.utils.Constants.ONE;


/**
 * The Class CommonUtils.
 */
@Slf4j
public final class CommonUtils {

  /** The constant VERSION_EXTRA_PATH_LENGTH. */
  public static final int VERSION_EXTRA_PATH_LENGTH = 3;

  /** Things To Do types Constant. */
  public static final List<String> THINGS_TO_DO_TYPES =
      Arrays.asList("attraction", "tour", "activity", "poi");

  /**
   * Regex for searching locale in the full path to resource, works for both
   * content and app pages (uses {@link Constants#NN_APP} as app page / node
   * name). {@code ^/content/sauditourism/(?!app1/)?(\w{2})/}
   */
  public static final Pattern REGEX_PAGE_LOCALE = Pattern
      .compile("^" + Constants.ROOT_CONTENT_PATH + "/(?:" + Constants.NN_APP + "/)?(\\w{2})/");

  /**
   * Date format for parsing dates to LocalDate.
   */
  private static final DateTimeFormatter LOCAL_DATE_PARSE_FORMAT = DateTimeFormatter
      .ofPattern("yyyy-MM-dd['T'HH:mm:ss[.SSS][XXX][X]]");

  /** Constant for Calendar - end of day hour. */
  public static final int CALENDAR_LAST_HOUR = 23;

  /** Constant for Calendar - last minute of hour / second of minute. */
  public static final int CALENDAR_LAST_MIN_SEC = 59;

  /** Constant for Calendar - end of day last millisecond. */
  public static final int CALENDAR_LAST_MS = 999;

  /** Regex for html tag. */
  private static final String REGEX_HTML_TAG = "<[^>]*>";

  /**
   * Regex for repeating space symbols.
   */
  private static final String REGEX_MANY_SPACES = "[\\s\\p{Cntrl}\\u202F\\u00A0\\u2000\\u2001\\u2003]+";
  /**
   * Number six.
   */
  public static final int NUMBER_SIX = 6;

  /**
   * Instantiates a new common utils.
   */
  private CommonUtils() {
  }

  /**
   * The list of arabic numerals.
   */
  private static char[] arabicNumerals = {'٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩'};

  /**
   * The ASCII Constant for char.
   */
  private static final int CHAR_ASCII_BEGIN = 48;

  /**
   * Write JSON.
   * @param response the response
   * @param status   the status
   * @param object   the object
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeJSON(SlingHttpServletResponse response, int status, Object object)
      throws IOException {
    final GsonBuilder topBuilder = createDefaultGsonBuilder();

    registerCustomSerializers(topBuilder);

    Gson gson = topBuilder.create();
    writeJSON(response, status, gson.toJson(object));
  }

  /**
   * Write JSON.
   * @param response the response
   * @param status   the status
   * @param object   the object
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeJSONExclude(SlingHttpServletResponse response, int status, Object object)
      throws IOException {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    writeJSON(response, status, gson.toJson(object));
  }

  /**
   * This registers custom type adapters for the Gson.
   * @param topBuilder the builder instance to configure
   */
  public static void registerCustomSerializers(final GsonBuilder topBuilder) {
    // Custom serialization adapters. Type adapter for the specific class shouldn't
    // contain its
    // serializer in the passed builder.
    final GsonBuilder tripPointInnerBuilder = createDefaultGsonBuilder();
    final GsonAnnotationsSerializer<TripPoint> tripPointSerializer = new GsonAnnotationsSerializer<>(
        tripPointInnerBuilder);
    topBuilder.registerTypeAdapter(TripPoint.class, tripPointSerializer);

    final GsonBuilder tripPlanInnerBuilder = createDefaultGsonBuilder();
    final GsonAnnotationsSerializer<TripPlan> tripPlanSerializer = new GsonAnnotationsSerializer<>(
        tripPlanInnerBuilder);
    tripPlanInnerBuilder.registerTypeAdapter(TripPoint.class, tripPointSerializer);
    topBuilder.registerTypeAdapter(TripPlan.class, tripPlanSerializer);
  }

  /**
   * Creates pre-configured Gson Builder.
   * @return new GsonBuilder instance
   */
  @NotNull
  public static GsonBuilder createDefaultGsonBuilder() {
    return new GsonBuilder().serializeNulls().disableHtmlEscaping();
  }

  /**
   * Writes a JSON via response.
   * @param response   a {@link SlingHttpServletResponse}
   * @param status     a status of the error.
   * @param jsonString a JSON string should be printed.
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeJSON(SlingHttpServletResponse response, int status,
      final String jsonString) throws IOException {
    if (status > 0) {
      response.setStatus(status);
    }
    response.setHeader("Access-Control-Allow-Methods", "GET");
    response.setContentType("application/json; charset=UTF-8");
    response.getWriter().write(jsonString);
  }

  /**
   * Write new JSON format.
   *
   * @param response the response
   * @param status the status
   * @param jsonString the json string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeNewJSONFormat(SlingHttpServletResponse response, int status,
      final String jsonString) throws IOException {
    if (status > 0) {
      response.setStatus(status);
    }
    response.setHeader("Access-Control-Allow-Methods", "GET");
    response.setContentType("application/json; charset=UTF-8");
    Gson gson = new Gson();
    String empty = "{}";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("code", status);
    if (status == Constants.SUCCESS_RESPONSE_CODE) {
      jsonObject.addProperty("message", "success");
      JsonParser parser = new JsonParser();
      JsonElement jsonElement = parser.parse(jsonString);
      if (jsonElement.isJsonObject()) {
        jsonObject.add("response", gson.fromJson(jsonString, JsonObject.class));
      } else if (jsonElement.isJsonArray()) {
        jsonObject.add("response", jsonElement);
      } else {
        jsonObject.add("response", gson.fromJson(empty, JsonObject.class));
      }
    } else {
      jsonObject.addProperty("message", jsonString);
      jsonObject.add("response", gson.fromJson(empty, JsonObject.class));
    }

    response.getWriter().write(gson.toJson(jsonObject));
  }

  /**
   * Write new External JSON format.
   *
   * @param response the response
   * @param status the status
   * @param jsonString the json string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeExtJSONFormat(SlingHttpServletResponse response, int status,
                                        final String jsonString) throws IOException {
    if (status > 0) {
      response.setStatus(status);
    }
    response.setHeader("Access-Control-Allow-Methods", "GET");
    response.setContentType("application/json; charset=UTF-8");
    Gson gson = new Gson();
    String empty = "{}";
    JsonObject jsonObject = new JsonObject();
    jsonObject.addProperty("code", status);
    if (status == Constants.SUCCESS_RESPONSE_CODE) {
      jsonObject.addProperty("message", "success");
      JsonParser parser = new JsonParser();
      JsonElement jsonElement = parser.parse(jsonString);
      if (jsonElement.isJsonObject()) {
        jsonObject.add("result", gson.fromJson(jsonString, JsonObject.class));
      } else if (jsonElement.isJsonArray()) {
        jsonObject.add("response", jsonElement);
      } else {
        jsonObject.add("response", gson.fromJson(empty, JsonObject.class));
      }
    } else {
      jsonObject.addProperty("message", jsonString);
      jsonObject.add("response", gson.fromJson(empty, JsonObject.class));
    }

    response.getWriter().write(gson.toJson(jsonObject));
  }

  /**
   * Write JSON.
   * @param response the response
   * @param status   the status
   * @param object   the object
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeNewJSONFormatExclude(SlingHttpServletResponse response, int status, Object object)
        throws IOException {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeNulls();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation();
    Gson gson = gsonBuilder.create();
    writeNewJSONFormat(response, status, gson.toJson(object));
  }

  /**
   * Gets and populate header values into map of object.
   * @param request a {@link SlingHttpServletRequest}.
   * @param objects it is to store header values if had.
   */
  public static void populateHeaders(final SlingHttpServletRequest request,
      Map<String, Object> objects) {
    Enumeration<String> headerNames = request.getHeaderNames();
    if (Objects.nonNull(headerNames)) {
      while (headerNames.hasMoreElements()) {
        String name = headerNames.nextElement();
        objects.put(name, request.getHeader(name));
      }
    }
  }

  /**
   * Gets data of the body of the request.
   * @param request a {@link SlingHttpServletRequest}.
   * @return a map of object if found.
   */
  @SuppressWarnings({ "unchecked", "squid:S2065" })
  public static Map<String, Object> getBody(final SlingHttpServletRequest request) {
    try {
      return RestHelper.getObjectMapper().readValue(request.getInputStream(), Map.class);
    } catch (Exception ex) {
      return null;
    }
  }

  /**
   * Convert date to String.
   * @param dateInput the date input
   * @param format    the format
   * @return the string
   */
  public static String convertDateToSTring(String dateInput, final String format) {

    if (StringUtils.isBlank(dateInput)) {
      return null;
    }

    try {
      Date date = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE).parse(dateInput);
      return DateFormatUtils.format(date, format);
    } catch (ParseException e) {
      LOGGER.error("[convertDateToSTring] {}", e.getMessage());
    }
    return null;
  }

  /**
   * Convert date instance to string using default format.
   * @param date the local date input
   * @return the date string
   */
  @Generated
  public static String dateToString(@NotNull Date date) {
    return dateToString(date, Constants.FORMAT_ORIGINAL_DATE);
  }

  /**
   * Convert date instance to string using specified format.
   * @param date   the local date input
   * @param format the format
   * @return the date string
   */
  @Generated
  public static String dateToString(@NotNull Date date, final String format) {
    return DateFormatUtils.format(date, format, Constants.DEFAULT_TIME_ZONE);
  }

  /**
   * Convert local date instance to string using default format.
   * @param localDate the local date input
   * @return the date string
   */
  @Generated
  public static String dateToString(@NotNull LocalDate localDate) {
    return dateToString(localDate, Constants.FORMAT_OUTPUT_DATE);
  }

  /**
   * Convert local date instance to string using specified format.
   * @param localDate the local date input
   * @param format    the format
   * @return the date string
   */
  @Generated
  public static String dateToString(@NotNull LocalDate localDate, final String format) {
    return DateFormatUtils.format(localDateToDate(localDate), format, Constants.DEFAULT_TIME_ZONE);
  }

  /**
   * Convert local date instance to string using specified format with taking
   * language into account.
   * @param localDate the local date input
   * @param format    the format
   * @param language  the current language
   * @return the date string
   */
  @Generated
  public static String dateToString(final LocalDate localDate, final String format,
      @NotNull final String language) {
    if (localDate == null) {
      return null;
    }

    return DateFormatUtils.format(localDateToDate(localDate), format, Constants.DEFAULT_TIME_ZONE,
        new Locale(language));
  }

  /**
   * Convert calendar date instance to string using default format.
   * @param calendarDate calendar date instance
   * @return the date string
   */
  @Generated
  public static String dateToString(Calendar calendarDate) {
    return dateToString(calendarDate, Constants.FORMAT_OUTPUT_DATE);
  }

  /**
   * Convert local date instance to string using specified format.
   * @param calendarDate calendar date instance
   * @param format       the format
   * @return the date string
   */
  @Generated
  public static String dateToString(Calendar calendarDate, final String format) {
    if (calendarDate == null) {
      return null;
    }

    return DateFormatUtils.format(calendarDate, format, Constants.DEFAULT_TIME_ZONE);
  }

  /**
   * Convert local date instance to string using specified format & locale.
   * @param calendarDate calendar date instance
   * @param format       the format
   * @param language     locale to use
   * @return the date string
   */
  @Generated
  public static String dateToString(Calendar calendarDate, final String format,
      final String language) {
    if (calendarDate == null) {
      return null;
    }

    return DateFormatUtils.format(calendarDate, format, Constants.DEFAULT_TIME_ZONE,
        new Locale(language));
  }

  /**
   * Calculate days between two dates.
   * @param date1
   * @param date2
   * @return the number of days between two dates
   */
  public static Long calculateDaysBetween(Calendar date1, Calendar date2) {
    if (Objects.isNull(date1) || Objects.isNull(date2)) {
      return null;
    }
    // Convert Calendar objects to Date objects
    Date startDate = date1.getTime();
    Date endDate = date2.getTime();

    // Calculate the difference in milliseconds
    long diffInMilliseconds = endDate.getTime() - startDate.getTime();

    // Convert milliseconds to days
    return TimeUnit.DAYS.convert(diffInMilliseconds, TimeUnit.MILLISECONDS);
  }

  /**
   * Get titles from tag name.
   * @param eventType        the event type
   * @param resourceResolver the resource resolver
   * @param language         the language
   * @return the List<String>
   */
  public static List<String> getCategoryFromTagName(String[] eventType,
      ResourceResolver resourceResolver, final String language) {

    try {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      List<String> category = new ArrayList<>();
      Locale locale = new Locale(language);
      if (eventType != null) {
        for (String tag : eventType) {
          category.add(getTagName(tag, tagManager, locale));
        }
        return category;
      }
    } catch (Exception e) {
      LOGGER.error("Tag read error {}", e);
    }
    return Collections.emptyList();
  }


  /**
   * Get Names from tag name.
   * @param eventType        the event type
   * @param resourceResolver the resource resolver
   * @return the List<String>
   */
  public static List<String> getTagNames(String[] eventType, ResourceResolver resourceResolver) {
    try {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      List<String> category = new ArrayList<>();
      if (eventType != null) {
        for (String tag : eventType) {
          category.add(getActualTagName(tag, tagManager));
        }
        return category;
      }
    } catch (Exception e) {
      LOGGER.error("Tag read error {}", e);
    }
    return Collections.emptyList();
  }

  /**
   * Builds list of CategoryTag objects from the provided array of tags.
   * @param tagsArray        array of tags
   * @param resourceResolver resource resolver for processing tags
   * @param language         the current locale
   * @return list of CategoryTag objects
   */
  public static List<CategoryTag> buildCategoryTagsList(@Nullable String[] tagsArray,
      @NotNull final ResourceResolver resourceResolver, @Nullable final String language) {
    if (tagsArray == null) {
      return Collections.emptyList();
    }

    final TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
    if (tagManager == null) {
      LOGGER.error("Couldn't get tag manager, only tag ids will be returned");
      return Arrays.stream(tagsArray).map(CategoryTag::new).collect(Collectors.toList());
    }

    final Locale locale;
    if (StringUtils.isBlank(language)) {
      locale = new Locale(Constants.DEFAULT_LOCALE);
    } else {
      locale = new Locale(language);
    }

    return Arrays.stream(tagsArray).filter(Objects::nonNull).map(tagId -> {
      final Tag tag = tagManager.resolve(tagId);
      if (tag != null) {
        final Resource tagResource = resourceResolver.resolve(tag.getPath());
        final ValueMap vm = tagResource.adaptTo(ValueMap.class);
        if (vm != null) {
          return new CategoryTag(tag.getTagID(), tag.getTitle(locale),
              vm.get(Constants.PN_ICON, String.class));
        }
      }

      // Old method here as a workaround if tag couldn't be found (unit tests)
      LOGGER.error("Couldn't find tag [{}]", tagId);
      return new CategoryTag(tagId, getTagName(tagId, tagManager, locale));
    }).collect(Collectors.toList());
  }

  /**
   * given the tag(ex saudi:events/promoted), if tag path present read title from
   * node else we convert to camel case title "Promoted" from node name.
   * @param tagName    the tag name
   * @param tagManager the tag manager
   * @param language   Locale
   * @return tag Title
   */
  public static String getTagName(String tagName, TagManager tagManager, final Locale language) {

    Tag tag = tagManager.resolve(Constants.TAGS_URL
        + tagName.replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER));
    if (tag != null) {
      return tag.getTitle(language);
    } else {
      if (tagName.contains(Constants.COLON_SLASH_CHARACTER)) {
        String tempName = tagName.split(Constants.COLON_SLASH_CHARACTER)[1];
        String[] tagSplit = tempName.split(Constants.FORWARD_SLASH_CHARACTER);
        tempName = tagSplit[tagSplit.length - 1];
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL,
            tempName.replace(Constants.MINUS, " -"));
      }
      return tagName;
    }
  }

  /**
   * given the tag(ex saudi:events/promoted), if tag path present read Tag Name from
   * node else we convert to camel case.
   * @param tagName    the tag name
   * @param tagManager the tag manager
   * @return tag Name
   */
  public static String getActualTagName(String tagName, TagManager tagManager) {

    Tag tag = tagManager.resolve(Constants.TAGS_URL
        + tagName.replace(Constants.COLON_SLASH_CHARACTER, Constants.FORWARD_SLASH_CHARACTER));
    if (tag != null) {
      return tag.getName();
    } else {
      if (tagName.contains(Constants.COLON_SLASH_CHARACTER)) {
        String tempName = tagName.split(Constants.COLON_SLASH_CHARACTER)[1];
        String[] tagSplit = tempName.split(Constants.FORWARD_SLASH_CHARACTER);
        tempName = tagSplit[tagSplit.length - 1];
        return CaseFormat.LOWER_HYPHEN.to(CaseFormat.UPPER_CAMEL,
          tempName.replace(Constants.MINUS, " -"));
      }
      return tagName;
    }
  }



  /**
   * Get the given date in format specified.
   * @param dateInput the date input
   * @param format    the format
   * @return date date month
   */
  public static String getDateMonth(String dateInput, String format) {

    try {
      if (StringUtils.isNotBlank(dateInput)) {
        Date date = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE).parse(dateInput);
        return DateFormatUtils.format(date, format);
      }
      return StringUtils.EMPTY;
    } catch (ParseException e) {
      return null;
    }

  }

  /**
   * Get the given date in format specified.
   * @param dateInput the date input
   * @param format    the format
   * @return date date month
   */
  public static Calendar getAEMDateFormat(String dateInput, String format) {

    try {
      if (StringUtils.isNotBlank(dateInput)) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new SimpleDateFormat(format).parse(dateInput));
        return cal;
      }
      return null;
    } catch (ParseException e) {
      return null;
    }

  }

  /**
   * Gets the page name.
   * @param path  the path
   * @param index the index
   * @return the page name
   */
  public static String getPageNameByIndex(String path, int index) {

    if (path != null && path.contains(Constants.FORWARD_SLASH_CHARACTER)) {
      try {
        String[] nodes = path.split(Constants.FORWARD_SLASH_CHARACTER);
        if (StringUtils.startsWith(path, Constants.SITE_VERSION_HISTORY)) {
          // add 3 in case of the path Version Compare
          // /tmp/versionhistory/8c700d6b7/c83da/sauditourism/en/packages/my-trip/test
          return nodes[index + VERSION_EXTRA_PATH_LENGTH];
        } else {
          return nodes[index];
        }

      } catch (ArrayIndexOutOfBoundsException e) {
        LOGGER.error("[getPageNameByIndex] {}", e.getMessage());
      }
    }
    return path;
  }

  /**
   * Is date between start and end Date.
   * @param filterStartDate   the filterStartDate
   * @param filterEndDate     the filterEndDate
   * @param calendarStartDate the calendar start date
   * @param calendarEndDate   the calendar end date
   * @return the boolean
   */
  public static boolean isDateBetweenStartEnd(final String filterStartDate,
      final String filterEndDate, final String calendarStartDate, final String calendarEndDate) {

    SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
    SimpleDateFormat filterDateFormat = new SimpleDateFormat(Constants.FORMAT_DATE);
    long eventStartDate = getTimeFromDate(calendarStartDate, aemDateFormat);
    long eventEndDateObj = getTimeFromDate(calendarEndDate, aemDateFormat);
    long filterEndDateObj = getTimeFromDate(filterEndDate, filterDateFormat);
    long filterStartDateObj = getTimeFromDate(filterStartDate, filterDateFormat);

    // when filterEndDate and EventEndDate both is given
    if (filterEndDateObj > 0 && eventEndDateObj > 0) {
      // Any day overlap the even must be shown
      return (eventStartDate >= filterStartDateObj && eventEndDateObj <= filterEndDateObj)
          || (eventStartDate <= filterStartDateObj && eventEndDateObj >= filterEndDateObj)
          || (eventStartDate <= filterStartDateObj && eventEndDateObj >= filterStartDateObj)
          || (eventStartDate <= filterEndDateObj && eventEndDateObj >= filterEndDateObj);
    } else if (filterEndDateObj > 0) { // when filterEndDate is there
      return (filterStartDateObj == 0 || eventStartDate >= filterStartDateObj)
          && eventStartDate <= filterEndDateObj;
    } else { // when EventEndDate is there and filterEndDate not there
      return filterStartDateObj <= eventEndDateObj
          || (eventEndDateObj == 0 && filterStartDateObj <= eventStartDate);
    }
  }

  /**
   * Is date between start and end Date.
   * @param filterStartDate   the filterStartDate
   * @param filterEndDate   the filterEndDate
   * @param calendarStartDate the calendar start date
   * @return the boolean
   */
  public static boolean isOneDayEvent(final String filterStartDate,
                                      final String filterEndDate, final String calendarStartDate) {
    SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
    SimpleDateFormat filterDateFormat = new SimpleDateFormat(Constants.FORMAT_DATE);
    long eventStartDate = getTimeFromDate(calendarStartDate, aemDateFormat);
    long filterStartDateObj = getTimeFromDate(filterStartDate, filterDateFormat);
    long filterEndDateObj = getTimeFromDate(filterEndDate, filterDateFormat);
    return eventStartDate > 0 && filterStartDateObj > 0
      && eventStartDate >= filterStartDateObj && eventStartDate <= filterEndDateObj;
  }

  /**
   *
   * @return last day of the current week.
   */
  public static LocalDate getLastDayOfWeek() {
    LocalDate now = LocalDate.now();
    DayOfWeek firstDayOfWeek = WeekFields.of(Locale.getDefault()).getFirstDayOfWeek();
    DayOfWeek lastDayOfWeek = firstDayOfWeek.plus(NUMBER_SIX);
    LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(lastDayOfWeek));
    return endOfWeek;
  }

  /**
   *
   * @return last day of the current month.
   */
  public static String getLastDayOfMonth() {
    LocalDate now = LocalDate.now();
    String lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toString();
    return lastDayOfMonth;
  }

  /**
   *
   * @return current date.
   */
  public static String getCurrentDate() {
    String currentDate = LocalDate.now().toString();
    return currentDate;
  }

  /**
   * @param monthIndex Index.
   * @return monthdate.
   */
  public static LocalDate getMonthDate(int monthIndex) {
    LocalDate monthLocalDate = LocalDate.now().plusMonths(monthIndex);
    return monthLocalDate;
  }
  /**
   * get Time long value.
   * @param date       String date
   * @param dateFormat SimpleDateFormat
   * @return time
   */
  private static long getTimeFromDate(String date, SimpleDateFormat dateFormat) {
    try {
      if (date != null) {
        return dateFormat.parse(date).getTime();
      }
    } catch (ParseException e) {
      LOGGER.error("Error in parsing event date");
    }
    return 0;
  }

  /**
   * Gets the resource path by country and language.
   * @param language the language
   * @param path     the path
   * @return the resource path
   */
  public static String getResourcePath(final String language, final String path) {

    if (language != null && path != null) {
      return StringUtils.replaceEach(path, new String[] {Constants.LANGUAGE_PLACEHOLDER},
          new String[] {language.toLowerCase()});
    } else {
      final String errorMessage = "Missing parameters [country, language, path]";
      LOGGER.error("[getResourcePath] {}", errorMessage, new Exception(errorMessage));
      return StringUtils.EMPTY;
    }
  }

  /**
   * Returns correct language for the specified path. Example: returns "en" for
   * the path /content/site/en/path/to/any/resource
   * @param path path to page or resource
   * @return proper language for our multilingual website
   */
  @Generated
  public static String getLanguageForPath(final String path) {
    if (StringUtils.isBlank(path)
        || StringUtils.startsWithAny(path, Constants.TEMPLATES_ROOT, "/content" + Constants.FORWARD_SLASH_CHARACTER
        + getSiteName(path) + "global-configs")
        || !StringUtils.startsWith(path, "/content" + Constants.FORWARD_SLASH_CHARACTER
        + getSiteName(path))) {
      return Constants.DEFAULT_LOCALE;
    } else if (StringUtils.startsWith(path, Constants.APP_ROOT)
        || StringUtils.startsWith(path, Constants.ROOT_EXP_FRAGS_PATH)
        || StringUtils.startsWith(path, Constants.MOBILE_APP_ROOT)) {
      return CommonUtils.getPageNameByIndex(path, Constants.AEM_APP_LANGAUAGE_PAGE_PATH_POSITION)
          .replaceAll(".html", "");
    } else if (StringUtils.startsWith(path, Constants.ROOT_CONTENT_DAM_CF_PATH)) {
      return CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGUAGE_CONTENT_DAM_CF_PATH_POSITION);
    } else {
      return CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION).replaceAll(".html", "");
    }
  }

  /**
   *
   * @param path pagepath.
   * @return sitename.
   */
  public static String getSiteName(final String path) {
    if (null != path) {
      String[] pathArray = path.split(Constants.FORWARD_SLASH_CHARACTER);
      return pathArray[2];
    } else {
      return Constants.PROJECT_ID;
    }
  }

  /**
   * Returns correct Vendor for the specified path. Example: returns
   * "quality-of-life" for the path /content/site/en/events/quality-of-life/boxing
   * @param path path to page or resource
   * @return vendor name
   */
  public static String getVendorForPath(final String path) {
    if (StringUtils.isBlank(path) || StringUtils.startsWith(path, Constants.TEMPLATES_ROOT)
        || !StringUtils.startsWith(path, Constants.ROOT_CONTENT_PATH)) {
      return null;
    }

    return CommonUtils.getPageNameByIndex(path, Constants.AEM_VENDOR_PAGE_PATH_POSITION);
  }

  /**
   * Get date representation in Arabic.
   * @param input     original date value.
   * @param separator between days and months.
   * @return arabic representation of date
   */
  public static String getArabicDate(String input, String separator) {
    String[] calendarInputs = input.split(separator);
    List<String> dateList = new ArrayList<>();
    for (String date : calendarInputs) {
      StringBuilder stringBuilder = new StringBuilder();
      int number = Integer.parseInt(date);
      if (number > NumberConstants.CONST_ZERO && number < NumberConstants.CONST_TEN) {
        stringBuilder.append(arabicNumerals[number]);
      } else {
        stringBuilder.append(getArabicNumeralChar(date));
      }
      dateList.add(stringBuilder.toString());
    }
    return String.join(separator, dateList);
  }

  /**
   * Get Arabic Numeral Representation for string.
   * @param numberString input numeric string
   * @return arabic representation of string
   */
  public static String getArabicNumeralChar(final String numberString) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < numberString.length(); i++) {
      if (Character.isDigit(numberString.charAt(i))) {
        builder.append(arabicNumerals[(numberString.charAt(i) - CHAR_ASCII_BEGIN)]);
      } else {
        builder.append(numberString.charAt(i));
      }
    }
    return builder.toString();
  }

  /**
   * Gets get app path.
   * @param path the path
   * @return the get app path
   */
  public static String getGetAppPath(final String path) {

    if (path != null && path.contains(Constants.APP_ROOT)) {
      return path;
    }
    return Constants.APP_ROOT + path;
  }

  /**
   * Get New App Path.
   * @param path path.
   * @return the path.
   */
  public static String getNewAppPath(final String path) {

    if (path != null && path.contains(Constants.NEW_APP_ROOT)) {
      return path;
    }
    return Constants.NEW_APP_ROOT + path;
  }

  /**
   * Gets get nativeApp path.
   * @param path the path
   * @return the get nativeApp path
   */
  public static String getNativeGetAppPath(final String path) {

    if (path != null && path.contains(Constants.APP_ROOT)) {
      return path;
    }
    return Constants.NEW_APP_ROOT + path;
  }

  /**
   * Gets the link in page properties.
   *
   * @param <T>      subclass of {@link Link}
   * @param resource resource the resource
   * @param clazz    required class
   * @return adapted value or null
   */
  public static <T extends Link> T getLinkInPageProperties(Resource resource, Class<T> clazz) {
    return Optional.ofNullable(resource).map(res -> res.getChild(JcrConstants.JCR_CONTENT))
        .map(jcrContent -> {
          final T link = jcrContent.adaptTo(clazz);
          if (link == null) {
            LOGGER.error("Link adapting error, resource: {}", jcrContent.getPath());
          }
          return link;
        }).orElse(null);
  }

  /**
   * Gets i 18 n string.
   * @param i18nBundle the 18 n bundle
   * @param key        the key
   * @return the i 18 n string
   */
  public static String getI18nString(final ResourceBundle i18nBundle, final String key) {
    if (Objects.nonNull(key)) {
      return i18nBundle.getString(key);
    }
    return null;
  }

  /**
   * Gets i18n string value or key.
   * @param i18nBundle the 18 n bundle
   * @param key        the key
   * @return the i18n string or key
   */
  public static String getI18nStringOrKey(final ResourceBundle i18nBundle, final String key) {
    if (Objects.nonNull(key) && i18nBundle.containsKey(key)) {
      return i18nBundle.getString(key);
    }
    return key;
  }

  /**
   * Gets ids.
   * @param items the items
   * @return the ids
   */
  public static List<String> getIds(final List<String> items) {
    List<String> category = new ArrayList<>();
    for (String item : items) {
      category.add(AppUtils.stringToID(item));
    }
    return category;
  }

  /**
   * Gets ids.
   *
   * @param delimiter   delimiter
   * @param filterItems the filterItems
   * @return the displayString
   */
  public static String getListString(final String delimiter,
      final List<AppFilterItem> filterItems) {
    return filterItems.stream().map(AppFilterItem::getValue).collect(Collectors.joining(delimiter));
  }

  /**
   * Gets ids.
   * @param filterItems the filterItems
   * @return the displayString
   */
  public static String getListString(final List<AppFilterItem> filterItems) {
    return getListString(", ", filterItems);
  }

  /**
   * Find first not blank value out of a list of strings, if not found returns
   * null.
   * @param values list of strings
   * @return first not blank value or null
   */
  public static String firstNotBlank(final String... values) {
    return Stream.of(values).filter(StringUtils::isNotBlank).findFirst().orElse(null);
  }

  /**
   * Parses date string to LocalDate.
   * @param date date string
   * @return local date instance
   */
  @Generated
  public static LocalDate dateToLocalDate(String date) {
    if (date == null) {
      return null;
    }
    return LocalDate.parse(date, LOCAL_DATE_PARSE_FORMAT);
  }

  /**
   * Converts LocalDate to Calendar.
   * @param localDate local date instance
   * @return calendar instance
   */
  @Generated
  public static Calendar localDateToCalendar(final LocalDate localDate) {
    if (localDate == null) {
      return null;
    }

    final Calendar calendar = Calendar.getInstance(Constants.DEFAULT_TIME_ZONE);
    calendar.clear();
    // noinspection MagicConstant
    calendar.set(localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());
    return calendar;
  }

  /**
   * Converts Calendar to LocalDate.
   * @param calendar calendar date instance
   * @return LocalDate instance
   */
  @Generated
  public static LocalDate calendarToLocalDate(final Calendar calendar) {
    if (calendar == null) {
      return null;
    }

    // Ignore calendar's timezone during conversion
    return LocalDate.of(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
        calendar.get(Calendar.DAY_OF_MONTH));
  }

  /**
   * Converts LocalDate to Date.
   * @param localDate local date instance
   * @return date instance
   */
  @Generated
  public static Date localDateToDate(LocalDate localDate) {
    if (localDate == null) {
      return null;
    }
    return localDateToCalendar(localDate).getTime();
  }

  /**
   * Get count of days between two LocalDate/Temporal dates.
   * @param start start date
   * @param end   end date
   * @return days between dates
   */
  @Generated
  public static int getDaysCount(@NotNull final Temporal start, @NotNull final Temporal end) {
    return (int) ChronoUnit.DAYS.between(start, end);
  }

  /**
   * Get count of days between two Calendar dates.
   * @param start start date
   * @param end   end date
   * @return days between dates
   */
  @Generated
  public static long getDaysCount(@NotNull final Calendar start, @NotNull final Calendar end) {
    return (int) Duration.between(start.toInstant(), end.toInstant()).toDays();
  }

  /**
   * Returns a stream from an iterator, can be used to stream resources after
   * search query.
   *
   * @param <T>      type of iterated values
   * @param iterator any iterator
   * @return stream of iterator elements
   */
  @Generated
  @NotNull
  public static <T> Stream<T> iteratorToStream(final Iterator<T> iterator) {
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, 0), false);
  }

  /**
   * Creates one AppFilterItem with id and localized name.
   * @param jcrText text from jcr (there are stored values, not ids)
   * @param i18n    localization bundle
   * @return new app filter item
   */
  @Generated
  @NotNull
  public static AppFilterItem createAppFilterItem(@NotNull final String jcrText,
      @NotNull final ResourceBundle i18n) {
    return new AppFilterItem(AppUtils.stringToID(jcrText), i18n.getString(jcrText));
  }

  /**
   * Updates specified calendar date instance to be the beginning of the specified
   * day.
   * @param calendar instance to update
   * @return prepared (the same) calendar instance
   */
  @Generated
  public static Calendar calendarToStartOfDay(Calendar calendar) {
    // Sets time 00:00:00.000
    calendar.set(Calendar.HOUR_OF_DAY, Constants.ZERO);
    calendar.set(Calendar.MINUTE, Constants.ZERO);
    calendar.set(Calendar.SECOND, Constants.ZERO);
    calendar.set(Calendar.MILLISECOND, Constants.ZERO);

    return calendar;
  }

  /**
   * Updates specified calendar date instance to be the end of the specified day.
   * @param calendar instance to update
   * @return prepared (the same) calendar instance
   */
  @Generated
  public static Calendar calendarToEndOfDay(Calendar calendar) {
    // Sets time 23:59:59.999
    calendar.set(Calendar.HOUR_OF_DAY, CALENDAR_LAST_HOUR);
    calendar.set(Calendar.MINUTE, CALENDAR_LAST_MIN_SEC);
    calendar.set(Calendar.SECOND, CALENDAR_LAST_MIN_SEC);
    calendar.set(Calendar.MILLISECOND, CALENDAR_LAST_MS);

    return calendar;
  }

  /**
   * Returns a calendar date instance for the beginning of the current day.
   * @return prepared calendar instance
   */
  @Generated
  public static Calendar getTodayCalendarDate() {
    return calendarToStartOfDay(Calendar.getInstance(Constants.DEFAULT_TIME_ZONE));
  }

  /**
   * Converts string date in the default format "yyyy-MM-dd'T'HH:mm:ss.SSS"
   * (2020-05-22T00:00:00.000) to calendar date instance.
   * @param date date string to parse
   * @return new calendar date instance
   * @throws ParseException if the date string not well-formatted
   */
  @Generated
  public static Calendar parseDateToCalendar(final String date) throws ParseException {
    return parseDateToCalendar(date, null);
  }

  /**
   * Converts string date in the specified format to the calendar instance.
   * @param date   date string to parse
   * @param format format of the date string
   * @return new calendar instance
   * @throws ParseException if the date string not well-formatted
   */
  @Generated
  public static Calendar parseDateToCalendar(final String date, String format)
      throws ParseException {
    if (StringUtils.isBlank(format)) {
      format = Constants.FORMAT_ORIGINAL_DATE;
    }

    final Calendar calendar = Calendar.getInstance(Constants.DEFAULT_TIME_ZONE);
    calendar.setTime(parseDate(date, format));
    return calendar;
  }

  /**
   * It parses date from string using specified format.
   * @param date   date string to parse
   * @param format format to use
   * @return Date instance
   * @throws ParseException if date string not well-formatted
   */
  @Generated
  private static Date parseDate(final String date, final String format) throws ParseException {
    return new SimpleDateFormat(format).parse(date);
  }

  /**
   * Get Analytics Event data.
   * @param eventName   event name
   * @param linkUrl     link url
   * @param linkTitle   link title
   * @param vendorName  vendor Name
   * @param packageName packageName
   * @return jsonString analytics event data
   */
  public static String getAnalyticsEventData(final String eventName, final String linkUrl,
      final String linkTitle, final String vendorName, final String packageName) {
    AnalyticsLinkModel analyticsLinkModel = new AnalyticsLinkModel(eventName, linkUrl, linkTitle,
        vendorName, packageName);
    return CommonUtils.getAnalyticsJsonString(analyticsLinkModel);
  }

  /**
   * Get analytics json string.
   * @param eventData eventdata
   * @return json string
   */
  public static String getAnalyticsJsonString(final AnalyticsLinkModel eventData) {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(eventData);
    } catch (JsonProcessingException je) {
      LOGGER.error("JsonProcessingException occurred.");
    }
    return StringUtils.EMPTY;
  }

  /**
   * Get Capitalize String. Input: all small letter Output: All Small Letter
   * @param inputString Input String.
   * @return Capitalize String.
   */
  public static String getCapitalizedString(final String inputString) {
    if (StringUtils.isNotBlank(inputString)) {
      return WordUtils.capitalize(inputString);
    }
    return inputString;
  }

  /**
   * Get Vendor name from current page properties.
   * @param currentPage current page
   * @return vendor name
   */
  public static String getVendorName(final Page currentPage) {
    ValueMap pageProperties = currentPage.getProperties();
    String[] pathSplit = currentPage.getPath().split("/");
    if (Objects.nonNull(pageProperties)) {

      String vendor = null;
      if (pageProperties.containsKey(SaudiConstants.PACKAGE_DMC)) {

        vendor = pageProperties.get(SaudiConstants.PACKAGE_DMC, StringUtils.EMPTY);

      } else if (pathSplit.length > SaudiConstants.DMC_PACKAGE_POSITION) {
        // get dmc name from page path
        // for "/content/sauditourism/ar/packages/al-shitaiwi/discover-riyadh" is
        // al-shitaiwi
        vendor = pathSplit[SaudiConstants.DMC_POSITION];

      }
      return CommonUtils
          .getCapitalizedString(StringUtils.replace(vendor, Constants.MINUS, Constants.SPACE));

    }
    return StringUtils.EMPTY;
  }

  /**
   * Get Package from Current Page properties.
   * @param currentPage current page
   * @return package global title
   */
  public static String getPackageName(final Page currentPage) {
    ValueMap pageProperties = currentPage.getProperties();
    if (Objects.nonNull(pageProperties)) {
      return StringUtils.defaultIfBlank(
          pageProperties.get(Constants.PN_GLOBAL_PACKAGE_TITLE, StringUtils.EMPTY),
          pageProperties.get(Constants.PN_TITLE, StringUtils.EMPTY));
    }
    return StringUtils.EMPTY;
  }

  /**
   * Get mandatory request parameter.
   * @param request       sling request
   * @param parameterName parameter name
   * @return parameter value
   */
  public static String getMandatoryParameter(SlingHttpServletRequest request,
      String parameterName) {
    return Optional.ofNullable(request.getParameter(parameterName)).filter(StringUtils::isNotEmpty)
        .orElseThrow(() -> new MissingRequestParameterException(
            "Mandatory " + parameterName + " request parameter is missing."));
  }

  /**
   * Method to remove paragraph tags from RTE.
   * @param text rich text.
   * @return normal text.
   */
  public static String getEscapedRteText(String text) {
    try {
      if (StringUtils.isNotBlank(text)) {
        return text.replaceAll(Constants.EMPTY_TAG_REGEX_PATTERN, Constants.BR_TAG)
            .replaceAll(Constants.BLANK_TAG_REGEX_PATTERN, Constants.BR_TAG)
            .replace(Constants.CLOSE_PARAGRAPH_TAG, Constants.BR_TAG)
            .replace(Constants.OPEN_PARAGRAPH_TAG, StringUtils.EMPTY);
      }
    } catch (Exception error) {
      LOGGER.error("Exception while converting Rich Text to Text '{}'", text);
    }
    return text;
  }

  /**
   * Read dmc from path string.
   * @param dmc  the dmc
   * @param path the path
   * @return the string
   */
  public static String readDmcFromPath(String dmc, String path) {
    if (dmc == null) {
      // get dmc name from page path
      // for "/content/sauditourism/ar/packages/al-shitaiwi/discover-riyadh" is
      // al-shitaiwi
      String[] pathSplit = path.split("/");
      if (pathSplit.length > SaudiConstants.DMC_PACKAGE_POSITION) {
        dmc = pathSplit[SaudiConstants.DMC_POSITION];
      } else {
        dmc = "dmc";
      }

    }
    return dmc;
  }

  /**
   * Get list of locales.
   * @param resourceResolver resource resolver
   * @return list of locales
   */
  public static List<String> getLocales(ResourceResolver resourceResolver) {

    List<String> locales = new ArrayList<>();
    Resource rootResource = resourceResolver.getResource(Constants.APP_ROOT);
    Iterator<Resource> iteratorResources = rootResource.listChildren();

    while (iteratorResources.hasNext()) {
      Resource resource = iteratorResources.next();
      if (!JcrConstants.JCR_CONTENT.equals(resource.getName())) {
        locales.add(resource.getName());
      }
    }
    return locales;
  }

  /**
   * Resolve resource silently.
   * @param resourceResolver resource resolver
   * @param node             node
   * @return resource or null
   */
  public static Resource resolveResource(ResourceResolver resourceResolver, Node node) {
    String path;
    try {
      path = node.getPath();
    } catch (RepositoryException e) {
      LOGGER.error("Error during {} node path retrieval", node, e);
      return null;
    }
    return resolveResource(resourceResolver, path);
  }

  /**
   * Resolve resource silently.
   * @param resourceResolver resource resolver
   * @param path             path
   * @return resource or null
   */
  public static Resource resolveResource(ResourceResolver resourceResolver, String path) {
    try {
      return resourceResolver.getResource(path);
    } catch (Exception e) {
      LOGGER.error("Error during {} path resolution", path, e);
      return null;
    }
  }

  /**
   * Corrects locale in the provided full AEM path to the specified one. Works for
   * both content and app paths.
   * @param fullPath     full path to node
   * @param targetLocale locale to use in the result
   * @return corrected full path
   */
  public static String correctFullPathLocale(@Nullable String fullPath,
      @NotNull final String targetLocale) {
    if (StringUtils.isAnyBlank(fullPath, targetLocale)) {
      return fullPath;
    }

    // noinspection ConstantConditions
    final Matcher matcher = REGEX_PAGE_LOCALE.matcher(fullPath);
    if (matcher.find()) {
      final String pathLocale = matcher.group(ONE);
      if (!StringUtils.equals(targetLocale, pathLocale)) {
        return new StringBuilder(fullPath)
            .replace(matcher.start(ONE), matcher.end(ONE), targetLocale).toString();
      }
    }

    // Return unchanged if locale not found or is already target locale
    return fullPath;
  }

  /**
   * Converts path to absolute path, with replacing/adding provided locale into
   * the resulting path.
   * @param path     favId or path to process
   * @param language the locale needs to be used in the resulting path
   * @return absolute path
   */
  public static String toAbsolutePath(final String path, final String language) {
    if (StringUtils.isAnyBlank(path, language)) {
      return path;
    }

    if (Constants.ROOT_CONTENT_PATH.equals(path)) {
      return Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER + language;
    }

    final String absolutePrefix = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER;
    final StringBuilder resultPathBuilder = new StringBuilder(absolutePrefix).append(language);

    if (StringUtils.startsWith(path, absolutePrefix)) {
      // /content/sauditourism/es/some/page -> /content/sauditourism/<lang>/some/page
      return resultPathBuilder.append(path.substring(SaudiConstants.CONTENT_LOCALE_INDEX))
          .toString();
    }

    final boolean firstIsSlash = path.charAt(0) == '/';
    final int checkSlashIndex;
    if (firstIsSlash) {
      // length of '/en'
      checkSlashIndex = Constants.THREE;
    } else {
      // length of 'en'
      checkSlashIndex = Constants.TWO;
    }

    // Check if has language in the path
    if ('/' == path.charAt(checkSlashIndex)) {
      // es/some/page or /es/some/page -> /content/sauditourism/<lang>/some/page
      return resultPathBuilder.append(path.substring(checkSlashIndex)).toString();
    }

    // some/page or /some/page -> /content/sauditourism/<lang>/some/page
    if (!firstIsSlash) {
      resultPathBuilder.append(Constants.FORWARD_SLASH_CHARACTER);
    }
    return resultPathBuilder.append(path).toString();
  }

  /**
   * Convert path to absolute path.
   * @param path path
   * @return absolute path
   */
  public static String toAbsolutePath(String path) {
    final String absolutePrefix = Constants.ROOT_CONTENT_PATH + Constants.FORWARD_SLASH_CHARACTER;
    if (Constants.ROOT_CONTENT_PATH.equals(path) || StringUtils.startsWith(path, absolutePrefix)) {
      return path;
    }

    // "/en/page" or "en/page" to "/content/sauditourism/en/page"
    return absolutePrefix + StringUtils.removeStart(path, Constants.FORWARD_SLASH_CHARACTER);
  }

  /**
   * Check if the resource is of provided resourceType.
   * @param resource     resource
   * @param resourceType resourceType
   * @return true if resource is of provided resourceType else false.
   */
  public static boolean isResourceOfResourceType(final Resource resource,
      final String resourceType) {
    return Objects.nonNull(resource) && StringUtils.isNotBlank(resourceType)
        && resource.getResourceType().equals(resourceType);
  }

  /**
   * Get service from the bundle context, if you can't get it using injection.
   *
   * @param <T>          class of the returning service
   * @param serviceClass service interface class
   * @return service instance
   */
  public static <T> T getService(final Class<T> serviceClass) {
    final BundleContext bContext = FrameworkUtil.getBundle(serviceClass).getBundleContext();
    final ServiceReference<?> sr = bContext.getServiceReference(serviceClass.getName());
    return serviceClass.cast(bContext.getService(sr));
  }

  /**
   * Extracts JsonObject's property value as String.
   * @param jsonObject   json object
   * @param propertyName property
   * @return property value or null if there's no such property
   */
  @Generated
  public static String getJsonPropStr(final JsonObject jsonObject, final String propertyName) {
    final JsonElement jsonElementId = jsonObject.get(propertyName);
    final String favoriteId;
    if (jsonElementId != null) {
      favoriteId = jsonElementId.getAsString();
    } else {
      favoriteId = null;
    }
    return favoriteId;
  }

  /**
   * Search json object in json array by value of its property.
   * @param jsonArray   array to seek
   * @param idPropName  property name to search
   * @param idPropValue property value to search
   * @return found json object from array oer null if couldn't find
   */
  @Generated
  public static JsonElement searchJsonArrByProp(@NotNull final JsonArray jsonArray,
      @NotNull final String idPropName, @NotNull final String idPropValue) {
    for (JsonElement jsonElement : jsonArray) {
      String currentElementValue;
      if (jsonElement.isJsonObject()) {
        currentElementValue = getJsonPropStr(jsonElement.getAsJsonObject(), idPropName);

      } else {
        currentElementValue = jsonElement.getAsString();
      }
      if (idPropValue.equals(currentElementValue)) {
        return jsonElement;
      }
    }
    return null;
  }

  /**
   * Search json object in json array by value of its property.
   * @param jsonArray    array to seek
   * @param filterParams properties map for filtering
   * @return found json object from array oer null if couldn't find
   */
  @Generated
  public static JSONArray filterJsonArrByProp(final JSONArray jsonArray,
      @Nullable final Map<String, Object> filterParams) {
    if (filterParams == null || filterParams.isEmpty()
        || (filterParams.size() == ONE && filterParams.containsKey(Constants.LOCALE))) {
      // No filter specified (or only locale is in the filter) - return array
      // unchanged
      return jsonArray;
    }

    final JSONArray result = new JSONArray();

    for (int i = 0; i < jsonArray.length(); i++) {
      final JSONObject jsonObject = jsonArray.optJSONObject(i);
      if (jsonObject == null) {
        continue;
      }

      // Check all properties for one json object
      boolean match = checkJsonObjectMatches(jsonObject, filterParams);
      if (match) {
        result.put(jsonObject);
      }
    }

    return result;
  }

  /**
   * Checks if the json object matches the provided filter.
   * @param jsonObject json object to check
   * @param filter     property-value map for filtering
   * @return true if all properties from filter have same values in json object
   */
  public static boolean checkJsonObjectMatches(final JSONObject jsonObject,
      final @NotNull Map<String, Object> filter) {
    boolean match = true;
    for (Map.Entry<String, Object> property : filter.entrySet()) {
      final String idPropName = property.getKey();
      final Object idPropValue = property.getValue();

      // Skip locale param from checking
      if (Constants.PN_LOCALE.equals(idPropName)) {
        continue;
      }

      if (idPropValue == null) {
        // Return only elements that don't have such field
        match = !jsonObject.has(idPropName);

      } else if (jsonObject.has(idPropName)) {
        // Filtering by value
        if (idPropValue instanceof String) {
          match = ((String) idPropValue).equalsIgnoreCase(jsonObject.optString(idPropName));
        } else if (idPropValue instanceof Boolean) {
          match = idPropValue.equals(jsonObject.optBoolean(idPropName));
        } else if (idPropValue instanceof Integer) {
          match = idPropValue.equals(jsonObject.optInt(idPropName));
        } else {
          match = idPropValue.equals(jsonObject.opt(idPropName));
        }
      } else {
        match = false;
      }

      if (!match) {
        break;
      }
    }
    return match;
  }

  /**
   * Get locale from request or return default.
   * @param request request
   * @return locale
   */
  public static String getLocale(SlingHttpServletRequest request) {
    String locale = request.getParameter(Constants.LOCALE);
    if (StringUtils.isBlank(locale)) {
      locale = Constants.DEFAULT_LOCALE;
    }
    return locale;
  }

  /**
   * Get page that resource belongs to.
   * @param resource resource
   * @return page
   */
  public static Optional<Page> getContainingPage(@NonNull Resource resource) {
    return Optional.ofNullable(resource.getResourceResolver())
        .map(resolver -> resolver.adaptTo(PageManager.class))
        .map(pageManager -> pageManager.getContainingPage(resource));
  }

  /**
   * Returns the first sentence (all symbols until the first dot).
   * @param text text to process
   * @return the first sentence
   */
  public static String getFirstSentence(@Nullable final String text) {
    return StringUtils.substringBefore(text, Constants.DOT) + Constants.DOT;
  }

  /**
   * Removes all tags (also control characters like \r \n \t) from the provided
   * text (converts rich text to plain text).
   * @param text text to process
   * @return text without tags
   */
  public static String stripHtml(@Nullable final String text) {
    if (StringUtils.isBlank(text)) {
      return text;
    }

    return StringEscapeUtils.unescapeHtml(text).replaceAll(REGEX_HTML_TAG, StringUtils.SPACE)
        .replaceAll(REGEX_MANY_SPACES, StringUtils.SPACE).trim();
  }

  /**
   * Get Configuration page path from content policy.
   * @param resourceResolver resourceResolver
   * @param path             content resource path
   * @param propertyName     config property name
   * @return configuration page
   */
  public static List<SmartCropRenditions> getSmartCropInfoFromContentPolicy(
      final ResourceResolver resourceResolver, final String path, final String propertyName) {
    List<SmartCropRenditions> smartCropRenditions = new ArrayList<>();
    ContentPolicy contentPolicy = getContentPolicyForResourcePath(resourceResolver, path);
    if (contentPolicy != null) {
      Resource policyResource = resourceResolver.getResource(contentPolicy.getPath());
      if (Objects.nonNull(policyResource) && policyResource.hasChildren()) {
        Resource cropResource = policyResource.getChild(propertyName);
        if (Objects.nonNull(cropResource)) {
          cropResource.listChildren().forEachRemaining(cropItem -> {
            SmartCropRenditions smartCropRendition = cropItem.adaptTo(SmartCropRenditions.class);
            smartCropRenditions.add(smartCropRendition);
          });
        }
      }
    }
    return smartCropRenditions;
  }

  /**
   * Get Content policy for resource path.
   *
   * @param resourceResolver resourceResolver
   * @param resourcePath resourcePath
   * @return content policy
   */
  private static ContentPolicy getContentPolicyForResourcePath(
      final ResourceResolver resourceResolver, final String resourcePath) {
    Resource resource = resourceResolver.getResource(resourcePath);

    if (Objects.isNull(resource)) {
      return null;
    }
    if ((resource.getPath().contains("h01_brand_page_hero")
        || resource.getPath().contains("c27_teaser_with_card")
        || resource.getPath().contains("traveller_quotes")
        || resource.getPath().contains("c04_full_bleed_slide")
        || resource.getPath().contains("make_a_reservation")
        || resource.getPath().contains("c17_interest_points")
        || resource.getPath().contains("winter_hero_slider")
        || resource.getPath().contains("c09_tabs")
        || resource.getPath().contains("guide_cards")
        || resource.getPath().contains("banner_slider")
        || resource.getPath().contains("page_banner")
        || resource.getPath().contains("latest_stories")
        || resource.getPath().contains("all_destinations")
        || resource.getPath().contains("destinations_map")
        || resource.getPath().contains("banner_slider")
        || resource.getPath().contains("promotional_banner"))
        && resource.getPath().contains("item")) {
      resource = resourceResolver.getResource(resource.getParent().getParent().getPath());
    }
    ContentPolicyManager policyManager = resourceResolver.adaptTo(ContentPolicyManager.class);
    if (Objects.nonNull(policyManager) && Objects.nonNull(resource)) {
      return policyManager.getPolicy(resource);
    }
    return null;
  }

  /**
   * Get Encoded string.
   * @param source source url string
   * @return encoded url
   */
  public static String getS7EncodedUrl(final String source) {
    try {
      if (!DynamicMediaUtils.isDamImage(source)) {
        return URIUtil.encodeQuery(URIUtil.decode(source));
      }
    } catch (URIException e) {
      LOGGER.error("URL Exception for image path ", source);
    }
    return source;
  }

  /**
   * Validate Basic Authentication.
   *
   * @param request    request to be validated
   * @param repository jcr repo for checking authentication
   * @return if basic auth header have right value or not
   * @throws IOException when jcr repo exception
   */
  public static boolean validateBasicAuth(SlingHttpServletRequest request,
      SlingRepository repository) throws IOException {
    final String authorization = request.getHeader("Authorization");
    try {
      if (authorization == null || !authorization.startsWith("Basic")) {
        return false;
      } else {
        StringTokenizer st = new StringTokenizer(authorization);
        if (st.hasMoreTokens()) {
          String basic = st.nextToken();
          if (basic.equalsIgnoreCase("Basic")) {
            String decodedStr = Base64.decode(st.nextToken());
            int p = decodedStr.indexOf(":");
            if (p != -1) {
              String login = decodedStr.substring(0, p).trim();
              String password = decodedStr.substring(p + 1).trim();
              Credentials credentials = new SimpleCredentials(login, password.toCharArray());
              Session session = repository.login(credentials);
              if (session == null) {
                return false;
              }
            } else {
              return false;
            }
          }
        }
      }
    } catch (RepositoryException e) {
      return false;
    }
    return true;
  }

  /**
   * Get title from tag name.
   *
   * @param tagType          the event type
   * @param resourceResolver the resource resolver
   * @param language         the language
   * @return the String
   */
  public static String getTagNameFromCategory(String tagType,
                                              ResourceResolver resourceResolver, final String language) {

    try {
      TagManager tagManager = resourceResolver.adaptTo(TagManager.class);
      Locale locale = new Locale(language);
      if (tagType != null) {
        String category = getTagName(tagType, tagManager, locale);
        return category;
      }
    } catch (Exception e) {
      LOGGER.error("Tag read error {}", e);
    }
    return null;
  }

  /**
   * Returns the distance between two coordinates in radians.
   *
   * @param la1 latitude1.
   * @param la2 latitude2.
   * @param lo1 longitude1.
   * @param lo2 longitude2.
   * @return distance.
   */
  public static double getDistanceByCoordinates(String la1,
                                                String la2, String lo1,
                                                String lo2) {
    if (StringUtils.isNotBlank(la1) && StringUtils.isNotBlank(la2)
        && StringUtils.isNotBlank(lo1) && StringUtils.isNotBlank(lo2)) {
      double lat1 = Double.parseDouble(la1);
      double lat2 = Double.parseDouble(la2);
      double lon1 = Double.parseDouble(lo1);
      double lon2 = Double.parseDouble(lo2);
      lon1 = Math.toRadians(lon1);
      lon2 = Math.toRadians(lon2);
      lat1 = Math.toRadians(lat1);
      lat2 = Math.toRadians(lat2);

      // Haversine formula
      double dlon = lon2 - lon1;
      double dlat = lat2 - lat1;
      double a = Math.pow(Math.sin(dlat / 2), 2)
          + Math.cos(lat1) * Math.cos(lat2)
          * Math.pow(Math.sin(dlon / 2), 2);

      double c = 2 * Math.asin(Math.sqrt(a));
      return (c);
    }
    return Double.MAX_VALUE;
  }

  /**
   * Is date between start and end Date , but also managing infinite limits.
   * @param dateToCompare   the filterStartDate
   * @param startDate the calendar start date
   * @param endDate   the calendar end date
   * @return the boolean
   */
  public static boolean isDateBetweenStartEndWithInfinite(final String dateToCompare, final String startDate,
                                                          final String endDate) {

    SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
    SimpleDateFormat filterDateFormat = new SimpleDateFormat(Constants.FORMAT_DATE);
    long startDateObj = getTimeFromDate(startDate, aemDateFormat);
    long endDateObj = getTimeFromDate(endDate, aemDateFormat);
    long dateToCompareObj = getTimeFromDate(dateToCompare, filterDateFormat);

    // if startDateObj and endDateObj provided
    if (startDateObj > 0 && endDateObj > 0) {
      return (startDateObj <= dateToCompareObj && dateToCompareObj <= endDateObj);
    } else if (startDateObj > 0) { // endDateObj not provided so infinite
      return (startDateObj <= dateToCompareObj);
    } else if (endDateObj > 0) { // endDateObj not provided so infinite
      return (dateToCompareObj <= endDateObj);
    } else {
      return true; // No limit date provided, so always considered between.
    }
  }


  /**
   * Is date before another date.
   * @param dateToCompare   date to be compared
   * @param dateToBeCompared date to be compared to
   * @return true if dateToCompare is before dateToBeCompared
   */
  public static boolean isDateBefore(final String dateToCompare, final String dateToBeCompared) {

    SimpleDateFormat aemDateFormat = new SimpleDateFormat(Constants.FORMAT_ORIGINAL_DATE);
    SimpleDateFormat filterDateFormat = new SimpleDateFormat(Constants.FORMAT_DATE);
    long dateToBeComparedObj = getTimeFromDate(dateToBeCompared, aemDateFormat);
    long dateToCompareObj = getTimeFromDate(dateToCompare, filterDateFormat);

    // dateToBeCompared provided
    if (dateToBeComparedObj > 0) {
      return dateToCompareObj < dateToBeComparedObj;
    } else {
      return false; // No dateToBeCompared  so always considered after.
    }
  }

  /**
   * Gets user id.
   *
   * @param request the request
   * @return the user id
   */
  @NotNull
  public static UserIDToken getUserID(final SlingHttpServletRequest request) {
    UserIDToken userIDToken = new UserIDToken();

    String userID = request.getHeader("userID");
    if (Objects.isNull(userID)) { // CDN converting userID to userid
      userID = request.getHeader("userid");
    }
    userIDToken.setToken(request.getHeader("token"));
    String locale =
        StringUtils.defaultIfBlank(request.getParameter("locale"), Constants.DEFAULT_LOCALE);
    userIDToken.setLocale(locale);
    userIDToken.setUser(userID);
    return userIDToken;
  }


}
