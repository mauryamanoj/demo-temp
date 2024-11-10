package com.saudi.tourism.core.models.components.essentiallinks;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Dictionary;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EssentialLinksModelTest {

  private static final String GRID_SMALL_RESOURCE_PATH =
      "/content/sauditourism/en/arabian-winter1/jcr:content/root/responsivegrid/grid-small";

  private static final String GRID_BIG_RESOURCE_PATH =
    "/content/sauditourism/en/arabian-winter1/jcr:content/root/responsivegrid/grid-big";

  private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

  @Mock
  private ResourceBundleProvider i18nProvider;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, properties);

    aemContext.load().json("/components/essential-links/grid-small.json", GRID_SMALL_RESOURCE_PATH);
    aemContext.load().json("/components/essential-links/grid-big.json", GRID_BIG_RESOURCE_PATH);
  }

  @Test
  void testEssentialLinksGridSmallModel(final AemContext aemContext) {
    // Arrange

    // Act
    final EssentialLinksModel model =
        aemContext.currentResource(GRID_SMALL_RESOURCE_PATH).adaptTo(EssentialLinksModel.class);
    final String json = model.getJson();
    final EssentialLinksModel data = gson.fromJson(json, EssentialLinksModel.class);

    // Assert
    assertEquals("https://wa.me/966920000890", data.getLinkButton().getUrlWithExtension());
    assertEquals("https://wa.me/966920000890", data.getLinkButton().getUrl());
    assertEquals("true", data.getHideMiddleOrnament());
    assertEquals("03A", data.getOrnamentId());
  }

  @Test
  void testEssentialLinksGridBigModel(final AemContext aemContext) {
    // Arrange

    // Act
    final EssentialLinksModel model =
      aemContext.currentResource(GRID_BIG_RESOURCE_PATH).adaptTo(EssentialLinksModel.class);
    final String json = model.getJson();
    final EssentialLinksModel data = gson.fromJson(json, EssentialLinksModel.class);

    // Assert
    assertEquals("https://wa.me/966920000890", data.getLinkButton().getUrlWithExtension());
    assertEquals("https://wa.me/966920000890", data.getLinkButton().getUrl());
    assertEquals("true", data.getHideMiddleOrnament());
    assertEquals("07A", data.getOrnamentId());
  }
}
