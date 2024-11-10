package com.saudi.tourism.core.models.components.bigslider.v1;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.FavoritesApiEndpoints;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.RepositoryException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.ONE;
import static com.saudi.tourism.core.utils.Constants.PN_REGION;
import static com.saudi.tourism.core.utils.NumberConstants.SEVEN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class BigSliderModelTest {
  public static final String SCENE_7_DOMAIN = "https://scene7.adobe.com/";

  private final Gson gson = new GsonBuilder()
    .excludeFieldsWithoutExposeAnnotation()
    .create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private EventService eventService;

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private RegionCityService regionCityService;

  @Mock
  private FavoritesService favoritesService;

  private ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      return "dummy_i18n_traduction";
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };

  @Captor
  private ArgumentCaptor<EventsRequestParams> eventsRequestParamsCaptor;

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @BeforeEach
  void setUp() {
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    aemContext.registerService(EventService.class, eventService);
    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    aemContext.registerService(SlingSettingsService.class, settingsService);
    aemContext.registerService(RegionCityService.class, regionCityService);
    aemContext.registerService(FavoritesService.class, favoritesService);

    //aemContext.addModelsForClasses(BigSliderModel.class, ComponentHeading.class, Heading.class, Link.class, Slide.class, Image.class);

    //For header tests
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-header.json", "/content/sauditourism/en/bigslider-header");
    // For event sliders tests
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-event-variant.json", "/content/sauditourism/en/bigslider-event-variant");
    // For category sliders tests
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-category-variant.json", "/content/sauditourism/en/bigslider-category-variant");
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-category-variant-path-1.json", "/content/sauditourism/en/do/adventure-activities/standing-on-the-edge-of-the-world");
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-category-variant-path-2.json", "/content/sauditourism/en/do/adventure-activities/4-top-scuba-sites-near-jeddah");
    // For map sliders tests
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-map-variant.json", "/content/sauditourism/en/bigslider-map-variant");
    // For simple sliders tests
    aemContext.load().json("/components/c25-bigslider.v1/bigslider-simple-variant.json", "/content/sauditourism/en/bigslider-simple-variant");

    lenient().when(saudiTourismConfigs.getScene7Domain()).thenReturn(SCENE_7_DOMAIN);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    final FavoritesApiEndpoints favoritesApiEndpoints = FavoritesApiEndpoints.builder()
      .updateFavUrl("/bin/api/v2/user/update.favorites")
      .deleteFavUrl("/bin/api/v2/user/delete.favorites")
      .getFavUrl("/bin/api/v2/user/get.favorites?locale=en")
      .build();

    when(favoritesService.computeFavoritesApiEndpoints(any(String.class)))
      .thenReturn(favoritesApiEndpoints);
  }

  @Test
  public void shouldReturnHeaderConfiguration() {
    //Arrange
    aemContext.currentResource("/content/sauditourism/en/bigslider-header");

    //Act
    final BigSliderModel model = aemContext.request().getResource().adaptTo(BigSliderModel.class);
    final String json = model.getJson();

    //Assert
    final BigSliderModel data = gson.fromJson(json, BigSliderModel.class);
    assertEquals("Hello I'm a big slider", data.getComponentHeading().getHeading().getText());
    assertEquals("h2", data.getComponentHeading().getHeading().getWeight());
    assertEquals("/en/about-e-visa", data.getComponentHeading().getLink().getUrl());
    assertEquals("Open", data.getComponentHeading().getLink().getCopy());
    assertTrue(data.getComponentHeading().getLink().isTargetInNewWindow());
    assertEquals("07A", data.getOrnamentId());
  }

  @Test
  public void shouldHandleEventSlide() throws RepositoryException {
    //Arrange
    aemContext.currentResource("/content/sauditourism/en/bigslider-event-variant");
    final EventDetail event1 = new EventDetail();
    event1.setTitle("Zuha Al Taif");
    event1.setShortDescription("Great event");
    event1.setFavId("/events/zuha-al-taif");
    event1.setFeatureEventImage("/content/dam/saudi-tourism/media/events-/7E2A5236.jpg");
    event1.setS7featureEventImage(String.join("", SCENE_7_DOMAIN, "saudi-tourism/media/events-/7E2A5236.jpg"));
    event1.setDisplayedStartDate("2021-07-21");
    event1.setDisplayedEndDate("2021-09-21");
    event1.setRegion("Al Bahah Region");

    final EventListModel listModel = new EventListModel();
    listModel.setData(
      Arrays.asList(new EventDetail[]{
        event1
      })
    );

    when(eventService.getFilteredEvents(any(EventsRequestParams.class))).thenReturn(listModel);

    //Act
    final BigSliderModel model = aemContext.request().getResource().adaptTo(BigSliderModel.class);
    final String json = model.getJson();

    //Assert
    final BigSliderModel data = gson.fromJson(json, BigSliderModel.class);
    assertEquals("Hello I'm a big slider", data.getComponentHeading().getHeading().getText());
    assertEquals("h2", data.getComponentHeading().getHeading().getWeight());
    assertEquals("/en/about-e-visa", data.getComponentHeading().getLink().getUrl());
    assertEquals("Open", data.getComponentHeading().getLink().getCopy());
    assertTrue(data.getComponentHeading().getLink().isTargetInNewWindow());
    assertEquals("07A", data.getOrnamentId());

    //Event Slider
    verify(eventService).getFilteredEvents(eventsRequestParamsCaptor.capture());
    assertEquals("en", eventsRequestParamsCaptor.getValue().getLocale());
    LocalDate today = LocalDate.now();
    LocalDate tomorrow = today.plusDays(ONE);
    LocalDate next = today.plusDays(SEVEN);
    assertEquals(tomorrow.format(DateTimeFormatter.ISO_LOCAL_DATE), eventsRequestParamsCaptor.getValue().getStartDate());
    assertEquals(next.format(DateTimeFormatter.ISO_LOCAL_DATE), eventsRequestParamsCaptor.getValue().getEndDate());
    assertEquals("event", data.getVariant());
    assertEquals("Zuha Al Taif", data.getSlides().get(0).getTitle());
    assertEquals("Great event", data.getSlides().get(0).getDescription());
    assertEquals("/events/zuha-al-taif", data.getSlides().get(0).getFavoriteId());
    assertEquals("/events/zuha-al-taif", data.getSlides().get(0).getFavoritePath());
    assertEquals("/content/dam/saudi-tourism/media/events-/7E2A5236.jpg", data.getSlides().get(0).getImage().getFileReference());
    assertEquals(String.join("", SCENE_7_DOMAIN, "saudi-tourism/media/events-/7E2A5236.jpg"), data.getSlides().get(0).getImage().getS7fileReference());
    assertEquals(String.join("", SCENE_7_DOMAIN, "saudi-tourism/media/events-/7E2A5236.jpg", ":crop-1920x1080?defaultImage=7E2A5236.jpg"), data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals(String.join("", SCENE_7_DOMAIN, "saudi-tourism/media/events-/7E2A5236.jpg", ":crop-375x280?defaultImage=7E2A5236.jpg"), data.getSlides().get(0).getImage().getMobileImage());
    assertEquals("2021-07-21", data.getSlides().get(0).getParameters().get("displayStartDate"));
    assertEquals("2021-09-21", data.getSlides().get(0).getParameters().get("displayEndDate"));
    assertEquals("Al Bahah Region", data.getSlides().get(0).getParameters().get(PN_REGION));

    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
  }

  @Test
  public void shouldHandleCategorySlide() throws RepositoryException {
    //Arrange
    aemContext.currentResource("/content/sauditourism/en/bigslider-category-variant");
    //Act
    final BigSliderModel model = aemContext.request().getResource().adaptTo(BigSliderModel.class);
    final String json = model.getJson();

    //Assert
    final BigSliderModel data = gson.fromJson(json, BigSliderModel.class);
    assertEquals("Hello I'm a big slider", data.getComponentHeading().getHeading().getText());
    assertEquals("h2", data.getComponentHeading().getHeading().getWeight());
    assertEquals("/en/about-e-visa", data.getComponentHeading().getLink().getUrl());
    assertEquals("Open", data.getComponentHeading().getLink().getCopy());
    assertTrue(data.getComponentHeading().getLink().isTargetInNewWindow());
    assertEquals("07A", data.getOrnamentId());

    //Category Slider
    assertEquals("category", data.getVariant());

    assertEquals("Standing on the Edge of the World", data.getSlides().get(0).getTitle());
    assertEquals("Experience breathtaking cliff-top views: this spectacular spot offers uninterrupted views from the edge of a sheer precipice.", data.getSlides().get(0).getDescription());
    assertEquals("/content/dam/no-dynamic-media-folder/edge-of-the-world-refreshed/edge_of_the_world_hero_large.jpg", data.getSlides().get(0).getImage().getFileReference());
    assertEquals("/content/dam/no-dynamic-media-folder/edge-of-the-world-refreshed/edge_of_the_world_hero_large.jpg", data.getSlides().get(0).getImage().getS7fileReference());
    assertEquals("/content/dam/no-dynamic-media-folder/edge-of-the-world-refreshed/edge_of_the_world_hero_large.jpg", data.getSlides().get(0).getImage().getMobileImage());
    assertEquals("/content/dam/no-dynamic-media-folder/edge-of-the-world-refreshed/edge_of_the_world_hero_large.jpg", data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals("/en/do/adventure-activities/standing-on-the-edge-of-the-world", data.getSlides().get(0).getLink().getUrl());
    assertEquals("/en/do/adventure-activities/standing-on-the-edge-of-the-world", data.getSlides().get(0).getLink().getUrlWithExtension());
    assertEquals("/do/adventure-activities/standing-on-the-edge-of-the-world", data.getSlides().get(0).getLink().getUrlSlingExporter());
    assertEquals("dummy_i18n_traduction", data.getSlides().get(0).getLink().getCopy());
    assertEquals("/do/adventure-activities/standing-on-the-edge-of-the-world", data.getSlides().get(0).getFavoritePath());
    assertEquals("", data.getSlides().get(0).getParameters().get("category"));

    assertEquals("4 Top Scuba Sites Near Jeddah", data.getSlides().get(1).getTitle());
    assertEquals("Saudi Arabia’s coastal waters are a scuba diver’s dream, with plenty of scuba spots to explore. Visit some of the best diving sites near Jeddah, Saudi Arabia.", data.getSlides().get(1).getDescription());
    assertEquals("/content/dam/saudi-tourism/media/articles/a102/1920x1080/brand-page-hero.jpg", data.getSlides().get(1).getImage().getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/brand-page-hero-1537", data.getSlides().get(1).getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/brand-page-hero-1537:crop-375x280?defaultImage=brand-page-hero-1537", data.getSlides().get(1).getImage().getMobileImage());
    assertEquals("https://scth.scene7.com/is/image/scth/brand-page-hero-1537:crop-1920x1080?defaultImage=brand-page-hero-1537", data.getSlides().get(1).getImage().getDesktopImage());
    assertEquals("/en/do/adventure-activities/4-top-scuba-sites-near-jeddah", data.getSlides().get(1).getLink().getUrl());
    assertEquals("/en/do/adventure-activities/4-top-scuba-sites-near-jeddah", data.getSlides().get(1).getLink().getUrlWithExtension());
    assertEquals("/do/adventure-activities/4-top-scuba-sites-near-jeddah", data.getSlides().get(1).getLink().getUrlSlingExporter());
    assertEquals("dummy_i18n_traduction", data.getSlides().get(1).getLink().getCopy());
    assertEquals("/do/adventure-activities/4-top-scuba-sites-near-jeddah", data.getSlides().get(1).getFavoritePath());
    assertEquals("", data.getSlides().get(0).getParameters().get("category"));

    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
  }

  @Test
  public void shouldHandleMapSlide() throws RepositoryException {
    //Arrange
    aemContext.currentResource("/content/sauditourism/en/bigslider-map-variant");

    //Act
    final BigSliderModel model = aemContext.request().getResource().adaptTo(BigSliderModel.class);
    final String json = model.getJson();

    //Assert
    final BigSliderModel data = gson.fromJson(json, BigSliderModel.class);
    assertEquals("Hello I'm a big slider", data.getComponentHeading().getHeading().getText());
    assertEquals("h2", data.getComponentHeading().getHeading().getWeight());
    assertEquals("/en/about-e-visa", data.getComponentHeading().getLink().getUrl());
    assertEquals("Open", data.getComponentHeading().getLink().getCopy());
    assertTrue(data.getComponentHeading().getLink().isTargetInNewWindow());
    assertEquals("07A", data.getOrnamentId());

    //Map Slider
    assertEquals("map", data.getVariant());

    assertEquals("Description 1", data.getSlides().get(0).getDescription());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(0).getImage().getFileReference());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg", data.getSlides().get(0).getImage().getS7fileReference());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg:crop-375x667?defaultImage=placeholder.jpg", data.getSlides().get(0).getImage().getMobileImage());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg:crop-1920x1080?defaultImage=placeholder.jpg", data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals("/en/regions/central", data.getSlides().get(0).getLink().getUrl());
    assertEquals("/content/sauditourism/en/regions/central.html", data.getSlides().get(0).getLink().getUrlWithExtension());
    assertEquals("/regions/central", data.getSlides().get(0).getLink().getUrlSlingExporter());
    assertEquals("Link Title 1", data.getSlides().get(0).getLink().getCopy());
    assertEquals("al-baha-region", data.getSlides().get(0).getParameters().get("regionId"));
    assertEquals("city", data.getSlides().get(0).getParameters().get("locationType"));
    assertEquals("abha", data.getSlides().get(0).getParameters().get("cityId"));

    assertEquals("Description 2", data.getSlides().get(1).getDescription());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(1).getImage().getFileReference());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg", data.getSlides().get(1).getImage().getS7fileReference());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg:crop-375x667?defaultImage=placeholder.jpg", data.getSlides().get(0).getImage().getMobileImage());
    assertEquals("https://scene7.adobe.com/is/image/placeholder.jpg:crop-1920x1080?defaultImage=placeholder.jpg", data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals("/en/regions/eastern", data.getSlides().get(1).getLink().getUrl());
    assertEquals("/content/sauditourism/en/regions/eastern.html", data.getSlides().get(1).getLink().getUrlWithExtension());
    assertEquals("/regions/eastern", data.getSlides().get(1).getLink().getUrlSlingExporter());
    assertEquals("Link Title 2", data.getSlides().get(1).getLink().getCopy());
    assertEquals("al-jouf-region", data.getSlides().get(1).getParameters().get("regionId"));
    assertEquals("city", data.getSlides().get(1).getParameters().get("locationType"));
    assertEquals("abu-arish", data.getSlides().get(1).getParameters().get("cityId"));

    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
  }

  @Test
  public void shouldHandleSimpleSlide() throws RepositoryException {
    //Arrange
    aemContext.currentResource("/content/sauditourism/en/bigslider-simple-variant");

    //Act
    final BigSliderModel model = aemContext.request().getResource().adaptTo(BigSliderModel.class);
    final String json = model.getJson();

    //Assert
    final BigSliderModel data = gson.fromJson(json, BigSliderModel.class);
    assertEquals("Hello I'm a big slider", data.getComponentHeading().getHeading().getText());
    assertEquals("h2", data.getComponentHeading().getHeading().getWeight());
    assertEquals("/en/about-e-visa", data.getComponentHeading().getLink().getUrl());
    assertEquals("Open", data.getComponentHeading().getLink().getCopy());
    assertTrue(data.getComponentHeading().getLink().isTargetInNewWindow());
    assertEquals("07A", data.getOrnamentId());

    //Map Slider
    assertEquals("simple", data.getVariant());

    assertEquals("I'm simple slide 1", data.getSlides().get(0).getDescription());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(0).getImage().getS7fileReference());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(0).getImage().getMobileImage());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", data.getSlides().get(0).getImage().getDesktopImage());
    assertEquals("/en/events/-bar-masa", data.getSlides().get(0).getLink().getUrl());
    assertEquals("/content/sauditourism/en/events/-bar-masa.html", data.getSlides().get(0).getLink().getUrlWithExtension());
    assertEquals("/events/-bar-masa", data.getSlides().get(0).getLink().getUrlSlingExporter());
    assertEquals("Link title 1", data.getSlides().get(0).getLink().getCopy());

    assertEquals("/bin/api/v2/user/update.favorites", data.getUpdateFavUrl());
    assertEquals("/bin/api/v2/user/delete.favorites", data.getDeleteFavUrl());
    assertEquals("/bin/api/v2/user/get.favorites?locale=en", data.getGetFavUrl());
  }
}