package com.saudi.tourism.core.models.components.alerts;

import java.io.IOException;
import java.util.*;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.RunModeService;
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
class AlertsModelTest {

  @Mock
  private RunModeService runModeService;

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(AlertsModel.class, AlertCFModel.class);
    context.registerService(RunModeService.class, runModeService);

    var attractionAdapter = new AlertAttractionCFAdapter();
    var activityAdapter = new AlertActivityCFAdapter();
    var tourAdapter = new AlertTourCFAdapter();
    var adapterFactory = new AlertCFAdapterFactory();
    List<AlertCFAdapter> adapters = new ArrayList<>();
    adapters.add(attractionAdapter);
    adapters.add(activityAdapter);
    adapters.add(tourAdapter);
    adapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.alerts.AlertCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }

  @Test
  void alertsModelManualAuthoringEnabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/alert/attraction-page-authoring-enabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/alert");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/alert")
      .adaptTo(AlertsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AlertsModel.class);

    // Assert
    assertNotNull(data);
    assertTrue(data.getEnableManualAuthoring());
    assertEquals("<p>Alert Text</p>", data.getAlert());
    assertEquals("Grey", data.getAlertColor());
  }

  @Test
  void alertsModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/alert/attraction-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/alert/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/alert");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "alert", "<p>Alert Text CF</p>");
    mockContentFragmentElement(spyFragment, "alertColor", "Red");

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(AlertsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AlertsModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("<p>Alert Text CF</p>", data.getAlert());
    assertEquals("Red", data.getAlertColor());
  }
  @Test
  void alertsActivityModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/alert/activity-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/alert/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activity/activity1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/alert");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activity/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "alert", "<p>Alert Text CF</p>");
    mockContentFragmentElement(spyFragment, "alertColor", "Red");

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(AlertsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AlertsModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("<p>Alert Text CF</p>", data.getAlert());
    assertEquals("Red", data.getAlertColor());
  }
  @Test
  void alertsTourModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/alert/tour-page-authoring-disabled.json",
        "/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context
      .load()
      .json(
        "/components/alert/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/first-tour");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context.currentResource("/content/sauditourism/en/test/sprint6/menu-demo/first-tour/jcr:content/root");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/first-tour").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "alert", "<p>Alert Text CF</p>");
    mockContentFragmentElement(spyFragment, "alertColor", "Red");

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(AlertsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AlertsModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("<p>Alert Text CF</p>", data.getAlert());
    assertEquals("Red", data.getAlertColor());
  }

  // Mock a ContentFragment element with the given name and value.
  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);
    Mockito.when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
  }
}