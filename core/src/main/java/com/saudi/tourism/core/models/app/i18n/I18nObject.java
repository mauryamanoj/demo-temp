package com.saudi.tourism.core.models.app.i18n;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * POJO used in the I18nServlet.
 */
@Getter
@Setter
@ToString
public class I18nObject implements Serializable {

  /**
   * Constructor.
   * @param locale locale
   * @param i18n list
   */
  public I18nObject(final String locale, final List<I18nDetail> i18n) {
    this.locale = locale;
    this.i18n = i18n;
  }

  /**
   * Locale.
   */
  private String locale;

  /**
   * List of details.
   */
  private List<I18nDetail> i18n;


}
