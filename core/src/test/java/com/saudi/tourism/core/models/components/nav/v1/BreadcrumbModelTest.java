package com.saudi.tourism.core.models.components.nav.v1;

import com.adobe.cq.wcm.core.components.services.link.PathProcessor;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.WCMException;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.RunModeService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class BreadcrumbModelTest {

  private static final String PAGE_PATH = "/content/sauditourism/en/calendar/riyadh-season";
  private static final String RESOURCE_PATH = "/content/sauditourism/en/calendar/riyadh-season/jcr:content/root/responsivegrid/breadcrumb";
  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();

  @Mock
  private RunModeService runModeService;

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private PathProcessor pathProcessor;

  @BeforeEach
  void setUp(final AemContext aemContext) throws WCMException {
    aemContext.pageManager().create("/", "content", "cq:Page", "content");
    aemContext.pageManager().create("/content", "sauditourism", "cq:Page", "sauditourism root page");
    aemContext.pageManager().create("/content/sauditourism", "en", "cq:Page", "Home");
   aemContext.pageManager().create("/content/sauditourism/en", "calendar", "cq:Page", "Festivals & Events");

    aemContext.load().json("/pages/riyadh-season.json", PAGE_PATH).adaptTo(Page.class);

    aemContext.registerService(PathProcessor.class, pathProcessor);
    aemContext.registerService(RunModeService.class, runModeService);

    when(pathProcessor.accepts(any(String.class), any(SlingHttpServletRequest.class))).thenReturn(Boolean.TRUE);
    when(pathProcessor.map(eq("/content/sauditourism/en.html"), any(SlingHttpServletRequest.class))).thenReturn("/en.html");
    when(pathProcessor.map(eq("/content/sauditourism/en/calendar.html"), any(SlingHttpServletRequest.class))).thenReturn("/en/calendar.html");
    when(pathProcessor.map(eq("/content/sauditourism/en/calendar/riyadh-season.html"), any(SlingHttpServletRequest.class))).thenReturn("/en/calendar/riyadh-season.html");
    when(runModeService.isPublishRunMode()).thenReturn(true);
  }


  @Test
  void testBreadcrumbModel(final AemContext aemContext){
    //Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(RESOURCE_PATH);

    //Act
    final BreadcrumbModel model = aemContext.request().adaptTo(BreadcrumbModel.class);
    final String json = model.getJson();
    final BreadcrumbModel data = gson.fromJson(json, BreadcrumbModel.class);

    //Assert
    assertNotNull(data.getItems());
    assertEquals(3, data.getItems().size());

    assertEquals("/en", data.getItems().get(0).getLink().getUrl());
    assertEquals("Home", data.getItems().get(0).getTitle());

    assertEquals("/en/calendar", data.getItems().get(1).getLink().getUrl());
    assertEquals("Festivals & Events", data.getItems().get(1).getTitle());

    assertEquals("/en/calendar/riyadh-season", data.getItems().get(2).getLink().getUrl());
    assertEquals("Riyadh Season", data.getItems().get(2).getTitle());
  }

}
