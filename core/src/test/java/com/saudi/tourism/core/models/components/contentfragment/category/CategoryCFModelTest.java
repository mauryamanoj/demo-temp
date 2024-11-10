package com.saudi.tourism.core.models.components.contentfragment.category;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class CategoryCFModelTest {

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.addModelsForClasses(CategoryCFModel.class);
    context
      .load()
      .json(
        "/components/all-destinations/food-cf.json",
        "/content/dam/sauditourism/cf/en/categories/food");
  }

  @Test
  void getCategoryCFModel(final AemContext aemContext) {
    //Arrange

    //Act
    final var model =
      aemContext
        .currentResource("/content/dam/sauditourism/cf/en/categories/food")
        .adaptTo(CategoryCFModel.class);
    final var json = model.getJson();
    final var data = gson.fromJson(json, CategoryCFModel.class);

    //Assert
    assertNotNull(model);
    assertEquals("Food", data.getTitle());
    assertEquals("categories_xs_food", data.getIcon());
    assertEquals("/content/dam/sauditourism/Holding image red wall.jpg", data.getImage().getFileReference());
    assertEquals("https://scth.scene7.com/is/image/scthacc/Holding image red wall", data.getImage().getS7fileReference());
  }

}