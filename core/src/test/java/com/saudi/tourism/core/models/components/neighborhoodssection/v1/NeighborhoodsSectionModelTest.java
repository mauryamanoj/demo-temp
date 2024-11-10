package com.saudi.tourism.core.models.components.neighborhoodssection.v1;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class NeighborhoodsSectionModelTest {
  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(NeighborhoodsSectionModel.class, NeighborhoodsSectionCard.class);

    var storyAdapter = new NeighborhoodsSectionStoryCFAdapter();
    var attractionAdapter = new NeighborhoodsSectionAttractionCFAdapter();
;   var adapterFactory = new NeighborhoodsSectionCFModelAdapterFactory();

    List<NeighborhoodsSectionCFAdapter> adapters = new ArrayList<>();
    adapters.add(storyAdapter);
    adapters.add(attractionAdapter);
    adapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.neighborhoodssection.v1.NeighborhoodsSectionCard");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }

  @Test
  void testNeighborhoods(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/neighborhoodssection/page.json",
        "/content/sauditourism/en/neighborhoodssection-page");

    context
      .load()
      .json(
        "/components/neighborhoodssection/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction");
    context
      .load()
      .json(
        "/components/neighborhoodssection/story-cf.json",
        "/content/dam/sauditourism/cf/en/stories/story");

    context.currentPage("/content/sauditourism/en/neighborhoodssection-page");
    context.currentResource("/content/sauditourism/en/neighborhoodssection-page/jcr:content/root/responsivegrid/neighborhoods");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction").adaptTo(ContentFragment.class);
    final var spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "CF Attraction");
    mockContentFragmentElement(spyFragment, "pagePath", "/path/to/page");
    mockContentFragmentElement(spyFragment, "images", new String[]{"{\"type\":\"image\",\"image\":\"/content/dam/sauditourism/en/attractions/attraction1/jcr:content/renditions/cq5dam.web.1280.1280.jpeg\",\"s7image\":\"/content/dam/sauditourism/en/attractions/attraction1/jcr:content/renditions/cq5dam.web.1280.1280.jpeg\"}"});
    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/neighborhoodssection-page/jcr:content/root/responsivegrid/neighborhoods")
      .adaptTo(NeighborhoodsSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, NeighborhoodsSectionModel.class);

      // Assert
    assertNotNull(data);
    assertEquals("Title", data.getTitle());
    assertEquals("/content/sauditourism/en/winter-season", data.getLink().getUrl());
    assertEquals("View all", data.getLink().getCopy());;
    assertEquals(1, data.getCards().size());
    assertEquals("CF Attraction", data.getCards().get(0).getTitle());
    assertEquals("/path/to/page", data.getCards().get(0).getCtaLink());
    assertEquals("/content/dam/sauditourism/en/attractions/attraction1/jcr:content/renditions/cq5dam.web.1280.1280.jpeg", data.getCards().get(0).getImage().getDesktopImage());
    assertEquals("/content/dam/sauditourism/en/attractions/attraction1/jcr:content/renditions/cq5dam.web.1280.1280.jpeg", data.getCards().get(0).getImage().getMobileImage());
  }

  @Test
  void testNeighborhoodsNoCF(final AemContext context) {
    // Arrange
    context
      .load()
      .json(
        "/components/neighborhoodssection/page-with-no-cf.json",
        "/content/sauditourism/en/neighborhoodssection-page-no-cf");

    context.currentPage("/content/sauditourism/en/neighborhoodssection-page-no-cf");
    context.currentResource("/content/sauditourism/en/neighborhoodssection-page-no-cf/jcr:content/root/responsivegrid/neighborhoods");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/neighborhoodssection-page-no-cf/jcr:content/root/responsivegrid/neighborhoods")
      .adaptTo(NeighborhoodsSectionModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, NeighborhoodsSectionModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Title", data.getTitle());
    assertEquals("/content/sauditourism/en/winter-season", data.getLink().getUrl());
    assertEquals("View all", data.getLink().getCopy());
    assertNull(data.getCards());
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