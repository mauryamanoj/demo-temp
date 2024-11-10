package com.saudi.tourism.core.models.common;

import com.saudi.tourism.core.utils.CommonUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;

import javax.annotation.PostConstruct;

/**
 * The Rich Text Formatter Model.
 * Model Can be used to remove structural html tags from provided text.
 */
@Model(adaptables = {
    SlingHttpServletRequest.class}, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
@Slf4j public class RichTextFormatterModel {

  /**
   * The Rich Text Input. Raw Input.
   */
  @RequestAttribute
  private String richtext;

  /**
   * The output Text.
   */
  @Getter
  @Setter
  private String paragraph;

  /**
   * Model Initializer.
   */
  @PostConstruct private void init() {
    setParagraph(richtext);
    if (StringUtils.isNotBlank(richtext)) {
      setParagraph(CommonUtils.getEscapedRteText(richtext));
    }
  }
}
