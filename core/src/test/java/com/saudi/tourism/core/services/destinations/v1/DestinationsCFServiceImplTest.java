package com.saudi.tourism.core.services.destinations.v1;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DestinationsCFServiceImplTest {

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private UserService userService;

  private DestinationsCFServiceImpl destinationsService = new DestinationsCFServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerInjectActivateService(destinationsService);
  }

  @Test
  void fetchAllDestinationShouldRaiseIfLocaleNotProvided(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
      destinationsService.fetchAllDestination(null);
    });


    //Assert
    assertEquals("locale is marked non-null but is null", exception.getMessage());
  }

  @Test
  void fetchAllDestinationShouldReturnNullIfNoRootFolder(final AemContext aemContext){
    //Arrange
    when(saudiTourismConfigs.getDestinationsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/destinations");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = destinationsService.fetchAllDestination("en");


    //Assert
    assertNull(result);
  }

  @Test
  void fetchAllDestinationShouldReturnNullIfNoCFsFound(final AemContext aemContext){
    //Arrange
    aemContext.load().json("/components/destinations/root-folder.json", "/content/dam/sauditourism/cf/en/destinations");

    when(saudiTourismConfigs.getDestinationsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/destinations");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = destinationsService.fetchAllDestination("en");


    //Assert
    assertTrue(CollectionUtils.isEmpty(result));
  }

  @Test
  void fetchAllDestinationShouldReturnDestinations(final AemContext aemContext){
    //Arrange
    aemContext.load().json("/components/destinations/root-folder.json", "/content/dam/sauditourism/cf/en/destinations");
    aemContext.load().json("/components/destinations/alahsa.json", "/content/dam/sauditourism/cf/en/destinations/al-ahsa");
    aemContext.load().json("/components/destinations/alula.json", "/content/dam/sauditourism/cf/en/destinations/alula");

    when(saudiTourismConfigs.getDestinationsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/destinations");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = destinationsService.fetchAllDestination("en");


    //Assert
    assertTrue(CollectionUtils.isNotEmpty(result));
    assertEquals(2, result.size());
    assertEquals("al-ahsa", result.get(0).getId());
    assertEquals("alula", result.get(1).getId());
  }

}