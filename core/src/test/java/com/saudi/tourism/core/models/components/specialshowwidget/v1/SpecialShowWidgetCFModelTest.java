package com.saudi.tourism.core.models.components.specialshowwidget.v1;


import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetCFAdapterFactory;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetCFModel;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetEventCFAdapter;
import com.saudi.tourism.core.models.components.events.eventdatewidget.v1.EventDateWidgetModel;
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
class SpeciaShowWidgetModelTest {

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(SpecialShowWidgetModel.class, SpecialShowWidgetCFModel.class);


    context
        .load()
        .json(
            "/components/event-date-widget/event-cf.json",
            "/content/dam/sauditourism/cf/en/events/event1");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);
    mockContentFragmentElement(spyFragment, "specialShowLabel", "Show Special Title");
    mockContentFragmentElement(spyFragment, "specialShowDetails", new String[]{"{\"key\":\"key1\",\"value\":\"value1\"}"});


    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);
  }



  @Test
  void testSpecialShowWidgetModel_fromEventContentFragment(final AemContext context) throws IOException {
    // Arrange




    // Act
    final var model = context
        .currentResource("/content/dam/sauditourism/cf/en/events/event1")
        .adaptTo(SpecialShowWidgetCFModel.class);


    // Assert
    assertNotNull(model);
    assertEquals("Show Special Title",  model.getTitle());
    assertEquals("key1",  model.getData().get(0).getKey());
    assertEquals("value1",  model.getData().get(0).getValue());



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