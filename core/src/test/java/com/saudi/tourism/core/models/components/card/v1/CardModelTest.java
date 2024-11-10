package com.saudi.tourism.core.models.components.card.v1;


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

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_BEST_FOR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CardModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en/card";


  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(final String key) {
        switch (key) {
          case I18_KEY_BEST_FOR:
            return "Best for";
          default:
            return null;
        }
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/c-03-card/content.json", CONTENT_PATH);
    context.registerService(
      ResourceBundleProvider.class,
      i18nProvider,
      ComponentConstants.COMPONENT_NAME,
      Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test public void testContent(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/card/jcr:content/root/responsivegrid/c03_card";

    CardModel model = context.currentResource(RESOURCE_PATH).adaptTo(CardModel.class);

    assertEquals("Title", model.getTitle());
    assertEquals("Subtitle", model.getSubtitle());
    assertEquals("Description", model.getDescription());
    assertEquals("/content/dam/we-retail-screens/italy.png", model.getImage().getFileReference());
    assertEquals("/content/dam/we-retail-screens/usa.png",
        model.getImage().getMobileImageReference());
    assertEquals("Alt image", model.getImage().getAlt());
    assertEquals("Best for", model.getBestForLabel());
  }
}
