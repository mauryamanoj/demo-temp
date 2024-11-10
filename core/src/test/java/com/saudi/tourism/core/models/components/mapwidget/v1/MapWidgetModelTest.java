package com.saudi.tourism.core.models.components.mapwidget.v1;

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class MapWidgetModelTest {

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(MapWidgetModel.class, MapWidgetCFModel.class);

    var eventAdapter = new MapWidgetEventCFAdapter();
    var attractionAdapter = new MapWidgetAttractionCFAdapter();
    var activityAdapter = new MapWidgetActivityCFAdapter();
    var adapterFactory = new MapWidgetCFAdapterFactory();
    var tourAdapter =  new MapWidgetTourCFAdapter();
    adapterFactory.setAdapters(Arrays.asList(eventAdapter, attractionAdapter, activityAdapter, tourAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.mapwidget.v1.MapWidgetCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    lenient().when(saudiTourismConfigs.getGoogleMapsKey()).thenReturn("7912345c-6098-11ee-8c99-0242ac120002");
  }


  @Test
  void testMapWidgetModel_fromEventContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/map-widget/event-page.json",
        "/content/sauditourism/en/event-page");
    context
      .load()
      .json(
        "/components/map-widget/event-cf.json",
        "/content/dam/sauditourism/cf/en/events/event1");

    context.currentPage("/content/sauditourism/en/event-page");
    context.currentResource("/content/sauditourism/en/event-page/jcr:content/root/rightsidebar/map_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "meetingPointHeader", "Meeting Point");
    mockContentFragmentElement(spyFragment, "meetingPointLabel", "Location");
    mockContentFragmentElement(spyFragment, "meetingPointValue", "Jeddah");
    mockContentFragmentElement(spyFragment, "meetingPointLat", "10.1");
    mockContentFragmentElement(spyFragment, "meetingPointLng", "9.0");
    mockContentFragmentElement(spyFragment, "getDirectionsLabel", "Get Directions");

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(MapWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MapWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Get Directions", data.getCtaLabel());
    assertEquals("10.1", data.getLatitude());
    assertEquals("9.0", data.getLongitude());

  }

  @Test
  void testMapWidgetModel_fromAttractionContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/map-widget/attraction-page.json",
        "/content/sauditourism/en/attraction-page");
    context
      .load()
      .json(
        "/components/map-widget/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/attraction-page");
    context.currentResource("/content/sauditourism/en/attraction-page/jcr:content/root/rightsidebar/map_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "meetingPointHeader", "Meeting Point");
    mockContentFragmentElement(spyFragment, "meetingPointLabel", "Location");
    mockContentFragmentElement(spyFragment, "meetingPointValue", "Jeddah");
    mockContentFragmentElement(spyFragment, "meetingPointLat", "2.1");
    mockContentFragmentElement(spyFragment, "meetingPointLng", "9.0");
    mockContentFragmentElement(spyFragment, "getDirectionsLabel", "Get Directions");

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(MapWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MapWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Get Directions", data.getCtaLabel());
    assertEquals("2.1", data.getLatitude());
    assertEquals("9.0", data.getLongitude());

  }

  @Test
  void testMapWidgetModel_fromActivityContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/map-widget/activity-page.json",
        "/content/sauditourism/en/activity-page");
    context
      .load()
      .json(
        "/components/map-widget/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activities/activity1");

    context.currentPage("/content/sauditourism/en/activity-page");
    context.currentResource("/content/sauditourism/en/activity-page/jcr:content/root/rightsidebar/map_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activities/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "meetingPointHeader", "Meeting Point");
    mockContentFragmentElement(spyFragment, "meetingPointLabel", "Location");
    mockContentFragmentElement(spyFragment, "meetingPointValue", "Jeddah");
    mockContentFragmentElement(spyFragment, "meetingPointLat", "12.1");
    mockContentFragmentElement(spyFragment, "meetingPointLng", "19.0");
    mockContentFragmentElement(spyFragment, "getDirectionsLabel", "Get Directions");

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(MapWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MapWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Meeting Point", data.getTitle());
    assertEquals("Location", data.getLocationLabel());
    assertEquals("Jeddah", data.getLocationValue());
    assertEquals("Get Directions", data.getCtaLabel());
    assertEquals("12.1", data.getLatitude());
    assertEquals("19.0", data.getLongitude());

  }
  @Test
  void testMapWidgetModel_fromTourContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/map-widget/tour-page.json",
        "/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context
      .load()
      .json(
        "/components/map-widget/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/first-tour");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context.currentResource("/content/sauditourism/en/test/sprint6/menu-demo/first-tour/jcr:content/root");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/first-tour").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "meetingPointHeader", "Meeting Point");
    mockContentFragmentElement(spyFragment, "meetingPointLabel", "Location");
    mockContentFragmentElement(spyFragment, "meetingPointValue", "Jeddah");
    mockContentFragmentElement(spyFragment, "meetingPointLat", "12.1");
    mockContentFragmentElement(spyFragment, "meetingPointLng", "19.0");
    mockContentFragmentElement(spyFragment, "getDirectionsLabel", "Get Directions");

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(MapWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, MapWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Meeting Point", data.getTitle());
    assertEquals("Location", data.getLocationLabel());
    assertEquals("Jeddah", data.getLocationValue());
    assertEquals("Get Directions", data.getCtaLabel());
    assertEquals("12.1", data.getLatitude());
    assertEquals("19.0", data.getLongitude());

  }

  // Mock a ContentFragment element with the given name and value.
  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);

    if(value instanceof Calendar) {
      Mockito.lenient().when(elementData.getValue(Calendar.class)).thenReturn((Calendar) value);
    } else {
      Mockito.lenient().when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
    }
  }
}