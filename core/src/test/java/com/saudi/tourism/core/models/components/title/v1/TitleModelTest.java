package com.saudi.tourism.core.models.components.title.v1;

import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class TitleModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/title";

  @BeforeEach public void setUp(AemContext context) {
    context.load().json("/components/c-02-title-intro/content.json", CONTENT_PATH);
  }

  @Test
  public void testTitleModel(AemContext context) {
    String RESOURCE_PATH =
        "/content/sauditourism/en/title/jcr:content/root/responsivegrid/c02_title";
    TitleModel model = context.currentResource(RESOURCE_PATH).adaptTo(TitleModel.class);

    //tab1
    assertEquals("Main Title", model.getMainTitle());
    assertEquals("Description Bellow title", model.getDescBelowTitle());

    //tab2
    assertEquals("Intro title", model.getIntroTitle());
    assertEquals("Intro sub", model.getIntroSubtitle());
    assertEquals("Description", model.getIntroDescription());

    //tab3
    assertEquals("Question 1", model.getFaqs().get(0).getQuestion());
    assertEquals("Answer 1", model.getFaqs().get(0).getAnswer());
    assertEquals(2, model.getFaqs().size());
  }

}
