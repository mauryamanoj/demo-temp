package com.saudi.tourism.core.models.components.categories;

import com.day.cq.tagging.Tag;
import com.day.cq.tagging.TagManager;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CategoriesCardsTest {
  private static final String CONTENT_PATH = "/content/sauditourism/en";
  @Mock
  private TagManager tagManager;
  @Mock
  private Tag tag;

  @BeforeEach
  public void setUp(AemContext context) {
    context.load().json("/components/categories/content.json", CONTENT_PATH);
    context.registerService(TagManager.class, tagManager);
  }

  @Test
  public void testCategoriesCardsModel(AemContext context) {
    String RESOURCE_PATH = "/content/sauditourism/en/jcr:content/root/responsivegrid/categories";

    when(tagManager.resolve(any())).thenReturn(tag);
    when(tag.getPath()).thenReturn("/content/cq:tags/sauditourism/categories/tags-icon/sauditourism:classes-and-training/kayaking");
    when(tag.getTitle(any())).thenReturn("Food");

    CategoriesCards categoriesCards =
      context.currentResource(RESOURCE_PATH).adaptTo(CategoriesCards.class);

    assertEquals(1, categoriesCards.getCards().size());
    assertNotNull(categoriesCards.getCards().get(0));
    assertEquals("Food", categoriesCards.getCards().get(0).getTitle());
    assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", categoriesCards.getCards().get(0).getIcon());

    assertEquals("DEMO", categoriesCards.getTitle());
    assertEquals("grid", categoriesCards.getView());
    Assertions.assertNotNull(categoriesCards.getBackgroundImage());
    Assertions.assertNotNull(categoriesCards.getBackgroundImage().getAlt());
    Assertions.assertNotNull(categoriesCards.getBackgroundImage().getMobileImage());
    assertEquals("/content/dam/sauditourism/visit_saudi_logo.png", categoriesCards.getBackgroundImage().getMobileImage());
    assertEquals("Test", categoriesCards.getBackgroundImage().getAlt());
    assertEquals("/content/dam/sauditourism/visit_saudi_logo.png", categoriesCards.getBackgroundImage().getDesktopImage());
    testCardSlidesData(categoriesCards);
  }


  /**
   * Test card slide specific data.
   *
   * @param categoriesCards CategoriesCards
   */
  void testCardSlidesData(CategoriesCards categoriesCards) {
    for (Card card : categoriesCards.getCards()) {
      Assertions.assertNotNull(card.getImage());
      assertEquals("https://s7e5a.scene7.com/is/image/scth/16520356267369094706", card.getImage().getS7fileReference());
      Assertions.assertNotNull(card.getTitle());
      assertEquals("Food", card.getTitle());
      Assertions.assertNotNull(card.getIcon());
      assertEquals("/content/dam/sauditourism/tag-icons/categories/tags-icon/sauditourism-classes-and-training/kayaking.svg", card.getIcon());
      Assertions.assertNotNull(card.getImage().getAlt());
      assertEquals("OKOK", card.getImage().getAlt());
      Assertions.assertNotNull(card.getLink());
      assertEquals("/content/sauditourism/app1", card.getLink().getUrl());
      assertEquals("APP", card.getLink().getCopy());
      assertEquals(true, card.getLink().isTargetInNewWindow());
      assertEquals("{\"event\":\"EVNT OK\",\"linkURL\":\"https://www.visitsaudi.com/app1\",\"linkTitle\":\"APP\",\"vendorName\":null,\"packageName\":\"\"}", card.getLink().getAppEventData());
    }
  }
}
