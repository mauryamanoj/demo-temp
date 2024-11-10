package com.saudi.tourism.core.models.components;

import com.adobe.cq.wcm.core.components.testing.mock.ContextPlugins;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.beans.AnalyticsListItemImpl;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextBuilder;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.apache.sling.testing.mock.sling.ResourceResolverType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AnalyticsTabsImplTest {
  @Mock
  private ResourceBundleProvider i18nProvider;

  @Mock
  private ResourceBundle resourceBundle;

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;

  private final Gson gson = new GsonBuilder().create();

  // Need this to test models extending Core Component Sling Models using the Delegate Pattern
  // c.f https://github.com/adobe/aem-core-wcm-components/issues/1777
  private final AemContext aemContext = new AemContextBuilder(ResourceResolverType.JCR_MOCK).plugin(ContextPlugins.CORE_COMPONENTS).build();

  private final static String PAGE_PATH = "/content/sauditourism/en/see/highlights/King-Abdulaziz-Center-for-World-Culture";

  private final static String RESOURCE_PATH = "/content/sauditourism/en/see/highlights/King-Abdulaziz-Center-for-World-Culture/jcr:content/root/responsivegrid/c09_tabs";

  @BeforeEach
  void setUp(final AemContext aemContext){

    aemContext.registerService(ResourceBundleProvider.class, i18nProvider, ComponentConstants.COMPONENT_NAME, Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    // Load c09-tabs & c27-teaser-with-cards components definition
    aemContext.load().json("/components/c09-tabs/c09-tabs-component-def.json", "/apps/sauditourism/components/content/c09-tabs/v1/c09-tabs");
    aemContext.load().json("/components/c09-tabs/c27-teaser-with-cards-component-def.json", "/apps/sauditourism/components/content/c27-teaser-with-cards/v1/c27-teaser-with-cards");


    aemContext.load().json("/components/c09-tabs/content.json", PAGE_PATH);

    lenient().when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(resourceBundle);
  }

  @Test
  void testAnalyticsTabsModelWithoutJsonSerialization(final AemContext aemContext){
    //Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(RESOURCE_PATH);

    //Act
    final AnalyticsTabsImpl model = aemContext.request().adaptTo(AnalyticsTabsImpl.class);

    //Assert
    assertEquals("01", model.getPatternId());
    assertEquals("Expositions", model.getHeading().getHeading().getText());
    assertEquals("h1", model.getHeading().getHeading().getWeight());
    assertEquals("/content/sauditourism/en/calendar/jeddah-season", model.getHeading().getLink().getUrl());
    assertEquals("/content/sauditourism/en/calendar/jeddah-season.html", model.getHeading().getLink().getUrlWithExtension());
    assertEquals("/calendar/jeddah-season", model.getHeading().getLink().getUrlSlingExporter());
    assertEquals("More", model.getHeading().getLink().getCopy());
    assertTrue(model.getHeading().getLink().isTargetInNewWindow());
    assertFalse(model.getHeading().isShowSlider());
    assertFalse(model.getHeading().isShowUnderline());

    assertEquals(6, model.getItems().size());

    AnalyticsListItemImpl item = model.getItems().get(0);
    assertNull(item.getUrl());
    assertEquals("Discover Ithra", item.getTitle());
    assertNull(item.getDescription());
    assertEquals("/content/sauditourism/en/see/highlights/King-Abdulaziz-Center-for-World-Culture/jcr:content/root/responsivegrid/c09_tabs/item_1", item.getPath());
    assertEquals("item_1", item.getName());
    assertNull(item.getEventName());
    assertEquals(1, item.getCards().size());
    assertEquals("/content/sauditourism/en/see/highlights/King-Abdulaziz-Center-for-World-Culture/jcr:content/root/responsivegrid/c09_tabs/item_1", item.getCards().get(0).getPath());
  }

  /*@Disabled
  @Test
  void testAnalyticsTabsModellWithJsonSerialization(final AemContext aemContext){
    //Arrange
    aemContext.currentPage(PAGE_PATH);
    aemContext.currentResource(RESOURCE_PATH);

    //Act
    final AnalyticsTabsImpl model = aemContext.request().adaptTo(AnalyticsTabsImpl.class);
    final String json = model.getJson();
    final AnalyticsTabsImpl data = gson.fromJson(json, AnalyticsTabsImpl.class);

    //Assert
    assertEquals("01", data.getOrnament());
    assertEquals("Expositions", data.getHeading().getHeading().getText());
    assertEquals("h1", data.getHeading().getHeading().getWeight());
    assertEquals("/content/sauditourism/en/calendar/jeddah-season", data.getHeading().getLink().getUrl());
    assertEquals("/content/sauditourism/en/calendar/jeddah-season.html", data.getHeading().getLink().getUrlWithExtension());
    assertEquals("/calendar/jeddah-season", data.getHeading().getLink().getUrlSlingExporter());
    assertEquals("More", data.getHeading().getLink().getCopy());
    assertTrue(data.getHeading().getLink().isTargetInNewWindow());
    assertFalse(data.getHeading().isShowSlider());
    assertFalse(data.getHeading().isShowUnderline());

    assertEquals(6, data.getItems().size());

    AnalyticsListItemImpl item = data.getItems().get(0);
    assertNull(item.getUrl());
    assertEquals("Discover Ithra", item.getTitle());
    assertNull(item.getDescription());
    assertNull(item.getPath());
    assertEquals("item_1", item.getName());
    assertNull(item.getEventName());
    assertEquals(1, item.getCards().size());
    AnalyticsListItemImpl.AnalyticsListItemCard analyticsItemCard = item.getCards().get(0);
    assertNull(analyticsItemCard.getPath());
    assertFalse(analyticsItemCard.getDetails().getHeader().isShowUnderline());
    assertFalse(analyticsItemCard.getDetails().getHeader().isShowSlider());
    assertEquals("white", analyticsItemCard.getDetails().getBackground());
    assertEquals("white", analyticsItemCard.getDetails().getBackground());
    assertEquals("04", analyticsItemCard.getDetails().getBackgroundOrnamentId());
    assertEquals("01", analyticsItemCard.getDetails().getPattern());
    assertFalse(analyticsItemCard.getDetails().getIsColorVariant());
    assertEquals("f-article-cards", analyticsItemCard.getDetails().getComponentType());
    assertEquals("f-article-cards", analyticsItemCard.getDetails().getComponentType());
    assertEquals(1, analyticsItemCard.getDetails().getListSize());
    assertEquals(
        "[{\"image\":{\"desktopImage\":\"https://scth.scene7.com/is/image/scth/card02-11\"},\"link\":{\"copy\":\"Discover Ithra\"},\"variant\":\"with-cta\",\"labels\":{\"simple-labels\":[],\"weather-label\":{}}}]",
      analyticsItemCard.getDetails().getCardsJsonData());
    assertTrue(analyticsItemCard.getDetails().isPublish());
    assertEquals(1, analyticsItemCard.getDetails().getCards().size());
    CardModel card = analyticsItemCard.getDetails().getCards().get(0);
    assertEquals("Discover Ithra", card.getTitle());
    assertEquals(
        "<p>To really immerse yourself in Ithra, it’s worth taking the Ithra Journey Tour: a 30-minute guided visit through all parts of the centre, as well as some of the art installations. Your guide will give you an overview of the building while relaying the story of its creation. It’s a great way to get your bearings and to help you decide which parts of Ithra you would like to delve into further.</p>\r\n",
        card.getDescription());
    assertEquals("left", card.getImagePosition());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a130/1920x1080/card02.jpg", card.getImage().getFileReference());
    assertEquals("/content/dam/saudi-tourism/media/highlights/a130/card02.jpg", card.getImage().getMobileImageReference());
    assertEquals("Discover Ithra", card.getImage().getAlt());
    assertEquals("https://scth.scene7.com/is/image/scth/card02-11", card.getImage().getS7fileReference());
    assertEquals("https://scth.scene7.com/is/image/scth/card02-9", card.getImage().getS7mobileImageReference());
    assertEquals("Discover Ithra", card.getLink().getCopy());
    assertEquals("Discover Ithra", card.getLink().getCopy());
    assertEquals(
        "{\"event\":\"Tour Package CTA Clicked\",\"linkURL\":null,\"linkTitle\":null,\"vendorName\":\"Highlights\",\"packageName\":\"\"}",
        card.getLink().getAppEventData());
    assertEquals("none", card.getCtaType());
    assertEquals("manual", card.getType());
  }*/
}