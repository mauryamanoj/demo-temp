package com.saudi.tourism.core.models.mobile.components.mobilelayout.v1;


import com.day.cq.wcm.api.Page;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(AemContextExtension.class)
public class SectionsModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/mobile/en/sections/section1";
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {

    context.addModelsForClasses(SectionsModel.class);
    
    context.load().json("/pages/mobile/mobile-section-page/content.json", CONTENT_PATH);
    context.currentPage("/content/sauditourism/mobile/en/sections/section1");



  }

  @Test
  public void testSectionsModel(AemContext context) {

    //Act
    final SectionsModel model = context.currentResource(CONTENT_PATH + "/jcr:content").adaptTo(SectionsModel.class);

    //Assert


    assertNotNull(model, "SectionsModel should not be null");

    // Test header model
    HeaderResponseModel header = model.getHeader();
    assertNotNull(header, "HeaderResponseModel should not be null");

    // Assertions for HeaderResponseModel properties
    assertEquals("section subtitle", header.getTitles().getSubTitle());
    assertEquals("section sidetitle", header.getTitles().getSideTitle());
    assertTrue(header.isShowSearchBar());
    assertFalse(header.isShowMapButton());
    assertEquals("HEADER_BANNER", header.getHeaderComponentStyle().getComponentUIType());
    assertFalse(header.getSteps().getShow());
    assertEquals("1/3", header.getSteps().getValue());
    assertFalse(header.getSideActions().getShowFilter());
    assertTrue(header.getSideActions().getShowFavorite());
    assertFalse(header.getSideActions().getShowMap());
    assertTrue(header.getSideActions().getShowShare());
    assertEquals("custom action title", header.getSideActions().getCustomAction().getTitle());
    assertTrue(header.getSideActions().getCustomAction().getShow());
    assertTrue(header.getSideActions().getCustomAction().getEnable());
    assertEquals("lefticon", header.getSideActions().getCustomAction().getLeftIcon());
    assertEquals("righticon", header.getSideActions().getCustomAction().getRightIcon());
    assertEquals("PRIMARY", header.getSideActions().getCustomAction().getButtonComponentStyle().getComponentUIType());
    assertEquals("WEB", header.getSideActions().getCustomAction().getCta().getType());
    assertEquals("cta Value", header.getSideActions().getCustomAction().getCta().getUrl());
    assertFalse(header.getTabs().isShow());
    assertNotNull(header.getTabs().getItems());
    assertEquals(1, header.getTabs().getItems().size());
    assertEquals("section icon id ", header.getTabs().getItems().get(0).getId());
    assertEquals("section icon icon", header.getTabs().getItems().get(0).getIcon());
    assertEquals("section icon title", header.getTabs().getItems().get(0).getTitle());

    // Test section model
    SectionResponseModel section = model.getSection();
    assertNotNull(section, "SectionResponseModel should not be null");

    // Assertions for SectionResponseModel properties
    assertEquals("MAIN_BIG", section.getItemComponentStyle().getComponentUIType());
    assertEquals("VERTICAL", section.getItemComponentStyle().getComponentScrollDirection());
    assertEquals("mediaUrl", section.getMediaGallery().get(0).getUrl());
    assertEquals("IMAGE", section.getMediaGallery().get(0).getType());
    assertEquals("thumbnailUrl", section.getMediaGallery().get(0).getThumbnailUrl());
    assertEquals("smallImageUrl", section.getMediaGallery().get(0).getSmallImageUrl());
    assertEquals(500.0, section.getMediaGallery().get(0).getHeight());
    assertEquals(200.0, section.getMediaGallery().get(0).getWidth());
    assertEquals("smallCenteredLogo", section.getMediaGallery().get(0).getCenteredLogoUrl());
    assertEquals("title", section.getSectionHeader().getTitle());
    assertEquals("iconTItle", section.getSectionHeader().getIconTitle());
    assertTrue(section.getSectionHeader().getShowViewAll());
    assertTrue(section.getSectionHeader().getViewHistory().getShow());
    assertEquals("DEEPLINK", section.getSectionHeader().getViewHistory().getCta().getType());
    assertEquals("cta Value", section.getSectionHeader().getViewHistory().getCta().getUrl());
    assertNotNull(section.getSectionHeader().getCategoriesTags());
    assertEquals(1, section.getSectionHeader().getCategoriesTags().size());
    assertEquals("title ", section.getSectionHeader().getCategoriesTags().get(0).getTitle());
    assertEquals("icon", section.getSectionHeader().getCategoriesTags().get(0).getIcon());

    // Test title
    String title = model.getTitle();
    assertNotNull(title, "Title should not be null");
    assertEquals("section1", title);
  }


}
