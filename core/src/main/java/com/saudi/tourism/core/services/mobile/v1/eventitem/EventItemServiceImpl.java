package com.saudi.tourism.core.services.mobile.v1.eventitem;

import com.saudi.tourism.core.models.mobile.components.mobilelayout.v1.SectionsModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import com.saudi.tourism.core.services.mobile.v1.MobileLayoutService;
import com.saudi.tourism.core.services.mobile.v1.calendar.CalendarBottomCards;
import com.saudi.tourism.core.services.mobile.v1.calendar.CalendarItem;
import com.saudi.tourism.core.services.mobile.v1.calendar.FetchCalendarRequest;
import com.saudi.tourism.core.servlets.mobile.v1.MobileRequestParams;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.MobileUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.saudi.tourism.core.services.mobile.v1.eventitem.EventItemServiceImpl.SERVICE_DESCRIPTION;

@Component(
    service = EventItemService.class,
    immediate = true,
    property = {Constants.SERVICE_DESCRIPTION + Constants.EQUAL + SERVICE_DESCRIPTION})
@Slf4j
public class EventItemServiceImpl implements EventItemService {
  /** This Service description for OSGi. */
  static final String SERVICE_DESCRIPTION = "All Events Section Items Service";

  /** Saudi Tourism Config. */
  @Reference private SaudiTourismConfigs saudiTourismConfigs;

  /** User Service. */
  @Reference private UserService userService;

  /** MobileLayoutService Service. */
  @Reference private MobileLayoutService mobileLayoutService;

  @Override
  public List<CalendarItem> fetchAllEventItems(@NonNull String locale) {
    final var allEventsConfigResourcePath =
        MessageFormat.format(saudiTourismConfigs.getAllEventsSectionPath(), locale);
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var alleEventsConfig = resourceResolver.getResource(allEventsConfigResourcePath);
      if (Objects.isNull(alleEventsConfig)) {
        LOGGER.warn("All Events Section node not found under %s", allEventsConfigResourcePath);
        return null;
      }

      final var allEventSectionPath =
          alleEventsConfig.getValueMap().get("allEventsPath", String.class);
      if (StringUtils.isBlank(allEventSectionPath)) {
        return null;
      }


      var sectionId = MobileUtils.extractSectionId(allEventSectionPath);
      MobileRequestParams mobileRequestParams = new MobileRequestParams();
      mobileRequestParams.setLocale(locale);
      mobileRequestParams.setSectionId(sectionId);
      SectionsModel section = mobileLayoutService.getSectionById(mobileRequestParams);
      if (section == null) {
        return null;
      }
      final var items = section.getSection().getItems();
      if (items == null) {
        return null;
      }

      return items.stream()
          .filter(Objects::nonNull)
          .map(i -> i.getDate())
          .filter(Objects::nonNull)
          .map(
              d ->
                  CalendarItem.builder()
                      .startDate(d.getStartDate())
                      .endDate(d.getEndDate())
                      .build())
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
  }

  public CalendarBottomCards getEventCard(@NonNull FetchCalendarRequest request) {
    final var allEventsConfigResourcePath =
        MessageFormat.format(saudiTourismConfigs.getAllEventsSectionPath(), request.getLocale());
    try (ResourceResolver resourceResolver = userService.getResourceResolver()) {
      final var alleEventsConfig = resourceResolver.getResource(allEventsConfigResourcePath);
      if (Objects.isNull(alleEventsConfig)) {
        LOGGER.error("All Events Section node not found under %s", allEventsConfigResourcePath);
        return null;
      }

      final var allEventSectionPath =
          alleEventsConfig.getValueMap().get("allEventsPath", String.class);
      if (StringUtils.isBlank(allEventSectionPath)) {
        return null;
      }

      final var dateFormatter = new SimpleDateFormat(Constants.FORMAT_DATE);
      Calendar startDate = null;
      Calendar endDate = null;

      if (StringUtils.isNotBlank(request.getStartDate())) {
        startDate = Calendar.getInstance();
        startDate.setTime(dateFormatter.parse(request.getStartDate()));
      }

      if (StringUtils.isNotBlank(request.getEndDate())) {
        endDate = Calendar.getInstance();
        endDate.setTime(dateFormatter.parse(request.getEndDate()));
      }

      return CalendarBottomCards.builder()
          .type("event")
          .title("")
          .startDate(startDate)
          .endDate(endDate)
          .redirectionId(MobileUtils.extractSectionId(allEventSectionPath))
          .build();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }
}
