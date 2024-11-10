package com.saudi.tourism.core.models;

import lombok.Data;

import java.util.List;

@Data
public class LanguageSwitchLocales {

  /**
   * List of unavailable locales.
   */
  private List<String> pageUnavailableLocales;
}
