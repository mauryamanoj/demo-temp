package com.saudi.tourism.core.models.components.evisa.v1;

import com.google.common.collect.ImmutableMap;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class EvisaConfigModelTest {

  private static final String RESOURCE_PATH = "/content/sauditourism/en/Configs/evisa/evisa-config/jcr:content/root/responsivegrid/c29_evisa_config";

  private static final ImmutableMap<String, Object> PROPERTIES =
    ImmutableMap.of("resource.resolver.mapping", ArrayUtils.toArray(
      "/:/",
      "^/content/sauditourism/</"
    ));

  private final AemContext aemContext =
    new AemContextBuilder().resourceResolverType(ResourceResolverType.JCR_MOCK)
      .resourceResolverFactoryActivatorProps(PROPERTIES)
      .build();


  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/evisa/evisa-config.json", RESOURCE_PATH);
  }

  @Test
  void shouldReturnImage(final AemContext aemContext){
    //Arrange

    //Act
    final EvisaConfigModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(EvisaConfigModel.class);

    //Assert
    Assertions.assertNotNull(model.getImage());
    assertEquals("/content/dam/sauditourism/favicon.png", model.getImage().getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", model.getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider", model.getImage().getS7mobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", model.getImage().getDesktopImage());
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider", model.getImage().getMobileImage());
    assertEquals("1280", model.getImage().getDeskTopBreakpoint());
    assertEquals("420", model.getImage().getMobileBreakpoint());

    assertTrue(CollectionUtils.isNotEmpty(model.getImage().getBreakpoints()));
    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=353&hei=263 353w", model.getImage().getBreakpoints().get(0).getSrcset());
    assertEquals("(max-width: 420px)", model.getImage().getBreakpoints().get(0).getMedia());

    assertEquals("https://scth.scene7.com/is/image/scth/riyadh-season-desktop-hero-banner-slider:crop-375x280?defaultImage=riyadh-season-desktop-hero-banner-slider&wid=604&hei=450 604w", model.getImage().getBreakpoints().get(1).getSrcset());

  }

  @Test
  void shouldReturnLabels(final AemContext aemContext){
    //Arrange

    //Act
    final EvisaConfigModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(EvisaConfigModel.class);

    //Assert
    Assertions.assertEquals("Select Country of Residency", model.getSelectCountryLabel());
    Assertions.assertEquals("Check if you need an entry visa to Saudi Arabia", model.getTitle());
    Assertions.assertEquals("Select a country", model.getSelectCountryPlaceholder());
    Assertions.assertEquals("Quick Search", model.getSearchCountryPlaceholder());
    Assertions.assertEquals("Tell us a bit more about you", model.getSelectQuestionLabel());
    Assertions.assertEquals("Please select an option", model.getSelectQuestionPlaceholder());
    Assertions.assertEquals("Requirements", model.getRequirementsLabel());
    Assertions.assertEquals("Informations", model.getInformationsLabel());
  }

  @Test
  void shouldReturnQuestions(final AemContext aemContext){
    //Arrange

    //Act
    final EvisaConfigModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(EvisaConfigModel.class);

    //Assert
    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getQuestionsByVisaGroup()));

    Assertions.assertEquals("eligible-country", model.getQuestionsByVisaGroup().get(0).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getQuestionsByVisaGroup().get(0).getQuestions()));
    Assertions.assertEquals("US_UK_SCHENGEN_VISA", model.getQuestionsByVisaGroup().get(0).getQuestions().get(0).getCode());
    Assertions.assertEquals("I have a US, UK or Schengen visa?", model.getQuestionsByVisaGroup().get(0).getQuestions().get(0).getLabel());

    Assertions.assertEquals("non-eligible-country", model.getQuestionsByVisaGroup().get(1).getVisaGroup());
    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getQuestionsByVisaGroup().get(1).getQuestions()));
    Assertions.assertEquals("US_UK_SCHENGEN_VISA", model.getQuestionsByVisaGroup().get(1).getQuestions().get(0).getCode());
    Assertions.assertEquals("I have a US, UK or Schengen visa?", model.getQuestionsByVisaGroup().get(1).getQuestions().get(0).getLabel());
    Assertions.assertEquals("GCC_RESIDENCY", model.getQuestionsByVisaGroup().get(1).getQuestions().get(1).getCode());
    Assertions.assertEquals("I have a GCC residency", model.getQuestionsByVisaGroup().get(1).getQuestions().get(1).getLabel());
  }

  @Test
  void shouldReturnVisaTypes(final AemContext aemContext){
    //Arrange

    //Act
    final EvisaConfigModel model = aemContext.currentResource(RESOURCE_PATH).adaptTo(EvisaConfigModel.class);

    //Assert
    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getVisaTypes()));

    Assertions.assertEquals("eligible-country-not-us-uk-schengen", model.getVisaTypes().get(0).getVisaGroup());

    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getVisaTypes().get(0).getQuestions()));
    Assertions.assertEquals("US_UK_SCHENGEN_VISA", model.getVisaTypes().get(0).getQuestions().get(0).getCode());
    Assertions.assertEquals("Three visa type available to choose from:", model.getVisaTypes().get(0).getVisaTypesTitle());
    Assertions.assertFalse(model.getVisaTypes().get(0).getQuestions().get(0).getResponse());

    Assertions.assertTrue(CollectionUtils.isNotEmpty(model.getVisaTypes().get(0).getSections()));
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", model.getVisaTypes().get(0).getSections().get(0).getIcon());
    Assertions.assertEquals("/content/dam/sauditourism/mobile/favicon.png", model.getVisaTypes().get(0).getSections().get(0).getMobileIcon());
    Assertions.assertEquals("You are eligible for an eVisa", model.getVisaTypes().get(0).getSections().get(0).getTitle());
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
      model.getVisaTypes().get(0).getSections().get(0).getRequirementsAndDocumentation());
    Assertions.assertEquals("Apply For eVisa", model.getVisaTypes().get(0).getSections().get(0).getCard().getLink().getText());
    Assertions.assertTrue(model.getVisaTypes().get(0).getSections().get(0).getCard().getLink().isTargetInNewWindow());
    Assertions.assertEquals("/en/apply-for-evisa", model.getVisaTypes().get(0).getSections().get(0).getCard().getLink().getUrl());
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", model.getVisaTypes().get(0).getSections().get(0).getCard().getIcon());
    Assertions.assertEquals("/content/dam/sauditourism/favicon.png", model.getVisaTypes().get(0).getSections().get(0).getCard().getIcon());

    Assertions.assertNotNull(model.getVisaTypes().get(0).getSections().get(0).getInformations());
    Assertions.assertEquals("Visa on Arrival Price", model.getVisaTypes().get(0).getSections().get(0).getInformations().get(0).getKey());
    Assertions.assertEquals("SAR 300 (about USD 80).", model.getVisaTypes().get(0).getSections().get(0).getInformations().get(0).getValue());
    Assertions.assertEquals("View All", model.getVisaTypes().get(0).getSections().get(0).getCard().getViewMoreLabel());
    Assertions.assertEquals("View All", model.getVisaTypes().get(0).getSections().get(0).getCard().getViewMoreMobileLabel());


  }


}