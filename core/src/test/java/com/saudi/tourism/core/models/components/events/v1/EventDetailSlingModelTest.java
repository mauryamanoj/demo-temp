package com.saudi.tourism.core.models.components.events.v1;

import com.saudi.tourism.core.models.common.EventDetailSlingModel;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.impl.RegionCityServiceImpl;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@ExtendWith(AemContextExtension.class)
public class EventDetailSlingModelTest {


  private static final String CONTENT_PATH = "/content/sauditourism/en";


  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/header-config-model/content.json", CONTENT_PATH);
    context.load().json("/components/events/content.json", CONTENT_PATH + "/event");

    final RegionCityService mockCitiesService = mock(RegionCityServiceImpl.class);
    context.registerService(mockCitiesService);
    final RegionCity testCity = new RegionCity("test", "test");
    doReturn(testCity).when(mockCitiesService).getRegionCityById(anyString(), anyString());

    final ResourceBundle i18n = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return key;
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };
    final ResourceBundleProvider i18nProvider = mock(ResourceBundleProvider.class);
    doReturn(i18n).when(i18nProvider).getResourceBundle(any(Locale.class));
    context.registerService(ResourceBundleProvider.class, i18nProvider,
        ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
  }

  @Test
  public void testSimpleSliderModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/event";

    EventDetailSlingModel model =getEventDetailSlingModel(RESOURCE_PATH, context);

    assertEquals("Omar Khairat", model.getEventDetail().getTitle());

  }

  private EventDetailSlingModel getEventDetailSlingModel(String resourcePath, AemContext context,
      Object... properties) {
    Resource resource = context.currentResource(resourcePath);
    if (resource != null && properties != null) {
      //context.contentPolicyMapping(resource.getResourceType(), properties);
    }
    context.request().setContextPath("");
    context.request().setAttribute("navResourcePath", "/content/sauditourism/en/Configs"
        + "/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327");
    return context.request().adaptTo(EventDetailSlingModel.class);
  }
}
