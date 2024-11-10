package com.saudi.tourism.core.models.app.i18n;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * POJO used in the I18nServlet.
 */
@Getter
@Setter
@ToString
public class I18nDetail implements Serializable {

  /**
   * Constructor.
   * @param key key
   * @param value translation
   */
  public I18nDetail(final String key, final String value) {
    this.key = key;
    this.value = value;
  }

  /**
   * i18n key.
   */
  private String key;

  /**
   * i18n translation.
   */
  private String value;



}
