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

@ExtendWith(AemContextExtension.class) public class EventsServletTest {

  private EventsServlet eventsServlet;

  private EventService eventService;

  @BeforeEach public void setUp(AemContext context) {
    eventService = Mockito.mock(EventService.class);
    eventsServlet = new EventsServlet();
    Utils.setInternalState(eventsServlet, "eventService", eventService);
  }

  @Test public void testNoParams(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    eventsServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void test(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    context.request().setParameterMap(parameters);
    Utils.setInternalState(eventsServlet, "eventService", null);
    eventsServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

  @Test public void testGetAllEvents(AemContext context)
      throws IllegalArgumentException, ServletException, IOException {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("locale", "en");
    parameters.put("all", "true");
    context.request().setParameterMap(parameters);
    eventsServlet.doGet(context.request(), context.response());
    Assertions.assertNotNull(context.response());
  }

}
