package com.saudi.tourism.core.models.components.welcomeintro;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.WelcomeIntroModel;
import com.saudi.tourism.core.utils.Constants;
import com.saudi.tourism.core.utils.I18nConstants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class WelcomeIntroModelTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en/welcome-intro";

  private final Gson gson = new GsonBuilder().create();


  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private ResourceBundle i18nBundle;

  @BeforeEach
  public void setUp(AemContext context) {
    context.registerService(ResourceBundleProvider.class, i18nProvider,
      ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    when(i18nBundle.getString(I18nConstants.I18_KEY_READ_MORE)).thenReturn(I18nConstants.I18_KEY_READ_MORE);
    when(i18nBundle.getString(I18nConstants.I18_KEY_READ_LESS)).thenReturn(I18nConstants.I18_KEY_READ_LESS);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
    context.load().json("/components/welcome-intro/content"
        + ".json", CONTENT_PATH);
    context.currentResource(CONTENT_PATH);
  }

  @Test
  public void testWelcomeIntroModel(AemContext context) {
    //Arrange


    //Act
    final WelcomeIntroModel model = context.currentResource(CONTENT_PATH).adaptTo(WelcomeIntroModel.class);
    final String json = model.getJson();

    //Assert
    final WelcomeIntroModel data = gson.fromJson(json, WelcomeIntroModel.class);
    assertEquals("Climate control", data.getMainTitle());
    assertEquals("Weather to expect in Saudi", data.getSubTitle());
    assertEquals("Contrary to popular belief, it’s not always hot in Saudi Arabia. The kingdom experiences four distinct seasons —from chilly winter breezes in January to peak desert heat in August —and a climate that varies between regions. Read on for a seasonal overview of the kingdom’s weather.", data.getDescription());
    assertEquals("Read More", data.getReadMore());
    assertEquals("Read Less", data.getReadLess());
  }
}