package com.saudi.tourism.core.models.components.attractions.v1;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AttractionsMapModelTest {

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext context =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(AttractionsMapModel.class);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    context
      .load()
      .json(
        "/components/attractions-map/attractions-map.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/attractions_map");
  }

  @Test
  void getAttractionsMapModel(final AemContext aemContext) {
    //Arrange
    when(saudiTourismConfigs.getGoogleMapsKey()).thenReturn("7912345c-6098-11ee-8c99-0242ac120002");

    //Act
    final var model =
      aemContext
        .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/attractions_map")
        .adaptTo(AttractionsMapModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, AttractionsMapModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("All Attractions", data.getTitle());
    assertEquals("7912345c-6098-11ee-8c99-0242ac120002", data.getMapApiKey());
    assertNotNull(data.getLink());
    assertEquals("View on Maps", data.getLink().getText());
    assertEquals("/en/shopping-in-saudi", data.getLink().getUrl());
    assertEquals("All categories", data.getCategoryFilter().getLabel());
    assertEquals("All destinations", data.getDestinationFilter().getLabel());
  }

}