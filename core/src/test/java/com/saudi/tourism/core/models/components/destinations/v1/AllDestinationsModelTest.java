package com.saudi.tourism.core.models.components.destinations.v1;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.contentfragment.category.CategoryCFModel;
import com.saudi.tourism.core.models.components.contentfragment.destination.DestinationCFModel;
import com.saudi.tourism.core.services.WeatherService;
import com.saudi.tourism.core.services.weather.model.output.ExtendedWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherMinMax;
import com.saudi.tourism.core.services.weather.model.output.SimpleWeatherModel;
import com.saudi.tourism.core.services.weather.model.output.Today;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
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
class AllDestinationsModelTest {
  @Mock
  private TagManager tagManager;
  @Mock
  private Tag tag;
  private final Gson gson = new GsonBuilder().create();

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext context =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private WeatherService weatherService;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(AllDestinationsModel.class, AllDestinationsModel.DestinationItem.class, DestinationCFModel.class);
    context
      .load()
      .json(
        "/components/all-destinations/riyadh-cf.json",
        "/content/dam/sauditourism/cf/en/destinations/riyad");

    context
      .load()
      .json(
        "/components/all-destinations/all-destinations.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/all_destinations");
    context.registerService(TagManager.class, tagManager);
    context.registerService(WeatherService.class, weatherService);
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
    //Act
    final var model =
      aemContext
        .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/all_destinations")
        .adaptTo(AllDestinationsModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, AllDestinationsModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("All destinations", data.getTitle());

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

    assertNotNull(destination.getPagePath());
    assertEquals("/en/destinations/riyadh", destination.getPagePath().getUrl());
  }
}