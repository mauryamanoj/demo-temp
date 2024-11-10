package com.saudi.tourism.core.servlets.geo.v1;

import com.saudi.tourism.core.services.geo.v1.CountriesService;
import com.saudi.tourism.core.services.geo.v1.Country;
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
import java.util.Collections;
import java.util.List;

import static com.saudi.tourism.core.utils.Constants.PN_LOCALE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CountriesServletTest {

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @Mock private transient CountriesService countriesService;

  @InjectMocks private CountriesServlet countriesServlet = new CountriesServlet();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(CountriesService.class, countriesService);

    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnBadRequestIfLocaleNotProvided() throws ServletException, IOException {
    // Arrange

    // Act
    countriesServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(400, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":400,\"message\":\"Parameters [locale] is empty or null\",\"response\":{}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnCountriesIfLocaleIsProvided(final AemContext aemContext)
      throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    final Country country =
        Country.builder()
            .countryName("Andorra")
            .flag("/content/dam/flags/Andorra.png")
            .visaGroup("eligible")
            .build();

    when(countriesService.fetchListOfCountries(eq("en"))).thenReturn(List.of(country));

    // Act
    countriesServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":200,\"message\":\"success\",\"response\":[{\"countryName\":\"Andorra\",\"flag\":\"/content/dam/flags/Andorra.png\",\"type\":\"eligible\",\"visaGroup\":\"eligible\"}]}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnTechnicalExceptionIfCountriesServiceRaised(final AemContext aemContext)
      throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    when(countriesService.fetchListOfCountries(eq("en")))
        .thenThrow(new RuntimeException("An exception happened"));

    // Act
    countriesServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(500, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":500,\"message\":\"An exception happened\",\"response\":{}}",
        response.getOutputAsString());
  }
}
