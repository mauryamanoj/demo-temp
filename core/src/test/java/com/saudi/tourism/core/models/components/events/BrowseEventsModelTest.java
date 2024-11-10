package com.saudi.tourism.core.models.components.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class BrowseEventsModelTest {
  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/saudi-calendar";

  private static final String CURRENT_RESOURCE = "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/browse_events";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  void setUp(final AemContext aemContext){
    aemContext.load().json("/pages/saudi-calendar.json", PAGE_PATH);

    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");
  }

  @Test
  void testBrowseEventsModel(final AemContext aemContext) {
    //Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(CURRENT_RESOURCE);

    final String todayDate = LocalDate.now().toString();
    final String thisWeeksEndDate = LocalDate.now().plusDays(6).toString();
    final String thisMonthsEndDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).toString();

    //Act
    final BrowseEventsModel model = aemContext.request().adaptTo(BrowseEventsModel.class);
    final String json = model.getJson();
    final BrowseEventsModel data = gson.fromJson(json, BrowseEventsModel.class);

    //Assert
    assertEquals("Browse Events", data.getHeadline());
    assertEquals("The calendar is empty!", data.getEmptyCalendarTitle());
    assertEquals("We will be adding new events soon. Please check back later", data.getEmptyCalendarSubtitle());
    assertEquals("/bin/api/v1/events", data.getApiUrl());
    assertEquals("Today", data.getTodayEventsText());
    assertEquals(String.format("/content/sauditourism/en/calendar?locale=en&startDate=%s&endDate=%s&type=today", todayDate, todayDate), data.getTodayEventsLink());
    assertEquals("This Week", data.getThisWeekEventsText());
    assertEquals(String.format("/content/sauditourism/en/calendar?locale=en&startDate=%s&endDate=%s&type=this-week", todayDate, thisWeeksEndDate), data.getThisWeekEventsLink());
    assertEquals("This Month", data.getThisMonthEventsText());
    assertEquals(String.format("/content/sauditourism/en/calendar?locale=en&startDate=%s&endDate=%s&type=this-month", todayDate, thisMonthsEndDate), data.getThisMonthEventsLink());
  }
}