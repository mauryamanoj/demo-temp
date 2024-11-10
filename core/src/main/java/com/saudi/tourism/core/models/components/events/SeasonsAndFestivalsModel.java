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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.apache.sling.settings.SlingSettingsService;

import javax.annotation.PostConstruct;
import javax.jcr.RepositoryException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** Seasons and Festivals Events Model. */
@Model(
    adaptables = {Resource.class, SlingHttpServletRequest.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class SeasonsAndFestivalsModel {

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
   * Headline.
   */
  @ValueMapValue
  @Expose
  private String headline;

  /**
   * Background ornament.
   */
  @ValueMapValue
  @Expose
  private String hideOrnament;

  /**
   * Background eventsLabel.
   */
  @ValueMapValue
  private String eventsLabel;

  /**
   * Seasons.
   */
  @ChildResource
  @Getter
  private List<Seasons> festivalcards;

  /**
   * Events Text.
   */
  private String eventsText = StringUtils.EMPTY;
  /**
   * The start date.
   */
  private String currentDate;

  /**
   * The end date.
   */
  private String endDate;
  /**
   * Locale.
   */
  private String locale;
  /**
   * List of seasons authored.
   */
  private List<String> totalSeasons;

  /**
   * Cards.
   */
  @Expose
  private List<EventsCard> cards;

  /**
   * images of 3 events per season.
   */
  private List<Image> eventImages;

  /** event Count. */
  private int eventCount;

  /** event array index. */
  private int indexValue = 0;

  /** total columns. */
  @Expose
  private int columns = 0;

  /**
   * total columns.
   */
  private Boolean noCards;

  /**
   * init method of sling model.
   */
  @PostConstruct
  protected void init() {
    if (null != festivalcards) {
      // list of all season types authored
      totalSeasons = new ArrayList<>();
      for (int i = 0; i < festivalcards.size(); i++) {
        totalSeasons.add(festivalcards.get(i).getSeasonType());
      }
      LOGGER.debug("Total Seasons authored -- > ", totalSeasons);

      //season starts from and to date
      currentDate = LocalDate.now().toString();
      endDate = LocalDate.now().plusYears(2).toString();

      InheritanceValueMap ivm = new HierarchyNodeInheritanceValueMap(currentPage.getContentResource());
      locale = ivm.getInherited(JcrConstants.JCR_LANGUAGE, Constants.DEFAULT_LOCALE);
      LOGGER.debug("*** Local value is --> ", locale);

      if (StringUtils.isNotBlank(eventsLabel)) {
        eventsText = eventsLabel;
      } else {
        ResourceBundle i18n = i18nProvider.getResourceBundle(new Locale(locale));
        eventsText = i18n.getString(EVENTS);
      }
      EventsRequestParams eventsRequestParams = new EventsRequestParams();
      eventsRequestParams.setLocale(locale);
      eventsRequestParams.setStartDate(currentDate);
      eventsRequestParams.setEndDate(endDate);
      eventsRequestParams.setSeason(totalSeasons);
      eventsRequestParams.setOffset(0);
      eventsRequestParams.setLimit(Integer.MAX_VALUE);
      LOGGER.debug("*** Event Request Parameters -->", eventsRequestParams);

      //hit events service to get all the event's data associated with season types.
      try {
        EventListModel model = eventService.getFilteredEvents(eventsRequestParams);
        List<EventDetail> data = model.getData();
        // for short names of the months.
        String[] displayMonthName = new DateFormatSymbols().getShortMonths();
        int count = 0;
        cards = new ArrayList<>();
        //iterate over all season types to make list of events associated.
        for (String seasonName : totalSeasons) {
          eventImages = new ArrayList<>();

          // Find events for the current season or on season 'all-seasons'
          // And with startDate & endDate between currentDate & endDate
          final List<EventDetail> seasonEvents = getSeasonEvents(seasonName, data);

          // Update event count
          eventCount = seasonEvents.size();
          eventImages =
              IntStream.range(0, Math.min(MAX_EVENT_IMAGES, seasonEvents.size()))
                  .mapToObj(
                      i -> {
                        final Image eventImage = new Image();
                        eventImage.setFileReference(seasonEvents.get(i).getImage().getSrc());
                        eventImage.setAlt(seasonEvents.get(i).getImage().getAlt());
                        return eventImage;
                      })
                  .collect(Collectors.toList());

          // Get first season event
          final EventDetail firstSeasonEvent = getFirstSeasonEvent(seasonName, data);

          // Get last season event
          final EventDetail lastSeasonEvent = getLastSeasonEvent(seasonName, data);

          LOGGER.debug("*** Event Count for Season " + seasonName + "is = ", eventCount);
          if (eventCount > 0) {
            if (eventCount == 1) {
              getSingleEventCards(firstSeasonEvent, lastSeasonEvent, displayMonthName, count, seasonName);
            } else {
              getMultiEventCards(firstSeasonEvent, lastSeasonEvent, displayMonthName, count, seasonName);
            }
          }
          count++;
        }
      } catch (RepositoryException e) {
        LOGGER.error("Error in getting module:{}, {}", Constants.EVENT_DETAIL_RES_TYPE, e.getLocalizedMessage(), e);
      }

      columns = cards.size();
    } else {
      noCards = true;
    }
  }

  /**
   * handle the creation of multi events cards.
   *
   * @param firstEvent The first event of the season.
   * @param lastEvent The last event of the season.
   * @param displayMonthName Array of the name of the months
   * @param count The index of the current season
   * @param seasonId The season ID
   */
  private void getMultiEventCards(
      final EventDetail firstEvent,
      final EventDetail lastEvent,
      final String[] displayMonthName,
      final int count,
      final String seasonId) {
    LOGGER.debug("Inside getMultiEventCards() method of class SeasonsAndFestivalsModel.");
    String filterPage;
    final EventsCard objMultiEveCard = new EventsCard();
    objMultiEveCard.setSeasonsId(seasonId);
    objMultiEveCard.setTitle(festivalcards.get(count).getTitleSeason());
    objMultiEveCard.setDescription(festivalcards.get(count).getSeasonDescription());
    final Image cardImage = new Image();
    cardImage.setFileReference(festivalcards.get(count).getFileReference());
    cardImage.setS7fileReference(festivalcards.get(count).getS7fileReference());
    objMultiEveCard.setImage(cardImage);
    objMultiEveCard.setAltText(festivalcards.get(count).getAltText());

    if (StringUtils.isNotEmpty(festivalcards.get(count).getFiltersPath())) {
      filterPage =
          LinkUtils.getAuthorPublishUrl(currentPage.getContentResource().getResourceResolver(),
            festivalcards.get(count).getFiltersPath(), settingsService.getRunModes().contains(Externalizer.PUBLISH));
      objMultiEveCard.setFiltersPath(filterPage);
    }

    objMultiEveCard.setEventImages(eventImages);
    objMultiEveCard.setEventCount(eventCount);
    objMultiEveCard.setEventsText(eventsText);

    if (firstEvent != null) {
      int startYear = firstEvent.getStartDateCal().getWeekYear();
      objMultiEveCard.setStartYear(startYear);
      if (null != firstEvent.getStartDateMonth() && null != firstEvent.getStartDateDay()) {
        String startMonthName =
            displayMonthName[Integer.parseInt(firstEvent.getStartDateMonth()) - 1];
        objMultiEveCard.setStartMonthDisplayName(startMonthName);
        objMultiEveCard.setStartDate(firstEvent.getStartDateDay());
      }
    }
    if (lastEvent != null) {
      if (null != lastEvent.getCalendarEndDate()
          && null != lastEvent.getEndDateMonth()
          && null != lastEvent.getEndDateDay()) {
        int endYear = lastEvent.getEndDateCal().getWeekYear();
        String endMonthName = displayMonthName[Integer.parseInt(lastEvent.getEndDateMonth()) - 1];
        objMultiEveCard.setEndMonthDisplayName(endMonthName);
        objMultiEveCard.setEndDate(lastEvent.getEndDateDay());
        objMultiEveCard.setEndYear(endYear);
      }
    }
    cards.add(objMultiEveCard);
    LOGGER.debug("*** List of Multiple Event Cards --> {}", cards);
  }

  /**
   * handle the creation of single events cards.
   *
   * @param firstEvent The first event of the season.
   * @param lastEvent The last event of the season.
   * @param displayMonthName Array of the name of the months
   * @param count The index of the current season
   * @param seasonId The season ID
   */
  private void getSingleEventCards(
      final EventDetail firstEvent,
      final EventDetail lastEvent,
      final String[] displayMonthName,
      final int count,
      final String seasonId) {
    LOGGER.debug("Inside getSingleEventCards() method of class SeasonsAndFestivalsModel.");
    String filterPage;
    final EventsCard objSingleEveCard = new EventsCard();
    objSingleEveCard.setSeasonsId(seasonId);

    if (firstEvent != null) {
      int startYear = firstEvent.getStartDateCal().getWeekYear();
      objSingleEveCard.setStartYear(startYear);
      if (null != firstEvent.getStartDateMonth() && null != firstEvent.getStartDateDay()) {
        String startMonthName = displayMonthName[Integer.parseInt(firstEvent.getStartDateMonth()) - 1];
        objSingleEveCard.setStartMonthDisplayName(startMonthName);
        objSingleEveCard.setStartDate(firstEvent.getStartDateDay());
      }
    }
    if (lastEvent != null) {
      if (null != lastEvent.getCalendarEndDate()
          && null != lastEvent.getEndDateMonth()
          && null != lastEvent.getEndDateDay()) {
        int endYear = lastEvent.getEndDateCal().getWeekYear();
        String endMonthName = displayMonthName[Integer.parseInt(lastEvent.getEndDateMonth()) - 1];
        objSingleEveCard.setEndMonthDisplayName(endMonthName);
        objSingleEveCard.setEndDate(lastEvent.getEndDateDay());
        objSingleEveCard.setEndYear(endYear);
      }
    }

    objSingleEveCard.setTitle(festivalcards.get(count).getTitleSeason());
    objSingleEveCard.setDescription(festivalcards.get(count).getSeasonDescription());
    final Image cardImage = new Image();
    cardImage.setFileReference(festivalcards.get(count).getFileReference());
    cardImage.setS7fileReference(festivalcards.get(count).getS7fileReference());
    objSingleEveCard.setImage(cardImage);
    objSingleEveCard.setAltText(festivalcards.get(count).getAltText());

    if (StringUtils.isNotEmpty(festivalcards.get(count).getFiltersPath())) {
      filterPage =
          LinkUtils.getAuthorPublishUrl(currentPage.getContentResource().getResourceResolver(),
            festivalcards.get(count).getFiltersPath(), settingsService.getRunModes().contains(Externalizer.PUBLISH));
      objSingleEveCard.setFiltersPath(filterPage);
    }

    cards.add(objSingleEveCard);
    LOGGER.debug("*** List of Single Event Cards --> {}", cards);
  }

  /**
   * Filter the events to only return the season events.
   *
   * @param seasonName season name.
   * @param data list.
   * @return List of season events.
   */
  private List<EventDetail> getSeasonEvents(final String seasonName, final List<EventDetail> data) {
    LOGGER.debug("Inside getSeasonEvents method of class SeasonsAndFestivalsModel.");

    // Find events for the current season or on season 'all-seasons'
    // And with startDate & endDate between currentDate & endDate
    final List<EventDetail> seasonEvents =
        Optional.ofNullable(data).orElse(Collections.emptyList()).stream()
            .filter(e -> e != null)
            .filter(
                e -> {
                  if (null != e.getCalendarStartDate() && null != e.getCalendarEndDate()) {
                    return CommonUtils.isDateBetweenStartEnd(
                                currentDate,
                                endDate,
                                e.getCalendarStartDate(),
                                e.getCalendarEndDate())
                            && seasonName.equals(e.getSeasonId())
                            && null != e.getSeasonId()
                        || e.getSeasonId().equals("all-seasons");
                  } else if (null != e.getCalendarStartDate() && null == e.getCalendarEndDate()) {
                    return CommonUtils.isOneDayEvent(currentDate, endDate, e.getCalendarStartDate())
                            && seasonName.equals(e.getSeasonId())
                            && null != e.getSeasonId()
                        || e.getSeasonId().equals("all-seasons");
                  }
                  return false;
                })
            .sorted(Comparator.comparing((EventDetail e) -> e.getCalendarStartDate()))
            .collect(Collectors.toList());

    return seasonEvents;
  }

  /**
   * Returns the first event by start date.
   *
   * @param seasonName season name.
   * @param data list.
   * @return The first event.
   */
  private EventDetail getFirstSeasonEvent(final String seasonName, final List<EventDetail> data) {
    LOGGER.debug("Inside getFirstSeasonEvent method of class SeasonsAndFestivalsModel.");

    // Find events for the current season or on season 'all-seasons'
    // And with startDate & endDate between currentDate & endDate
    final List<EventDetail> seasonEvents = getSeasonEvents(seasonName, data);

    return Optional.ofNullable(seasonEvents).orElse(Collections.emptyList()).stream()
        .min(Comparator.comparing((EventDetail e) -> e.getCalendarStartDate()))
        .orElse(null);
  }

  /**
   * Returns the last event by end date.
   *
   * @param seasonName season name.
   * @param data list.
   * @return The last event.
   */
  private EventDetail getLastSeasonEvent(final String seasonName, final List<EventDetail> data) {
    LOGGER.debug("Inside getFirstSeasonEvent method of class SeasonsAndFestivalsModel.");

    // Find events for the current season or on season 'all-seasons'
    // And with startDate & endDate between currentDate & endDate
    final List<EventDetail> seasonEvents = getSeasonEvents(seasonName, data);

    return Optional.ofNullable(seasonEvents).orElse(Collections.emptyList()).stream()
      .max(Comparator.comparing((EventDetail e) -> e.getCalendarEndDate()))
      .orElse(null);
  }

  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }
}
