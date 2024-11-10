package com.saudi.tourism.core.models.components.events.eventdatewidget.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class EventDateWidgetModelTest {

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(EventDateWidgetModel.class, EventDateWidgetCFModel.class);

    var eventAdapter = new EventDateWidgetEventCFAdapter();
    var adapterFactory = new EventDateWidgetCFAdapterFactory();
    adapterFactory.setAdapters(Arrays.asList(eventAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }



  @Test
  void testEventWidgetModel_fromEventContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/event-date-widget/event-page.json",
        "/content/sauditourism/en/event-page");
    context
      .load()
      .json(
        "/components/event-date-widget/event-cf.json",
        "/content/dam/sauditourism/cf/en/events/event1");

    context.currentPage("/content/sauditourism/en/event-page");
    context.currentResource("/content/sauditourism/en/event-page/jcr:content/root/rightsidebar/event_date_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "Event Title");
    mockContentFragmentElement(spyFragment, "comingSoonLabel", "Coming soon");
    mockContentFragmentElement(spyFragment, "expiredLabel", "Expired Label");
    TimeZone timeZone = TimeZone.getTimeZone("GMT+3"); // +03:00 offset

    Calendar mockStartDate = new GregorianCalendar(timeZone);
    mockStartDate.set(2023, Calendar.NOVEMBER, 1, 0, 0, 0);  // Setting hour, minute, and second to zero
    mockStartDate.set(Calendar.MILLISECOND, 0);  // Setting millisecond to zero

    Calendar mockEndDate = new GregorianCalendar(timeZone);
    mockEndDate.set(2023, Calendar.NOVEMBER, 3, 0, 0, 0);  // Setting hour, minute, and second to zero
    mockEndDate.set(Calendar.MILLISECOND, 0);  // Setting millisecond to zero

    mockContentFragmentElement(spyFragment, "startDate", mockStartDate);
    mockContentFragmentElement(spyFragment, "endDate", mockEndDate);

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(EventDateWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, EventDateWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Event Title", data.getTitle());
    assertEquals("Coming soon", data.getComingSoonLabel());
    assertEquals("Expired Label", data.getExpiredLabel());
    assertEquals("2023-11-01T00:00:00.000+03:00", data.getStartDate());
    assertEquals("2023-11-03T00:00:00.000+03:00", data.getEndDate());

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