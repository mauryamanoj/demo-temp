package com.saudi.tourism.core.models.components.informationsection.v1;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * Information Section Model.
 */
@Builder
@Getter
public class InformationSectionCFModel {

  /**
   * Title.
   */
  @Getter
  private String qaTitle;

  /**
   * Hide Read More.
   */
  @Getter
  private boolean qaHideReadMore;

  /**
   * Read More Label.
   */
  @Getter
  private String qaReadMoreLabel;

  /**
   * Read Less Label.
   */
  @Getter
  private String qaReadLessLabel;

  /**
   * Questions and Answers.
   */
  @Getter
  private List<String> questionsAnswers;
}
