package com.saudi.tourism.core.models.components.events;

import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EventsVenueModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/riyadh-season";
  private static final String RESOURCE_PATH = "/content/sauditourism/en/calendar/riyadh-season/jcr:content/root/responsivegrid/events_cards_1696104";

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  void setUp(final AemContext aemContext) throws InvalidTagFormatException {
    aemContext.load().json("/pages/riyadh-season.json", PAGE_PATH);

    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    when(saudiTourismConfigs.getEventsApiEndpoint()).thenReturn("/bin/api/v1/events");

    TagManager tagManager = aemContext.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:events/Convention & Exhibition", "Convention & Exhibition", "Convention & Exhibition");
    tagManager.createTag("sauditourism:events/Shows & Performing Arts", "Shows & Performing Arts", "Shows & Performing Arts");
  }

  @Test
  void testEventsVenueModel(final AemContext aemContext) {
    //Arrange
    aemContext.currentResource(RESOURCE_PATH);

    //Act
    final EventsVenueModel model = aemContext.currentResource().adaptTo(EventsVenueModel.class);
    final String json = model.getJson();
    final EventsVenueModel data = gson.fromJson(json, EventsVenueModel.class);

    //Assert
    assertEquals("calendar", data.getCardType());
    assertEquals("grouped", data.getCardStyle());
    assertEquals("All Venues", data.getHeadline());
    assertEquals("/bin/api/v1/events", data.getApiUrl());
    assertEquals("View All", data.getLink().getCopy());
    assertEquals("/en/calendar", data.getLink().getUrl());
    assertFalse(data.getLink().isTargetInNewWindow());
    assertEquals(List.of("Convention & Exhibition", "Shows & Performing Arts"), data.getVenues());
  }
}