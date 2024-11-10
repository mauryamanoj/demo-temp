package com.saudi.tourism.core.models.components.cardsgrid;

import com.saudi.tourism.core.models.components.card.v1.CardModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import lombok.Getter;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.Enumeration;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test class for testing {@link CardsGridModel} methods.
 */
@ExtendWith(AemContextExtension.class)
@Getter
public class CardsGridModelTest {

  private ResourceBundleProvider i18nProvider;
  private static final String CONTENT_PATH = "/content/sauditourism/en/activity";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/cardsgrid/content.json", CONTENT_PATH);

    ResourceBundle i18nBundle = new ResourceBundle() {
      @Override
      protected Object handleGetObject(String key) {
        return "fake_translated_value";
      }

      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };

    i18nProvider = Mockito.mock(ResourceBundleProvider.class);
    Mockito.when(i18nProvider.getResourceBundle(Mockito.any())).thenReturn(i18nBundle);

    context.registerService(ResourceBundleProvider.class, i18nProvider, "component.name","org.apache.sling.i18n.impl.JcrResourceBundleProvider");
  }

  @Test public void testContent(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/activity/jcr:content/root/responsivegrid"
        + "/cardsgrid";

    CardsGridModel model = context.currentResource(RESOURCE_PATH).adaptTo(CardsGridModel.class);

    assertNotNull(model.getCardModels());

    for (CardModel cardModel : model.getCardModels()) {
      assertTrue(cardModel.isActivities());
    }
  }
}
