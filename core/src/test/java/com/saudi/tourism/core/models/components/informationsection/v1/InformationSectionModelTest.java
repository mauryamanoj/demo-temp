package com.saudi.tourism.core.models.components.informationsection.v1;

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
class InformationSectionModelTest {

  @Mock
  private RunModeService runModeService;

  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(InformationSectionModel.class, InformationSectionCFModel.class, FAQ.class);

    var attractionAdapter = new InformationSectionAttractionCFAdapter();
    var activityAdapter = new InformationSectionActivityCFAdapter();
    var tourAdapter = new InformationSectionTourCFAdapter();
    var adapterFactory = new InformationSectionCFAdapterFactory();
    List<InformationSectionCFAdapter> informationSectionCFAdapters = new ArrayList<>();
    informationSectionCFAdapters.add(activityAdapter);
    informationSectionCFAdapters.add(attractionAdapter);
    informationSectionCFAdapters.add(tourAdapter);
    adapterFactory.setAdapters(informationSectionCFAdapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.informationsection.v1.InformationSectionCFModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
    context.registerService(RunModeService.class, runModeService);
  }

  @Test
  void informationSectionModelManualAuthoringEnabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/information-section/story-page-authoring-enabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section")
      .adaptTo(InformationSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationSectionModel.class);

    // Assert
    assertNotNull(data);
    assertTrue(data.getEnableManualAuthoring());
    assertEquals("Title", data.getTitle());
    assertEquals("View More Label", data.getReadMoreLabel());
    assertEquals("View Less Label", data.getReadLessLabel());
    assertEquals(true, data.getHideReadMore());
    assertEquals("information", data.getComponentId());
    assertEquals("Question 1", data.getData().get(0).getQuestion());
    assertEquals("<p>Ansewer 1</p>", data.getData().get(0).getAnswer());
  }

  @Test
  void informationSectionModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/information-section/story-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/information-section/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "qaTitle", "QA Title");
    mockContentFragmentElement(spyFragment, "qaHideReadMore", true);
    mockContentFragmentElement(spyFragment, "qaReadMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "qaReadLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "questionAnswer", new String[]{"{\"question\":\"Question CF\",\"answer\":\"Answer CF\"}"});

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(InformationSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("QA Title", data.getTitle());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertEquals(true, data.getHideReadMore());
    assertEquals("Question CF", data.getData().get(0).getQuestion());
    assertEquals("Answer CF", data.getData().get(0).getAnswer());
  }

  @Test
  void informationSectionActivityModelManualAuthoringEnabled(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/information-section/activity/activity-page-authoring-enabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section")
      .adaptTo(InformationSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationSectionModel.class);

    // Assert
    assertNotNull(data);
    assertTrue(data.getEnableManualAuthoring());
    assertEquals("Title", data.getTitle());
    assertEquals("View More Label", data.getReadMoreLabel());
    assertEquals("View Less Label", data.getReadLessLabel());
    assertEquals(true, data.getHideReadMore());
    assertEquals("Question 1", data.getData().get(0).getQuestion());
    assertEquals("<p>Ansewer 1</p>", data.getData().get(0).getAnswer());
  }

  @Test
  void informationSectionActivityModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/information-section/activity/activity-page-authoring-disabled.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/information-section/activity/activity-cf.json",
        "/content/dam/sauditourism/cf/en/activity/activity1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_section");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/activity/activity1").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "qaTitle", "QA Title");
    mockContentFragmentElement(spyFragment, "qaHideReadMore", true);
    mockContentFragmentElement(spyFragment, "qaReadMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "qaReadLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "questionAnswer", new String[]{"{\"question\":\"Question CF\",\"answer\":\"Answer CF\"}"});

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(InformationSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("QA Title", data.getTitle());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertEquals(true, data.getHideReadMore());
    assertEquals("Question CF", data.getData().get(0).getQuestion());
    assertEquals("Answer CF", data.getData().get(0).getAnswer());
  }

  @Test
  void informationSectionTourModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/information-section/tour-page-authoring-disabled.json",
        "/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context
      .load()
      .json(
        "/components/information-section/tour-cf.json",
        "/content/dam/sauditourism/cf/en/tours/first-tour");

    context.currentPage("/content/sauditourism/en/test/sprint6/menu-demo/first-tour");
    context.currentResource("/content/sauditourism/en/test/sprint6/menu-demo/first-tour/jcr:content/root/responsivegrid");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/tours/first-tour").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "qaTitle", "QA Title");
    mockContentFragmentElement(spyFragment, "qaHideReadMore", true);
    mockContentFragmentElement(spyFragment, "qaReadMoreLabel", "Read More");
    mockContentFragmentElement(spyFragment, "qaReadLessLabel", "Read Less");
    mockContentFragmentElement(spyFragment, "questionAnswer", new String[]{"{\"question\":\"What is the best time to visit saudi ?\",\"answer\":\"<p>This is the answer for the first question</p>\\n\",\"textIsRich\":\"true\"}"});

    when(ft.getTitle()).thenReturn("tour");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(InformationSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationSectionModel.class);

    // Assert
    assertNotNull(data);
    assertFalse(data.getEnableManualAuthoring());
    assertEquals("QA Title", data.getTitle());
    assertEquals("Read More", data.getReadMoreLabel());
    assertEquals("Read Less", data.getReadLessLabel());
    assertEquals(true, data.getHideReadMore());
    assertEquals("What is the best time to visit saudi ?", data.getData().get(0).getQuestion());
    assertEquals("<p>This is the answer for the first question</p>", data.getData().get(0).getAnswer());
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