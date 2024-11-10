package com.saudi.tourism.core.models.components;

import com.day.cq.tagging.TagManager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.AdminSettingsService;
import com.saudi.tourism.core.services.SaudiTourismConfigs;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.Optional;

/**
 * User feedback Config Model.
 */
@Data
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class PageUserFeedbackConfigModel extends UserFeedbackConfigModel {

  /**
   * adminSettingsService.
   */
  @OSGiService
  private AdminSettingsService adminSettingsService;

  /**
   * ResourceResolver.
   */
  @Self
  @JsonIgnore
  private transient Resource currentResource;

  /**
   * ResourceResolver.
   */
  @JsonIgnore
  @Inject
  private transient ResourceResolver resourceResolver;


  /**
   * category of page.
   */
  @ValueMapValue
  @Expose
  private String category;

  /**
   * userFeedbackApiEndpoint.
   */
  @Expose
  private String userFeedbackApiEndpoint;

  /**
   * Reference of Saudi Tourism Configuration.
   */
  @OSGiService
  private transient SaudiTourismConfigs saudiTourismConfigs;

  public String getCategory() {
    return category;
  }

  /**
   * @return json data.
   */
  @JsonIgnore
  public String getJson() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting();
    Gson gson = gsonBuilder.create();
    return gson.toJson(this);
  }

  @PostConstruct
  protected void init() {

    userFeedbackApiEndpoint =
        saudiTourismConfigs.getMiddlewareDNS() + saudiTourismConfigs.getMiddlewareUserFeedbackEndpoint();


    TagManager tagManager;
    if (resourceResolver != null && org.apache.commons.lang.StringUtils.isNotBlank(category)) {
      tagManager = resourceResolver.adaptTo(TagManager.class);
      category = CommonUtils.getActualTagName(category, tagManager);
    }

    String path = Optional.ofNullable(currentResource.getParent()).map(Resource::getPath)
        .orElse(StringUtils.EMPTY);
    String language =
        CommonUtils.getPageNameByIndex(path, Constants.AEM_LANGAUAGE_PAGE_PATH_POSITION);

    UserFeedbackConfigModel globalUserfeedbackConfigModel = adminSettingsService.getUserFeedbackConfig(language);

    if (globalUserfeedbackConfigModel != null) {

      if (getPlaceholder() == null || getPlaceholder().isEmpty()) {
        setPlaceholder(globalUserfeedbackConfigModel.getPlaceholder());
      }

      if (getTitle() == null || getTitle().isEmpty()) {
        setTitle(globalUserfeedbackConfigModel.getTitle());
      }

      if (getYes() == null || getYes().isEmpty()) {
        setYes(globalUserfeedbackConfigModel.getYes());
      }

      if (getNo() == null || getNo().isEmpty()) {
        setNo(globalUserfeedbackConfigModel.getNo());
      }

      if (getReportProblem() == null || getReportProblem().isEmpty()) {
        setReportProblem(globalUserfeedbackConfigModel.getReportProblem());
      }

      if (getThankYouMessage() == null || getThankYouMessage().isEmpty()) {
        setThankYouMessage(globalUserfeedbackConfigModel.getThankYouMessage());
      }

      if (getModalTitle() == null || getModalTitle().isEmpty()) {
        setModalTitle(globalUserfeedbackConfigModel.getModalTitle());
      }

      if (getModalDescription() == null || getModalDescription().isEmpty()) {
        setModalDescription(globalUserfeedbackConfigModel.getModalDescription());
      }

      if (getMessageTitle() == null || getMessageTitle().isEmpty()) {
        setMessageTitle(globalUserfeedbackConfigModel.getMessageTitle());
      }

      if (getSubmit() == null || getSubmit().isEmpty()) {
        setSubmit(globalUserfeedbackConfigModel.getSubmit());
      }

      if (getSurveyLinkTitle() == null || getSurveyLinkTitle().isEmpty()) {
        setSurveyLinkTitle(globalUserfeedbackConfigModel.getSurveyLinkTitle());
      }

      if (getSurveyLinkURL() == null || getSurveyLinkURL().isEmpty()) {
        setSurveyLinkURL(globalUserfeedbackConfigModel.getSurveyLinkURL());
      }

      if (getOpenNewTab() == null) {
        setOpenNewTab(globalUserfeedbackConfigModel.getOpenNewTab());
      }

      if (getModalThankYouMessage() == null || getModalThankYouMessage().isEmpty()) {
        setModalThankYouMessage(globalUserfeedbackConfigModel.getModalThankYouMessage());
      }

      if (getClose() == null || getClose().isEmpty()) {
        setClose(globalUserfeedbackConfigModel.getClose());
      }

    }
  }

}
