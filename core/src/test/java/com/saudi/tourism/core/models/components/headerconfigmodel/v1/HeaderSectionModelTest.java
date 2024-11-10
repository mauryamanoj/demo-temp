package com.saudi.tourism.core.models.components.headerconfigmodel.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.header.*;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
public class HeaderSectionModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @Mock
  private FragmentTemplate ft;

  @BeforeEach
  public void setUp(AemContext context) {
    context.addModelsForClasses(HeaderSectionCFModel.class, HeaderSectionModel.class, Image.class, Author.class);
    var headerSectionCFAdapter = new HeaderSectionAttractionCFAdapter();
    var headerSectionCFActivityAdapter = new HeaderSectionActivityCFAdapter();
    var headerSectionCFAdapterFactory = new HeaderSectionCFAdapterFactory();
    List<HeaderSectionCFAdapter> adapters = new ArrayList<>();
    adapters.add(headerSectionCFAdapter);
    adapters.add(headerSectionCFActivityAdapter);
    headerSectionCFAdapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.header.HeaderSectionCFModel");
    context.registerService(AdapterFactory.class, headerSectionCFAdapterFactory, properties);
    context.load().json("/components/header-config-model/content-section.json", CONTENT_PATH);
  }

  @Test
  public void testHeaderSectionWithoutCFModel(AemContext context) {

    context
      .load()
      .json(
        "/components/header-config-model/content-section.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section_without");
    // Act
    final var headerSectionModel = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section_without")
      .adaptTo(HeaderSectionModel.class);
    assertEquals("DEMO", headerSectionModel.getTitle());
    assertEquals("Sub Title", headerSectionModel.getSubtitle());
    assertEquals("Author Text", headerSectionModel.getAuthor().getAuthorText());
    assertEquals("/content/sauditourism/dmeo", headerSectionModel.getAuthor().getAuthorCtaLink());
    assertEquals("/content/dam/static-images/resources/flags/png/034-china.png", headerSectionModel.getAuthor().getImage().getFileReference());
  }


  @Test
  public void testHeaderSectionWithAttractionCFModel(AemContext context) {
    context
      .load()
      .json(
        "/components/header-config-model/content-section.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section");
    // Act

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section");

    final var contentFragment = context.resourceResolver().getResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section/cf").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "CF title");
    mockContentFragmentElement(spyFragment, "subtitle", "CF subtitle");
    mockContentFragmentElement(spyFragment, "authorText", "CF authorText");
    mockContentFragmentElement(spyFragment, "authorCtaLink", "/content/sauditourism/dmeo");
    mockContentFragmentElement(spyFragment, "s7image", "/content/sauditourism/dmeo/image.png");
    mockContentFragmentElement(spyFragment, "alt", "TEST");

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var headerSectionModel = context
      .currentResource()
      .adaptTo(HeaderSectionModel.class);


    assertEquals("CF title", headerSectionModel.getTitle());
    assertEquals("CF subtitle", headerSectionModel.getSubtitle());
    assertEquals("CF authorText", headerSectionModel.getAuthor().getAuthorText());
    assertEquals("/content/sauditourism/dmeo", headerSectionModel.getAuthor().getAuthorCtaLink());
    assertNotNull(headerSectionModel.getAuthor().getImage());
  }

  @Test
  public void testHeaderSectionWithActivityCFModel(AemContext context) {
    context
      .load()
      .json(
        "/components/header-config-model/content-section-activity.json",
        "/content/sauditourism/en/demo-cf-page");
    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section");
    // Act

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section");

    final var contentFragment = context.resourceResolver().getResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/header_section/cf").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "CF title");
    mockContentFragmentElement(spyFragment, "subtitle", "CF subtitle");
    mockContentFragmentElement(spyFragment, "authorText", "CF authorText");
    mockContentFragmentElement(spyFragment, "authorCtaLink", "/content/sauditourism/dmeo");
    mockContentFragmentElement(spyFragment, "s7image", "/content/sauditourism/dmeo/image.png");
    mockContentFragmentElement(spyFragment, "alt", "TEST");

    when(ft.getTitle()).thenReturn("activity");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var headerSectionModel = context
      .currentResource()
      .adaptTo(HeaderSectionModel.class);


    assertEquals("CF title", headerSectionModel.getTitle());
    assertEquals("CF subtitle", headerSectionModel.getSubtitle());
    assertEquals("CF authorText", headerSectionModel.getAuthor().getAuthorText());
    assertEquals("/content/sauditourism/dmeo", headerSectionModel.getAuthor().getAuthorCtaLink());
    assertNotNull(headerSectionModel.getAuthor().getImage());
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
