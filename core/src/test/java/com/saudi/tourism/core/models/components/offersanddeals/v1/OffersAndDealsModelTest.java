package com.saudi.tourism.core.models.components.offersanddeals.v1;

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
class OffersAndDealsModelTest {
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setup(final AemContext context) {
    context.addModelsForClasses(OffersAndDealsModel.class, Card.class);
  }

  @Test
  public void offersAndDealsShouldPassSuccessfully(final AemContext context) {
    // Arrange
    context
      .load()
      .json("/components/offers-deals/offers-deals.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/offers_deals");

    // Act
    final var model = context
      .currentResource("/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/offers_deals")
      .adaptTo(OffersAndDealsModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, OffersAndDealsModel.class);

    // Assert
    assertNotNull(data);
    //assertTrue(data.getBgTransparent());
    assertTrue(data.getIsWithOrnament());
    assertEquals("Offers", data.getTitle());
    assertEquals("offer", data.getType());
    assertEquals("Global", data.getLink().getText());
    assertEquals("/content/sauditourism/en/seasons", data.getLink().getUrl());
    assertEquals("ctaEventName", data.getCtaData().getCtaEventName());
    assertEquals("pageCategory", data.getCtaData().getPageCategory());
    assertEquals("pageSubCategory", data.getCtaData().getPageSubCategory());
    assertEquals("sectionName", data.getCtaData().getSectionName());
    assertEquals(1, data.getCards().size());
    assertEquals("Card", data.getCards().get(0).getTitle());
    assertEquals("Lorem Ipsum", data.getCards().get(0).getDescription());
    assertEquals("dateTimeLabel", data.getCards().get(0).getTitle2());
    assertEquals("priceLabel", data.getCards().get(0).getDescription2());
    assertEquals("/content/sauditourism/en/saudi-stopover", data.getCards().get(0).getLink().getUrl());
    assertEquals("CTA 2", data.getCards().get(0).getCta().getText());
    assertEquals("/content/sauditourism/en/seasons", data.getCards().get(0).getCta().getUrl());
    assertEquals("/content/dam/sauditourism/red-sea.jpeg", data.getCards().get(0).getImage().getFileReference());
    assertEquals("/content/dam/sauditourism/red-sea.jpeg", data.getCards().get(0).getImage().getS7fileReference());
    assertTrue(data.getCards().get(0).getImage().isTransparent());
    assertEquals("ctaEventName", data.getCards().get(0).getCardCtaData().getCtaEventName());
    assertEquals("pageCategory", data.getCards().get(0).getCardCtaData().getPageCategory());
    assertEquals("pageSubCategory", data.getCards().get(0).getCardCtaData().getPageSubCategory());
    assertEquals("sectionName", data.getCards().get(0).getCardCtaData().getSectionName());
  }
}
