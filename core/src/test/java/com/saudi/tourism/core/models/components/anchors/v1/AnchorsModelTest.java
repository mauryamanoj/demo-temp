package com.saudi.tourism.core.models.components.anchors.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.settings.SlingSettingsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class AnchorsModelTest {
  private final Gson gson = new GsonBuilder().create();

  private final AemContext aemContext = new AemContext();

  @Mock
  private SlingSettingsService settingsService;

  @BeforeEach
  void setUp(final AemContext aemContext) {
    aemContext.registerService(SlingSettingsService.class, settingsService);

    aemContext.addModelsForClasses(AnchorsModel.class);
    aemContext.load().json("/components/anchors/anchors.json", "/anchors-component");
  }

  @Test
  public void shouldReturnAnchorsComponent() {
    //Arrange
    aemContext.currentResource("/anchors-component");

    //Act
    final AnchorsModel model = aemContext.request().getResource().adaptTo(AnchorsModel.class);
    final String json = model.getJson();

    //Assert
    final AnchorsModel data = gson.fromJson(json, AnchorsModel.class);
    assertEquals("You might also like", data.getTitle());
    assertTrue(data.isLine());
    assertFalse(data.isShowInResponsive());
    assertEquals(2,data.getLinks().size());
    assertEquals("02",data.getLinks().get(0).getNumber());
    assertEquals("Riyadh",data.getLinks().get(0).getText());
    assertEquals("03",data.getLinks().get(1).getNumber());
    assertEquals("XYZ",data.getLinks().get(1).getText());
  }


}
