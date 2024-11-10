package com.saudi.tourism.core.models.components.hero.v1;

import com.saudi.tourism.core.services.FavoritesService;
import com.saudi.tourism.core.services.RegionCityService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class DetailHeroTest {
  private static final String CONTENT_PATH =
      "/content/sauditourism/en/homepage-usa/first-level/detail-hero/";

  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  @Mock
  private RegionCityService citiesService;

  @Mock
  private FavoritesService favoritesService;

  private ResourceBundle i18nBundle =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(final String key) {
          return "dummy_i18n_traduction";
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/h-03-detail-hero/content.json", CONTENT_PATH);

    final Dictionary properties = new Hashtable();
    properties.put(ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);
    context.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);
    context.registerService(RegionCityService.class, citiesService);
    context.registerService(FavoritesService.class, favoritesService);
  }

  @Test
  public void testContent(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/homepage-usa/first-level/detail-hero/jcr:content/root/responsivegrid/h03_detail_hero";

    CommonHeroModel model = context.currentResource(RESOURCE_PATH).adaptTo(CommonHeroModel.class);

    assertEquals("Title", model.getTitle());
    assertEquals("Subtitle", model.getSubtitle());
    assertEquals("Link Title", model.getLink().getCopy());
    assertEquals("/content/sauditourism/ar.html", model.getLink().getUrlWithExtension());
    assertEquals(false, model.getLink().isTargetInNewWindow());

    assertEquals("/content/dam/sauditourism/card-01.jpg", model.getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/card-mobile-01.jpg",
        model.getImage().getMobileImageReference());
    assertEquals("alt", model.getImage().getAlt());

  }
}
