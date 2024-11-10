package com.saudi.tourism.core.models.components.headerconfigmodel.v1;

import com.saudi.tourism.core.models.components.HeaderConfigModel;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class HeaderConfigModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/header-config-model/content.json", CONTENT_PATH);
  }

  @Test public void testHeaderConfig(AemContext context) {
    String RESOURCE_PATH =
        "/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
    context.currentResource(CONTENT_PATH + RESOURCE_PATH);
    HeaderConfigModel headerConfigModel = context.request().adaptTo(HeaderConfigModel.class);
    Assertions.assertNotNull(headerConfigModel.getTopLinkItems());
    Assertions.assertNotNull(headerConfigModel.getMultiSiteItems());
    Assertions.assertNotNull(headerConfigModel.getEVisaItems());
    Assertions.assertNotNull(headerConfigModel.getNavItems());

  }

}
