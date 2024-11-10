package com.saudi.tourism.core.models.components.packages;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.models.common.Slide;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SimpleSliderModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en/see/highlights/Al-Masmak-Fortress/jcr%3Acontent/root/responsivegrid/c05_simple_slider";


  private final Gson gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create();

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private FavoritesService favoritesService;


  @BeforeEach
  public void setUp(final AemContext aemContext) {
    aemContext.registerService(SlingSettingsService.class, settingsService);

    aemContext.load().json("/components/simple-slider/content.json", CONTENT_PATH);

    aemContext.addModelsForClasses(SimpleSliderModel.class, ComponentHeading.class, Heading.class, Link.class, Slide.class, Image.class);

    aemContext.registerService(FavoritesService.class, favoritesService);

    final FavoritesApiEndpoints favoritesApiEndpoints = FavoritesApiEndpoints.builder()
      .updateFavUrl("/bin/api/v2/user/update.favorites")
      .deleteFavUrl("/bin/api/v2/user/delete.favorites")
      .getFavUrl("/bin/api/v2/user/get.favorites?locale=en")
      .build();

    when(favoritesService.computeFavoritesApiEndpoints(any(String.class)))
      .thenReturn(favoritesApiEndpoints);
  }

  @Test
  public void shouldReturnCompnentHeadingForSlides(AemContext context) {
    //Arrange

    //Act
    final SimpleSliderModel model = context.currentResource(CONTENT_PATH).adaptTo(SimpleSliderModel.class);
    final String json = model.getJson();

    //Assert
    final SimpleSliderModel data = gson.fromJson(json, SimpleSliderModel.class);
    assertEquals("More highlights in Saudi Arabia", data.getComponentHeading().getHeading().getText());
    assertEquals("h1", data.getComponentHeading().getHeading().getWeight());
    assertFalse(data.getComponentHeading().isShowSlider());
    assertFalse(data.getComponentHeading().isShowUnderline());
  }

  @Test
  public void shouldReturnSlides(AemContext context) {
    //Arrange

    //Act
    final SimpleSliderModel model = context.currentResource(CONTENT_PATH).adaptTo(SimpleSliderModel.class);
    final String json = model.getJson();

    //Assert
    final SimpleSliderModel data = gson.fromJson(json, SimpleSliderModel.class);
    assertEquals(4, data.getSlides().size());
    assertEquals(4, data.getSize());

    assertEquals("Historical Dir’iyah", data.getSlides().get(0).getTitle());
    assertEquals("h1", data.getSlides().get(0).getTitleWeight());
    assertEquals("See the future through Saudi Arabia’s past", data.getSlides().get(0).getSubtitle());
    assertEquals("h1", data.getSlides().get(0).getSubtitleWeight());
    assertEquals("Welcome to Ad Dir’iyah, birthplace of the first Saudi state, historical crossroads of pilgrims and traders, and home to [...]", data.getSlides().get(0).getDescription());
    assertEquals("/see/highlights/historical-Dir-iyah", data.getSlides().get(0).getFavoritePath());
    assertEquals("/content/sauditourism/en/see/highlights/historical-Dir-iyah", data.getSlides().get(0).getLink().getUrl());
    assertEquals("/content/sauditourism/en/see/highlights/historical-Dir-iyah.html", data.getSlides().get(0).getLink().getUrlWithExtension());
    assertEquals("/see/highlights/historical-Dir-iyah", data.getSlides().get(0).getLink().getUrlSlingExporter());
    assertEquals("Explore All", data.getSlides().get(0).getLink().getCopy());
    assertEquals("Historical Dir’iyah", data.getSlides().get(0).getImage().getAlt());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a031/slide-01.jpg", data.getSlides().get(0).getImage().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a031/slide-mobile-01.jpg", data.getSlides().get(0).getImage().getMobileImageReference());
    assertEquals("Historical Dir’iyah", data.getSlides().get(0).getImage().getAlt());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-01-122", data.getSlides().get(0).getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-01-122:crop-360x480?defaultImage=slide-01-122", data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-mobile-01-101", data.getSlides().get(0).getImage().getS7mobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-mobile-01-101:crop-360x480?defaultImage=slide-mobile-01-101", data.getSlides().get(0).getImage().getMobileImage());

    assertEquals("Riyadh", data.getSlides().get(0).getRegion());
    assertEquals("More highlights in Saudi Arabia", data.getSlides().get(0).getDataTracker().getCarouselTitle());
    assertEquals(0, data.getSlides().get(0).getDataTracker().getItemNumber());
    assertEquals(4, data.getSlides().get(0).getDataTracker().getTotalItems());

    assertEquals("Jeddah Waterfront", data.getSlides().get(1).getTitle());
    assertEquals("h1", data.getSlides().get(1).getTitleWeight());
    assertEquals("Sun, sea and sports at the city’s newly developed coastline", data.getSlides().get(1).getSubtitle());
    assertEquals("h1", data.getSlides().get(1).getSubtitleWeight());
    assertEquals("With its long stretches of sandy beaches, cool walkways and lush parklands, Jeddah Corniche is among the city’s main [...]", data.getSlides().get(1).getDescription());
    assertEquals("/see/highlights/jeddah-waterfront", data.getSlides().get(1).getFavoritePath());
    assertEquals("/content/sauditourism/en/see/highlights/jeddah-waterfront", data.getSlides().get(1).getLink().getUrl());
    assertEquals("/content/sauditourism/en/see/highlights/jeddah-waterfront.html", data.getSlides().get(1).getLink().getUrlWithExtension());
    assertEquals("/see/highlights/jeddah-waterfront", data.getSlides().get(1).getLink().getUrlSlingExporter());
    assertEquals("Explore All", data.getSlides().get(1).getLink().getCopy());
    assertEquals("Jeddah Waterfront", data.getSlides().get(1).getImage().getAlt());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a031/slide-02.jpg", data.getSlides().get(1).getImage().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a031/slide-mobile-02.jpg", data.getSlides().get(1).getImage().getMobileImageReference());
    assertEquals("Jeddah Waterfront", data.getSlides().get(1).getImage().getAlt());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-02-114", data.getSlides().get(1).getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-02-114:crop-360x480?defaultImage=slide-02-114", data.getSlides().get(1).getImage().getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-mobile-02-106", data.getSlides().get(1).getImage().getS7mobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/slide-mobile-02-106:crop-360x480?defaultImage=slide-mobile-02-106", data.getSlides().get(1).getImage().getMobileImage());
    assertEquals("Makkah", data.getSlides().get(1).getRegion());
    assertEquals("More highlights in Saudi Arabia", data.getSlides().get(1).getDataTracker().getCarouselTitle());
    assertEquals(1, data.getSlides().get(1).getDataTracker().getItemNumber());
    assertEquals(4, data.getSlides().get(1).getDataTracker().getTotalItems());
  }

  @Test
  public void shouldReturnFavoritesApiPAth(AemContext context) {
    //Arrange

    //Act
    final SimpleSliderModel model = context.currentResource(CONTENT_PATH).adaptTo(SimpleSliderModel.class);
    final String json = model.getJson();

    //Assert
    final SimpleSliderModel data = gson.fromJson(json, SimpleSliderModel.class);
    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
  }
}