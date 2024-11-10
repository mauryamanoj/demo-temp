package com.saudi.tourism.core.models.components.smallstories.v1;

import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class SmallStoriesModelTest {
  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(SmallStoriesModel.class, CardModel.class);

    var storyAdapter = new SmallStoriesStoryCFAdapter();
    var adapterFactory = new SmallStoriesCFAdapterFactory();
    List<SmallStoriesCFAdapter> adapters = new ArrayList<>();
    adapters.add(storyAdapter);
    adapterFactory.setAdapters(adapters);
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.smallstories.v1.CardModel");
    context.registerService(AdapterFactory.class, adapterFactory, properties);
  }

  @Test
  public void testSmallStoriesUsingStoryCF(final AemContext context) {

    // Arrange
    context
      .load()
      .json("/components/small-stories/small-stories.json", "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/small_stories");

    // Act
    final var model = context
      .currentResource(
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/small_stories")
      .adaptTo(SmallStoriesModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, SmallStoriesModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Small Stories", data.getTitle());
    assertEquals("BuyCards", data.getType());
    assertEquals("View All", data.getLink().getText());
    assertEquals("/content/sauditourism/en/abha-festival", data.getLink().getUrl());

  }
}
