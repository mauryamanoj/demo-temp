package com.saudi.tourism.core.models.components;

import com.google.gson.annotations.Expose;
import lombok.Data;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * User feedback Config Model.
 */
@Data
@Model(adaptables = Resource.class,
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class UserFeedbackConfigModel {


  /**
   * The title to display.
   */
  @ValueMapValue
  @Expose
  private String title;

  /**
   * The label for 'Yes'.
   */
  @ValueMapValue
  @Expose
  private String yes;

  /**
   * The label for 'No'.
   */
  @ValueMapValue
  @Expose
  private String no;

  /**
   * The label for 'Report a Problem'.
   */
  @ValueMapValue
  @Expose
  private String reportProblem;

  /**
   * The 'Thank You' message.
   */
  @ValueMapValue
  @Expose
  private String thankYouMessage;

  /**
   * The title for the modal.
   */
  @ValueMapValue
  @Expose
  private String modalTitle;

  /**
   * The description for the modal.
   */
  @ValueMapValue
  @Expose
  private String modalDescription;

  /**
   * The title for the message.
   */
  @ValueMapValue
  @Expose
  private String messageTitle;

  /**
   * The placeholder for the message.
   */
  @ValueMapValue
  @Expose
  private String placeholder;

  /**
   * The label for 'Submit'.
   */
  @ValueMapValue
  @Expose
  private String submit;

  /**
   * The title for the survey link.
   */
  @ValueMapValue
  @Expose
  private String surveyLinkTitle;

  /**
   * The URL for the survey link.
   */
  @ValueMapValue
  @Expose
  private String surveyLinkURL;

  /**
   * The setting for opening in a new tab.
   */
  @ValueMapValue
  @Expose
  private Boolean openNewTab;

  /**
   * The 'Thank You' message for the modal.
   */
  @ValueMapValue
  @Expose
  private String modalThankYouMessage;

  /**
   * The label for 'Close'.
   */
  @ValueMapValue
  @Expose
  private String close;
}
