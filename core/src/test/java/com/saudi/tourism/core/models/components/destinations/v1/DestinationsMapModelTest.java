package com.saudi.tourism.core.models.components.destinations.v1;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.contentfragment.category.CategoryCFModel;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.Today;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class DestinationsMapModelTest {
  private final Gson gson = new GsonBuilder().create();

  @Mock
  private TagManager tagManager;
  @Mock
  private Tag tag;

  @Mock
  private WeatherService weatherService;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(DestinationsMapModel.class, AllDestinationsModel.class, AllDestinationsModel.DestinationItem.class, DestinationCFModel.class, CategoryCFModel.class);
    context
      .load()
      .json(
        "/components/all-destinations/riyadh-cf.json",
        "/content/dam/sauditourism/cf/en/destinations/riyad");
    context
      .load()
      .json(
        "/components/all-destinations/destinations-map.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/destinations_map");

    context.registerService(WeatherService.class, weatherService);
    context.registerService(TagManager.class, tagManager);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");
  }

  @Test
  void getAllDestinationsModel(final AemContext aemContext) {
    //Arrange
    var weather = new ExtendedWeatherModel();
    Today today = Today.builder().weather("Clear").temp(28.13f).build();
    weather.setToday(today);
    weather.setCity("Riyadh");
    weather.setCityId("riyadh");
    SimpleWeatherMinMax simpleWeatherMinMax = new SimpleWeatherMinMax(weather);
    simpleWeatherMinMax.setWeather("Clear");

    when(weatherService.getSimpleWeatherSingleCity(any())).thenReturn(simpleWeatherMinMax);
    when(saudiTourismConfigs.getGoogleMapsKey()).thenReturn("7912345c-6098-11ee-8c99-0242ac120002");
    //Act
    final var model =
      aemContext
        .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/destinations_map")
        .adaptTo(DestinationsMapModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, DestinationsMapModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("All destinations", data.getTitle());
    assertEquals("7912345c-6098-11ee-8c99-0242ac120002", data.getMapApiKey());
    assertNotNull(data.getLink());
    assertEquals("View more", data.getLink().getText());
    assertEquals("https://map.visitsaudi.com/", data.getLink().getUrl());

    Destination destination = data.getDestinations().get(0);
    assertEquals("Live the Modern Life to its Fullest", destination.getSubTitle());
    assertEquals("/content/dam/wvs/destinations/riyadh/riyadh-banner.jpg", destination.getBannerImage().getFileReference());
    assertEquals("/content/dam/wvs/destinations/riyadh/riyadh-banner.jpg", destination.getBannerImage().getMobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scthacc/riyadh-banner", destination.getBannerImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scthacc/riyadh-banner", destination.getBannerImage().getS7mobileImageReference());
    assertEquals("/content/dam/wvs/pop-up-images/riyadh.png", destination.getPopUpImage().getFileReference());
    assertEquals("/content/dam/wvs/pop-up-images/riyadh.png", destination.getPopUpImage().getMobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scthacc/riyadh", destination.getPopUpImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scthacc/riyadh", destination.getPopUpImage().getS7mobileImageReference());
    assertEquals("24.687731", destination.getLatitude());
    assertEquals("46.721851", destination.getLongitude());
    assertEquals("riyadh", destination.getId());
    assertEquals("About Riyadh", destination.getAboutHeading());
    assertEquals("<p>In the ever-growing and flourishing city of Riyadh, you will discover the birthplace of the Kingdom of Saudi Arabia, along with its historical treasures hidden in the old palaces that witnessed the founding of the kingdom. It is a destination for tourists from all over the globe who wish to discover a world of shopping, entertainment and business. The malls offer the most exciting shopping experience, and the sand dunes combined with the brightest stars in the sky present the most magical experience in nature. It is an environment full of a natural variety and unique characteristics that are intriguing for explorers. On the other side of the bustling city, you can enjoy a variety of experiences in luxurious restaurants that offer their special services, and their elaborate international dishes. Simultaneously, the local Riyadh restaurants will tempt you with their authentic flavors and blend of spices originating from Saudi culture. Everyone is happy in the city of Riyadh where the biggest cultural events are always celebrated. Endless entertainment destinations are always emerging, for the city’s visitors to have intriguing and renewing experiences.</p>\n", destination.getAboutDescription());
    assertEquals("Clear", destination.getWeather().getWeather());
    assertEquals("Clear", destination.getWeather().getIcon());
    assertEquals("Riyadh", destination.getWeather().getCity());
    assertEquals("riyadh", destination.getWeather().getCityId());
    assertEquals(28.1f, destination.getWeather().getTemp());


    assertNotNull(destination.getCategories());
    assertEquals("Food", destination.getCategories().get(0).getTitle());
   }
}