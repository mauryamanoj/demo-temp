package com.saudi.tourism.core.models.components.faq;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ChildResource;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import java.io.Serializable;
import java.util.List;

/**
 * FAQ Topic.
 */
@Model(adaptables = {Resource.class},
    defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Getter
@Slf4j
public class FAQTopic implements Serializable {

  /**
   * FAQ Topic.
   */
  @ValueMapValue
  @Expose
  private String faqTopic;

  /**
   * FAQ.
   */
  @ChildResource
  @Expose
  private List<FAQ> faq;
}
