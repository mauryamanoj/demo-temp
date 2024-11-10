package com.saudi.tourism.core.models.components.thingstodo.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
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

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static com.saudi.tourism.core.utils.Constants.*;
import static com.saudi.tourism.core.utils.I18nConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ThingsToDoCardsModelTest {
  private final Gson gson = new GsonBuilder().create();

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(final String key) {
        switch (key) {
          case LOAD_MORE_TEXT:
            return "Load More";
          case ALL_LABEL:
            return "All";
          default:
            return null;
        }
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };


  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(ThingsToDoCardsModel.class);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);

    when(saudiTourismConfigs.getThingsToDoApiEndpoint()).thenReturn("/bin/api/v1/things-to-do");
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test
  public void testAutoThingsToDoCardsModel(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/things-todo/auto-things-todo-cards.json",
        "/content/sauditourism/en/revamp/things-todo/jcr:content/root/responsivegrid/things_todo_cards");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/revamp/things-todo/jcr:content/root/responsivegrid/things_todo_cards")
        .adaptTo(ThingsToDoCardsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ThingsToDoCardsModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("Recommanded Activities & Tours (Auto)", data.getTitle());

    assertNotNull(data.getLink());
    assertEquals("View all", data.getLink().getText());
    assertEquals("/content/sauditourism/en", data.getLink().getUrl());

    assertNotNull(data.getDisplay());
    assertEquals("small", data.getDisplay().getCardSize());
    assertEquals("scrollable", data.getDisplay().getDisplayType());

    assertNotNull(data.getLoadMore());
    assertEquals("Load More", data.getLoadMore().getLoadMoreLabel());

    assertEquals("/bin/api/v1/things-to-do", data.getApiUrl());

    assertNotNull(data.getFilter());
    assertEquals("attraction", data.getFilter().getType().get(0));
    assertEquals("activity", data.getFilter().getType().get(1));
    assertNotNull(data.getFilter().getCategories());
    assertEquals("sauditourism:categories/nature", data.getFilter().getCategories().get(0));
    assertEquals("sauditourism:categories/entertainment", data.getFilter().getCategories().get(1));
    assertEquals("sauditourism:categories/beauty_relax", data.getFilter().getCategories().get(2));
    assertNotNull(data.getFilter().getPoiTypes());
    assertEquals("sauditourism:type/ttd/activities/dancing", data.getFilter().getPoiTypes().get(0));
    assertEquals("sauditourism:type/ttd/activities/camping", data.getFilter().getPoiTypes().get(1));
    assertNotNull(data.getSort());
    assertEquals("randomized", data.getSort().getSortBy().get(0));
  }

  @Test
  public void testHandPickedThingsToDoCardsModel(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/things-todo/things-todo-cards.json",
        "/content/sauditourism/en/revamp/things-todo/jcr:content/root/responsivegrid/things_todo_cards");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/revamp/things-todo/jcr:content/root/responsivegrid/things_todo_cards")
        .adaptTo(ThingsToDoCardsModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, ThingsToDoCardsModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("Recommanded Activities & Tours", data.getTitle());

    assertNotNull(data.getLink());
    assertEquals("View all", data.getLink().getText());
    assertEquals("/content/sauditourism/en", data.getLink().getUrl());

    assertNotNull(data.getDisplay());
    assertEquals("small", data.getDisplay().getCardSize());
    assertEquals("scrollable", data.getDisplay().getDisplayType());

    assertNotNull(data.getLoadMore());
    assertEquals("Load More", data.getLoadMore().getLoadMoreLabel());

    assertEquals("/bin/api/v1/things-to-do", data.getApiUrl());

    assertNull(data.getFilter());
    assertNull(data.getSort());

    assertNotNull(data.getHandpick());
    assertNotNull(data.getHandpick().getThingsToDoCFPaths());
    assertEquals("/content/dam/sauditourism/cf/en/attractions/medina-attraction", data.getHandpick().getThingsToDoCFPaths().get(0));
    assertEquals("/content/dam/sauditourism/cf/en/attractions/jeddah-bobattraction", data.getHandpick().getThingsToDoCFPaths().get(1));
    assertEquals(DEFAULT_CATEGORY_ICON, data.getTabs().get(0).getIcon());
    assertEquals("All", data.getTabs().get(0).getTitle());
    assertEquals("demo", data.getTabs().get(1).getIcon());
    assertEquals("title", data.getTabs().get(1).getTitle());
    assertEquals("sauditourism:categories/nature", data.getTabs().get(1).getCategory());
  }

}