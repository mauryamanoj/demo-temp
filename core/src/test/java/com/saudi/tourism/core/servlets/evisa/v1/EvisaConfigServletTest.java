package com.saudi.tourism.core.servlets.evisa.v1;

import com.saudi.tourism.core.models.components.evisa.v1.EvisaConfigModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.evisa.v1.EvisaService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletRequest;
import org.apache.sling.testing.mock.sling.servlet.MockSlingHttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collections;

import static com.saudi.tourism.core.utils.Constants.PN_LOCALE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EvisaConfigServletTest {

  private static final String RESOURCE_PATH =
      "/content/sauditourism/en/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config";

  @Mock private EvisaService evisaService;

  @Mock private RunModeService runModeService;

  private MockSlingHttpServletRequest request;

  private MockSlingHttpServletResponse response;

  @InjectMocks private EvisaConfigServlet evisaConfigServlet = new EvisaConfigServlet();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.load().json("/components/evisa/evisa-config.json", RESOURCE_PATH);

    aemContext.registerService(RunModeService.class, runModeService);

    request = aemContext.request();
    response = aemContext.response();
  }

  @Test
  void doGetShouldReturnBadRequestIfLocaleNotProvided() throws ServletException, IOException {
    // Arrange

    // Act
    evisaConfigServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(400, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":400,\"message\":\"Parameters [locale] is empty or null\",\"response\":{}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnEvisaConfigIfLocaleIsProvided(final AemContext aemContext)
      throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    final EvisaConfigModel evisaConfigModel =
        aemContext.currentResource(RESOURCE_PATH).adaptTo(EvisaConfigModel.class);
    when(evisaService.fetchFilteredEvisaConfig(eq("en"), nullable(String.class), eq(false), eq(false),
        nullable(String.class), nullable(String.class))).thenReturn(evisaConfigModel);

    // Act
    evisaConfigServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(200, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":200,\"message\":\"success\",\"response\":{\"image\":{\"fileReference\":\"/content/dam/sauditourism/favicon.png\",\"s7fileReference\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider\",\"s7mobileImageReference\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider\",\"desktopImage\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage\\u003driyadh-season-desktop-hero-banner-slider\",\"mobileImage\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage\\u003driyadh-season-desktop-hero-banner-slider\",\"deskTopBreakpoint\":\"1280\",\"mobileBreakpoint\":\"420\",\"breakpoints\":[{\"srcset\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage\\u003driyadh-season-desktop-hero-banner-slider\\u0026wid\\u003d353\\u0026hei\\u003d263 353w\",\"media\":\"(max-width: 420px)\"},{\"srcset\":\"https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage\\u003driyadh-season-desktop-hero-banner-slider\\u0026wid\\u003d604\\u0026hei\\u003d450 604w\"}]},\"title\":\"Check if you need an entry visa to Saudi Arabia\",\"selectCountryLabel\":\"Select Country of Residency\",\"selectCountryPlaceholder\":\"Select a country\",\"searchCountryPlaceholder\":\"Quick Search\",\"selectQuestionLabel\":\"Tell us a bit more about you\",\"selectQuestionPlaceholder\":\"Please select an option\",\"informationsLabel\":\"Informations\",\"requirementsLabel\":\"Requirements\",\"questionsByVisaGroup\":[{\"visaGroup\":\"eligible-country\",\"questions\":[{\"code\":\"US_UK_SCHENGEN_VISA\",\"label\":\"I have a US, UK or Schengen visa?\"}]},{\"visaGroup\":\"non-eligible-country\",\"questions\":[{\"code\":\"US_UK_SCHENGEN_VISA\",\"label\":\"I have a US, UK or Schengen visa?\"},{\"code\":\"GCC_RESIDENCY\",\"label\":\"I have a GCC residency\"}]}],\"visaTypes\":[{\"visaGroup\":\"eligible-country-not-us-uk-schengen\",\"visaTypesTitle\":\"Three visa type available to choose from:\",\"questions\":[{\"code\":\"US_UK_SCHENGEN_VISA\",\"response\":false}],\"sections\":[{\"title\":\"You are eligible for an eVisa\",\"icon\":\"/content/dam/sauditourism/favicon.png\",\"mobileIcon\":\"/content/dam/sauditourism/mobile/favicon.png\",\"requirementsAndDocumentation\":\"\\u003ch1\\u003e\\u003cb\\u003eRequired\\u003c/b\\u003e \\u003cb\\u003eDocuments\\u003c/b\\u003e\\u003c/h1\\u003e\\n\\u003ch1\\u003e \\u003c/h1\\u003e\\n\\u003ch3\\u003ePCR Test\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eA PCR test with a negative result is required.\\u003c/li\\u003e\\n\\u003cli\\u003eTest must be performed at maximum of 72 hours prior to your trip\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\\u003ch3\\u003eMedical Insurance\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eHave a medical insurance test that covers the costs of COVID-19 treatment in outpatient clinics, emergencies and hospitals, including the institutional quarantine for a period of 14 days.\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\\u003ch3\\u003eCOVID 19\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eVisitors must monitor COVID-19 Symptoms\\u003c/li\\u003e\\n\\u003cli\\u003eImmediately call 937 is any symptoms appear.\\u003c/li\\u003e\\n\\u003cli\\u003eGo to primary healthcare center or emergency if necessary\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\",\"card\":{\"title\":\"Apply to eVisa!\",\"link\":{\"hideMap\":false,\"url\":\"/content/sauditourism/en/apply-for-evisa\",\"urlWithExtension\":\"/content/sauditourism/en/apply-for-evisa.html\",\"urlSlingExporter\":\"/apply-for-evisa\",\"targetInNewWindow\":true,\"text\":\"Apply For eVisa\",\"appType\":\"external\",\"internalUrl\":\"/content/sauditourism/en/apply-for-evisa\"},\"icon\":\"/content/dam/sauditourism/favicon.png\"},\"informations\":[{\"key\":\"Visa on Arrival Price\",\"value\":\"SAR 300 (about USD 80).\"},{\"key\":\"Medical Insurance Fees\",\"value\":\"SAR 180 (about  USD 48.03).\"},{\"key\":\"Entry\",\"value\":\"Multiple Entry or single entry depending on Ministry of Foreign Affairs.\"},{\"key\":\"Validity\",\"value\":\"The multiple entry visa is valid for a year from its issue date. The visa allows for stays of up to 90 days.\"}]}]},{\"visaGroup\":\"eligible-country-not-us-uk-schengen\",\"visaTypesTitle\":\"Three visa type available to choose from:\",\"questions\":[{\"code\":\"US_UK_SCHENGEN_VISA\",\"response\":true}],\"sections\":[{\"title\":\"You are eligible for an eVisa US UK Schengen\",\"icon\":\"/content/dam/sauditourism/favicon.png\",\"mobileIcon\":\"/content/dam/sauditourism/mobile/favicon.png\",\"requirementsAndDocumentation\":\"\\u003ch1\\u003e\\u003cb\\u003eRequired\\u003c/b\\u003e \\u003cb\\u003eDocuments\\u003c/b\\u003e\\u003c/h1\\u003e\\n\\u003ch1\\u003e \\u003c/h1\\u003e\\n\\u003ch3\\u003ePCR Test\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eA PCR test with a negative result is required.\\u003c/li\\u003e\\n\\u003cli\\u003eTest must be performed at maximum of 72 hours prior to your trip\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\\u003ch3\\u003eMedical Insurance\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eHave a medical insurance test that covers the costs of COVID-19 treatment in outpatient clinics, emergencies and hospitals, including the institutional quarantine for a period of 14 days.\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\\u003ch3\\u003eCOVID 19\\u003c/h3\\u003e\\n\\u003cul\\u003e\\n\\u003cli\\u003eVisitors must monitor COVID-19 Symptoms\\u003c/li\\u003e\\n\\u003cli\\u003eImmediately call 937 is any symptoms appear.\\u003c/li\\u003e\\n\\u003cli\\u003eGo to primary healthcare center or emergency if necessary\\u003c/li\\u003e\\n\\u003c/ul\\u003e\\n\",\"card\":{\"title\":\"Apply to eVisa!\",\"link\":{\"hideMap\":false,\"url\":\"/content/sauditourism/en/apply-for-evisa\",\"urlWithExtension\":\"/content/sauditourism/en/apply-for-evisa.html\",\"urlSlingExporter\":\"/apply-for-evisa\",\"targetInNewWindow\":true,\"text\":\"Apply For eVisa US UK Schengen\",\"appType\":\"external\",\"internalUrl\":\"/content/sauditourism/en/apply-for-evisa\"},\"icon\":\"/content/dam/sauditourism/favicon.png\"}}]}]}}",
        response.getOutputAsString());
  }

  @Test
  void doGetShouldReturnTechnicalExceptionIfEvisaServiceRaised(final AemContext aemContext)
      throws ServletException, IOException {
    // Arrange
    request.setParameterMap(Collections.singletonMap(PN_LOCALE, "en"));

    when(evisaService.fetchFilteredEvisaConfig(eq("en"), nullable(String.class), eq(false), eq(false),
        nullable(String.class), nullable(String.class)))
        .thenThrow(new RuntimeException("An exception happened"));

    // Act
    evisaConfigServlet.doGet(request, response);

    // Assert
    Assertions.assertEquals(500, response.getStatus());
    Assertions.assertEquals(
        "{\"code\":500,\"message\":\"An exception happened\",\"response\":{}}",
        response.getOutputAsString());
  }
}
