package com.saudi.tourism.core.servlets.events.v1;

import com.saudi.tourism.core.services.events.v1.Event;
import com.saudi.tourism.core.services.events.v1.EventsCFService;
import com.saudi.tourism.core.services.events.v1.FetchEventsRequest;
import com.saudi.tourism.core.services.events.v1.FetchEventsResponse;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import static com.saudi.tourism.core.utils.PrimConstants.PN_LOCALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventsCFServletTest {

  @Mock
  private EventsCFService eventsCFService;

  @InjectMocks
  private EventsCFServlet servlet = new EventsCFServlet();

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnBadRequestIfLocaleNotProvided() throws ServletException, IOException {
    // Arrange

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(400, response.getStatus());
    Assertions.assertEquals(
      "{\"code\":400,\"message\":\"Parameters [locale] is empty or null\",\"response\":{}}",
      response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnEventsIfLocaleIsProvided(final AemContext aemContext)
    throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    final var startDate = Calendar.getInstance();
    startDate.set(Calendar.YEAR, 2023);
    startDate.set(Calendar.MONTH, Calendar.NOVEMBER);
    startDate.set(Calendar.DAY_OF_MONTH, 14);
    startDate.set(Calendar.HOUR, 17);
    startDate.set(Calendar.MINUTE, 52);
    startDate.set(Calendar.SECOND, 0);
    startDate.set(Calendar.MILLISECOND, 0);

    final var endDate = (Calendar) startDate.clone();
    endDate.add(Calendar.MONTH, 1);


    final var event = Event.builder()
            .type("Event")
            .title("Food Culture Festival")
            .subtitle("Subtitle")
            .lat("24.672612957506793")
            .lng("46.625595")
            .startDate(startDate)
            .endDate(endDate)
            .hideFavorite(Boolean.TRUE)
            .hideShareIcon(Boolean.FALSE)
            .build();

    when(eventsCFService.getFilteredEvents(any(FetchEventsRequest.class)))
        .thenReturn(FetchEventsResponse.builder().data(Arrays.asList(event)).build());

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":200,\"message\":\"success\",\"response\":{\"data\":[{\"type\":\"Event\",\"title\":\"Food Culture Festival\",\"subtitle\":\"Subtitle\",\"lat\":\"24.672612957506793\",\"lng\":\"46.625595\",\"startDate\":\"2023-11-15T04:52:00.000+00:00\",\"endDate\":\"2023-12-15T04:52:00.000+00:00\",\"hideFavorite\":true,\"hideShareIcon\":false}]}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnTechnicalExceptionIfEventsCFServiceRaised(final AemContext aemContext)
    throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    when(eventsCFService.getFilteredEvents(any(FetchEventsRequest.class)))
      .thenThrow(new RuntimeException("An exception happened"));

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(500, response.getStatus());
    Assertions.assertEquals(
      "{\"code\":500,\"message\":\"An exception happened\",\"response\":{}}",
      response.getOutputAsString());
  }

}