package com.saudi.tourism.core.utils;

import com.day.cq.commons.Externalizer;
import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.saudi.tourism.core.beans.bestexperience.ExperienceData;
import com.saudi.tourism.core.models.common.Category;
import com.saudi.tourism.core.models.common.CommonCFModel;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.models.components.contentfragment.event.EventCFModel;
import com.saudi.tourism.core.models.components.contentfragment.season.SeasonCFModel;
import com.saudi.tourism.core.models.mobile.components.atoms.ButtonComponentStyle;
import com.saudi.tourism.core.models.mobile.components.atoms.Cta;
import com.saudi.tourism.core.models.mobile.components.atoms.CustomAction;
import com.saudi.tourism.core.models.mobile.components.atoms.Date;
import com.saudi.tourism.core.models.mobile.components.atoms.Titles;
import com.saudi.tourism.core.models.mobile.components.atoms.PriceWidget;
import com.saudi.tourism.core.models.mobile.components.atoms.Location;
import com.saudi.tourism.core.models.mobile.components.atoms.Destination;
import com.saudi.tourism.core.models.mobile.components.atoms.MediaGallery;
import com.saudi.tourism.core.models.mobile.components.autosections.utils.AtomsBuilder;
import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.items.ItemResponseModel;
import com.saudi.tourism.core.services.stories.v1.Story;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import com.saudi.tourism.core.utils.gson.IsoCalendarAdapter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

/**
 * The type Mobile util.
 */
@Slf4j
public final class MobileUtils {

  /**
   * CF master node.
   */
  private static final String CF_MASTER_NODE = "/jcr:content/data/master";

  /**
   * Constant date pattern.
   */
  private static final String DATE_FORMAT_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

  /**
   * The constant RANDOM_RANGE.
   */
  public static final int RANDOM_RANGE = 1000;

  /**
   * Instantiates a Mobile utils.
   */
  private MobileUtils() {
  }

  /**
   * Write new JSON format.
   *
   * @param response          the response
   * @param httpStatusCode    the status
   * @param errorBusinessCode the errorBusinessCode
   * @param jsonString        the json string
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public static void writeMobileV1Format(
      SlingHttpServletResponse response,
      int httpStatusCode,
      int errorBusinessCode,
      final String jsonString)
      throws IOException {
    // Set HTTP status code
    response.setStatus(httpStatusCode);
    response.setHeader("Access-Control-Allow-Methods", "GET");
    response.setContentType("application/json; charset=UTF-8");

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeHierarchyAdapter(Calendar.class, new IsoCalendarAdapter());

    Gson gson = gsonBuilder.create();
    JsonObject jsonObject = new JsonObject();

    if (httpStatusCode >= HttpServletResponse.SC_OK
        && httpStatusCode < HttpServletResponse.SC_MULTIPLE_CHOICES) {
      // Success case
      try {
        JsonElement jsonElement = JsonParser.parseString(jsonString);

        if (jsonElement.isJsonObject() || jsonElement.isJsonArray()) {
          jsonObject.add("data", jsonElement);
        } else {
          // If jsonString is neither a JSON object nor a JSON array
          jsonObject.add("data", new JsonObject());
        }
        jsonObject.addProperty("code", errorBusinessCode);
        jsonObject.addProperty("message", "success");

      } catch (Exception e) {
        // In case of an error parsing the JSON string, use an empty JSON object
        jsonObject.add("data", new JsonObject());
        jsonObject.addProperty("code", errorBusinessCode); // Error business code for parsing errors
        jsonObject.addProperty("message", "Unknown error");
      }
    } else {

      jsonObject.addProperty("message", jsonString);
      jsonObject.add("data", new JsonObject()); // Empty data for error cases
      // Error case for HTTP status codes like 404
      jsonObject.add("data", new JsonObject()); // Empty data for error cases
      jsonObject.addProperty("code", errorBusinessCode); // Error business code for HTTP errors
    }

    // Write the JSON response
    response.getWriter().write(gson.toJson(jsonObject));
  }

  /**
   * Extracts the section ID from a given path.
   *
   * @param path The path string from which the section ID is to be extracted.
   * @return The extracted section ID or null if not found.
   */
  public static String extractItemId(String path) {
    if (StringUtils.isBlank(path)) {
      return null;
    }

    // Splitting the string on "/sections/"
    String[] parts = path.split("/items/");

    // If the array has at least 2 elements, return the part after "/sections/"
    if (parts.length > 1) {
      return parts[1];
    }

    // Return null if "/sections/" is not found or there's no section ID following it
    return null;
  }

  /**
   * Extracts the section ID from a given path.
   *
   * @param path The path string from which the section ID is to be extracted.
   * @return The extracted section ID or null if not found.
   */
  public static String extractSectionId(String path) {
    if (StringUtils.isBlank(path)) {
      return null;
    }

    String prefix = "/content/sauditourism/mobile/";

    // Check if the path starts with the required prefix
    if (path.startsWith(prefix)) {
      // Extract the part of the path after the prefix
      String pathAfterPrefix = path.substring(prefix.length());

      // Split the remaining path into segments
      String[] parts = pathAfterPrefix.split("/");

      // Ensure there are at least three segments
      if (parts.length > 2 && Arrays.asList("sections", "items").contains(parts[1])) {
        // Reconstruct the relevant part of the URL from this point
        return String.join("/", Arrays.copyOfRange(parts, 1, parts.length));
      }
    }

    return null;
  }

  /**
   * This method fills the price widget objects from cfModel.
   *
   * @param event           event
   * @param resolver        resolver
   * @param settingsService settingsService
   * @param language        language
   * @param i18nProvider
   * @return priceWidget
   */
  public static ItemResponseModel buildItemFromEventCFModel(EventCFModel event, ResourceResolver resolver,
                                                            SlingSettingsService settingsService, String language,
                                                            ResourceBundleProvider i18nProvider) {
    ItemResponseModel item = new ItemResponseModel();
    Titles titles = new Titles();
    titles.setTitle(event.getTitle());

    Date date = new Date();
    date.setShow(true);
    date.setStartDate(event.getStartDate());
    date.setEndDate(event.getEndDate());

    if (StringUtils.isNotBlank(event.getTicketPrice())) {
      ItemResponseModel.Price price = new ItemResponseModel.Price();
      price.setPriceLabel(event.getTicketPrice());
      item.setPrice(price);
    }


    ItemResponseModel.PrimaryTag primaryTag = new ItemResponseModel.PrimaryTag();
    primaryTag.setShow(true);

    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    primaryTag.setTitle(CommonUtils.getI18nString(i18n, I18nConstants.EVENT).toUpperCase());
    item.setId(event.getLongWebpath());
    item.setType(Constants.TYPE_AUTO);

    item.setPublishedDate(event.getPublishedDate());
    item.setShowFavorite(false);
    item.setMediaGallery(
        List.of(
            AtomsBuilder.buildMediaGallery(event.getBannerImages(), resolver, settingsService)));
    item.setCustomAction(AtomsBuilder.buildCustomAction(event.getTicketsCtaLink()));
    item.setLocation(
        AtomsBuilder.buildLocation(event.getLat(), event.getLng(), event.getDestination()));
    item.setCategories(
        AtomsBuilder.getCategoriesFromCFTags(
            event.getCategoriesTags(), resolver, settingsService, language));
    item.setSeason(AtomsBuilder.buildSeason(event.getSeason()));
    item.setTitles(titles);
    item.setDate(date);
    item.setPrimaryTag(primaryTag);

    return item;
  }

  /**
   * This method fills the price widget objects from cfModel.
   *
   * @param cfModel cfModel
   * @return priceWidget
   */
  private static PriceWidget getPriceWidget(CommonCFModel cfModel) {
    String ticketCTALabel = cfModel.getTicketCTALabel();
    String ticketType = "FREE_TICKETS";
    if (StringUtils.isBlank(cfModel.getTicketType()) || StringUtils.equals(cfModel.getTicketType(), "bookTicket")) {
      ticketType = "PRICE_TICKET";
    }
    if (StringUtils.equals(cfModel.getTicketType(), "noTicket")) {
      ticketType = "NO_TICKETS_NEEDED";
    }

    Cta cta = new Cta();
    cta.setType("WEB");

    CustomAction customAction = new CustomAction();
    customAction.setTitle(ticketCTALabel);
    customAction.setShow(true);
    customAction.setEnable(true);
    customAction.setButtonComponentStyle(new ButtonComponentStyle("PRIMARY"));
    customAction.setCta(cta);

    PriceWidget.Price price = new PriceWidget.Price();

    price.setPrice(cfModel.getTicketPrice());

    price.setTier(cfModel.getTicketPriceSuffix());

    PriceWidget priceWidget = new PriceWidget();
    priceWidget.setType(ticketType);
    priceWidget.setDescription(CommonUtils.stripHtml(cfModel.getTicketDetails()));
    priceWidget.setPrice(price);
    priceWidget.setCustomAction(customAction);

    return priceWidget;
  }

  /**
   * This method fill a common cf model from tour, event, activities and attraction CFs.
   *
   * @param currentPage     currentPage
   * @param resolver        resolver
   * @param settingsService settings service
   * @return eventCF
   */
  public static CommonCFModel loadCommonCF(
      Page currentPage, ResourceResolver resolver, SlingSettingsService settingsService) {
    String cfPath =
        currentPage.getProperties().get(Constants.REFERENCED_FRAGMENT_REFERENCE, String.class);
    if (StringUtils.isEmpty(cfPath)) {
      return null;
    }

    var cfResource = resolver.getResource(cfPath + CF_MASTER_NODE);
    if (cfResource == null) {
      return null;
    }

    var title = cfResource.getValueMap().get("title", String.class);
    var subtitle = cfResource.getValueMap().get("subtitle", String.class);
    var ticketType = cfResource.getValueMap().get("ticketType", String.class);
    var ticketPrice = cfResource.getValueMap().get("ticketPrice", String.class);
    var ticketPriceSuffix = cfResource.getValueMap().get("ticketPriceSuffix", String.class);
    var ticketDetails = cfResource.getValueMap().get("ticketDetails", String.class);
    var ticketCTALink = cfResource.getValueMap().get("ticketCTALink", String.class);
    var detailsPage = cfResource.getValueMap().get("pagePath", String.class);
    var ticketCTALabel = cfResource.getValueMap().get("ticketCTALabel", String.class);
    var destinationCFPath = cfResource.getValueMap()
            .get("locationValue", cfResource.getValueMap().get("destination", String.class));
    ticketCTALink =
        LinkUtils.getAuthorPublishUrl(
            resolver, ticketCTALink, settingsService.getRunModes().contains(Externalizer.PUBLISH));

    DestinationCFModel destination = new DestinationCFModel();
    var destinationCFRes = resolver.getResource(destinationCFPath);
    if (destinationCFRes != null) {
      destination = destinationCFRes.adaptTo(DestinationCFModel.class);
    }

    return CommonCFModel.builder()
        .title(title)
        .subtitle(subtitle)
        .ticketType(ticketType)
        .ticketPrice(ticketPrice)
        .ticketPriceSuffix(ticketPriceSuffix)
        .ticketDetails(ticketDetails)
        .ticketCTALink(ticketCTALink)
        .ticketCTALabel(ticketCTALabel)
        .detailsPage(detailsPage)
        .destination(destination)
        .build();
  }

  public static String generateUniqueIdentifier() {
    // Get the current time in milliseconds
    long timePart = System.currentTimeMillis();

    // Generate a random number (for example, in the range 0-999)
    // This reduces the chance of a collision if IDs are generated in the same millisecond
    long randomPart = ThreadLocalRandom.current().nextLong(RANDOM_RANGE);

    return Long.toString(timePart * RANDOM_RANGE + randomPart);
  }

  /**
   * This method build the item response model from things to do model.
   *
   * @param thingToDo       ThingsToDo model
   * @param resolver        Resource Resolver
   * @param settingsService Settings Service
   * @param language        Language
   * @param i18nProvider
   * @return item
   */
  public static ItemResponseModel buildItemFromThingsToDo(ThingToDoModel thingToDo, ResourceResolver resolver,
                                                          SlingSettingsService settingsService, String language,
                                                          ResourceBundleProvider i18nProvider) {
    ItemResponseModel item = new ItemResponseModel();
    Titles titles = new Titles();
    titles.setTitle(thingToDo.getTitle());
    if (StringUtils.isNotBlank(thingToDo.getTicketPrice())) {
      ItemResponseModel.Price price = new ItemResponseModel.Price();
      price.setPriceLabel(thingToDo.getTicketPrice());
      item.setPrice(price);
    }
    Date date = new Date();
    date.setShow(true);
    date.setStartDate(thingToDo.getStartDate());
    date.setEndDate(thingToDo.getEndDate());

    ItemResponseModel.PrimaryTag primaryTag = new ItemResponseModel.PrimaryTag();
    primaryTag.setShow(true);
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));
    if (StringUtils.isNotBlank(thingToDo.getType())) {
      primaryTag.setTitle(CommonUtils.getI18nString(i18n, thingToDo.getType().toLowerCase()).toUpperCase());
    }
    if (StringUtils.isNotBlank(thingToDo.getPagePath())) {
      item.setId(thingToDo.getPagePath());
    }
    item.setType(Constants.TYPE_AUTO);
    item.setPublishedDate(thingToDo.getPublishedDate());
    item.setShowFavorite(false);
    item.setMediaGallery(
        List.of(
            AtomsBuilder.buildMediaGalleryFromImages(
                thingToDo.getBannerImages(), resolver, settingsService)));
    if (Objects.nonNull(thingToDo.getTicketsCtaLink())) {
      item.setCustomAction(AtomsBuilder.buildCustomAction(thingToDo.getTicketsCtaLink()));
    }
    if (Objects.nonNull(thingToDo.getDestination())) {
      Resource resource = resolver.getResource(thingToDo.getDestination().getPath());
      if (resource != null) {
        DestinationCFModel destinationCF = resource.adaptTo(DestinationCFModel.class);
        item.setLocation(
            AtomsBuilder.buildLocation(thingToDo.getLat(), thingToDo.getLng(), destinationCF));
      }
    }
    if (Objects.nonNull(thingToDo.getSeason())) {
      Resource resource = resolver.getResource(thingToDo.getSeason().getPath());
      if (resource != null) {
        SeasonCFModel seasonCF = resource.adaptTo(SeasonCFModel.class);
        item.setSeason(AtomsBuilder.buildSeason(seasonCF));
      }
    }
    item.setCategories(
        AtomsBuilder.getCategoriesFromThingsToDo(
            thingToDo.getCategoriesTags(), resolver, settingsService, language));
    item.setPoiTypes(Arrays.asList(AtomsBuilder.getPoiTypeFromThingsToDo(
        thingToDo.getTypeValue(), resolver, language)));
    item.setTitles(titles);
    item.setDate(date);
    item.setPrimaryTag(primaryTag);

    return item;
  }

  /**
   * This method build the item response model from things to do model.
   *
   * @param story           Story model
   * @param resolver        Resource Resolver
   * @param settingsService Settings Service
   * @param language        Language
   * @param i18nProvider
   * @return item
   */
  public static ItemResponseModel buildItemFromStory(Story story, ResourceResolver resolver,
                                                     SlingSettingsService settingsService, String language,
                                                     ResourceBundleProvider i18nProvider) {
    ItemResponseModel item = new ItemResponseModel();
    Titles titles = new Titles();
    titles.setTitle(story.getTitle());

    final ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(language));

    if (StringUtils.isNotBlank(CommonUtils.getI18nString(i18n, I18nConstants.STORY))) {
      ItemResponseModel.PrimaryTag primaryTag = new ItemResponseModel.PrimaryTag();
      primaryTag.setShow(true);
      primaryTag.setTitle(CommonUtils.getI18nString(i18n, I18nConstants.STORY).toUpperCase());
      item.setPrimaryTag(primaryTag);
    }

    if (StringUtils.isNotBlank(story.getPagePath())) {
      item.setId(story.getPagePath());
    }
    item.setType(Constants.TYPE_AUTO);

    item.setPublishedDate(story.getPublishedDate());
    item.setShowFavorite(false);
    item.setMediaGallery(
        List.of(
            AtomsBuilder.buildMediaGalleryFromImages(
                List.of(story.getImage()), resolver, settingsService)));


    if (StringUtils.isNotBlank(story.getDestinationPath())) {
      Resource resource = resolver.getResource(story.getDestinationPath());
      if (resource != null) {
        DestinationCFModel destinationCF = resource.adaptTo(DestinationCFModel.class);
        item.setLocation(
            AtomsBuilder.buildLocation(story.getLat(), story.getLng(), destinationCF));
      }
    }


    item.setCategories(
        AtomsBuilder.getCategoriesFromThingsToDo(
            story.getCategoriesTags(), resolver, settingsService, language));
    item.setTitles(titles);

    return item;
  }

  /**
   * This method build the item response model from things to do model.
   *
   * @param experience ThingsToDo model
   * @param resolver Resource Resolver
   * @param settingsService Settings Service
   * @param language Language
   * @param i18nProvider
   * @return item
   */
  public static ItemResponseModel buildItemFromExperience(
      ExperienceData experience,
      ResourceResolver resolver,
      SlingSettingsService settingsService,
      String language,
      ResourceBundleProvider i18nProvider)
      throws ParseException {
    ItemResponseModel item = new ItemResponseModel();
    final var i18n = i18nProvider.getResourceBundle(new Locale(language));

    ItemResponseModel.Price price = new ItemResponseModel.Price();
    price.setOriginalPrice(experience.getOriginalPrice());
    price.setFinalPrice(experience.getFinalPrice());
    if (StringUtils.isBlank(price.getFinalPrice()) && StringUtils.isNotBlank(price.getOriginalPrice())) {
      price.setFinalPrice(price.getOriginalPrice());
      price.setOriginalPrice(StringUtils.EMPTY);
    }
    price.setCurrency(CommonUtils.getI18nString(i18n, Constants.SAR));

    String destinationTitle = experience.getCity();
    if (StringUtils.isNotBlank(destinationTitle)) {
      destinationTitle = experience.getCity().toUpperCase();
    }

    Location location = new Location();
    location.setDestination(
        Destination.builder()
            .id(AppUtils.stringToID(experience.getCity()))
            .title(destinationTitle)
            .lat(experience.getLatitude())
            .lng(experience.getLongitude())
            .build());

    Date date = new Date();
    date.setShow(true);

    final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);


    if (StringUtils.isNotBlank(experience.getStartDate())) {
      final var startDate = dateFormatter.parse(experience.getStartDate());
      date.setStartDate(CommonUtils.parseDateToCalendar(startDate.toString(), DATE_FORMAT_PATTERN));
    }
    if (StringUtils.isNotBlank(experience.getStartDate())) {
      final var endDate = dateFormatter.parse(experience.getEndDate());
      date.setEndDate(CommonUtils.parseDateToCalendar(endDate.toString(), DATE_FORMAT_PATTERN));
    }

    ItemResponseModel.PrimaryTag primaryTag = new ItemResponseModel.PrimaryTag();
    primaryTag.setShow(true);
    primaryTag.setTitle(CommonUtils.getI18nString(i18n, Constants.EXPERIENCE).toUpperCase());

    item.setId(experience.getId());
    item.setIsDmc(experience.getIsDmc());
    item.setPackageUrl(experience.getPackageUrl());
    item.setMediaGallery(
        Collections.singletonList(
            MediaGallery.builder().type("IMAGE").url(experience.getBackgroundImage()).build()));
    item.setTitles(Titles.builder().title(experience.getName()).build());
    item.setPrice(price);
    item.setType(Constants.TYPE_EXPERIENCE);
    item.setLocation(location);
    item.setShowFavorite(false);
    item.setPrimaryTag(primaryTag);
    String categoryName = experience.getCategoryName();
    if (StringUtils.isNotBlank(categoryName)) {
      categoryName = experience.getCategoryName().toUpperCase();
    }
    item.setCategories(
        Collections.singletonList(
            Category.builder()
                .id(experience.getCategorySlug())
                .title(categoryName)
                .build()));
    item.setDate(date);
    item.setPrimaryTag(primaryTag);

    return item;
  }

  /**
   * This method build the icon for general Informations.
   *
   * @param iconPath  iconPath
   * @param resolver  Resource Resolver
   * @param isPublish isPublish
   * @return item
   */
  public static String generateIconUrlGeneralInformation(final ResourceResolver resolver, String iconPath,
                                                         Boolean isPublish) {


    return LinkUtils.getAuthorPublishAssetUrl(
        resolver,
        Constants.CONTENT_DAM_ICON_GENERAL_INFORMATIONS + iconPath + ".png",
        isPublish
                                             );
  }

  /**
   * This method build the icon for automatic alert section.
   *
   * @param iconPath iconPath
   * @param resolver Resource Resolver
   * @param isPublish isPublish
   * @return item
   */
  public static String generateAlertIconUrl(
      final ResourceResolver resolver, String iconPath, Boolean isPublish) {

    return LinkUtils.getAuthorPublishAssetUrl(
        resolver, Constants.CONTENT_DAM_ICON_ALERT + iconPath + ".png", isPublish);
  }

  /**
   * List of days as three-letter day abbreviation .
   *
   * */
  private static final List<String> DAYS = Arrays.asList("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN");

  /**
   * Converts a three-letter day abbreviation to a full localized day name using language and optionally country.
   *
   * @param dayAbbreviation the abbreviated day of the week (e.g., "MON", "TUE").
   * @param language the language part of the locale (e.g., "en", "fr").
   * @return the full localized name of the day.
   * @throws IllegalArgumentException if the abbreviation or locale is not valid.
   */
  public static String getFullDayName(String dayAbbreviation, String language)
      throws IllegalArgumentException {
    // Normalize the input and find the index in the array
    int index = DAYS.indexOf(dayAbbreviation.toUpperCase());
    if (index == -1) {
      // if not found retourn original to handle sameTimeAcrossWeek case
      return dayAbbreviation;
    }

    // Create the Locale object, handling the country part conditionally
    Locale locale = new Locale(language);

    // DayOfWeek indices start at 1, so we add 1 to the zero-based array index
    DayOfWeek day = DayOfWeek.of(index + 1);

    // Get the localized full day name
    return day.getDisplayName(TextStyle.SHORT_STANDALONE, locale);
  }

  /**
   * Formats a given time string into a specified format based on the provided language. For Arabic
   * locale, it formats the time with custom AM/PM indicators ("صباحا" for AM and "مساء" for PM) and
   * ensures the text is displayed in right-to-left (RTL) order by prefixing the formatted string
   * with an RTL mark. For other locales, it uses the default AM/PM format.
   *
   * @param time The time string to be formatted, expected in "HH:mm:ss" format.
   * @param language The language code to determine the formatting.
   * @return The formatted time string. If the input time string cannot be parsed, the original
   *     string is returned.
   */
  public static String formatTimeToHHMM(String time, String language) {
    try {
      String pattern = "HH:mm";
      if (StringUtils.countMatches(time, Constants.COLON_SLASH_CHARACTER) == 2) {
        pattern = "HH:mm:ss";
      }
      DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(pattern);
      LocalTime localTime = LocalTime.parse(time, inputFormat);

      // Check if the language is Arabic
      if (language.equals(Constants.ARABIC_LOCALE)) {
        // Custom formatter for Arabic AM/PM
        DateTimeFormatter outputFormatter =
            new DateTimeFormatterBuilder()
                .appendLiteral("\u200F") // Append RTL mark to write from right to left
                .appendPattern("hh:mm ")
                .appendText(
                    ChronoField.AMPM_OF_DAY,
                    new HashMap<Long, String>() {
                      {
                        put(0L, "صباحا");
                        put(1L, "مساء");
                      }
                    })
                .toFormatter(new Locale(language));
        return localTime.format(outputFormatter);
      } else {
        // Default formatter for other locales
        DateTimeFormatter defaultFormatter = DateTimeFormatter.ofPattern("hh:mm a", new Locale(language));
        return localTime.format(defaultFormatter);
      }
    } catch (Exception e) {
      LOGGER.error("Error while parsing the time", e);
      return time;
    }
  }
}


