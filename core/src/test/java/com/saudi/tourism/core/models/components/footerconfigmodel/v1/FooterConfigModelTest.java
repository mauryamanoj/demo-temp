package com.saudi.tourism.core.models.components.footerconfigmodel.v1;

import com.saudi.tourism.core.models.components.FooterConfigModel;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Assertions;
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
import java.util.Locale;
import java.util.ResourceBundle;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class FooterConfigModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @Mock private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18n =
      new ResourceBundle() {
        @Override
        protected Object handleGetObject(String key) {
          return "dummy_i18n_traduction";
        }

        @Override
        public Enumeration<String> getKeys() {
          return Collections.emptyEnumeration();
        }
      };

  @BeforeEach
  public void setUp(AemContext context) {
    final Dictionary properties = new Hashtable();
    properties.put(
        ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    context.registerService(ResourceBundleProvider.class, i18nProvider, properties);

    context.load().json("/components/footer-config-model/content.json", CONTENT_PATH);

    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18n);
  }

    @Test
    public void testFooterConfig(AemContext context) {
        String RESOURCE_PATH = "/Configs/footer-links-configs/jcr:content/root/responsivegrid/f01_footer_178390632";
        context.currentResource(CONTENT_PATH + RESOURCE_PATH);
        FooterConfigModel footerConfigModel = context.request().adaptTo(FooterConfigModel.class);
        Assertions.assertNotNull(footerConfigModel.getFooterLinkItems());

    }

}
