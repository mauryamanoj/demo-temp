package com.saudi.tourism.core.models.informationlistwidget.v1;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import com.adobe.cq.dam.cfm.FragmentTemplate;
import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetAttractionCFAdapter;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFAdapterFactory;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFModel;
import com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetModel;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import com.saudi.tourism.core.utils.Utils;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.adapter.AdapterFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class InformationListWidgetModelTest {
  @Mock
  private ResourceBundleProvider i18nProvider;
  @Mock
  private FragmentTemplate ft;

  private final Gson gson = new GsonBuilder().create();
  private final ResourceBundle i18n = new ResourceBundle() {
    @Override
    protected Object handleGetObject(String key) {
      switch (key) {
        case I18nConstants.I18_KEY_READ_MORE:
          return "Read More";
        case I18nConstants.I18_KEY_READ_LESS:
          return "Read Less";
        default:
          return "fake_i18n_value";
      }
    }

    @Override
    public Enumeration<String> getKeys() {
      return Collections.emptyEnumeration();
    }
  };
  @Mock
  private TagManager tagManager;
  @Mock
  private Tag tag;

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(InformationListWidgetModel.class, InformationListWidgetCFModel.class);

    var attractionAdapter = new InformationListWidgetAttractionCFAdapter();
    var adapterFactory = new InformationListWidgetCFAdapterFactory();
    adapterFactory.setAdapters(Arrays.asList(attractionAdapter));
    Dictionary properties = new Hashtable();
    properties.put("adaptables", "org.apache.sling.api.resource.Resource");
    properties.put("adapters", "com.saudi.tourism.core.models.components.informationlistwidget.v1.InformationListWidgetCFModel");
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    context.registerService(AdapterFactory.class, adapterFactory, properties);
    context.registerService(TagManager.class, tagManager);
  }


  @Test
  void informationSectionModelManualAuthoringDisabled(final AemContext context) throws IOException {
    // Arrange
    context
      .load()
      .json(
        "/components/information-list-widget/attraction-page.json",
        "/content/sauditourism/en/demo-cf-page");
    context
      .load()
      .json(
        "/components/information-section/attraction-cf.json",
        "/content/dam/sauditourism/cf/en/attractions/attraction1");

    context.currentPage("/content/sauditourism/en/demo-cf-page");
    context.currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/information_list_widget");

    final var contentFragment = context.resourceResolver().getResource("/content/dam/sauditourism/cf/en/attractions/attraction1").adaptTo(ContentFragment.class);
    var spyFragment = Mockito.spy(contentFragment);
    mockContentFragmentElement(spyFragment, "categories", new String[]{"sauditourism:classes-and-training/kayaking"});

    when(ft.getTitle()).thenReturn("attraction");
    doReturn(ft).when(spyFragment).getTemplate();
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/food_and_drink/restaurant/sauditourism-demo");
    when(tag.getTitle(Locale.forLanguageTag("en"))).thenReturn("demo");
    context.registerAdapter(Resource.class, ContentFragment.class, spyFragment);


    // Act
    final var model = context
      .currentResource()
      .adaptTo(InformationListWidgetModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, InformationListWidgetModel.class);

    // Assert
    assertNotNull(data);
    assertNotNull(data.getCategoriesTags());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/food_and_drink/restaurant/sauditourism-demo.svg", data.getCategoriesTags().get(0).getIcon());
    assertEquals("Demo", data.getCategoriesTags().get(0).getTitle());
  }

  // Mock a ContentFragment element with the given name and value.
  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);
    Mockito.when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
  }

}