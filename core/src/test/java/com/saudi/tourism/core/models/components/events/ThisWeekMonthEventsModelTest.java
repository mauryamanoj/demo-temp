package com.saudi.tourism.core.models.components.events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.scripting.SlingBindings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ThisWeekMonthEventsModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/saudi-calendar";
  private static final String THIS_WEEK_RESOURCE_PATH =
      "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/events_cards_1570274218";

  private static final String THIS_MONTH_RESOURCE_PATH =
    "/content/sauditourism/en/calendar/saudi-calendar/jcr:content/root/responsivegrid/events_cards_1570274";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/pages/saudi-calendar.json", PAGE_PATH);

    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events/");
  }

  @Test
  void shouldReturnThisWeekEventsData(final AemContext aemContext) {
    // Arrange
    aemContext.currentResource(THIS_WEEK_RESOURCE_PATH);
    final SlingBindings bindings = (SlingBindings) aemContext.request().getAttribute(SlingBindings.class.getName());
    bindings.put("cardStyle", "this-week");

    // Act
    final ThisWeekMonthEventsModel model = aemContext.request().adaptTo(ThisWeekMonthEventsModel.class);
    final String json = model.getJson();
    final ThisWeekMonthEventsModel data = gson.fromJson(json, ThisWeekMonthEventsModel.class);

    // Assert
    assertEquals("This Week", data.getHeadline());
    assertEquals("calendar", data.getCardType());
    assertEquals("this-week", data.getCardStyle());
    assertEquals("/bin/api/v1/events/", data.getApiUrl());
    assertEquals("View All", data.getLink().getCopy());
    assertEquals("/content/sauditourism/en/calendar", data.getLink().getUrl());
    assertFalse(data.getLink().isTargetInNewWindow());
  }

  @Test
  void shouldReturnThisMonthEventsData(final AemContext aemContext) {
    // Arrange
    aemContext.currentResource(THIS_MONTH_RESOURCE_PATH);
    final SlingBindings bindings = (SlingBindings) aemContext.request().getAttribute(SlingBindings.class.getName());
    bindings.put("cardStyle", "this-month");

    // Act
    final ThisWeekMonthEventsModel model = aemContext.request().adaptTo(ThisWeekMonthEventsModel.class);
    final String json = model.getJson();
    final ThisWeekMonthEventsModel data = gson.fromJson(json, ThisWeekMonthEventsModel.class);

    // Assert
    assertEquals("This Month", data.getHeadline());
    assertEquals("calendar", data.getCardType());
    assertEquals("this-month", data.getCardStyle());
    assertEquals("/bin/api/v1/events/", data.getApiUrl());
    assertEquals("View All", data.getLink().getCopy());
    assertEquals("/content/sauditourism/en/calendar", data.getLink().getUrl());
    assertFalse(data.getLink().isTargetInNewWindow());
  }
}
