package com.saudi.tourism.core.models.components.contentfragment.season;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.adobe.cq.dam.cfm.FragmentData;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
class SeasonCFModelTest {

  @BeforeEach
  public void setUp(final AemContext aemContext) {
    aemContext.addModelsForClasses(SeasonCFModel.class);

    aemContext
      .load()
      .json(
        "/components/seasons/riyadh-season.json",
        "/content/dam/sauditourism/cf/en/seasons/riyadh-season");

    ContentFragment contentFragment = aemContext.resourceResolver().getResource("/content/dam/sauditourism/cf/en/seasons/riyadh-season").adaptTo(ContentFragment.class);
    ContentFragment spyFragment = Mockito.spy(contentFragment);

    mockContentFragmentElement(spyFragment, "title", "Riyadh Season");

    aemContext.registerAdapter(Resource.class, ContentFragment.class, spyFragment);
  }

  @Test
  void getSeasonCFModel(final AemContext aemContext) {
    //Arrange

    //Act
    final var model =
      aemContext
        .currentResource("/content/dam/sauditourism/cf/en/seasons/riyadh-season")
        .adaptTo(SeasonCFModel.class);

    //Assert
    assertNotNull(model);
    assertEquals("Riyadh Season", model.getTitle());
  }

  private <T> void mockContentFragmentElement(ContentFragment contentFragment, String elementName, T value) {
    ContentElement element = Mockito.mock(ContentElement.class);
    FragmentData elementData = Mockito.mock(FragmentData.class);

    Mockito.when(contentFragment.getElement(elementName)).thenReturn(element);
    Mockito.when(contentFragment.hasElement(elementName)).thenReturn(true); // Mimic the element presence
    Mockito.when(element.getValue()).thenReturn(elementData);
    Mockito.when(elementData.getValue((Class<T>) value.getClass())).thenReturn(value);
  }

}