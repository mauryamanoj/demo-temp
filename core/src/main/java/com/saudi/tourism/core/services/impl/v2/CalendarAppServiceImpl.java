package com.saudi.tourism.core.services.impl.v2;

import com.saudi.tourism.core.beans.nativeapp.CalendarData;
import com.saudi.tourism.core.beans.nativeapp.CalendarResponse;
import com.saudi.tourism.core.beans.nativeapp.EventsDataObj;
import com.saudi.tourism.core.models.components.CalendarAppModel;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.v2.CalendarAppService;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service fetch the event's data for Calendar page.
 */
@Component(service = CalendarAppService.class,
      immediate = true,
      property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + "Calendar App Service"})
@Slf4j
public class CalendarAppServiceImpl implements CalendarAppService {


  /**
   * NEXT Month.
   */
  private static final int NEXT_MONTH = 1;
  /**
   * Third Month.
   */
  private static final int THIRD_MONTH = 3;
  /**
   * ID.
   */
  public static final String ID = "id";
  /**
   * this week.
   */
  public static final String THIS_WEEK = "this-week";
  /**
   * this month.
   */
  public static final String THIS_MONTH = "this-month";
  /**
   * custom.
   */
  public static final String CUSTOM = "custom";

  /**
   * Weekly Format.
   */
  public static final String WEEKLY_FORMAT = "dd MMMM yyyy";

  /**
   * Monthly Format.
   */
  public static final String MONTHLY_FORMAT = "MMMM yyyy";
  /**
   * Start date param.
   */
  public static final String START_DATE = "startDate";
  /**
   * End date param.
   */
  public static final String END_DATE = "endDate";
  /**
   * locale param.
   */
  public static final String LOCALE = "locale";

  /**
   * limit value for calendar.
   */
  public static final int LIMIT_CALENDAR = 5;


  /**
   * adminService.
   */
  @Reference
  private AdminSettingsService adminService;

  @Override
  public List<EventsDataObj> getUpcomingEvents(EventListModel model, String locale) {
    LocalDate nextMonthLocalDate = CommonUtils.getMonthDate(NEXT_MONTH);
    String nextMonthStartDate = nextMonthLocalDate.with(TemporalAdjusters.firstDayOfMonth()).toString();
    LocalDate thirdMonthLocalDate = CommonUtils.getMonthDate(THIRD_MONTH);
    String thirdMonthEndDate = thirdMonthLocalDate.with(TemporalAdjusters.lastDayOfMonth()).toString();
    List<EventsDataObj> upComingMonthEvents = new ArrayList<>();
    getEventsData(model, nextMonthStartDate, thirdMonthEndDate, upComingMonthEvents);

    return upComingMonthEvents.stream().limit(LIMIT_CALENDAR).collect(Collectors.toList());
  }

  @Override
  public List<EventsDataObj> getWeeklyEvents(EventListModel model, String locale) {
    String thisWeekStartDate = CommonUtils.getCurrentDate();
    String thisWeekEndDate = CommonUtils.getLastDayOfWeek().toString();
    List<EventsDataObj> thisWeekEvents = new ArrayList<>();
    getEventsData(model, thisWeekStartDate, thisWeekEndDate, thisWeekEvents);

    return thisWeekEvents.stream().limit(LIMIT_CALENDAR).collect(Collectors.toList());
  }

  @Override
  public List<EventsDataObj> getMonthlyEvents(EventListModel model, String locale) {
    String thisMonthStartDate = CommonUtils.getCurrentDate();
    String thisMonthEndDate = CommonUtils.getLastDayOfMonth().toString();
    List<EventsDataObj> thisMonthEvents = new ArrayList<>();
    getEventsData(model, thisMonthStartDate, thisMonthEndDate, thisMonthEvents);

    return thisMonthEvents.stream().limit(LIMIT_CALENDAR).collect(Collectors.toList());
  }

  @Override
  public EventsDataObj getEventObjData(EventDetail event) {
    EventsDataObj eventsDataObj = new EventsDataObj();
    eventsDataObj.setFeatureEventImage(event.getFeatureEventImage());
    eventsDataObj.setS7featureEventImage(event.getS7featureEventImage());
    eventsDataObj.setTitle(event.getTitle());
    eventsDataObj.setStartDate(event.getCalendarStartDate());
    eventsDataObj.setEndDate(event.getCalendarEndDate());
    eventsDataObj.setCity(event.getCity());
    eventsDataObj.setAppId(event.getAppId());
    eventsDataObj.setId(event.getId());
    eventsDataObj.setFavId(event.getFavId());
    eventsDataObj.setLink(event.getLink());
    eventsDataObj.setFeatured(event.isFeatured());
    return eventsDataObj;
  }

  @Override
  public void getEventsData(EventListModel model, String filterStartDate,
                            String filterEndDate, List<EventsDataObj> lisOfEventsDataObj) {
    List<EventDetail> data = model.getData();
    for (EventDetail event : data) {
      if (null != event.getCalendarEndDate()
            && CommonUtils.isDateBetweenStartEnd(filterStartDate, filterEndDate,
            event.getCalendarStartDate(), event.getCalendarEndDate())) {
        EventsDataObj eventsDataObj = getEventObjData(event);
        lisOfEventsDataObj.add(eventsDataObj);
      } else if (null == event.getCalendarEndDate()
            && CommonUtils.isOneDayEvent(filterStartDate, filterEndDate, event.getCalendarStartDate())) {
        EventsDataObj eventsDataObj = getEventObjData(event);
        lisOfEventsDataObj.add(eventsDataObj);
      }
    }
  }

  @Override
  public CalendarResponse getCalendarResponse(EventListModel model, String locale) {
    List<CalendarData> calDataList = new ArrayList<>();
    List<String> weeklyFilters = new ArrayList<>();
    List<String> monthlyFilters = new ArrayList<>();
    List<String> upcomingMonthFilters = new ArrayList<>();
    weeklyFilters.add(THIS_WEEK);
    monthlyFilters.add(THIS_MONTH);
    upcomingMonthFilters.add(CUSTOM);
    CalendarAppModel calendaAppModel = adminService.getCalendarAppData(locale);

    String subtitleSectionWeeklyCurrentDate = CommonUtils.dateToString(LocalDate.now(), WEEKLY_FORMAT, locale);
    String subtitleSectionWeeklyLastDayOfWeek =
        CommonUtils.dateToString(CommonUtils.getLastDayOfWeek(), WEEKLY_FORMAT, locale);

    if (Constants.ARABIC_LOCALE.equals(locale)) {
      subtitleSectionWeeklyCurrentDate = "\u200F" + subtitleSectionWeeklyCurrentDate; // Fixing the mix of RTL / LTR
    }

    CalendarData sectionWeekly = CalendarData.builder().name(calendaAppModel.getWeekHeading())
          .data(getWeeklyEvents(model, locale))
        .subtitle(subtitleSectionWeeklyCurrentDate + Constants.SPACE_MINUS_DASH
            + subtitleSectionWeeklyLastDayOfWeek)
        .filterPath(buildFilterPath(locale, CommonUtils.getCurrentDate(), CommonUtils.getLastDayOfWeek().toString()))
        .filter(weeklyFilters).build();

    String subtitleSectionMonthlyCurrentMonthAndYear =
        CommonUtils.dateToString(LocalDate.now(), MONTHLY_FORMAT, locale);

    CalendarData sectionMonthly = CalendarData.builder().name(calendaAppModel.getMonthHeading())
          .data(getMonthlyEvents(model, locale))
          .subtitle(subtitleSectionMonthlyCurrentMonthAndYear)
        .filterPath(buildFilterPath(locale, CommonUtils.getCurrentDate(), CommonUtils.getLastDayOfMonth().toString()))
        .filter(monthlyFilters).build();
    String subtitleSectionUpcomingMonthNextMonth =
        CommonUtils.dateToString(CommonUtils.getMonthDate(NEXT_MONTH), MONTHLY_FORMAT, locale);
    String subtitleSectionUpcomingMonthNextThreeMonth =
        CommonUtils.dateToString(CommonUtils.getMonthDate(THIRD_MONTH), MONTHLY_FORMAT, locale);

    CalendarData sectionUpcomingMonths = CalendarData.builder()
          .name(calendaAppModel.getUpComingMonthsHeading())
          .data(getUpcomingEvents(model, locale))
        .subtitle(subtitleSectionUpcomingMonthNextMonth + Constants.SPACE_MINUS_DASH
            + subtitleSectionUpcomingMonthNextThreeMonth)
        .filterPath(buildFilterPath(locale,
            CommonUtils.getMonthDate(NEXT_MONTH).with(TemporalAdjusters.firstDayOfMonth()).toString(),
            CommonUtils.getMonthDate(THIRD_MONTH).with(TemporalAdjusters.lastDayOfMonth()).toString()))
        .filter(upcomingMonthFilters).build();
    calDataList.add(sectionWeekly);
    calDataList.add(sectionMonthly);
    calDataList.add(sectionUpcomingMonths);
    CalendarResponse calendarRes = new CalendarResponse();
    calendarRes.setSection(calDataList);
    return calendarRes;
  }


  /**
   * utils to builder filter path.
   *
   * @param locale    locale
   * @param startDate startDate
   * @param endDate   endDate
   * @return buildFilterPath
   */
  private String buildFilterPath(String locale, String startDate, String endDate) {
    URIBuilder uriBuilder = new URIBuilder();
    uriBuilder.addParameter(LOCALE, locale);
    uriBuilder.addParameter(START_DATE, startDate);
    uriBuilder.addParameter(END_DATE, endDate);

    return uriBuilder.toString();
  }

}
