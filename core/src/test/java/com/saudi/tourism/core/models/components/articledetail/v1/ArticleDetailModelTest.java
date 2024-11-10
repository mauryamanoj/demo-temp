package com.saudi.tourism.core.models.components.articledetail.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class ArticleDetailModelTest {
  private final Gson gson = new GsonBuilder().create();

  private final AemContext aemContext = new AemContext();

  @BeforeEach
  void setUp(final AemContext aemContext) {

    aemContext.addModelsForClasses(ArticleDetailModel.class);

    aemContext.load().json("/components/c12-article-detail/article-detail.json", "/article-detail");
  }

  @Test
  public void shouldResturnText(){
    //Arrange
    aemContext.currentResource("/article-detail");

    //Act
    final ArticleDetailModel model = aemContext.request().getResource().adaptTo(ArticleDetailModel.class);
    final String json = model.getJson();

    //Assert
    final ArticleDetailModel data = gson.fromJson(json, ArticleDetailModel.class);
    assertEquals("<p>Lors de votre séjour, prenez le train pour profiter des vues pittoresques et voyager rapidement. Le train reliant Dammam à Riyad est un moyen économique de visiter les villes du royaume tout en admirant le magnifique paysage.<br />\n" +
      "</p>\n", data.getText());
  }
}
