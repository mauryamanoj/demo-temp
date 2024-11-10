package com.saudi.tourism.core.models.components.contentfragmentfooter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.components.contentfragment.footer.*;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ContentFragmentFooterModelTest {
  private final Gson gson = new GsonBuilder().create();

  private final AemContext aemContext = new AemContext();

  @Mock
  private SlingSettingsService settingsService;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;
  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SlingSettingsService.class, settingsService);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    aemContext.addModelsForClasses(ContentFragmentFooterModel.class, FragmentFooter.class, SubFragmentContainer.class, SubFragmentNewsletter.class, SubFragmentDownloads.class, SubFragmentContact.class, SubFragmentBranding.class, SubFragmentGroup.class, SubFragmentInternalLinks.class, SubFragmentExternalLinks.class);
    aemContext.load().json("/components/contentfragmentfooter/content.json", "/content/sauditourism/en");
    aemContext.load().json("/components/contentfragmentfooter/content-fr.json", "/content/sauditourism/fr");
  }

  @Test
  public void shouldReturnFooterConfigurationEn() {
    //Arrange
    String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid";
    aemContext.currentResource(RESOURCE_PATH);
    aemContext.request().setPathInfo(RESOURCE_PATH);
    Mockito.when(saudiTourismConfigs.getMiddlewareNewsLetterEndpoint()).thenReturn("/v1/ssid/mailchimp/storeMail");
    Mockito.when( saudiTourismConfigs.getMiddlewareDNS()).thenReturn("https://middleware-qa.visitsaudi.com");
    //Act
    final ContentFragmentFooterModel model = aemContext.request().adaptTo(ContentFragmentFooterModel.class);
    assertNotNull(model.getFragmentFooter().getFragmentContact());
    assertNotNull(model.getFragmentFooter().getFragmentNewsletter());
    assertNotNull(model.getFragmentFooter().getFragmentNewsletter().getPlaceholder());
    assertEquals("Your email address", model.getFragmentFooter().getFragmentNewsletter().getPlaceholder());
    assertEquals(model.getFragmentFooter().getFragmentNewsletter().getApiUrl(),"https://middleware-qa.visitsaudi.com/v1/ssid/mailchimp/storeMail");

    assertNotNull(model.getFragmentFooter().getFragmentNewsletter().getApiUrl());
    assertNotNull(model.getFragmentFooter().getFragmentBranding());
    assertEquals(model.getFragmentFooter().getFragmentBranding().getStaLogo(),"/content/dam/sauditourism/footer/icon/logo2/logo.png");
    assertEquals(model.getFragmentFooter().getFragmentBranding().getVisitSaudiLogo(),"/content/dam/sauditourism/footer/icon/logo1/en.svg");
    assertNotNull(model.getFragmentFooter().getFragmentContainer());
    assertNotNull(model.getFragmentFooter().getFragmentGroup());
    assertNotNull(model.getFragmentFooter().getFragmentDownloads());
    assertNotNull(model.getFragmentFooter().getFragmentInternalLinks());
    assertNotNull(model.getFragmentFooter().getFragmentExternalLinks());
    final String json = model.getJson();
    //Assert
    assertNotNull( json);
  }

  @Test
  public void shouldReturnFooterConfigurationFr() {

    //Arrange
    String RESOURCE_PATH = "/content/sauditourism/fr/jcr:content/root/responsivegrid";
    aemContext.currentResource(RESOURCE_PATH);
    aemContext.request().setPathInfo(RESOURCE_PATH);
    Mockito.when(saudiTourismConfigs.getMiddlewareNewsLetterEndpoint()).thenReturn("/v1/ssid/mailchimp/storeMail");
    Mockito.when( saudiTourismConfigs.getMiddlewareDNS()).thenReturn("https://middleware-qa.visitsaudi.com");

    //Act
    final ContentFragmentFooterModel model = aemContext.request().adaptTo(ContentFragmentFooterModel.class);
    assertNotNull(model.getFragmentFooter().getFragmentNewsletter());
    assertNotNull(model.getFragmentFooter().getFragmentNewsletter().getApiUrl());
    assertNotNull(model.getFragmentFooter().getFragmentNewsletter().getPlaceholder());
    assertEquals("Votre email address", model.getFragmentFooter().getFragmentNewsletter().getPlaceholder());
    assertEquals(model.getFragmentFooter().getFragmentNewsletter().getApiUrl(),"https://middleware-qa.visitsaudi.com/v1/ssid/mailchimp/storeMail");

    assertNotNull(model.getFragmentFooter().getFragmentContact());
    assertNotNull(model.getFragmentFooter().getFragmentBranding());
    assertEquals(model.getFragmentFooter().getFragmentBranding().getStaLogo(),"/content/dam/sauditourism/footer/icon/logo2/logo.png");
    assertEquals(model.getFragmentFooter().getFragmentBranding().getVisitSaudiLogo(),"/content/dam/sauditourism/footer/icon/logo1/fr.svg");
    assertNotNull(model.getFragmentFooter().getFragmentContainer());
    assertNotNull(model.getFragmentFooter().getFragmentGroup());
    assertNotNull(model.getFragmentFooter().getFragmentDownloads());

    assertNotNull(model.getFragmentFooter().getFragmentInternalLinks());
    assertNotNull(model.getFragmentFooter().getFragmentExternalLinks());
    final String json = model.getJson();
    //Assert
    assertNotNull( json);
    assertEquals(json,"{\n" +
      "  \"fragmentNewsletter\": {\n" +
      "    \"title\": \"SUBSCRIBE TO OUR NEWSLETTER\",\n" +
      "    \"placeholder\": \"Votre email address\",\n" +
      "    \"ctaLabel\": \"SUBSCRIBE to our newsletterLETTER\",\n" +
      "    \"invalidEmailMessage\": \"Invalid Email Address.Invalid Email Address.Invalid Email Address.Invalid Email Address.\",\n" +
      "    \"successMessage\": \"You\\u0027ve been subscribed successfully\",\n" +
      "    \"failureMessage\": \"Error Happened While Registering your email. Please, try again later.\",\n" +
      "    \"apiUrl\": \"https://middleware-qa.visitsaudi.com/v1/ssid/mailchimp/storeMail\"\n" +
      "  },\n" +
      "  \"fragmentDownloads\": {\n" +
      "    \"title\": \"Downloads Banner\",\n" +
      "    \"ctaImageList\": [\n" +
      "      {\n" +
      "        \"ctaImage\": \"/content/dam/sauditourism/app_store.png\",\n" +
      "        \"ctaLink\": \"https://apps.apple.com/us/app/visit-saudi/id818179871\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaImage\": \"/content/dam/google-play-blanco-desktop.png\",\n" +
      "        \"ctaLink\": \"https://play.google.com/store/apps/details?id\\u003dsa.gov.apps.sauditourism\",\n" +
      "        \"isOpenInNewTab\": false\n" +
      "      }\n" +
      "    ],\n" +
      "    \"joinCta\": \"Click\"\n" +
      "  },\n" +
      "  \"fragmentGroup\": {\n" +
      "    \"orientation\": \"Horizontal\",\n" +
      "    \"patternType\": \"1\"\n" +
      "  },\n" +
      "  \"fragmentBranding\": {\n" +
      "    \"visitSaudiLogo\": \"/content/dam/sauditourism/footer/icon/logo1/fr.svg\",\n" +
      "    \"visitSaudiLogoLink\": \"https://www.visitsaudi.com/en\",\n" +
      "    \"poweredBy\": \"Powered by\",\n" +
      "    \"staLogo\": \"/content/dam/sauditourism/footer/icon/logo2/logo.png\",\n" +
      "    \"staLogoLink\": \"https://www.visitsaudi.com/en\"\n" +
      "  },\n" +
      "  \"fragmentInternalLinks\": {\n" +
      "    \"titleLink\": \"Discover Saudi\",\n" +
      "    \"ctaList\": [\n" +
      "      {\n" +
      "        \"ctaLabel\": \"See \\u0026 Do\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/see-do\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Plan Your Trip\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/plan-your-trip\",\n" +
      "        \"isOpenInNewTab\": false\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Travel Essentials\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/travel-essentials\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      }\n" +
      "    ]\n" +
      "  },\n" +
      "  \"fragmentExternalLinks\": {\n" +
      "    \"titleLink\": \"Business \\u0026 Partners\",\n" +
      "    \"ctaList\": [\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Aseer Summer Season\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/calendar/calendar-events/aseer-summer-season\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Rethink Summer\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/calendar/calendar-events/aseer-summer-season\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Saudi Toyota Championship 2023\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/calendar/calendar-events/aseer-summer-season\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      }\n" +
      "    ]\n" +
      "  },\n" +
      "  \"fragmentContact\": {\n" +
      "    \"titleLink\": \"Call Center\",\n" +
      "    \"ctaList\": [\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Local Call Center\",\n" +
      "        \"ctaLink\": \"tel:930\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"International Call Center\",\n" +
      "        \"ctaLink\": \"tel:930\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      }\n" +
      "    ]\n" +
      "  },\n" +
      "  \"ctaListIcon\": [\n" +
      "    {\n" +
      "      \"iconLabel\": \"facebook\",\n" +
      "      \"iconLink\": \"https://facebook.com/VisitSaudi\",\n" +
      "      \"isOpenInNewTab\": true\n" +
      "    },\n" +
      "    {\n" +
      "      \"iconLabel\": \"instagram\",\n" +
      "      \"iconLink\": \"https://instagram.com/VisitSaudi\",\n" +
      "      \"isOpenInNewTab\": true\n" +
      "    },\n" +
      "    {\n" +
      "      \"iconLabel\": \"twitter\",\n" +
      "      \"iconLink\": \"https://twitter.com/VisitSaudi\",\n" +
      "      \"isOpenInNewTab\": true\n" +
      "    },\n" +
      "    {\n" +
      "      \"iconLabel\": \"youtube\",\n" +
      "      \"iconLink\": \"https://www.youtube.com/channel/UCmFRhhc6Gt8b4bSUmTzNIoA\",\n" +
      "      \"isOpenInNewTab\": true\n" +
      "    }\n" +
      "  ],\n" +
      "  \"fragmentContainer\": {\n" +
      "    \"copyrights\": \"Copyrights\",\n" +
      "    \"upArrowCta\": \"CTA\",\n" +
      "    \"ctaList\": [\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Contact Us\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/help-support\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Privacy Policy\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/help-support\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      },\n" +
      "      {\n" +
      "        \"ctaLabel\": \"Cookies\",\n" +
      "        \"ctaLink\": \"https://www.visitsaudi.com/en/help-support\",\n" +
      "        \"isOpenInNewTab\": true\n" +
      "      }\n" +
      "    ]\n" +
      "  }\n" +
      "}");
  }
}