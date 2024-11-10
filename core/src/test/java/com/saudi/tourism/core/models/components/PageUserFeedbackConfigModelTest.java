package com.saudi.tourism.core.models.components;


import com.day.cq.tagging.InvalidTagFormatException;
import com.day.cq.tagging.TagManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.saudi.tourism.core.services.AdminSettingsService;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.sling.api.resource.ResourceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import com.day.cq.tagging.Tag;

@ExtendWith({AemContextExtension.class, MockitoExtension.class})
public class PageUserFeedbackConfigModelTest {

  private static final String CONTENT_PATH = "/content/sauditourism/en/admin/Configs";

  @Mock
  private AdminSettingsService adminSettingsService;

  @Mock
  private ResourceResolver resourceResolver;

  @Mock
  private TagManager tagManager;


  private static final String RESOURCE_PATH_FULL_CONFIG = "/content/sauditourism/en/fullconfig";
  private static final String RESOURCE_PATH_EMPTY_CONFIG = "/content/sauditourism/en/emptyconfig";

  private final Gson gson = new GsonBuilder().create();

  @BeforeEach
  public void setUp(final AemContext context) throws InvalidTagFormatException {
    context.load().json("/components/c100-page-user-feedback/content.json", RESOURCE_PATH_FULL_CONFIG);
    context.load().json("/components/c100-page-user-feedback/content-empty.json", RESOURCE_PATH_EMPTY_CONFIG);

    context.addModelsForClasses(PageUserFeedbackConfigModel.class);
    context.registerService(AdminSettingsService.class, adminSettingsService);


    TagManager tagManager = context.resourceResolver().adaptTo(TagManager.class);
    tagManager.createTag("sauditourism:userFeedbackCategories/article", "article", "article");


  }

  @Test
  public void testPageUserFeedbackConfigModelWithEmptyUserFeedBackConfig(AemContext context) {

    //Arrange
    UserFeedbackConfigModel userFeedbackConfigModel = new UserFeedbackConfigModel();
    userFeedbackConfigModel.setMessageTitle("globalMessageTitle");
    userFeedbackConfigModel.setClose("globalClose");
    userFeedbackConfigModel.setNo("globalNo");
    userFeedbackConfigModel.setYes("globalYes");
    userFeedbackConfigModel.setPlaceholder("globalPlaceholder");
    userFeedbackConfigModel.setSubmit("globalSubmit");
    userFeedbackConfigModel.setModalThankYouMessage("globalModalThankYouMessage");
    userFeedbackConfigModel.setReportProblem("globalReportProblem");
    userFeedbackConfigModel.setOpenNewTab(true);
    userFeedbackConfigModel.setModalTitle("globalModalTitle");
    userFeedbackConfigModel.setSurveyLinkTitle("globalSurveyLinkTitle");
    userFeedbackConfigModel.setSurveyLinkURL("globalSurveyLinkURL");


    when(adminSettingsService.getUserFeedbackConfig(Mockito.anyString())).thenReturn(userFeedbackConfigModel);




    //Act
    final PageUserFeedbackConfigModel model = context.currentResource(RESOURCE_PATH_EMPTY_CONFIG).adaptTo(PageUserFeedbackConfigModel.class);

    //Assert

    assertEquals("globalMessageTitle", model.getMessageTitle());
    assertEquals("globalClose", model.getClose());
    assertEquals("globalNo", model.getNo());
    assertEquals("globalYes", model.getYes());
    assertEquals("globalPlaceholder", model.getPlaceholder());
    assertEquals("globalSubmit", model.getSubmit());
    assertEquals("globalModalThankYouMessage", model.getModalThankYouMessage());
    assertEquals("globalReportProblem", model.getReportProblem());
    assertEquals(true, model.getOpenNewTab());
    assertEquals("globalModalTitle", model.getModalTitle());
    assertEquals("globalSurveyLinkTitle", model.getSurveyLinkTitle());
    assertEquals("globalSurveyLinkURL", model.getSurveyLinkURL());
    assertEquals("article", model.getCategory());



  }


  @Test
  public void testPageUserFeedbackConfigModelWithFullUserFeedBackConfig(AemContext context) {

    //Arrange
    UserFeedbackConfigModel userFeedbackConfigModel = new UserFeedbackConfigModel();
    userFeedbackConfigModel.setMessageTitle("globalMessageTitle");
    userFeedbackConfigModel.setClose("globalClose");
    userFeedbackConfigModel.setNo("globalNo");
    userFeedbackConfigModel.setYes("globalYes");
    userFeedbackConfigModel.setPlaceholder("globalPlaceholder");
    userFeedbackConfigModel.setSubmit("globalSubmit");
    userFeedbackConfigModel.setModalThankYouMessage("globalModalThankYouMessage");
    userFeedbackConfigModel.setReportProblem("globalReportProblem");
    userFeedbackConfigModel.setOpenNewTab(true);
    userFeedbackConfigModel.setModalTitle("globalModalTitle");
    userFeedbackConfigModel.setSurveyLinkTitle("globalSurveyLinkTitle");
    userFeedbackConfigModel.setSurveyLinkURL("globalSurveyLinkURL");


    when(adminSettingsService.getUserFeedbackConfig(Mockito.anyString())).thenReturn(userFeedbackConfigModel);




    //Act
    final PageUserFeedbackConfigModel model = context.currentResource(RESOURCE_PATH_FULL_CONFIG).adaptTo(PageUserFeedbackConfigModel.class);

    //Assert

    assertEquals("localMessageTitle", model.getMessageTitle());
    assertEquals("localClose", model.getClose());
    assertEquals("localNo", model.getNo());
    assertEquals("localYes", model.getYes());
    assertEquals("localPlaceholder", model.getPlaceholder());
    assertEquals("localSubmit", model.getSubmit());
    assertEquals("localModalThankYouMessage", model.getModalThankYouMessage());
    assertEquals("localReportProblem", model.getReportProblem());
    assertEquals(false, model.getOpenNewTab());
    assertEquals("localModalTitle", model.getModalTitle());
    assertEquals("localSurveyLinkTitle", model.getSurveyLinkTitle());
    assertEquals("localSurveyLinkURL", model.getSurveyLinkURL());
    assertEquals("article", model.getCategory());



  }


}
