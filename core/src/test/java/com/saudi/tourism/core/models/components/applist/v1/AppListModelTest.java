package com.saudi.tourism.core.models.components.applist.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class AppListModelTest {
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(
      AppListModel.class,
      AppCardsModel.class
    );
  }

  @Test
  public void testAppListCards(final AemContext context) {
    // Arrange
    context
      .load()
      .json("/components/applist/applist.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/app_list");

    // Act
    final var model =
      context
        .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/app_list")
        .adaptTo(AppListModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, AppListModel.class);

    // Assert
    assertNotNull(data);
    assertEquals("Intercom apps", data.getTitle());
    assertEquals("See All", data.getLink().getText());
    assertEquals("sauditourism/link/url", data.getLink().getUrl());
    assertEquals(true, data.getLink().isTargetInNewWindow());
    assertEquals("big", data.getVariation());

    final var appCards = data.getCards();
    assertNotNull(appCards);
    assertEquals(1, appCards.size());
    assertEquals("/content/dam/sauditourism/placeholder.jpg", appCards.get(0).getImage().getS7fileReference());
    assertEquals("Placeholder image", appCards.get(0).getImage().getAlt());
    assertEquals("/content/sauditourism/ar/abha-festival", appCards.get(0).getLink().getUrl());
    assertEquals(false, appCards.get(0).getLink().isTargetInNewWindow());
    assertEquals("new", appCards.get(0).getPill());
    assertEquals("First line", appCards.get(0).getTitle());
    assertEquals("Second line",appCards.get(0).getSubTitle());
    assertNotNull(appCards.get(0).getCtaData());
    assertEquals("ctaEventName", appCards.get(0).getCtaData().getCtaEventName());
    assertEquals("pageCategory", appCards.get(0).getCtaData().getPageCategory());
    assertEquals("pageSubCategory", appCards.get(0).getCtaData().getPageSubCategory());
    assertEquals("sectionName", appCards.get(0).getCtaData().getSectionName());
  }
}