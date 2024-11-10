package com.saudi.tourism.core.models.components;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(AemContextExtension.class)
public class UserFeedbackConfigModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/admin/Configs";


  private static final String RESOURCE_PATH = CONTENT_PATH+ "/jcr:content/user-feedback-settings";
  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) {
    context.load().json("/pages/admin-config-userfeedback.json", CONTENT_PATH);
    context.addModelsForClasses(UserFeedbackConfigModel.class);


  }

  @Test
  public void testUserFeedbackConfigModel(AemContext context) {

    //Act
    final UserFeedbackConfigModel model = context.currentResource(RESOURCE_PATH).adaptTo(UserFeedbackConfigModel.class);

    

    //Assert
    assertEquals("surveyLinkURL", model.getSurveyLinkURL());
    assertEquals("surveyLinkTitle", model.getSurveyLinkTitle());
    assertEquals("modalTitle", model.getModalTitle());
    assertEquals("thankYouMessage", model.getThankYouMessage());
    assertEquals("messageTitle", model.getMessageTitle());
    assertEquals("placeholder", model.getPlaceholder());
    assertEquals("reportProblem", model.getReportProblem());
    assertEquals("submit", model.getSubmit());
    assertEquals("modalThankYouMessage", model.getModalThankYouMessage());
    assertEquals("modalDescription", model.getModalDescription());
    assertEquals("no", model.getNo());
    assertEquals("yes", model.getYes());
    assertEquals("close", model.getClose());
    assertEquals("title", model.getTitle());
    assertEquals(true, model.getOpenNewTab());

  }


}
