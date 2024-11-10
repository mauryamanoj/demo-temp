package com.saudi.tourism.core.models.common;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.SlingConstants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(AemContextExtension.class)
public class ConfigurationModelTest {

    private static final String CONTENT_PATH = "/content/sauditourism/en";

    private static final String RESOURCE_TYPE = "sauditourism/components/content/n01-navigation-menu/v1/n01-navigation-menu";

    @BeforeEach
    public void setUp(AemContext context) {
        context.load().json("/components/header-config-model/content.json", CONTENT_PATH);
    }

    @Test
    public void testValidPagePath(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
        context.currentResource(RESOURCE_PATH);
        context.request().setAttribute(SlingConstants.PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE);
        context.request().setAttribute("configPage", "/content/sauditourism/en/Configs/navigation-menu-configs");
        context.request().setAttribute("isRelativePath", true);

        ConfigurationModel configModel = context.request().adaptTo(ConfigurationModel.class);
        Assertions.assertEquals(
                "/content/sauditourism/en/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327",
                configModel.getResourcePath());
    }

    @Test
    public void testIfNoResource(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
        context.currentResource(RESOURCE_PATH);
        context.request().setAttribute(SlingConstants.PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE);
        context.request().setAttribute("configPage", "/content/sauditourism/en/Configs/evisa-configs");
        context.request().setAttribute("isRelativePath", true);

        ConfigurationModel configModel = context.request().adaptTo(ConfigurationModel.class);
        Assertions.assertEquals("", configModel.getResourcePath());
    }

    @Test
    public void testInValidPage(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
        context.currentResource(RESOURCE_PATH);
        context.request().setAttribute(SlingConstants.PROPERTY_RESOURCE_TYPE, RESOURCE_TYPE);
        context.request().setAttribute("configPage", "/content/sauditourism/en/Configs/invalid");
        context.request().setAttribute("isRelativePath", true);
        ConfigurationModel configModel = context.request().adaptTo(ConfigurationModel.class);
        Assertions.assertEquals("", configModel.getResourcePath());
    }

    @Test
    public void testNoParameters(AemContext context) {
        String RESOURCE_PATH = "/content/sauditourism/en/Configs/navigation-menu-configs/jcr:content/root/responsivegrid/n01_navigation_menu_1396922327";
        context.currentResource(RESOURCE_PATH);
        ConfigurationModel configModel = context.request().adaptTo(ConfigurationModel.class);
        Assertions.assertEquals("", configModel.getResourcePath());
    }

}
