package com.saudi.tourism.core.models.components.events;

import com.day.crx.JcrConstants;
import com.saudi.tourism.core.models.common.RegionCity;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.impl.RegionCityServiceImpl;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.SpecialCharConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;


@ExtendWith(AemContextExtension.class)
public class EventDetailTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/events";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/events/event-detail.json", CONTENT_PATH);

    final RegionCityService mockCitiesService = mock(RegionCityServiceImpl.class);
    context.registerService(mockCitiesService);
    final RegionCity testCity = new RegionCity("test", "test");
    doReturn(testCity).when(mockCitiesService).getRegionCityById(anyString(), anyString());

  }

  @Test public void testEventDetailModel(AemContext context) {
    String RESOURCE_PATH = "enrique-iglesias-";
    final Resource currentResource =
        context.currentResource(CONTENT_PATH + SpecialCharConstants.FORWARD_SLASH + RESOURCE_PATH);

    context.registerService(SlingSettingsService.class, Mockito.mock(SlingSettingsService.class));

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
    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name",
        "org.apache.sling.i18n.impl.JcrResourceBundleProvider");

    EventDetail eventDetail = testEventDetail(context);
    Assertions
        .assertEquals("/events/enrique-iglesias-", eventDetail.getUrlSlingExporter());
    Assertions.assertEquals("events/" + RESOURCE_PATH, eventDetail.getId());
  }

  private EventDetail testEventDetail(AemContext context) {
    EventDetail eventDetail =
        context.currentResource().getChild(JcrConstants.JCR_CONTENT).adaptTo(EventDetail.class);

    Assertions.assertNotNull(eventDetail);
    Assertions.assertTrue(eventDetail.getTargetGroup().size()==0);
    Assertions.assertEquals(Constants.LOCATION_ICON, eventDetail.getLocationIcon());
    Assertions.assertNotNull(eventDetail.getCardImage());
    Assertions.assertNotNull(eventDetail.getCardImageMobile());
    Assertions.assertNotNull(eventDetail.getFeatureEventImage());
    Assertions.assertNotNull(eventDetail.getFeatureEventMobileImage());
    Assertions.assertNotNull(eventDetail.getSliderImage());
    Assertions.assertNotNull(eventDetail.getStartDateCal());
    Assertions.assertNotNull(eventDetail.getEndDateCal());
    Assertions.assertNull(eventDetail.getStartTimeCal());
    Assertions.assertNull(eventDetail.getEndTimeCal());
    Assertions.assertNull(eventDetail.getLatitude());
    Assertions.assertNull(eventDetail.getLongitude());
    Assertions.assertNull(eventDetail.getStreet());
    Assertions.assertNull(eventDetail.getStreetNumber());
    Assertions.assertNull(eventDetail.getZipcode());
    Assertions.assertNotNull(eventDetail.getShortDescription());
    Assertions.assertNotNull(eventDetail.getStartDateMonth());
    Assertions.assertNotNull(eventDetail.getEndDateMonth());
    Assertions.assertNotNull(eventDetail.getImage());
    Assertions.assertNotNull(eventDetail.getYear());
    Assertions.assertNotNull(eventDetail.getStartDateDay());
    Assertions.assertNotNull(eventDetail.getEndDateDay());
    Assertions.assertNull(eventDetail.getEventInfo());
    Assertions.assertNotNull(eventDetail.getEventLink());
    Assertions.assertNotNull(eventDetail.getPath());
//    Assertions.assertNotNull(eventDetail.getLink());

    // STOCH related fields
    Assertions.assertEquals(true, eventDetail.getWeekendEvent());
    Assertions.assertEquals(94, eventDetail.getPromoTagText());
    Assertions.assertEquals(false, eventDetail.getFeaturedInMap());
    Assertions.assertEquals("Tier-2", eventDetail.getEventType());

    Assertions.assertEquals("2019-11-15T16:41:27.000Z", eventDetail.getLastModified());
    Assertions.assertEquals("2019-11-22T09:44:16.000+01:00", eventDetail.getCreatedDate());

    Assertions.assertEquals(1, eventDetail.getMapLinks().size());
    Assertions.assertEquals("name", eventDetail.getMapLinks().get(0).getName());
    Assertions.assertEquals("label", eventDetail.getMapLinks().get(0).getLabel());
    Assertions.assertEquals("google", eventDetail.getMapLinks().get(0).getPlatform());

    Assertions.assertEquals("https://www.google.com", eventDetail.getMapLinks().get(0).getLink());
    Assertions.assertEquals("parkingLinks", eventDetail.getMapLinks().get(0).getTypeMapLink());
    Assertions.assertEquals(false, eventDetail.getMapLinks().get(0).getActive());



    return eventDetail;
  }
}
