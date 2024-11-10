package com.saudi.tourism.core.servlets;

import com.saudi.tourism.core.models.components.events.EventAppFilterModel;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;

@ExtendWith(AemContextExtension.class) public class EventsAppFiltersServletTest {

  private EventsAppFiltersServlet eventsAppFiltersServlet;

  private EventService eventService;

  @BeforeEach public void setUp(AemContext context) {

    eventService = Mockito.mock(EventService.class);
    eventsAppFiltersServlet = new EventsAppFiltersServlet();

    Mockito.when(eventService.getEventAppFilters(any())).thenReturn(new EventAppFilterModel());
    Utils.setInternalState(eventsAppFiltersServlet, "eventService", eventService);

  }

  @Test public void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    eventsAppFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

  @Test public void test(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);
    eventsAppFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

  @Test public void testError(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "zh");
    context.request().setParameterMap(parameters);
    Utils.setInternalState(eventsAppFiltersServlet, "eventService", null);
    eventsAppFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

}
