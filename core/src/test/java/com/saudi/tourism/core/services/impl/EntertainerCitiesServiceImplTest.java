package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.beans.nativeapp.RegionCityEntertainer;
import com.saudi.tourism.core.login.services.SSIDFavouritesTripsConfig;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.jcr.Node;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EntertainerCitiesServiceImplTest {

  private EntertainerCitiesServiceImpl entertainerCitiesServiceImpl;

  private ResourceResolver resourceResolver;

  @Mock
  private RegionCityService regionCityService;

  @Mock
  private transient SSIDFavouritesTripsConfig config;

  @Mock
  private UserService userService;

  // Use JCR_MOCK resolver type
  private final AemContext aemContext = new AemContext(ResourceResolverType.JCR_MOCK);



  @BeforeEach
  void setUp() {
    entertainerCitiesServiceImpl = new EntertainerCitiesServiceImpl();

    Map<String, Object> props = new HashMap<>();
    props.put("immediate", "true");
    resourceResolver = aemContext.resourceResolver();
    aemContext.registerService(RegionCityService.class, regionCityService);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerService(SSIDFavouritesTripsConfig.class, config);

    aemContext.registerInjectActivateService(entertainerCitiesServiceImpl, props);

    aemContext.addModelsForClasses(Node.class);

  }

  @Test
  void getCityConfig__shouldReturnListOfCities() {

    // Given
    aemContext.load().json("/pages/global-config.json", "/content/sauditourism/global-configs/Configs");

    List<RegionCity> localizedCityList = new ArrayList<RegionCity>();
    RegionCity regionCity = new RegionCity("id", "name");
    RegionCity regionCityRiyadh = new RegionCity("riyadh", "Riyadh");
    localizedCityList.add(regionCity);
    localizedCityList.add(regionCityRiyadh);

    when(regionCityService.getCities(anyString())).thenReturn(localizedCityList);


  // When
    List<RegionCityEntertainer> results = entertainerCitiesServiceImpl.getCityConfig("en", resourceResolver, "city", false);


    //Then
    assertEquals(2, results.size());

    RegionCityEntertainer riyadhCity =
        results.stream().filter(city -> city.getId().equals("riyadh")).findAny().orElse(null);
    assertNotNull(riyadhCity);
    assertEquals("Riyadh", riyadhCity.getName());
    assertEquals("/content/dam/we-retail/en/activities/biking/cycling_1.jpg", riyadhCity.getImage());
    assertEquals("Riyadh", riyadhCity.getName());
    assertEquals("tabuk-region", riyadhCity.getLocationId());
    assertFalse(riyadhCity.getCoordinates().isEmpty());
    assertTrue(riyadhCity.getDestinationFeatureTags().contains("Agriculture"));
    assertTrue(riyadhCity.getDestinationFeatureTags().contains("Art"));




  }


  @Test
  void getCityConfig__shouldReturnNullIfNoGlobalConfigFound() {

    // Given

    List<RegionCity> localizedCityList = new ArrayList<RegionCity>();
    RegionCity regionCity = new RegionCity("id", "name");
    RegionCity regionCityRiyadh = new RegionCity("riyadh", "Riyadh");
    localizedCityList.add(regionCity);
    localizedCityList.add(regionCityRiyadh);

    when(regionCityService.getCities(anyString())).thenReturn(localizedCityList);

    // When
    List<RegionCityEntertainer> results =
        entertainerCitiesServiceImpl.getCityConfig("en", resourceResolver, "city", false);

    //Then
    assertNull(results);


  }


  @Test
  void getCityConfig__shouldReturnNullIfNoEntertainerCitiesPageConfig() {

    // Given
    aemContext.load().json("/pages/global-config-without-entertainer-config.json", "/content/sauditourism/global-configs/Configs");
    List<RegionCity> localizedCityList = new ArrayList<RegionCity>();
    RegionCity regionCity = new RegionCity("id", "name");
    RegionCity regionCityRiyadh = new RegionCity("riyadh", "Riyadh");
    localizedCityList.add(regionCity);
    localizedCityList.add(regionCityRiyadh);

    when(regionCityService.getCities(anyString())).thenReturn(localizedCityList);

    // When
    List<RegionCityEntertainer> results = entertainerCitiesServiceImpl.getCityConfig("en", resourceResolver, "city", false);

    //Then
    assertNull(results);


  }





}
