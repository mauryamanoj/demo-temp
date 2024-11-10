package com.saudi.tourism.core.models.components.topactivities;

import com.saudi.tourism.core.models.common.ComponentHeading;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import lombok.Getter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit test class for testing TopActivitiesModel class.
 */
@ExtendWith(AemContextExtension.class)
@Getter
class TopActivitiesModelTest {

  public static final String ACTIVITIES_PAGE = "/content/sauditourism/en/activities";

  public static final String CARDS_GRID_RESOURCE_PATH =
      ACTIVITIES_PAGE + "/jcr:content/root/responsivegrid/cardsgrid";

  // Testing object
  private TopActivitiesModel topActivitiesModel;

  @BeforeEach void setUp(AemContext context) {
    // All Activities page
    context.load().json("/components/cardsgrid/content.json", ACTIVITIES_PAGE);

    topActivitiesModel = mock(TopActivitiesModel.class, Mockito.CALLS_REAL_METHODS);

    Utils.setInternalState(topActivitiesModel, "cards", new LinkedList<>());
    Utils.setInternalState(topActivitiesModel, "itemCount", TopActivitiesModel.DEFAULT_ITEM_COUNT);
  }

  // TODO How to check init() method without executing getCardsFromCardsGrid?
//  @Test void testPostConstruct(AemContext context) {
//  }

  /**
   * I18n service initialization for the specified context.
   *
   * @param context AEM context
   */
  private void initI18nMock(AemContext context) {
    ResourceBundle i18nBundle = new ResourceBundle() {
      @Override protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    ResourceBundleProvider i18nProvider = mock(ResourceBundleProvider.class);
    Mockito.when(i18nProvider.getResourceBundle(Mockito.any())).thenReturn(i18nBundle);

    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");
  }

  @Test void getCardsFromCardsGrid(AemContext context) {
    final ResourceResolver resourceResolver = context.resourceResolver();

    // Init i18n for adapting CardsGridModel
    initI18nMock(context);

    final Resource cardsGridResource =
        context.resourceResolver().getResource(CARDS_GRID_RESOURCE_PATH);

    doReturn(cardsGridResource).when(topActivitiesModel)
        .getCardsGridResource(resourceResolver, CARDS_GRID_RESOURCE_PATH);

    // Check with blank path
    assertNull(topActivitiesModel.getHeader());
    topActivitiesModel.getCardsFromCardsGrid(resourceResolver);
    assertTrue(CollectionUtils.isEmpty(topActivitiesModel.getCards()));

    final ComponentHeading header = new ComponentHeading();
    final Link link = new Link();
    link.setUrl(CARDS_GRID_RESOURCE_PATH);
    Utils.setInternalState(header, "link", link);
    Utils.setInternalState(topActivitiesModel, "header", header);
    topActivitiesModel.getCardsFromCardsGrid(resourceResolver);

    // two items in json, check if both of them were processed
    assertEquals(2, topActivitiesModel.getCards().size());

    // Check LinkUtils.getUrlWithExtension() was executed
    assertNull(topActivitiesModel.getHeader().getLink().getUrlWithExtension());
  }

  @Test void getCardsGridResource(AemContext context) {
    // Not existing
    final Resource nullResource =
        topActivitiesModel.getCardsGridResource(context.resourceResolver(), "not-existing-path");
    assertNull(nullResource);

    final String properResourcePath =
        ACTIVITIES_PAGE + "/jcr:content/root/responsivegrid/cardsgrid";

    // Full path to the resource
    final Resource cardsGridResource =
        topActivitiesModel.getCardsGridResource(context.resourceResolver(), properResourcePath);
    assertNotNull(cardsGridResource);
    assertEquals(properResourcePath, cardsGridResource.getPath());
    assertTrue(cardsGridResource.isResourceType(Constants.CARDS_GRID_RESOURCE_TYPE));

    // Path to the page
    final Resource byPagePath =
        topActivitiesModel.getCardsGridResource(context.resourceResolver(), ACTIVITIES_PAGE);
    assertEquals(properResourcePath, byPagePath.getPath());

    // Path to the page content resource
    final Resource byContentPath = topActivitiesModel
        .getCardsGridResource(context.resourceResolver(), ACTIVITIES_PAGE + "/jcr:content");
    assertEquals(properResourcePath, byContentPath.getPath());

    // Path to some container
    final Resource byContainerPath = topActivitiesModel
        .getCardsGridResource(context.resourceResolver(), ACTIVITIES_PAGE + "/jcr:content/root");
    assertEquals(properResourcePath, byContainerPath.getPath());
  }
}
