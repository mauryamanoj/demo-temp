package com.saudi.tourism.core.services.impl;

import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.ExperienceService;
import com.saudi.tourism.core.services.ResourceExporterService;
import com.saudi.tourism.core.services.RoadTripScenariosService;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class FavoritesServiceImplTest {

  @Mock
  private SaudiTourismConfigs saudiTourismConfig;

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private ExperienceService experienceService;

  @Mock
  private ResourceExporterService resourceExporterService;

  @Mock
  private RoadTripScenariosService roadTripScenariosService;

  @Mock
  private RunModeService runModeService;

  private FavoritesServiceImpl favoritesService = new FavoritesServiceImpl();

  @BeforeEach
  void setUpt(final AemContext aemContext){
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfig);
    aemContext.registerService(AdminSettingsService.class, adminSettingsService);
    aemContext.registerService(ExperienceService.class, experienceService);
    aemContext.registerService(ResourceExporterService.class, resourceExporterService);
    aemContext.registerService(RoadTripScenariosService.class, roadTripScenariosService);
    aemContext.registerService(RunModeService.class, runModeService);
    aemContext.registerInjectActivateService(favoritesService);

    when(saudiTourismConfig.getFavBaseUrl()).thenReturn("/bin/api/v2/");
  }

  @Test
  void testFavoritesApiEndpoints(){
    //Arrange
    //Act
    final FavoritesApiEndpoints favoritesApiEndpoints = favoritesService.computeFavoritesApiEndpoints("en");

    //Assert
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", favoritesApiEndpoints.getGetFavUrl());
    assertEquals("/bin/api/v2/user/update.favorites", favoritesApiEndpoints.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", favoritesApiEndpoints.getDeleteFavUrl());
  }
}