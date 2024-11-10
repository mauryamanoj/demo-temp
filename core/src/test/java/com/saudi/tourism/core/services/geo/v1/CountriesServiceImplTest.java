package com.saudi.tourism.core.services.geo.v1;

import com.saudi.tourism.core.cache.Cache;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CountriesServiceImplTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/country-list";
  private static final String RESOURCE_PATH =
      "/content/sauditourism/{0}/country-list/jcr:content/root/responsivegrid/countrylist";

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private UserService userService;

  private CountriesService countriesService = new CountriesServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerInjectActivateService(countriesService);

    aemContext.load().json("/pages/countries-config.json", PAGE_PATH);

    lenient().when(saudiTourismConfigs.getCountriesConfigPath()).thenReturn(RESOURCE_PATH);
    lenient().when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
  }

  @Test
  void fetchListOfCountriesShouldRaiseIfProvideLocaleIsNull() {
    // Arrange

    // Act
    final Exception exception =
        assertThrows(
            NullPointerException.class,
            () -> {
              countriesService.fetchListOfCountries(null);
            });

    // Assert
    assertEquals("locale is marked non-null but is null", exception.getMessage());
  }

  @Test
  void fetchListOfCountriesShouldReturnCountries() {
    // Arrange

    // Act
    final List<Country> result = countriesService.fetchListOfCountries("en");

    // Assert
    assertTrue(CollectionUtils.isNotEmpty(result));
    assertEquals("Afghanistan", result.get(0).getCountryName());
    assertEquals("Albania", result.get(1).getCountryName());
    assertEquals("Algeria", result.get(2).getCountryName());
    assertEquals("American Samoa", result.get(3).getCountryName());
  }
}
