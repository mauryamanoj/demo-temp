package com.saudi.tourism.core.models.components.filterExplorer.v1;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

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

import static com.saudi.tourism.core.utils.I18nConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class FilterExplorerModelTest {

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
          case CANCEL:
            return "Cancel";
          case SEARCH_CLEAR:
            return "Clear";
          case I18_KEY_APPLY:
            return "Apply";
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
  void init(final AemContext context) {
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test
  public void testThingsToDoFilterExplorerModel(final AemContext context){
    //Arrange
    when(saudiTourismConfigs.getThingsToDoApiEndpoint()).thenReturn("/bin/api/v1/things-to-do");

    context
      .load()
      .json(
        "/components/filter-explorer/things-todo-search.json",
        "/content/sauditourism/en/revamp/things-search/jcr:content/root/responsivegrid/things_todo_explorer");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/revamp/things-search/jcr:content/root/responsivegrid/things_todo_explorer")
        .adaptTo(FilterExplorerModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, FilterExplorerModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("Things to do", data.getTitle());
    assertEquals("Cancel", data.getCancelLabel());
    assertEquals("Clear", data.getClearLabel());
    assertEquals("Apply", data.getApplyLabel());
    assertEquals("Loading...", data.getLoadingLabel());
    assertEquals("Cleat all", data.getClearAllButton());
    assertEquals("/bin/api/v1/things-to-do", data.getApiUrl());
    assertEquals("All", data.getTypes().get(0).getLabel());
    assertEquals("activity", data.getTypes().get(1).getId());
    assertEquals("Activities", data.getTypes().get(1).getLabel());
  }

  @Test
  public void testStoriesFilterExplorerModel(final AemContext context){
    //Arrange
    when(saudiTourismConfigs.getStoriesApiEndpoint()).thenReturn("/bin/api/v1/stories");

    context
      .load()
      .json(
        "/components/filter-explorer/stories-search.json",
        "/content/sauditourism/en/revamp/things-search/jcr:content/root/responsivegrid/stories_explorer");

    //Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/revamp/things-search/jcr:content/root/responsivegrid/stories_explorer")
        .adaptTo(FilterExplorerModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, FilterExplorerModel.class);

    //Assert
    assertNotNull(data);
    assertEquals("Stories", data.getTitle());
    assertEquals("Cancel", data.getCancelLabel());
    assertEquals("Clear", data.getClearLabel());
    assertEquals("Apply", data.getApplyLabel());
    assertEquals("Loading...", data.getLoadingLabel());
    assertEquals("Cleat all", data.getClearAllButton());
    assertEquals("stories", data.getFilterType());
    assertEquals("/bin/api/v1/stories", data.getApiUrl());
    assertNull(data.getTypes());
  }
}