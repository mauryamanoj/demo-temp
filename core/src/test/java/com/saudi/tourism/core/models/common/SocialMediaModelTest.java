package com.saudi.tourism.core.models.common;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class SocialMediaModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/footer-config-model/content.json", CONTENT_PATH);
  }

  @Test
   public void testSocialMediaModel(AemContext context) {
    String RESOURCE_PATH = "/Configs/footer-links-configs/jcr:content/root/responsivegrid/f01_footer_178390632";
    SocialMediaModel socialMediaModel = context.currentResource( CONTENT_PATH + RESOURCE_PATH).adaptTo(SocialMediaModel.class);
    Assertions.assertNotNull(socialMediaModel.getSocialItems());
    Assertions.assertEquals(5, socialMediaModel.getSocialItems().size());

    for(SocialChannelModel socialChannel : socialMediaModel.getSocialItems()){
        Assertions.assertNotNull(socialChannel.getSocialChannelUrl());
        Assertions.assertNotNull(socialChannel.getSocialChannel());

    }

  }
}
