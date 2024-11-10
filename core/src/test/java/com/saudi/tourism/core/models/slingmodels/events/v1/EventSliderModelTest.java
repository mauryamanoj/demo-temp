package com.saudi.tourism.core.models.slingmodels.events.v1;

import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.models.components.events.EventDetail;
import com.saudi.tourism.core.models.components.events.EventListModel;
import com.saudi.tourism.core.models.components.events.EventsRequestParams;
import com.saudi.tourism.core.services.EventService;
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
import org.mockito.Mockito;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(AemContextExtension.class)
public class EventSliderModelTest {


  private static final String CONTENT_PATH = "/content/sauditourism/en/event";
  private EventService eventService;
  private EventDetail eventDetail;


  @BeforeEach public void setUp(AemContext context) throws RepositoryException {
    context.load().json("/components/events/content.json", CONTENT_PATH);
    eventService = Mockito.mock(EventService.class);
    List<EventDetail> list = new ArrayList<>();
    eventDetail = context.currentResource(CONTENT_PATH + "/jcr:content").adaptTo(EventDetail.class);
    list.add(eventDetail);
    when(eventService.getRelatedEvents(anyString(), anyBoolean(), any(EventDetail.class)))
        .thenReturn(new ArrayList<>());

    EventListModel eventListModel = new EventListModel();
    eventListModel.setData(list);
    when(eventService.getFilteredEvents(any(EventsRequestParams.class))).thenReturn(eventListModel);
    context.registerService(EventService.class, eventService);

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

  @Test public void testSimpleSliderModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/event";

    EventSliderModel model = getEventDetailSlingModel(RESOURCE_PATH, context);

    assertEquals(1, model.getEvents().size());

  }

  private EventSliderModel getEventDetailSlingModel(String resourcePath, AemContext context,
      Object... properties) {
    Resource resource = context.currentResource(resourcePath);
    if (resource != null && properties != null) {
      //context.contentPolicyMapping(resource.getResourceType(), properties);
    }
    context.request().setContextPath("");
    return context.request().adaptTo(EventSliderModel.class);
  }
}
