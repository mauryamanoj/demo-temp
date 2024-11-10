package com.saudi.tourism.core.services.seasons.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SeasonsCFServiceImplTest {

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private UserService userService;

  private SeasonsCFServiceImpl seasonsCFService = new SeasonsCFServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerInjectActivateService(seasonsCFService);
  }

  @Test
  void fetchAllSeasonsShouldRaiseIfLocaleNotProvided(){
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
      seasonsCFService.fetchAllSeasons(null);
    });


    //Assert
    assertEquals("locale is marked @NonNull but is null", exception.getMessage());
  }

  @Test
  void fetchAllSeasonsShouldReturnNullIfNoRootFolder(final AemContext aemContext){
    //Arrange
    when(saudiTourismConfigs.getSeasonsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/seasons");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = seasonsCFService.fetchAllSeasons("en");


    //Assert
    assertNull(result);
  }

  @Test
  void fetchAllSeasonsShouldReturnNullIfNoCFsFound(final AemContext aemContext){
    //Arrange
    aemContext.load().json("/components/destinations/root-folder.json", "/content/dam/sauditourism/cf/en/seasons");

    when(saudiTourismConfigs.getSeasonsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/seasons");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = seasonsCFService.fetchAllSeasons("en");


    //Assert
    assertTrue(CollectionUtils.isEmpty(result));
  }

  @Test
  void fetchAllSeasonsShouldReturnDestinations(final AemContext aemContext){
    //Arrange
    aemContext.load().json("/components/destinations/root-folder.json", "/content/dam/sauditourism/cf/en/seasons");
    aemContext.load().json("/components/seasons/riyadh-season.json", "/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    final var contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/seasons/riyadh-season").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "Riyadh Season");

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    when(saudiTourismConfigs.getSeasonsCFPath()).thenReturn("/content/dam/sauditourism/cf/{0}/seasons");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final var result = seasonsCFService.fetchAllSeasons("en");


    //Assert
    assertTrue(CollectionUtils.isNotEmpty(result));
    assertEquals(2, result.size()); // it should be one, but because we are mocking the adapter, jcr:content get adapted too
    assertEquals("Riyadh Season", result.get(1).getTitle());
  }

  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);
    Mockito.when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
  }

}