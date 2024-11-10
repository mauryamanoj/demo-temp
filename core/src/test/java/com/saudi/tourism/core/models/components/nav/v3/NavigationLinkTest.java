package com.saudi.tourism.core.models.components.nav.v3;

import com.saudi.tourism.core.models.components.DestinationPageModel;
import com.saudi.tourism.core.utils.Utils;
import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Getter
class NavigationLinkTest {

  private static final String REGION_1 = "region1";
  private static final String REGION_2 = "region2";

  private Resource currentResource = mock(Resource.class);
  private ResourceResolver resourceResolver = mock(ResourceResolver.class);
  private Resource regionResource1 = mock(Resource.class);
  private Resource regionResource2 = mock(Resource.class);
  private DestinationPageModel destinationPageModel = mock(DestinationPageModel.class);

  @BeforeEach
  public void setUp() {
    when(currentResource.getResourceResolver()).thenReturn(resourceResolver);
    when(resourceResolver.getResource(REGION_1)).thenReturn(regionResource1);
    when(resourceResolver.getResource(REGION_2)).thenReturn(regionResource2);
    when(regionResource1.adaptTo(eq(DestinationPageModel.class))).thenReturn(destinationPageModel);
    when(regionResource2.adaptTo(eq(DestinationPageModel.class))).thenReturn(destinationPageModel);
  }

  @Test
  @DisplayName("Test isMap when regions and cities are null")
  public void testIsMapWhenRegionsAndCitiesAreNull() {
    NavigationLink link = new NavigationLink(null, null, currentResource);
    assertFalse(link.isMap());
  }

  @Test
  @DisplayName("Test isMap when regions is not null")
  public void testIsMapWhenRegionsIsNotNull() {
    NavigationLink link = new NavigationLink(new String[]{REGION_1, REGION_2}, null,
        currentResource);
    assertTrue(link.isMap());
    assertEquals(2, link.getRegions().size());
  }

  @Test
  @DisplayName("Test isMap when cities is not null")
  public void testIsMapWhenCitiesIsNotNull() {
    NavigationLink link = new NavigationLink(new String[]{REGION_1, REGION_2}, new String[]{REGION_1},
        currentResource);
    assertTrue(link.isMap());
    assertEquals(2, link.getRegions().size());
    assertEquals(1, link.getCities().size());
  }

  @Test
  @DisplayName("Test hasArticle when article data is null")
  public void testHasArticleWhenArticleDataIsNull() {
    NavigationLink link = new NavigationLink(null, null, currentResource);
    assertFalse(link.hasArticle());
  }

  @Test
  @DisplayName("Test hasArticle when articleImage is not null and articlePath is null")
  public void testHasArticleWhenArticlePathIsNull() {
    NavigationLink link = new NavigationLink(null, null, currentResource);
    Utils.setInternalState(link, "articleImage", "articleImage");
    assertFalse(link.hasArticle());
  }

  @Test
  @DisplayName("Test hasArticle when articlePath is not null and articleImage is null")
  public void testHasArticleWhenArticleImageIsNull() {
    NavigationLink link = new NavigationLink(null, null, currentResource);
    Utils.setInternalState(link, "articlePath", "articlePath");
    assertFalse(link.hasArticle());
  }

  @Test
  @DisplayName("Test hasArticle when article data is not null")
  public void testHasArticleWhenArticleDataIsNotNull() {
    NavigationLink link = new NavigationLink(null, null, currentResource);
    Utils.setInternalState(link, "articleImage", "articleImage");
    Utils.setInternalState(link, "articlePath", "articlePath");
    assertTrue(link.hasArticle());
  }

}