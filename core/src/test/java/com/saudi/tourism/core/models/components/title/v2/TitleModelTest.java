package com.saudi.tourism.core.models.components.title.v2;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.common.Link;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
class TitleModelTest {

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  private static final String RESOURCE_PATH = "/content/sauditourism/en/offers/jcr:content/root/responsivegrid/c02_title";
  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @BeforeEach
  void setUp(final AemContext aemContext){
    aemContext.load().json("/components/c-02-title/content.json", RESOURCE_PATH);
  }

  @Test
  void testTitleModel(final AemContext aemContext){
    //Arrange

    //Act
    final TitleModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(TitleModel.class);
    final String json = model.getJson();
    final TitleModel data = gson.fromJson(json, TitleModel.class);

    //Assert
    assertNotNull(data);
    assertNotNull(data.getHeading());
    assertNotNull(data.getLink());

    final Heading heading = data.getHeading();
    assertEquals("Hotels just for you", heading.getText());
    assertEquals("h3", heading.getWeight());

    final Link link = data.getLink();
    assertEquals("See More", link.getCopy());
    assertEquals("/en/offers", link.getUrl());
    assertFalse(link.isTargetInNewWindow());

    assertTrue(data.isShowUnderline());
  }
}