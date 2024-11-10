package com.saudi.tourism.core.models.common;

import com.day.cq.wcm.api.Page;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.Constants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;

import javax.annotation.PostConstruct;
import java.util.Locale;
import java.util.Objects;

/**
 * Model can be used to convert numbers into Arabic number representation.
 */
@Model(adaptables = SlingHttpServletRequest.class, defaultInjectionStrategy =
    DefaultInjectionStrategy.OPTIONAL)
@Slf4j
public class NumberTranslationModel {

  /**
   * Requested Number to be changed.
   */
  @RequestAttribute
  private String input;

  /**
   * Current Locale.
   */
  @ScriptVariable
  private Page currentPage;

  /**
   * Requested Number Type.
   */
  @RequestAttribute
  private String type;

  /**
   * Separator.
   */
  @RequestAttribute
  private String separator;

  /**
   * Translated Number.
   */
  @Setter
  @Getter
  private String translatedNumber;

  /**
   * Model Initializer.
   */
  @PostConstruct protected void init() {
    Locale locale = currentPage.getLanguage(true);
    if (locale.toString().equals(Constants.ARABIC_LOCALE)) {
      if (Objects.nonNull(type) && type.equals(Constants.DATE_TYPE) && Objects.nonNull(separator)) {
        setTranslatedNumber(CommonUtils.getArabicDate(input, separator));
      } else {
        setTranslatedNumber(CommonUtils.getArabicNumeralChar(input));
      }
    } else {
      setTranslatedNumber(input);
    }
  }
}
