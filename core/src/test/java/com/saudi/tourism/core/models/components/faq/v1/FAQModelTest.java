package com.saudi.tourism.core.models.components.faq.v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.models.common.Link;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith({ AemContextExtension.class, MockitoExtension.class})
class FAQModelTest {

  @Mock
  private SaudiTourismConfigs saudiTourismConfigs;
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext aemContext) {
    aemContext.addModelsForClasses(
      FAQModel.class,
      Link.class);
    aemContext.registerService(SaudiTourismConfigs.class, saudiTourismConfigs);

    when(saudiTourismConfigs.getFaqCategoriesEndpoint()).thenReturn("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveCategories");
    when(saudiTourismConfigs.getFaqArticlesEndpoint()).thenReturn("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveKnowledgeArticles");
    when(saudiTourismConfigs.getMuleSoftClientId()).thenReturn("8c54ee59-f8c4-45df-a78e-b44eb8408b31");
    when(saudiTourismConfigs.getMuleSoftClientSecret()).thenReturn("3db0fce1-57b6-4bf8-bb2a-b1596170226f");
  }

  @Test
  public void testFaq(final AemContext context){
    //Arrange
    context
      .load()
      .json(
        "/components/faq/page.json",
        "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/faq_section");

    // Act
    final var model =
      context
        .currentResource(
          "/content/sauditourism/en/demo-cf-page/jcr:content/root/responsivegrid/faq_section")
        .adaptTo(FAQModel.class);

    final var json = model.getJson();
    final var data = gson.fromJson(json, FAQModel.class);

    //Assert
    assertEquals("Frequently Asked Questions V1", data.getHeadline());
    assertEquals("faq", data.getComponentId());
    assertEquals("/content/sauditourism/en", data.getLink().getUrl());
    assertEquals("Link Title", data.getLink().getCopy());
    assertEquals("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveCategories", data.getFaqCategoriesEndpoint());
    assertEquals("https://acc-api.sta.gov.sa/vswebapi/v1/KnowledgeArticle/RetrieveKnowledgeArticles", data.getFaqArticlesEndpoint());
  }

}