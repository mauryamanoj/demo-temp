package com.saudi.tourism.core.servlets;

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

@ExtendWith(AemContextExtension.class) public class EventsFilterServletTest {

  private EventsFiltersServlet eventsFiltersServlet;

  private EventService eventService;

  @BeforeEach public void setUp(AemContext context) {

    eventService = Mockito.mock(EventService.class);
    eventsFiltersServlet = new EventsFiltersServlet();
    Utils.setInternalState(eventsFiltersServlet, "eventService", eventService);

  }

  @Test public void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    eventsFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }
  @Test public void test(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);
    eventsFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }  @Test public void testError(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "zh");
    context.request().setParameterMap(parameters);
    Utils.setInternalState(eventsFiltersServlet, "eventService", null);
    eventsFiltersServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());

  }

}
