package com.saudi.tourism.core.utils;

import com.saudi.tourism.core.models.components.HeaderConfigModel;
import com.saudi.tourism.core.models.components.NavItem;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

@ExtendWith(AemContextExtension.class)
public class NavItemUtilsTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/header-config-model/content.json", CONTENT_PATH);
  }

  @Test public void testNavigationLink(AemContext context) {
    String RESOURCE_PATH =
        "/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
    context.currentResource(CONTENT_PATH + RESOURCE_PATH);
    HeaderConfigModel headerConfigModel = context.request().adaptTo(HeaderConfigModel.class);
    Assertions.assertNotNull(headerConfigModel.getTopLinkItems());
    Assertions.assertNotNull(headerConfigModel.getMultiSiteItems());
    Assertions.assertNotNull(headerConfigModel.getEVisaItems());
    Assertions.assertNotNull(headerConfigModel.getNavItems());

    List<NavItem> navLinks = NavItemUtils
        .generateChildListItems(context.currentResource(), context.pageManager(), 2, false,false);
    Assertions.assertNotNull(navLinks);
    Assertions.assertNotNull(navLinks.get(navLinks.size() - 1).getTitle());
    for (NavItem navItem : navLinks) {
      Assertions.assertNotNull(navItem.getUrl());
      Assertions.assertNotNull(navItem.getUrlWithExtension());
      Assertions.assertNotNull(navItem.getTitle());
      Assertions.assertNotNull(navItem.getDescription());
      Assertions.assertNotNull(navItem.getFeatureImage());
      if (navItem.getTitle().equalsIgnoreCase("UNDERSTAND")) {
        Assertions.assertNotNull(navItem.getChildList());

      }
      Assertions.assertNotNull(navItem.getSiteName());

    }
  }

}
