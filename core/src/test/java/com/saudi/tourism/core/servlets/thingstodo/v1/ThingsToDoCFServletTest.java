package com.saudi.tourism.core.servlets.thingstodo.v1;

import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoRequest;
import com.saudi.tourism.core.services.thingstodo.v1.FetchThingsToDoResponse;
import com.saudi.tourism.core.services.thingstodo.v1.ThingToDoModel;
import com.saudi.tourism.core.services.thingstodo.v1.ThingsToDoCFService;
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
import java.util.Collections;

import static com.saudi.tourism.core.utils.PrimConstants.PN_LOCALE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ThingsToDoCFServletTest {

  @Mock private ThingsToDoCFService thingsToDoCFService;

  @InjectMocks private ThingsToDoCFServlet servlet = new ThingsToDoCFServlet();

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

    final var thingToDo =
        ThingToDoModel.builder()
            .title("Food Culture Festival")
            .subtitle("Subtitle")
            .lat("24.672612957506793")
            .lng("46.625595")
            .hideFavorite(Boolean.TRUE)
            .hideShareIcon(Boolean.FALSE)
            .build();

    when(thingsToDoCFService.getFilteredThingsToDo(any(FetchThingsToDoRequest.class)))
        .thenReturn(FetchThingsToDoResponse.builder().data(Arrays.asList(thingToDo)).build());

    // Act
    servlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":200,\"message\":\"success\",\"response\":{\"data\":[{\"title\":\"Food Culture Festival\",\"subtitle\":\"Subtitle\",\"lat\":\"24.672612957506793\",\"lng\":\"46.625595\",\"hideFavorite\":true,\"hideShareIcon\":false}]}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnTechnicalExceptionIfEventsCFServiceRaised(final AemContext aemContext)
      throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    when(thingsToDoCFService.getFilteredThingsToDo(any(FetchThingsToDoRequest.class)))
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
