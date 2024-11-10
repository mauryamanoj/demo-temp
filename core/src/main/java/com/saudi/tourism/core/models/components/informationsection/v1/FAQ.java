package com.saudi.tourism.core.models.components.informationsection.v1;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.services.RunModeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

/**
 * FAQ Model.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class FAQ implements Serializable {

  /**
   * Resource Resolver.
   */
  @Inject
  private transient ResourceResolver resourceResolver;

  /**
   * RunMode Service.
   *
   * N.B: It's marked transient to avoid serializing it via CommonUtils.writeNewJSONFormat.
   */
  @OSGiService
  @Getter(AccessLevel.NONE)
  private transient RunModeService runModeService;

  /**
   * Question.
   */
  @ValueMapValue
  @Expose
  private String question;

  /**
   * Answer.
   */
  @ValueMapValue
  @Expose
  private String answer;

  @PostConstruct
  void init() {
    answer = FAQDeserializer.rewriteLinksInHtml(answer, resourceResolver, runModeService);
  }
}
