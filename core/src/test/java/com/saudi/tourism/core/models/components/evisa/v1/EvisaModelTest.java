package com.saudi.tourism.core.models.components.evisa.v1;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(AemContextExtension.class)
public class EvisaModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/about-e-visa/jcr:content";
  private static final String RESOURCE_PATH = "/content/sauditourism/en/about-e-visa/jcr:content/root/responsivegrid/c07_evisa";
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/components/c-07-evisa/content.json", CONTENT_PATH);
    context.contentPolicyMapping(
        "c07-evisa/v1/c07-evisa",
        "corps/item0/breakpoint",
        "420",
        "corps/item0/imgRelativeWidth",
        "100vw",
        "corps/item0/rendition",
        "crop-375x280",
        "corps/item0/widths",
        "375,750");
  }

  @Test
  public void testEvisaModelWithoutSerialization(AemContext context) {

    //Act
    final EvisaModel model = context.currentResource(RESOURCE_PATH).adaptTo(EvisaModel.class);

    //Assert
    assertEquals("Get your eVisa", model.getMainTitle());
    assertEquals("The Saudi Arabian visa is accessible to all. We look forward to welcoming and hosting you.", model.getDescription());
    assertEquals("/content/dam/saudi-tourism/media/eVisalogo.png", model.getImage().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/eVisalogo.png", model.getImage().getMobileImageReference());
    assertEquals("Get your eVisa", model.getImage().getAlt());
    assertNull(model.getImage().getS7fileReference());
    assertNull(model.getImage().getS7mobileImageReference());
    assertEquals("/content/dam/no-dynamic-media-folder/manifest-newarticles-batch2/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", model.getBackgroundImage().getFileReference());
    assertEquals("/content/dam/no-dynamic-media-folder/manifest-newarticles-batch2/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", model.getBackgroundImage().getMobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", model.getBackgroundImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", model.getBackgroundImage().getS7mobileImageReference());
    assertEquals("https://visa.visitsaudi.com/", model.getLinkButton().getUrl());
    assertEquals("https://visa.visitsaudi.com/", model.getLinkButton().getUrlWithExtension());
    assertEquals("https://visa.visitsaudi.com/", model.getLinkButton().getUrlSlingExporter());
    assertEquals("Apply now", model.getLinkButton().getCopy());
    assertEquals(false, model.getLinkButton().isTargetInNewWindow());
  }

  @Test
  public void testEvisaModelWithSerialization(AemContext context) {
    //Act
    final EvisaModel model = context.currentResource(RESOURCE_PATH).adaptTo(EvisaModel.class);
    final String json = model.getJson();
    final EvisaModel data = gson.fromJson(json, EvisaModel.class);

    //Assert
    assertEquals("Get your eVisa", data.getMainTitle());
    assertEquals("The Saudi Arabian visa is accessible to all. We look forward to welcoming and hosting you.", data.getDescription());
    assertEquals("/content/dam/saudi-tourism/media/eVisalogo.png", data.getImage().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/eVisalogo.png", data.getImage().getMobileImageReference());
    assertEquals("Get your eVisa", data.getImage().getAlt());
    assertNull(data.getImage().getS7fileReference());
    assertNull(data.getImage().getS7mobileImageReference());
    assertEquals("/content/dam/no-dynamic-media-folder/manifest-newarticles-batch2/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", data.getBackgroundImage().getFileReference());
    assertEquals("/content/dam/no-dynamic-media-folder/manifest-newarticles-batch2/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", data.getBackgroundImage().getMobileImageReference());
    assertEquals("https://scth.scene7.com/is/image/scth/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", data.getBackgroundImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/sip-on-this_coffee-qa/sip_this_coffee_qa_card_1.jpg", data.getBackgroundImage().getS7mobileImageReference());
    assertEquals("https://visa.visitsaudi.com/", data.getLinkButton().getUrl());
    assertEquals("https://visa.visitsaudi.com/", data.getLinkButton().getUrlWithExtension());
    assertEquals("https://visa.visitsaudi.com/", data.getLinkButton().getUrlSlingExporter());
    assertEquals("Apply now", data.getLinkButton().getCopy());
    assertEquals(false, data.getLinkButton().isTargetInNewWindow());
  }

}
