package com.saudi.tourism.core.services.evisa.v1;

import com.saudi.tourism.core.models.common.CountriesGroupsListConfigModel;
import com.saudi.tourism.core.models.components.evisa.v1.EvisaConfigModel;
import com.saudi.tourism.core.services.RunModeService;
import com.saudi.tourism.core.services.UserService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EvisaServiceImplTest {

  @Mock
  private EvisaConfig evisaConfig;

  @Mock
  private UserService userService;

  @Mock
  private RunModeService runModeService;

  private EvisaService evisaService = new EvisaServiceImpl();

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext
        .load()
        .json(
            "/components/evisa/evisa-config.json",
            "/content/sauditourism/en/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config");
    aemContext
        .load()
        .json(
            "/components/evisa/countries-groups.json",
            "/content/sauditourism/en/Configs/evisa/e-visa-countries-groups/jcr:content/root/responsivegrid/evisa_countries_grou");

    aemContext.registerService(RunModeService.class, runModeService);
    aemContext.registerService(EvisaConfig.class, evisaConfig);
    aemContext.registerService(UserService.class, userService);
    aemContext.registerInjectActivateService(evisaService);
  }

  @Test
  void fetchFilteredEvisaConfigShouldRaiseIfProvideLocaleIsNull() {
    //Arrange

    //Act
    final Exception exception = assertThrows(NullPointerException.class, () -> {
      evisaService.fetchFilteredEvisaConfig(null, null, false, false, null, null);
    });

    //Assert
    assertEquals("locale is marked non-null but is null", exception.getMessage());
  }

  @Test
  void fetchFilteredEvisaConfigShouldReturnConfigForLocale(final AemContext aemContext) {
    //Arrange
    when(evisaConfig.getEVisaConfigPath()).thenReturn(
        "/content/sauditourism/{0}/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    final EvisaConfigModel result = evisaService.fetchFilteredEvisaConfig("en", null, false, false, null,  null);

    //Assert
    Assertions.assertNotNull(result.getImage());
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", result.getImage().getFileReference());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", result.getImage().getS7fileReference());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", result.getImage().getS7mobileImageReference());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", result.getImage().getDesktopImage());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", result.getImage().getMobileImage());
    Assertions.assertEquals("1280", result.getImage().getDeskTopBreakpoint());
    Assertions.assertEquals("420", result.getImage().getMobileBreakpoint());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getImage().getBreakpoints()));
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=353&hei=263 353w", result.getImage().getBreakpoints().get(0).getSrcset());
    Assertions.assertEquals("(max-width: 420px)", result.getImage().getBreakpoints().get(0).getMedia());
    Assertions.assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=604&hei=450 604w", result.getImage().getBreakpoints().get(1).getSrcset());

    Assertions.assertEquals("Select Country of Residency", result.getSelectCountryLabel());
    Assertions.assertEquals("Check if you need an entry visa to Saudi Arabia", result.getTitle());
    Assertions.assertEquals("Select a country", result.getSelectCountryPlaceholder());
    Assertions.assertEquals("Quick Search", result.getSearchCountryPlaceholder());
    Assertions.assertEquals("Tell us a bit more about you", result.getSelectQuestionLabel());
    Assertions.assertEquals("Please select an option", result.getSelectQuestionPlaceholder());

    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getQuestionsByVisaGroup()));
    Assertions.assertEquals("eligible-country", result.getQuestionsByVisaGroup().get(0).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getQuestionsByVisaGroup().get(0).getQuestions()));
    Assertions.assertEquals("I have a US, UK or Schengen visa?",
        result.getQuestionsByVisaGroup().get(0).getQuestions().get(0).getLabel());
    Assertions.assertEquals("non-eligible-country", result.getQuestionsByVisaGroup().get(1).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getQuestionsByVisaGroup().get(1).getQuestions()));
    Assertions.assertEquals("I have a US, UK or Schengen visa?",
        result.getQuestionsByVisaGroup().get(1).getQuestions().get(0).getLabel());
    Assertions.assertEquals("I have a GCC residency",
        result.getQuestionsByVisaGroup().get(1).getQuestions().get(1).getLabel());

    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getVisaTypes()));
    Assertions.assertEquals("eligible-country-not-us-uk-schengen", result.getVisaTypes().get(0).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getVisaTypes().get(0).getQuestions()));
    Assertions.assertEquals("US_UK_SCHENGEN_VISA", result.getVisaTypes().get(0).getQuestions().get(0).getCode());
    Assertions.assertEquals("Three visa type available to choose from:", result.getVisaTypes().get(0).getVisaTypesTitle());
    Assertions.assertFalse(result.getVisaTypes().get(0).getQuestions().get(0).getResponse());


    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getVisaTypes().get(0).getSections()));
    Assertions.assertEquals("You are eligible for an eVisa",
        result.getVisaTypes().get(0).getSections().get(0).getTitle());
    Assertions.assertEquals(
      "<h1><b>Required</b> <b>Documents</b></h1>\n"
        + "<h1> </h1>\n"
        + "<h3>PCR Test</h3>\n"
        + "<ul>\n"
        + "<li>A PCR test with a negative result is required.</li>\n"
        + "<li>Test must be performed at maximum of 72 hours prior to your trip</li>\n"
        + "</ul>\n"
        + "<h3>Medical Insurance</h3>\n"
        + "<ul>\n"
        + "<li>Have a medical insurance test that covers the costs of COVID-19 treatment in outpatient clinics, emergencies and hospitals, including the institutional quarantine for a period of 14 days.</li>\n"
        + "</ul>\n"
        + "<h3>COVID 19</h3>\n"
        + "<ul>\n"
        + "<li>Visitors must monitor COVID-19 Symptoms</li>\n"
        + "<li>Immediately call 937 is any symptoms appear.</li>\n"
        + "<li>Go to primary healthcare center or emergency if necessary</li>\n"
        + "</ul>\n",
      result.getVisaTypes().get(0).getSections().get(0).getRequirementsAndDocumentation());
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", result.getVisaTypes().get(0).getSections().get(0).getIcon());
    Assertions.assertEquals("/content/dam/sauditourism/mobile/favicon.png", result.getVisaTypes().get(0).getSections().get(0).getMobileIcon());
    Assertions.assertEquals("Apply For eVisa", result.getVisaTypes().get(0).getSections().get(0).getCard().getLink().getText());
    Assertions.assertTrue(result.getVisaTypes().get(0).getSections().get(0).getCard().getLink().isTargetInNewWindow());
    Assertions.assertEquals("/content/sauditourism/en/apply-for-evisa", result.getVisaTypes().get(0).getSections().get(0).getCard().getLink().getUrl());
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", result.getVisaTypes().get(0).getSections().get(0).getCard().getIcon());

    Assertions.assertNotNull(result.getVisaTypes().get(0).getSections().get(0).getInformations());
    Assertions.assertEquals("Visa on Arrival Price", result.getVisaTypes().get(0).getSections().get(0).getInformations().get(0).getKey());
    Assertions.assertEquals("SAR 300 (about USD 80).", result.getVisaTypes().get(0).getSections().get(0).getInformations().get(0).getValue());
  }


  @Test
  void fetchFilteredEvisaConfigWithOnlyQuestionsShouldReturnOnlyQuestions(final AemContext aemContext) {
    //Arrange
    when(evisaConfig.getEVisaConfigPath()).thenReturn(
        "/content/sauditourism/{0}/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    final String locale = "en";
    final boolean onlyQuestions = true;
    final boolean onlyVisaDetails = false;

    final String visaGroup = null;
    //Act
    final EvisaConfigModel result =
        evisaService.fetchFilteredEvisaConfig(locale, visaGroup, onlyQuestions, onlyVisaDetails, null,  null);

    //Assert
    Assertions.assertNull(result.getSelectCountryLabel());
    Assertions.assertNull(result.getTitle());
    Assertions.assertNull(result.getSelectCountryPlaceholder());
    Assertions.assertNull(result.getSearchCountryPlaceholder());
    Assertions.assertNull(result.getSelectQuestionLabel());
    Assertions.assertNull(result.getSelectQuestionPlaceholder());

    Assertions.assertEquals(2, result.getQuestionsByVisaGroup().size());
    Assertions.assertEquals("eligible-country", result.getQuestionsByVisaGroup().get(0).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(result.getQuestionsByVisaGroup().get(0).getQuestions()));
    Assertions.assertEquals("I have a US, UK or Schengen visa?",
        result.getQuestionsByVisaGroup().get(0).getQuestions().get(0).getLabel());


    Assertions.assertNull(result.getVisaTypes());

  }


  @Test
  void fetchFilteredEvisaConfigWithVisaGroupShouldReturnOnlyThatVisaGroup(final AemContext aemContext) {
    //Arrange
    when(evisaConfig.getEVisaConfigPath()).thenReturn(
        "/content/sauditourism/{0}/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    final String locale = "en";
    final boolean onlyQuestions = false;
    final boolean onlyVisaDetails = false;

    final String visaGroup = "eligible-country";

    //Act
    final EvisaConfigModel result =
        evisaService.fetchFilteredEvisaConfig(locale, visaGroup, onlyQuestions, onlyVisaDetails, null, null);

    //Assert

    Assertions.assertEquals(1, result.getQuestionsByVisaGroup().size());
    Assertions.assertEquals("eligible-country", result.getQuestionsByVisaGroup().get(0).getVisaGroup());
    Assertions.assertEquals(0, result.getVisaTypes().size());


  }

  @Test
  void fetchFilteredEvisaConfigWithVisaGroupAndResponseCodeShouldReturnOnlyMatchingVisaGroupAndResponseCode(final AemContext aemContext) {
    //Arrange
    when(evisaConfig.getEVisaConfigPath()).thenReturn(
        "/content/sauditourism/{0}/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());
    final String locale = "en";
    final boolean onlyQuestions = false;
    final boolean onlyVisaDetails = false;

    final String visaGroup = "eligible-country-not-us-uk-schengen";
    final String responseCode = "US_UK_SCHENGEN_VISA";



    //Act
    final EvisaConfigModel result =
        evisaService.fetchFilteredEvisaConfig(locale, visaGroup, onlyQuestions, onlyVisaDetails, responseCode, null);

    //Assert

    Assertions.assertEquals(0, result.getQuestionsByVisaGroup().size());
    Assertions.assertEquals(1, result.getVisaTypes().size());
    Assertions.assertEquals("eligible-country-not-us-uk-schengen", result.getVisaTypes().get(0).getVisaGroup());
    Assertions.assertEquals("You are eligible for an eVisa US UK Schengen",
        result.getVisaTypes().get(0).getSections().get(0).getTitle());
    Assertions.assertEquals("Apply For eVisa US UK Schengen",
        result.getVisaTypes().get(0).getSections().get(0).getCard().getLink().getText());
  }


  @Test
  void getCountriesGroups(final AemContext aemContext){
    //Arrange
    when(evisaConfig.getEVisaCountriesGroupsConfigPath()).thenReturn(
      "/content/sauditourism/{0}/Configs/evisa/e-visa-countries-groups/jcr:content/root/responsivegrid/evisa_countries_grou");
    when(userService.getResourceResolver()).thenReturn(aemContext.resourceResolver());

    //Act
    List<CountriesGroupsListConfigModel.CountryGroupModel> countriesGroups = evisaService.getCountriesGroups("en");

    //Assert
    assertNotNull(countriesGroups);
    assertEquals(4, countriesGroups.size());
    assertEquals("gcc", countriesGroups.get(0).getName());
    assertEquals("eligible", countriesGroups.get(1).getName());
    assertEquals("noneligible", countriesGroups.get(2).getName());
    assertEquals("Group D", countriesGroups.get(3).getName());
  }

}