package com.saudi.tourism.core.models.components.tripplan;

import com.saudi.tourism.core.models.components.tripplan.v1.TripPlanFilter;
import com.saudi.tourism.core.services.SSIDTripPlanService;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.osgi.service.component.ComponentConstants;

import javax.jcr.RepositoryException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

/**
 * Unit test for the create-itinerary-config admin component.
 */
@ExtendWith(AemContextExtension.class)
class CreateItineraryConfigTest {

  private static final String PATH =
      "/content/sauditourism/en/Configs/admin/jcr:content/create-itinerary-config";

  private static final TripPlanFilter SAMPLE_DEST = new TripPlanFilter();

  static {
    SAMPLE_DEST.setCityIds(Arrays.asList("riyadh", "makkah"));
  }

  @BeforeEach
  public void setUp(AemContext context) throws RepositoryException {
    context.load().json("/components/trip-plans/configs-admin-create-itinerary-config.json", PATH);

    final ResourceBundle i18n = new ResourceBundle() {
      @Override
      protected Object handleGetObject(@NotNull String key) {
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

    final SSIDTripPlanService tripPlanService = mock(SSIDTripPlanService.class);
    doReturn(SAMPLE_DEST).when(tripPlanService).getTripPlanFilter(anyString());
    context.registerService(SSIDTripPlanService.class, tripPlanService);
  }

  @Test
  void testAdaptingModel(AemContext context) {
    context.currentResource(PATH);
    final CreateItineraryConfig configModel =
        context.request().adaptTo(CreateItineraryConfig.class);
    assertNotNull(configModel);
    assertNotNull(configModel.getBackgroundImage().get("desktopImage"));
    assertNotNull(configModel.getBackgroundImage().get("mobileImage"));
    assertNotNull(configModel.getCreateTripPlanEndpoint());
    assertNotNull(configModel.getCitiesEndpoint());

    final String json = configModel.getJson();
    assertTrue(StringUtils.contains(json, "stepsCopies"));
    assertTrue(StringUtils.contains(json, "tripNameStep"));
    assertTrue(StringUtils.contains(json, "citiesStep"));
    assertTrue(StringUtils.contains(json, "dateSelectionStep"));
    assertTrue(StringUtils.contains(json, "modalCopies"));
    assertTrue(StringUtils.contains(json, "login"));
    assertTrue(StringUtils.contains(json, "error"));
    assertTrue(StringUtils.contains(json, "close"));
  }
}
