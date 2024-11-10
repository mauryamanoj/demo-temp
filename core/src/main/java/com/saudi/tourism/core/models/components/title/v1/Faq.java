package com.saudi.tourism.core.models.components.title.v1;

import lombok.Getter;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.google.gson.annotations.Expose;

/**
 * This class contains FAQ details.
 */
@Model(adaptables = Resource.class,
       defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class Faq {

  /**
   * Variable for the question.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String question;

  /**
   * Variable for the answer.
   */
  @Getter
  @ValueMapValue
  @Expose
  private String answer;

}
