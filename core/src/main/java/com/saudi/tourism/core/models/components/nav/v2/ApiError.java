package com.saudi.tourism.core.models.components.nav.v2;

import com.google.gson.annotations.Expose;
import com.saudi.tourism.core.utils.CommonUtils;
import com.saudi.tourism.core.utils.I18nConstants;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.ResourceBundle;

/** Messages to show when SSID API returns an error. */
@Value
class ApiError {

  /**
   * Message to show when SSID API returns an error.
   */
  @Expose
  private String message;

  /**
   * CTA of the button to show when SSID API returns an error.
   */
  @Expose
  private String ctaText;

  ApiError(ResourceBundle i18nBundle) {
    String i18nMessage = CommonUtils.getI18nString(i18nBundle, I18nConstants.API_ERROR_MESSAGE);
    if (StringUtils.isBlank(i18nMessage)) {
      i18nMessage = I18nConstants.API_ERROR_MESSAGE;
    }
    this.message = i18nMessage;
    String i18nCtaText = CommonUtils.getI18nString(i18nBundle, I18nConstants.API_ERROR_CTA_TEXT);
    if (StringUtils.isBlank(i18nCtaText)) {
      i18nCtaText = I18nConstants.API_ERROR_CTA_TEXT;
    }
    this.ctaText = i18nCtaText;
  }
}
