package com.saudi.tourism.core.models.components.events;


import com.day.cq.commons.Externalizer;
import com.day.cq.commons.inherit.HierarchyNodeInheritanceValueMap;
import com.day.cq.commons.inherit.InheritanceValueMap;
import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.LinkUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Upcoming Events Model Model.
 */
@Model(adaptables = {Resource.class, SlingHttpServletRequest.class},
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class UpcomingEventsModel {

  /**
   * Card Type 'calendar'.
   */
  private static final String CARD_TYPE = "calendar";

  /**
   * Card Style 'upcoming-months'.
   */
  private static final String CARD_STYLE = "upcoming-months";

  /**
   * Card type.
   */
  @Getter
  @Expose
  private String cardType = CARD_TYPE;

  /**
   * Card Style.
   */
  @Getter
  @Expose
  private String cardStyle = CARD_STYLE;

  /**
   * NEXT Month.
   */
  private static final int NEXT_MONTH = 1;

  /**
   * SECOND Month.
   */
  private static final int SECOND_MONTH = 2;

  /**
   * Third Month.
   */
  private static final int THIRD_MONTH = 3;
  /**
   * Four Month.
   */
  private static final int FOUR_MONTH = 4;
  /**
   * Five Month.
   */
  private static final int FIVE_MONTH = 5;
  /**
   * Six Month.
   */
  private static final int SIX_MONTH = 6;
  /**
   * Seven Month.
   */
  private static final int SEVEN_MONTH = 7;
  /**
   * Eight Month.
   */
  private static final int EIGHT_MONTH = 8;
  /**
   * EVENTS.
   */
  private static final String EVENTS = "EVENTS";

  /**
   * Max Event Images.
   */
  private static final int MAX_EVENT_IMAGES = 3;
  /**
   * Event Service.
   */
  @OSGiService
  private EventService eventService;

  /**
   * Resource Bundle Provider.
   */
  @OSGiService(filter = Constants.I18N_PROVIDER_SERVICE_TARGET)
  private ResourceBundleProvider i18nProvider;

  /**
   * The Sling settings service.
   */
  @OSGiService
  private SlingSettingsService settingsService;

  /**
   * Variable for current resource.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * ResourceResolver.
   */
  @SlingObject
  private transient ResourceResolver resourceResolver;

  /**
   * Events Filter Page Path.
   */
  @ValueMapValue
  private String filtersPagePath;

  /**
   * Number of Months.
   */
  @ValueMapValue
  private String numberOfMonths;


  /**
   * Season.
   */
  @ValueMapValue
  private String seasonType;

  /**
   * Events Text.
   */
  private String eventsText = StringUtils.EMPTY;

  /**
   * Locale.
   */
  private String locale;

  /**
   * Next Month Background Image.
   */
  @ChildResource(name = "nextMonth")
  @Setter
  private Image firstMonthImage;

  /**
   * Second Month Background Image.
   */
  @ChildResource(name = "secondMonth")
  @Setter
  private Image secondMonthImage;

  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "thirdMonth")
  @Setter
  private Image thirdMonthImage;

  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "fourthMonth")
  @Setter
  private Image fourthMonthImage;
  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "fifthMonth")
  @Setter
  private Image fifthMonthImage;
  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "sixthMonth")
  @Setter
  private Image sixthMonthImage;
  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "seventhMonth")
  @Setter
  private Image seventhMonthImage;
  /**
   * Third Month Background Image.
   */
  @ChildResource(name = "eightMonth")
  @Setter
  private Image eighthMonthImage;

  /**
   * First Month Event Images.
   */
  private List<String> firstMonthEventsImages = new ArrayList<>();

  /**
   * Second Month Event Images.
   */
  private List<String> secondMonthEventsImages = new ArrayList<>();

  /**
   * Third Month Event Images.
   */
  private List<String> thirdMonthEventsImages = new ArrayList<>();
  /**
   * Fouth Month Event Images.
   */
  private List<String> fourthMonthEventsImages = new ArrayList<>();
  /**
   * Fifth Month Event Images.
   */
  private List<String> fifthMonthEventsImages = new ArrayList<>();
  /**
   * Sixth Month Event Images.
   */
  private List<String> sixthMonthEventsImages = new ArrayList<>();
  /**
   * Seventh Month Event Images.
   */
  private List<String> seventhMonthEventsImages = new ArrayList<>();
  /**
   * Eight Month Event Images.
   */
  private List<String> eighthMonthEventsImages = new ArrayList<>();

  /**
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;
  /**
   * Upcoming Events.
   */
  @Getter
  @Expose
  private List<UpcomingEvents> upcomingEvents = new ArrayList<>();
  /**
   * The min number of events to show the '+sign' for 'upcoming-months'.
   * By default it will be 99.
   */
  @ValueMapValue
  private Integer minNumberEventShowPlusSign;
  /**
   *nextMonthEventsCount.
   */
  private int nextMonthEventsCount;
  /**
   *secondMonthEventsCount.
   */
  private int secondMonthEventsCount;
  /**
   *thirdMonthEventsCount.
   */
  private int thirdMonthEventsCount;
  /**
   *fourthMonthEventsCount.
   */
  private int fourMonthEventsCount;
  /**
   *fifthMonthEventsCount.
   */
  private int fifthMonthEventsCount;
  /**
   *sixMonthEventsCount.
   */
  private int sixMonthEventsCount;
  /**
   *sevenMonthEventsCount.
   */
  private int sevenMonthEventsCount;
  /**
   *eightMonthEventsCount.
   */
  private int eightMonthEventsCount;
  /**
   *months.
   */
  private int months;
  /**
   *nextMonthStartDate.
   */
  private String nextMonthStartDate;
  /**
   *nextMonthEndDate.
   */
  private String nextMonthEndDate;
  /**
   *secondMonthStartDate.
   */
  private String secondMonthStartDate;
  /**
   *secondMonthEndDate.
   */
  private String secondMonthEndDate;
  /**
   *thirdMonthStartDate.
   */
  private String thirdMonthStartDate;
  /**
   *thirdMonthEndDate.
   */
  private String thirdMonthEndDate;
  /**
   *fourMonthStartDate.
   */
  private String fourMonthStartDate;
  /**
   *fourMonthEndDate.
   */
  private String fourMonthEndDate;
  /**
   *fiveMonthStartDate.
   */
  private String fiveMonthStartDate;
  /**
   *fiveMonthEndDate.
   */
  private String fiveMonthEndDate;
  /**
   *sixMonthStartDate.
   */
  private String sixMonthStartDate;
  /**
   *sixMonthEndDate.
   */
  private String sixMonthEndDate;
  /**
   *sevenMonthStartDate.
   */
  private String sevenMonthStartDate;
  /**
   *sevenMonthEndDate.
   */
  private String sevenMonthEndDate;
  /**
   *eightMonthStartDate.
   */
  private String eightMonthStartDate;
  /**
   *eightMonthEndDate.
   */
  private String eightMonthEndDate;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {

    filtersPagePath = LinkUtils.getAuthorPublishUrl(
            resourceResolver,
            filtersPagePath,
            settingsService.getRunModes().contains(Externalizer.PUBLISH));

    InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentPage.getContentResource());
    locale = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);

    ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
    eventsText = i18n.getString(EVENTS);

    EventsRequestParams eventsRequestParams = new EventsRequestParams();
    eventsRequestParams.setLocale(locale);
    eventsRequestParams.setLimit(Integer.MAX_VALUE);
    if (StringUtils.isNotEmpty(seasonType)) {
      eventsRequestParams.setSeason(Arrays.asList(seasonType));
    }
    if (null == numberOfMonths) {
      numberOfMonths = "1";
    }
    months = Integer.parseInt(numberOfMonths);
    LocalDate nextMonthLocalDate = LocalDate.now().plusMonths(NEXT_MONTH);
    nextMonthStartDate = nextMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    nextMonthEndDate = nextMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String nextMonthDisplayName = Month.of(nextMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate secondMonthLocalDate = LocalDate.now().plusMonths(SECOND_MONTH);
    secondMonthStartDate = secondMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    secondMonthEndDate = secondMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String secondMonthDisplayName = Month.of(secondMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate thirdMonthLocalDate = LocalDate.now().plusMonths(THIRD_MONTH);
    thirdMonthStartDate = thirdMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    thirdMonthEndDate = thirdMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String thirdMonthDisplayName = Month.of(thirdMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate fourMonthLocalDate = LocalDate.now().plusMonths(FOUR_MONTH);
    fourMonthStartDate = fourMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    fourMonthEndDate = fourMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String fourMonthDisplayName = Month.of(fourMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate fiveMonthLocalDate = LocalDate.now().plusMonths(FIVE_MONTH);
    fiveMonthStartDate = fiveMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    fiveMonthEndDate = fiveMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String fiveMonthDisplayName = Month.of(fiveMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate sixMonthLocalDate = LocalDate.now().plusMonths(SIX_MONTH);
    sixMonthStartDate = sixMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    sixMonthEndDate = sixMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String sixMonthDisplayName = Month.of(sixMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate sevenMonthLocalDate = LocalDate.now().plusMonths(SEVEN_MONTH);
    sevenMonthStartDate = sevenMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    sevenMonthEndDate = sevenMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String sevenMonthDisplayName = Month.of(sevenMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    LocalDate eightMonthLocalDate = LocalDate.now().plusMonths(EIGHT_MONTH);
    eightMonthStartDate = eightMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    eightMonthEndDate = eightMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    String eightMonthDisplayName = Month.of(eightMonthLocalDate.getMonthValue())
        .getDisplayName(TextStyle.FULL_STANDALONE, new Locale(locale));

    if (months == NEXT_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(nextMonthEndDate);
    }
    if (months == SECOND_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(secondMonthEndDate);
    }
    if (months == THIRD_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(thirdMonthEndDate);
    }
    if (months == FOUR_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(fourMonthEndDate);
    }
    if (months == FIVE_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(fiveMonthEndDate);
    }
    if (months == SIX_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(sixMonthEndDate);
    }
    if (months == SEVEN_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(sevenMonthEndDate);
    }
    if (months == EIGHT_MONTH) {
      eventsRequestParams.setStartDate(nextMonthStartDate);
      eventsRequestParams.setEndDate(eightMonthEndDate);
    }
    try {
      EventListModel model = eventService.getFilteredEvents(eventsRequestParams);

      List<EventDetail> data = model.getData();

      // get months events count.
      getMonthEventsCount(data);

      if (months >= NEXT_MONTH) {
        setMonthEvents(nextMonthEventsCount, nextMonthDisplayName, nextMonthStartDate,
            nextMonthEndDate, firstMonthEventsImages, firstMonthImage);
      }
      if (months >= SECOND_MONTH) {
        setMonthEvents(secondMonthEventsCount, secondMonthDisplayName, secondMonthStartDate,
            secondMonthEndDate, secondMonthEventsImages, secondMonthImage);
      }
      if (months >= THIRD_MONTH) {
        setMonthEvents(thirdMonthEventsCount, thirdMonthDisplayName, thirdMonthStartDate,
            thirdMonthEndDate, thirdMonthEventsImages, thirdMonthImage);
      }
      if (months >= FOUR_MONTH) {
        setMonthEvents(fourMonthEventsCount, fourMonthDisplayName, fourMonthStartDate,
            fourMonthEndDate, fourthMonthEventsImages, fourthMonthImage);
      }
      if (months >= FIVE_MONTH) {
        setMonthEvents(fifthMonthEventsCount, fiveMonthDisplayName, fiveMonthStartDate,
            fiveMonthEndDate, fifthMonthEventsImages, fifthMonthImage);
      }
      if (months >= SIX_MONTH) {
        setMonthEvents(sixMonthEventsCount, sixMonthDisplayName, sixMonthStartDate,
            sixMonthEndDate, sixthMonthEventsImages, sixthMonthImage);
      }
      if (months >= SEVEN_MONTH) {
        setMonthEvents(sevenMonthEventsCount, sevenMonthDisplayName, sevenMonthStartDate,
            sevenMonthEndDate, seventhMonthEventsImages, seventhMonthImage);
      }
      if (months >= EIGHT_MONTH) {
        setMonthEvents(eightMonthEventsCount, eightMonthDisplayName, eightMonthStartDate,
            eightMonthEndDate, eighthMonthEventsImages, eighthMonthImage);
      }
    } catch (RepositoryException e) {
      LOGGER.error("Error in getting module:{}, {}", Constants.EVENT_DETAIL_RES_TYPE,
          e.getLocalizedMessage(), e);
    }
  }

  /**
   * get months events count based on the selected months.
   * @param data .
   */
  private void getMonthEventsCount(List<EventDetail> data) {
    for (EventDetail event : data) {
      if (months >= NEXT_MONTH) {
        nextMonthEventsCount = getEventsCount(nextMonthStartDate, nextMonthEndDate,
          event, firstMonthEventsImages, nextMonthEventsCount);
      }
      if (months >= SECOND_MONTH) {
        secondMonthEventsCount = getEventsCount(secondMonthStartDate, secondMonthEndDate,
          event, secondMonthEventsImages, secondMonthEventsCount);
      }
      if (months >= THIRD_MONTH) {
        thirdMonthEventsCount = getEventsCount(thirdMonthStartDate, thirdMonthEndDate,
          event, thirdMonthEventsImages, thirdMonthEventsCount);
      }
      if (months >= FOUR_MONTH) {
        fourMonthEventsCount = getEventsCount(fourMonthStartDate, fourMonthEndDate,
          event, fourthMonthEventsImages, fourMonthEventsCount);
      }
      if (months >= FIVE_MONTH) {
        fifthMonthEventsCount = getEventsCount(fiveMonthStartDate, fiveMonthEndDate,
          event, fifthMonthEventsImages, fifthMonthEventsCount);
      }
      if (months >= SIX_MONTH) {
        sixMonthEventsCount = getEventsCount(sixMonthStartDate, sixMonthEndDate,
          event, sixthMonthEventsImages, sixMonthEventsCount);
      }
      if (months >= SEVEN_MONTH) {
        sevenMonthEventsCount = getEventsCount(sevenMonthStartDate, sevenMonthEndDate,
          event, seventhMonthEventsImages, sevenMonthEventsCount);
      }
      if (months >= EIGHT_MONTH) {
        eightMonthEventsCount = getEventsCount(eightMonthStartDate, eightMonthEndDate,
          event, eighthMonthEventsImages, eightMonthEventsCount);
      }
    }
  }

  /**
   * Method to Set Number of Events Per month.
   *
   * @param monthStartDate Month Start Date.
   * @param monthEndDate Month End Date.
   * @param event Event.
   * @param monthEventsImages Month Event Images.
   * @param monthEventsCount Events Count.
   * @return events count.
   */
  private int getEventsCount(String monthStartDate, String monthEndDate, EventDetail event,
                             List<String> monthEventsImages, int monthEventsCount) {
    if (CommonUtils.isDateBetweenStartEnd(monthStartDate, monthEndDate,
            event.getCalendarStartDate(), event.getCalendarEndDate())) {

      if (monthEventsImages.size() < MAX_EVENT_IMAGES) {
        monthEventsImages.add(event.getImage().getSrc());
      }

      monthEventsCount++;
    }
    return monthEventsCount;
  }

  /**
   * Method to Set Events Details per month.
   *
   * @param eventsCount Events Count.
   * @param monthDisplayName Month Display Name.
   * @param monthStartDate Month Start Date.
   * @param monthEndDate Month End Date.
   * @param monthEventsImages Month Events Images.
   * @param backgroundImage Background Images.
   */
  private void setMonthEvents(int eventsCount, String monthDisplayName,
                              String monthStartDate, String monthEndDate,
                              List<String> monthEventsImages, Image backgroundImage) {
    if (eventsCount > 0 && backgroundImage.getFileReference() != null) {
      String link = filtersPagePath + "?" + Constants.LOCALE + "=" + locale + "&" + Constants.START_DATE + "="
          + monthStartDate + "&" + Constants.END_DATE + "=" + monthEndDate + "&type=custom";
      if (StringUtils.isNotEmpty(seasonType)) {
        link = link + "&season=" + seasonType;
      }
      UpcomingEvents month = new UpcomingEvents(
              monthDisplayName, eventsCount, eventsText, minNumberEventShowPlusSign, link);
      month.setEventImages(monthEventsImages);
      month.setBackGroundImage(backgroundImage);
      upcomingEvents.add(month);
    }
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
