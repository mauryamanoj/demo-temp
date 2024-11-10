package com.saudi.tourism.core.models.components.pricewidget.v1;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.Hashtable;

import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.utils.Utils;
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
class PriceWidgetModelTest {

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(PriceWidgetModel.class, PriceWidgetCFModel.class);

    var eventAdapter = new PriceWidgetEventCFAdapter();
    var attractionAdapter = new PriceWidgetAttractionCFAdapter();
    var activityAdapter = new PriceWidgetActivityCFAdapter();
    var tourAdapter = new PriceWidgetTourCFAdapter();
    var adapterFactory = new PriceWidgetCFAdapterFactory();
    adapterFactory.setAdapters(Arrays.asList(eventAdapter, attractionAdapter, activityAdapter, tourAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.pricewidget.v1.PriceWidgetCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }

  @Test
  void testPriceWidgetModel_fromEventContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/price-widget/event-page.json",
        "/content/sauditourism/en/event-page");
    context
      .load()
      .json(
        "/components/price-widget/event-cf.json",
        "/content/dam/sauditourism/cf/en/events/event1");

    context.currentPage("/content/sauditourism/en/event-page");
    context.currentResource("/content/sauditourism/en/event-page/jcr:content/root/rightsidebar/price_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "ticketType", "bookTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "1000 SAD");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "/Per person");
    Utils.mockContentFragmentElement(spyFragment, "ticketDetails", "<p>Minimum number of 3 person</p>");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Buy Now");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(PriceWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, PriceWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("bookTicket", data.getTicketType());
    assertEquals("1000 SAD", data.getPrice());
    assertEquals("/Per person", data.getPriceSuffix());
    assertEquals("<p>Minimum number of 3 person</p>", data.getText());
    assertEquals("Buy Now", data.getButtonLabel());
    assertEquals("/content/sauditourism/page", data.getUrl());
  }

  @Test
  void testPriceWidgetModel_fromAttractionContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/price-widget/attraction-page.json",
        "/content/sauditourism/en/attraction-page");
    context
      .load()
      .json(
        "/components/price-widget/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/attraction-page");
    context.currentResource("/content/sauditourism/en/attraction-page/jcr:content/root/rightsidebar/price_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "ticketType", "noTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketDetails", "<p>You don't need ticket</p>");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "");

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(PriceWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, PriceWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("noTicket", data.getTicketType());
    assertEquals("", data.getPrice());
    assertEquals("", data.getPriceSuffix());
    assertEquals("<p>You don't need ticket</p>", data.getText());
    assertEquals("", data.getButtonLabel());
    assertNull(data.getUrl());
  }

  @Test
  void testPriceWidgetModel_fromActivityContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/price-widget/activity-page.json",
        "/content/sauditourism/en/activity-page");
    context
      .load()
      .json(
        "/components/price-widget/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activities/activity1");

    context.currentPage("/content/sauditourism/en/activity-page");
    context.currentResource("/content/sauditourism/en/activity-page/jcr:content/root/rightsidebar/price_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activities/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "ticketType", "getTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketDetails", "<p>Tickets information are available on the event main website</p>");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "Get Tickets");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(PriceWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, PriceWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("getTicket", data.getTicketType());
    assertEquals("", data.getPrice());
    assertEquals("", data.getPriceSuffix());
    assertEquals("<p>Tickets information are available on the event main website</p>", data.getText());
    assertEquals("Get Tickets", data.getButtonLabel());
    assertEquals("/content/sauditourism/page", data.getUrl());
  }

  @Test
  void testPriceWidgetModel_fromTourContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/price-widget/tour-page.json",
        "/content/sauditourism/en/tour-page");
    context
      .load()
      .json(
        "/components/price-widget/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/tour1");

    context.currentPage("/content/sauditourism/en/tour-page");
    context.currentResource("/content/sauditourism/en/tour-page/jcr:content/root/rightsidebar/price_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/tour1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "ticketType", "noTicket");
    Utils.mockContentFragmentElement(spyFragment, "ticketPrice", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketPriceSuffix", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketDetails", "<p>You don't need ticket</p>");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALabel", "");
    Utils.mockContentFragmentElement(spyFragment, "ticketCTALink", "");

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(PriceWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, PriceWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("noTicket", data.getTicketType());
    assertEquals("", data.getPrice());
    assertEquals("", data.getPriceSuffix());
    assertEquals("<p>You don't need ticket</p>", data.getText());
    assertEquals("", data.getButtonLabel());
    assertNull(data.getUrl());
  }
}