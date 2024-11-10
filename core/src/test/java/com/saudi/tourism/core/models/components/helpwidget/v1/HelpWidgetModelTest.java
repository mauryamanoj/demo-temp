package com.saudi.tourism.core.models.components.helpwidget.v1;

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
class HelpWidgetModelTest {

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(HelpWidgetModel.class, HelpWidgetCFModel.class);

    var eventAdapter = new HelpWidgetEventCFAdapter();
    var attractionAdapter = new HelpWidgetAttractionCFAdapter();
    var activityAdapter = new HelpWidgetActivityCFAdapter();
    var tourAdapter = new HelpWidgetTourCFAdapter();
    var adapterFactory = new HelpWidgetCFAdapterFactory();
    adapterFactory.setAdapters(Arrays.asList(eventAdapter, attractionAdapter, activityAdapter, tourAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.helpwidget.v1.HelpWidgetCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }

  @Test
  void testHelpWidgetModel_fromEventContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/help-widget/event-page.json",
        "/content/sauditourism/en/event-page");
    context
      .load()
      .json(
        "/components/help-widget/event-cf.json",
        "/content/dam/sauditourism/cf/en/events/event1");

    context.currentPage("/content/sauditourism/en/event-page");
    context.currentResource("/content/sauditourism/en/event-page/jcr:content/root/rightsidebar/help_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/events/event1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "helpWidgetHeading", "Do You Need Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetDescription", "<p>Do you have a question or need more information ?</p>");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALabel", "Get More Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("event");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(HelpWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, HelpWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Do You Need Help", data.getTitle());
    assertEquals("<p>Do you have a question or need more information ?</p>", data.getDescription());
    assertEquals("Get More Help", data.getCta().getText());
    assertEquals("/content/sauditourism/page", data.getCta().getUrl());
  }

  @Test
  void testHelpWidgetModel_fromAttractionContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/help-widget/attraction-page.json",
        "/content/sauditourism/en/attraction-page");
    context
      .load()
      .json(
        "/components/help-widget/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/attraction-page");
    context.currentResource("/content/sauditourism/en/attraction-page/jcr:content/root/rightsidebar/help_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "helpWidgetHeading", "Do You Need Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetDescription", "<p>Do you have a question or need more information ?</p>");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALabel", "Get More Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(HelpWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, HelpWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Do You Need Help", data.getTitle());
    assertEquals("<p>Do you have a question or need more information ?</p>", data.getDescription());
    assertEquals("Get More Help", data.getCta().getText());
    assertEquals("/content/sauditourism/page", data.getCta().getUrl());
  }

  @Test
  void testHelpWidgetModel_fromActivityContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/help-widget/activity-page.json",
        "/content/sauditourism/en/activity-page");
    context
      .load()
      .json(
        "/components/help-widget/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activities/activity1");

    context.currentPage("/content/sauditourism/en/activity-page");
    context.currentResource("/content/sauditourism/en/activity-page/jcr:content/root/rightsidebar/help_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activities/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "helpWidgetHeading", "Do You Need Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetDescription", "<p>Do you have a question or need more information ?</p>");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALabel", "Get More Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCTALink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(HelpWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, HelpWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Do You Need Help", data.getTitle());
    assertEquals("<p>Do you have a question or need more information ?</p>", data.getDescription());
    assertEquals("Get More Help", data.getCta().getText());
    assertEquals("/content/sauditourism/page", data.getCta().getUrl());
  }

  @Test
  void testHelpWidgetModel_fromTourContentFragment(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/help-widget/tour-page.json",
        "/content/sauditourism/en/tour-page");
    context
      .load()
      .json(
        "/components/help-widget/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/tour1");

    context.currentPage("/content/sauditourism/en/tour-page");
    context.currentResource("/content/sauditourism/en/tour-page/jcr:content/root/rightsidebar/help_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/tour1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    Utils.mockContentFragmentElement(spyFragment, "helpWidgetHeading", "Do You Need Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetDescription", "<p>Do you have a question or need more information ?</p>");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCtaLabel", "Get More Help");
    Utils.mockContentFragmentElement(spyFragment, "helpWidgetCtaLink", "/content/sauditourism/page");

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource()
      .adaptTo(HelpWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, HelpWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Do You Need Help", data.getTitle());
    assertEquals("<p>Do you have a question or need more information ?</p>", data.getDescription());
    assertEquals("Get More Help", data.getCta().getText());
    assertEquals("/content/sauditourism/page", data.getCta().getUrl());
  }
}