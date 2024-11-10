package com.saudi.tourism.core.models.components.listcomponent.v1;

import com.saudi.tourism.core.models.common.Heading;
import com.saudi.tourism.core.models.common.Image;
import com.saudi.tourism.core.models.components.card.v1.CardModel;
import com.saudi.tourism.core.utils.Constants;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.i18n.ResourceBundleProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.service.component.ComponentConstants;

import java.util.*;

import static com.saudi.tourism.core.utils.I18nConstants.I18_KEY_BEST_FOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @noinspection ConstantConditions
 */
@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class CardListModelTest {

  public static final String CONTEXT_JSON = "/components/list-component/content.json";

  private static final String CONTENT_PATH = "/content/sauditourism/en/test-components";

  @Mock
  private ResourceBundleProvider i18nProvider;

  private ResourceBundle i18nBundle =
    new ResourceBundle() {
      @Override
      protected Object handleGetObject(final String key) {
        switch (key) {
          case I18_KEY_BEST_FOR:
            return "Best for";
          case "King":
            return "King";
          case "Capital":
            return "Capital";
          case "Population":
            return "Population";
          case "Currency":
            return "Currency";
          default:
            return null;
        }
      }
      @Override
      public Enumeration<String> getKeys() {
        return Collections.emptyEnumeration();
      }
    };


  @BeforeEach public void setUp(AemContext context) {

    context.load().json(CONTEXT_JSON, CONTENT_PATH);
    context.registerService(
      ResourceBundleProvider.class,
      i18nProvider,
      ComponentConstants.COMPONENT_NAME,
      Constants.I18N_RESOURCE_BUNDLE_PROVIDER_CLASS);
    when(i18nProvider.getResourceBundle(any(Locale.class))).thenReturn(i18nBundle);
  }

  @Test public void testCardListModel(AemContext context) {
    String RESOURCE_PATH = CONTENT_PATH + "/jcr:content/root/responsivegrid/list_component";
    CardListModel listModel = context.currentResource(RESOURCE_PATH).adaptTo(CardListModel.class);
    Assertions.assertEquals(2, listModel.getCards().size());
    testComponentHeading(listModel);
    testCardsData(listModel);
  }

  /**
   * Test component title data.
   *
   * @param listModel CardListModel
   */
  void testComponentHeading(CardListModel listModel) {
    Heading heading = listModel.getComponentTitle();
    Assertions.assertNotNull(heading.getText());
  }

  /**
   * Test card specific data.
   *
   * @param listModel CardListModel
   */
  void testCardsData(CardListModel listModel) {
    for (CardModel cardModel : listModel.getCards()) {
      Assertions.assertNotNull(cardModel.getImage());
      Image image = cardModel.getImage();
      Assertions.assertNotNull(image.getFileReference());
      Assertions.assertNotNull(image.getMobileImageReference());
      Assertions.assertNotNull(cardModel.getTitle());
      Assertions.assertNotNull(cardModel.getSubtitle());
      Assertions.assertNotNull(cardModel.getPosition());
      testTableItems(cardModel.getTableItems());
    }

    Assertions.assertEquals("Title, edit me", listModel.getCards().get(0).getTitle());
    Assertions.assertEquals("Subtitle, edit me", listModel.getCards().get(0).getSubtitle());
    Assertions.assertEquals("Position, edit me", listModel.getCards().get(0).getPosition());
    Assertions.assertEquals("/content/dam/saudi-tourism/media/photo-1582560770014-b802f96a6020.jpeg",
        listModel.getCards().get(0).getImage().getMobileImageReference());
    Assertions.assertEquals(2, listModel.getCards().size());
    Assertions.assertEquals(1, listModel.getCards().get(0).getTableItems().size());
    Assertions.assertEquals("King",
        listModel.getCards().get(0).getTableItems().get(0).getKey());
    Assertions.assertEquals("Value 1, edit me",
        listModel.getCards().get(0).getTableItems().get(0).getValue());

  }

  void testTableItems(List<ListTableItem> tableItems) {
    for (ListTableItem listTableItem : tableItems) {
      Assertions.assertNotNull(listTableItem);
      Assertions.assertNotNull(listTableItem.getKey());
      Assertions.assertNotNull(listTableItem.getValue());
    }
  }
}


